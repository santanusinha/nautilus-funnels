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

package io.appform.nautilus.funnel.model.session;

import com.google.common.reflect.TypeToken;
import io.appform.nautilus.funnel.model.core.TemporalTypedEntity;
import lombok.*;

/**
 * Transitions between nodes
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateTransition extends TemporalTypedEntity<StateTransition> {
    private String sessionId;
    private int sequence;
    private String from;
    private String to;
    private String normalizedPath;

    @Override
    protected TypeToken<StateTransition> token() {
        return TypeToken.of(StateTransition.class);
    }

}
