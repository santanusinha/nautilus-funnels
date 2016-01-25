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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.appform.nautilus.funnel.model.filter.impl.general.*;
import io.appform.nautilus.funnel.model.filter.impl.numeric.*;
import io.appform.nautilus.funnel.model.filter.impl.string.Contains;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Base filter.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "filter")
@JsonSubTypes({
        //Numeric
        @JsonSubTypes.Type(value = GreaterEqual.class, name = FilterType.greater_equal),
        @JsonSubTypes.Type(value = GreaterThan.class, name = FilterType.greater_than),
        @JsonSubTypes.Type(value = LessEqual.class, name = FilterType.less_equal),
        @JsonSubTypes.Type(value = LessThan.class, name = FilterType.less_than),
        @JsonSubTypes.Type(value = Between.class, name = FilterType.between),

        //General
        @JsonSubTypes.Type(value = Equals.class, name = FilterType.equals),
        @JsonSubTypes.Type(value = In.class, name = FilterType.in),
        @JsonSubTypes.Type(value = NotEquals.class, name = FilterType.not_equals),
        @JsonSubTypes.Type(value = Any.class, name = FilterType.any),
        @JsonSubTypes.Type(value = Exists.class, name = FilterType.exists),
        @JsonSubTypes.Type(value = Missing.class, name = FilterType.missing),

        //String
        @JsonSubTypes.Type(value = Contains.class, name = FilterType.contains),

})
@Data
@EqualsAndHashCode
@ToString
public abstract class Filter {
    @JsonIgnore
    private final String filter;

    @NotNull
    private String attribute;

    protected Filter(String filter) {
        this.filter = filter;
    }

    protected Filter(String filter, String attribute) {
        this.filter = filter;
        this.attribute = attribute;
    }

    public abstract void accept(FilterVisitor visitor) throws Exception;

}
