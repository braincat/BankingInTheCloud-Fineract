/*
 * Copyright 2016 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.group.service.internal.service;

import io.mifos.group.api.v1.domain.GroupDefinition;
import io.mifos.group.service.ServiceConstants;
import io.mifos.group.service.internal.mapper.GroupDefinitionMapper;
import io.mifos.group.service.internal.repository.GroupDefinitionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupDefinitionService {

  private final Logger logger;
  private final GroupDefinitionRepository groupDefinitionRepository;

  @Autowired
  public GroupDefinitionService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                final GroupDefinitionRepository groupDefinitionRepository) {
    super();
    this.logger = logger;
    this.groupDefinitionRepository = groupDefinitionRepository;
  }

  public Optional<GroupDefinition> findByIdentifier(final String identifier) {
    return this.groupDefinitionRepository.findByIdentifier(identifier).map(GroupDefinitionMapper::map);
  }

  public List<GroupDefinition> fetchAllGroupDefinitions() {
    return this.groupDefinitionRepository.findAll()
        .stream()
        .map(GroupDefinitionMapper::map)
        .collect(Collectors.toList());
  }
}
