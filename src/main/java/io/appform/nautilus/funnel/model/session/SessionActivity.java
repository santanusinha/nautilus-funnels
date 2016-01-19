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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Model class to represent a state change activity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionActivity {
    private long timestamp;

    @NotNull
    @NotEmpty
    private String state;

    private Map<String, Object> attributes;

    @ValidationMethod(message = "Only primitive types and strings can be passed as attributes")
    public boolean validateAttributes() {
        return AttributeUtils.isValid(attributes);
    }
}
