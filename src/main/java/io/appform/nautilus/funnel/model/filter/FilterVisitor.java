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

package io.appform.nautilus.funnel.model.filter;


import io.appform.nautilus.funnel.model.filter.impl.general.*;
import io.appform.nautilus.funnel.model.filter.impl.numeric.*;
import io.appform.nautilus.funnel.model.filter.impl.string.Contains;

/**
 * User: Santanu Sinha (santanu.sinha@flipkart.com)
 * Date: 14/03/14
 * Time: 2:20 PM
 */
public interface FilterVisitor {

    void visit(Between between) throws Exception;

    void visit(Equals equals) throws Exception;

    void visit(NotEquals notEquals) throws Exception;

    void visit(Contains stringContainsElement) throws Exception;

    void visit(GreaterThan greaterThan) throws Exception;

    void visit(GreaterEqual greaterEqual) throws Exception;

    void visit(LessThan lessThan) throws Exception;

    void visit(LessEqual lessEqual) throws Exception;

    void visit(Any any) throws Exception;

    void visit(In in) throws Exception;

    void visit(Exists exists) throws Exception;

    void visit(Missing missing) throws Exception;
}
