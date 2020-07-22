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

package org.apache.james.transport.matchers;

import static org.apache.james.transport.mailets.remote.delivery.Bouncer.IS_DELIVERY_PERMANENT_ERROR;

import java.util.Collection;
import java.util.function.Predicate;

import org.apache.james.core.MailAddress;
import org.apache.mailet.AttributeUtils;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMatcher;

import com.google.common.collect.ImmutableList;

/**
 * <p>
 * Checks if the mail has a temporary remote delivery failure attribute set to false (meaning it's a temporary error)
 * </p>
 *
 * Example:
 *
 * <pre><code>
 * &lt;mailet match=&quot;IsRemoteDeliveryTemporaryError&quot; class=&quot;&lt;any-class&gt;&quot;/&gt;
 * </code></pre>
 */
public class IsRemoteDeliveryTemporaryError extends GenericMatcher {
    @Override
    public Collection<MailAddress> match(Mail mail) {
        return AttributeUtils.getValueAndCastFromMail(mail, IS_DELIVERY_PERMANENT_ERROR, Boolean.class)
            .filter(Predicate.isEqual(false))
            .map(any -> mail.getRecipients())
            .orElse(ImmutableList.of());
    }
}
