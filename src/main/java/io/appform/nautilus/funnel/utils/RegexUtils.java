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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by santanu.s on 26/01/16.
 */
public class RegexUtils {
    public static String convertToRegex(final List<String> stages) {
        Preconditions.checkNotNull(stages);
        Preconditions.checkArgument(!stages.isEmpty());
        String regex = "";
        int i = 0;
        for (String stage : stages) {
            if (0 == i) {
                regex = String.format("(%s.*)", PathUtils.transformName(stage));
            } else {
                regex = String.format("%s|(\\%d%s.*)", regex, i, PathUtils.transformName(stage));
            }
            i++;
        }
        return String.format(".*(%s)", regex);
    }

    public static Map<String, String> separateRegexes(final List<String> stages) {
        String regex = "";
        Map<String, String> segments = Maps.newHashMap();
        int i = 0;
        for (String stage : stages) {
            if (0 == i) {
                regex = String.format("%s.*", PathUtils.transformName(stage));
            } else {
                regex = String.format("%s%s.*", regex, PathUtils.transformName(stage));
            }
            i++;
            segments.put(String.format(".*%s", regex), stage);
        }
        return segments;
    }
}
