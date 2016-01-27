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

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by santanu.s on 27/01/16.
 */
@Slf4j
public class RegexUtilsTest {

    @Test
    public void testConvertToRegex() throws Exception {

    }

    @Test
    public void testRegexes() throws Exception {
        String regex = RegexUtils.convertToRegex(ImmutableList.of("A", "B", "C"));
        System.out.println(regex);
    }

    @Test
    public void testSeparateRegexes() throws Exception {
        System.out.println(RegexUtils.separateRegexes(ImmutableList.of("A", "B", "C")));
        List<String> stages = ImmutableList.of("A", "B", "C");
        final Collection<String> evalualtedRegex = RegexUtils.separateRegexes(stages).keySet();
        log.debug("Evaluated regular expression: {}", evalualtedRegex);
        matchWithRegexes("%X%->%A%->%X%->%X%->%X%->%C%", evalualtedRegex, ImmutableList.of(false, false, true));
        matchWithRegexes("%X%->%A%->%X%->%B%->%X%->%Y%", evalualtedRegex, ImmutableList.of(false, true, true));
        matchWithRegexes("%X%->%A%->%X%->%B%->%X%->%C%", evalualtedRegex, ImmutableList.of(true, true, true));
        matchWithRegexes("%X%->%M%->%X%->%B%->%X%->%Y%", evalualtedRegex, ImmutableList.of(false, false, false));
    }

    private void matchWithRegexes(final String target, Collection<String> evalualtedRegex, List<Boolean> expectedResult) {
        List<Boolean> result = evalualtedRegex.stream().map(target::matches).collect(Collectors.toCollection(ArrayList::new));
        System.out.println("Matching with: " + target);
        evalualtedRegex.forEach(regex -> System.out.println(String.format("%-30s: %b", regex, target.matches(regex))));
        Assert.assertEquals(expectedResult, result);
    }
}