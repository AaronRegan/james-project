/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.jmap.api.filtering.impl;

import java.util.List;

import javax.inject.Inject;

import org.apache.james.core.Username;
import org.apache.james.eventsourcing.EventSourcingSystem;
import org.apache.james.eventsourcing.Subscriber;
import org.apache.james.eventsourcing.eventstore.EventStore;
import org.apache.james.jmap.api.filtering.FilteringManagement;
import org.apache.james.jmap.api.filtering.Rule;
import org.reactivestreams.Publisher;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import reactor.core.publisher.Mono;

public class EventSourcingFilteringManagement implements FilteringManagement {

    private static final ImmutableSet<Subscriber> NO_SUBSCRIBER = ImmutableSet.of();

    private final EventStore eventStore;
    private final EventSourcingSystem eventSourcingSystem;

    @Inject
    public EventSourcingFilteringManagement(EventStore eventStore) {
        this.eventSourcingSystem = EventSourcingSystem.fromJava(
            ImmutableSet.of(new DefineRulesCommandHandler(eventStore)),
            NO_SUBSCRIBER,
            eventStore);
        this.eventStore = eventStore;
    }

    @Override
    public Publisher<Void> defineRulesForUser(Username username, List<Rule> rules) {
        return eventSourcingSystem.dispatch(new DefineRulesCommand(username, rules));
    }

    @Override
    public Publisher<Rule> listRulesForUser(Username username) {
        Preconditions.checkNotNull(username);

        FilteringAggregateId aggregateId = new FilteringAggregateId(username);

        return Mono.from(eventStore.getEventsOfAggregate(aggregateId))
            .flatMapIterable(history -> FilteringAggregate.load(aggregateId, history).listRules());
    }
}
