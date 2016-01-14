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

package io.appform.nautilus.funnel.persistence;

import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.model.core.TemporalTypedEntity;

import javax.ws.rs.client.Entity;
import java.util.List;

/**
 * Storage for {@link TemporalTypedEntity}
 */
public interface TemporalTypedEntityStore {
    default <T extends TemporalTypedEntity<T>> boolean store(final String tenantName, TemporalTypedEntity<T> entity) throws NautilusException {
        return store(tenantName, entity, null);
    }

    default <T extends TemporalTypedEntity<T>> boolean store(final String tenantName, TemporalTypedEntity<T> entity, String parent) throws NautilusException {
        return store(tenantName, entity, parent, false);
    }

    <T extends TemporalTypedEntity<T>> boolean store(String tenantName, TemporalTypedEntity<T> entity, String parent, boolean createOnly) throws NautilusException;

    default <T extends TemporalTypedEntity<T>> List<Boolean> store(final String tenantName, List<TemporalTypedEntity<T>> entities) throws NautilusException {
        return store(tenantName, entities, null);
    }

    default <T extends TemporalTypedEntity<T>> List<Boolean> store(final String tenantName, List<TemporalTypedEntity<T>> entities, String parent) throws NautilusException {
        return store(tenantName, entities, parent, true);
    }


    <T extends TemporalTypedEntity<T>> List<Boolean> store(String tenantName, List<TemporalTypedEntity<T>> entities, String parent, boolean createOnly) throws NautilusException;

    <T extends TemporalTypedEntity<T>> Entity<T> get(String id, Class<T> clazz) throws NautilusException;
}
