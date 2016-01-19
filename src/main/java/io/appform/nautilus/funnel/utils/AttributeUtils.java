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

package io.appform.nautilus.funnel.utils;

import org.apache.commons.lang3.ClassUtils;

import java.util.Map;

/**
 * Created by santanu.s on 19/01/16.
 */
public class AttributeUtils {
    public static boolean isValid(Map<String, Object> attributes) {
        if(null == attributes) {
            return true;
        }
        final boolean[] valid = {true};
        attributes.entrySet().forEach(entry -> {
            if(!entry.getKey().equals("location")) {
                Object value = entry.getValue();
                final Class<?> clazz = value.getClass();
                valid[0] &= (!ClassUtils.isPrimitiveOrWrapper(clazz)
                        && !(value instanceof String));
            }

        });
        return valid[0];
    }
}
