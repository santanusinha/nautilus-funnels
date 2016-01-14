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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test to for {@link PathUtils#normalise(List)}
 */
@Slf4j
public class PathUtilsTest {

    @Test
    public void testNormalise() throws Exception {
        List<Integer> input = ImmutableList.of(1, 22, 31, 49, 22, 22, 31, 49, 31, 49, 2, 25, 1, 2, 3, 4, 2, 2, 3, 4, 3, 4, 2, 1, 2, 3, 4, 2, 2, 3, 4, 3, 4, 2);
        List<Integer> output = PathUtils.normalise(new ArrayList<>(input));
        log.debug("INPUT: " + Joiner.on("->").join(input));
        log.debug("OUTPUT: " + Joiner.on("->").join(output));
        assertEquals("1->22->31->49->2->25->1->2->3->4->2", Joiner.on("->").join(output));
    }
}