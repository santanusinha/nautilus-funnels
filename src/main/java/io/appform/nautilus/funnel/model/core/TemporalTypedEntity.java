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

package io.appform.nautilus.funnel.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.reflect.TypeToken;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.lucene.uid.Versions;

@Data
@NoArgsConstructor
public abstract class TemporalTypedEntity<T> {
    @JsonIgnore
    private final TypeToken<T> type = token();
    private String id;

    private long timestamp;

    //The following field is used for MVCC and should not be exposed to clients
    @JsonIgnore
    private long version = Versions.MATCH_ANY;

    public TemporalTypedEntity(String id) {
        this.id = id;
    }

    public TemporalTypedEntity(String id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    abstract protected TypeToken<T> token();
}
