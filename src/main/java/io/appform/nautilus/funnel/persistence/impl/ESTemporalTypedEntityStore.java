/*
 * Copyright 2016 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appform.nautilus.funnel.persistence.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.appform.nautilus.funnel.common.ErrorMessageTable;
import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.elasticsearch.ESConfiguration;
import io.appform.nautilus.funnel.elasticsearch.ESConnection;
import io.appform.nautilus.funnel.model.core.TemporalTypedEntity;
import io.appform.nautilus.funnel.persistence.EntityIDGenerator;
import io.appform.nautilus.funnel.persistence.TemporalTypedEntityStore;
import io.appform.nautilus.funnel.utils.ESUtils;
import io.appform.nautilus.funnel.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.engine.DocumentAlreadyExistsException;
import org.elasticsearch.index.engine.VersionConflictEngineException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ESTemporalTypedEntityStore implements TemporalTypedEntityStore {
    private final ObjectMapper mapper;
    private final ESConfiguration esConfiguration;
    private final ESConnection esConnection;

    public ESTemporalTypedEntityStore(ObjectMapper mapper,
                                      ESConfiguration esConfiguration,
                                      ESConnection esConnection) {
        this.mapper = mapper;
        this.esConfiguration = esConfiguration;
        this.esConnection = esConnection;
    }

    @Override
    public <T extends TemporalTypedEntity<T>> boolean store(final String tenantName, T entity, String parent, boolean createOnly) throws NautilusException {
        //esConnection.client().
        String id = null;
        final String type = TypeUtils.typeName(entity);
        try {
            id = new EntityIDGenerator().id(entity);
            IndexResponse response = esConnection
                    .client()
                    .prepareIndex(ESUtils.getCurrentIndex(tenantName, entity.getTimestamp()), type, id)
                                .setSource(mapper.writeValueAsBytes(entity))
                                .setParent(parent)
                                .setVersion(entity.getVersion())
                                .setOpType((createOnly)
                                        ? IndexRequest.OpType.CREATE
                                        : IndexRequest.OpType.INDEX)
                    .execute()
                    .actionGet();
            return (response.getVersion() > 0);
        } catch (DocumentAlreadyExistsException e) {
            return true;
        } catch (VersionConflictEngineException e) {
            return false;
        } catch (Exception e) {
            log.error(ErrorMessageTable.errorMessage(ErrorMessageTable.ErrorCode.ENTITY_SAVE_ERROR,
                    id, TypeUtils.typeName(entity)));
            throw new NautilusException(e, ErrorMessageTable.ErrorCode.ENTITY_SAVE_ERROR,
                                        id, TypeUtils.typeName(entity));
        }
    }

    @Override
    public <T extends TemporalTypedEntity<T>> List<Boolean> store(final String tenantName, List<T> entities, String parent, boolean createOnly) throws NautilusException {
        //esConnection.client().
        try {
            BulkRequestBuilder bulkBuilder = esConnection.client()
                    .prepareBulk();
            for(TemporalTypedEntity<T> entity : entities) {
                final String type = TypeUtils.typeName(entity);
                final String id = new EntityIDGenerator().id(entity);
                bulkBuilder.add(
                        Requests.indexRequest(ESUtils.getCurrentIndex(tenantName, entity.getTimestamp()))
                                .id(id)
                                .type(type)
                                .source(mapper.writeValueAsBytes(entity))
                                .parent(parent)
                                .version(entity.getVersion())
                                .opType((createOnly)
                                        ? IndexRequest.OpType.CREATE
                                        : IndexRequest.OpType.INDEX)

                );
            }
            BulkResponse response = bulkBuilder
                    .setConsistencyLevel(WriteConsistencyLevel.ALL)
                    .execute()
                    .actionGet();
            List<Boolean> responseEntities = Lists.newArrayList();
            for(BulkItemResponse item : response.getItems()) {
                responseEntities.add(!item.isFailed());
                if(item.isFailed()) {
                    log.warn("Indexing failed: [{}] {}", item.getFailure().getId(), item.getFailureMessage());
                }
            }
            return responseEntities;
        } catch (Exception e) {
            log.error(ErrorMessageTable.errorMessage(ErrorMessageTable.ErrorCode.ENTITY_BULK_SAVE_ERROR));
            throw new NautilusException(e, ErrorMessageTable.ErrorCode.ENTITY_BULK_SAVE_ERROR);
        }
    }

    @Override
    public <T extends TemporalTypedEntity<T>> Optional<T> get(final String tenantName, final String id, long timestamp, Class<T> clazz) throws NautilusException {
        T result = null;
        try {
            GetResponse response = esConnection.client().prepareGet()
                    .setIndex(ESUtils.getCurrentIndex(tenantName, timestamp))
                    .setId(id)
                    .setType(TypeUtils.typeName(clazz))
                    .execute()
                    .actionGet();
            if(!response.isExists()) {
                return Optional.empty();
            }
            result = mapper.readValue(response.getSourceAsBytes(), clazz);
            result.setVersion(response.getVersion());
            return Optional.of(result);
        } catch (IndexNotFoundException e) {
            log.warn("Index was not found: {}", e.getMessage());
            return Optional.empty();
        } catch (IOException e) {
            throw new NautilusException(e, ErrorMessageTable.ErrorCode.ENTITY_GET_ERROR, id, TypeUtils.typeName(clazz));
        }
    }

    /*
    @Override
    public <T extends TemporalTypedEntity<T>> Entity<T> latestForParent(String parent, Class<T> clazz) throws NautilusException {
        return null;
    }

    @Override
    public List<Entity> get(Filter filter) throws NautilusException {
        return null;
    }

    @Override
    public <T extends TemporalTypedEntity<T>> List<Entity<T>> get(Filter filter, TypeReference<T> type) throws NautilusException {
        return null;
    }*/
}
