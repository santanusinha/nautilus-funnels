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

import io.appform.nautilus.funnel.utils.AttributeUtils;
import io.dropwizard.validation.ValidationMethod;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * A set of activities done in this session. The specified activities will be added to existing session, if both
 * {@link SessionActivitySet#sessionId} and {@link SessionActivitySet#sessionStartTime} are same as provided before.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionActivitySet {

    /**
     * Session ID for the session.
     */
    private String sessionId;

    private long sessionStartTime;

    private Map<String, Object> attributes;

    @Singular
    private List<SessionActivity> activities;

    @ValidationMethod
    public boolean validateAttributes() {
        return AttributeUtils.isValid(attributes);
    }
}
