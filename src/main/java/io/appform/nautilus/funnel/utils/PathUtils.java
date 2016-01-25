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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities for path manipulation
 */
public class PathUtils {

    public static List<String> normalise(final List<String> inputPath) {
        List<String> path = new ArrayList<>(inputPath);
        //Remove self loops
        for (int i = 1; i < path.size(); i++) {
            if (Objects.equals(path.get(i), path.get(i - 1))) {
                path.set(i, null);
            }
        }
        List<String> dedupList = path.parallelStream()
                .filter(listItem -> !Strings.isNullOrEmpty(listItem))
                .collect(Collectors.toCollection(ArrayList::new));
        //Sequence can't be greater than half of the path
        int seqSize = dedupList.size()/2;
        while (seqSize >= 2) { //Can't have smaller repetitions

            //The algo marks the duplicate bits as true
            BitSet bitSet = new BitSet(dedupList.size());
            //System.out.println("SEQ SIZE: " + seqSize);
            for(int i = 0; i < dedupList.size() - seqSize; i++) {

                //Create the sequence to search for
                final List<String> subList = dedupList.subList(i, i + seqSize);
                boolean duplicateSeqFound = true;
                //System.out.println("CHECKING: " + Joiner.on("->").join(subList));
                if(dedupList.size() < i + 2 * seqSize ) {
                    //System.out.println("SKIPPING");
                    continue;
                }
                for(int j = i + seqSize, k = 0; k < subList.size(); j++, k++) {
                    if(!subList.get(k).equals(dedupList.get(j))) {
                        //There is a non-duplicate bit
                        //Useless to go further with this sequence
                        duplicateSeqFound = false;
                        break;
                    }
                }

                if(duplicateSeqFound) {
                    //Set the bits
                    bitSet.set(i, i + seqSize);
                    List<String> tmpList = Lists.newArrayListWithCapacity(dedupList.size() - seqSize);
                    for (int ii = 0; ii < dedupList.size(); ii++) {
                        if (!bitSet.get(ii)) {
                            //Add not new path only if bit is not set
                            tmpList.add(dedupList.get(ii));
                        }
                    }
                    //Next loop will use the compressed path
                    dedupList = tmpList;
                    //System.out.println("DUPL: " + Joiner.on("->").join(subList));
                    //System.out.println("NORM PATH: " + Joiner.on("->").join(dedupList));
                    break;
                }
            }

            seqSize = (bitSet.isEmpty())        //If no duplicate was found in this iteration
                        ? seqSize - 1           //Try with a smaller sequence
                        : dedupList.size() / 2; //Deduplicate the modified path
        }


        return dedupList;
    }

    public static Map<String, Integer> rankNodes(final List<String> paths) {
        Map<String, Integer> ranks = new HashMap<>();

        paths.stream().forEach(path -> {
            String[] nodes = path.split(Constants.PATH_STATE_SEPARATOR);
            for (int j = 0; j < nodes.length; j++) {
                if (!ranks.containsKey(nodes[j])) {
                    ranks.put(nodes[j], j);
                } else {
                    ranks.put(nodes[j], Math.min(ranks.get(nodes[j]), j));
                }
            }
        });
        return ranks;
    }

}
