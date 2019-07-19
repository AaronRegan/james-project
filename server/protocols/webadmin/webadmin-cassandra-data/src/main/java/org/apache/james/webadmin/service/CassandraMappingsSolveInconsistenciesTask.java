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

package org.apache.james.webadmin.service;

import java.util.Optional;
import java.util.function.BiFunction;

import javax.inject.Inject;

import org.apache.james.json.DTOModule;
import org.apache.james.rrt.cassandra.CassandraMappingsSourcesDAO;
import org.apache.james.rrt.cassandra.migration.MappingsSourcesMigration;
import org.apache.james.server.task.json.dto.TaskDTO;
import org.apache.james.server.task.json.dto.TaskDTOModule;
import org.apache.james.task.Task;
import org.apache.james.task.TaskExecutionDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import reactor.core.publisher.Mono;

public class CassandraMappingsSolveInconsistenciesTask implements Task {
    public static final String TYPE = "cassandraMappingsSolveInconsistencies";

    private static class CassandraMappingsSolveInconsistenciesTaskDTO implements TaskDTO {

        private final String type;

        public CassandraMappingsSolveInconsistenciesTaskDTO(@JsonProperty("type") String type) {
            this.type = type;
        }

        @Override
        public String getType() {
            return type;
        }
    }

    public static final BiFunction<MappingsSourcesMigration, CassandraMappingsSourcesDAO, TaskDTOModule<CassandraMappingsSolveInconsistenciesTask, CassandraMappingsSolveInconsistenciesTaskDTO>> MODULE = (mappingsSourcesMigration, cassandraMappingsSourcesDAO) ->
        DTOModule
            .forDomainObject(CassandraMappingsSolveInconsistenciesTask.class)
            .convertToDTO(CassandraMappingsSolveInconsistenciesTaskDTO.class)
            .toDomainObjectConverter(dto -> new CassandraMappingsSolveInconsistenciesTask(mappingsSourcesMigration, cassandraMappingsSourcesDAO))
            .toDTOConverter((domainObject, typeName) -> new CassandraMappingsSolveInconsistenciesTaskDTO(typeName))
            .typeName(TYPE)
            .withFactory(TaskDTOModule::new);

    private final Task mappingsSourcesMigration;
    private final CassandraMappingsSourcesDAO cassandraMappingsSourcesDAO;

    @Inject
    CassandraMappingsSolveInconsistenciesTask(MappingsSourcesMigration mappingsSourcesMigration,
                                              CassandraMappingsSourcesDAO cassandraMappingsSourcesDAO) {
        this.mappingsSourcesMigration = mappingsSourcesMigration.asTask();
        this.cassandraMappingsSourcesDAO = cassandraMappingsSourcesDAO;
    }

    @Override
    public Result run() {
        return cassandraMappingsSourcesDAO.removeAllData()
            .doOnError(e -> LOGGER.error("Error while cleaning up data in mappings sources projection table"))
            .then(Mono.fromCallable(mappingsSourcesMigration::run))
            .onErrorResume(e -> Mono.just(Result.PARTIAL))
            .block();
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Optional<TaskExecutionDetails.AdditionalInformation> details() {
        return mappingsSourcesMigration.details();
    }
}
