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
import io.appform.nautilus.funnel.elasticsearch.ESConfiguration;
import io.appform.nautilus.funnel.elasticsearch.ESConnection;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.model.session.StateTransition;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Elasticsearch utilities
 */
@Slf4j
public class ESUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("dd-M-yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd-M-yyyy");
    private static final String TABLENAME_PREFIX = "nautilus-funnels";
    private static final Object TABLENAME_POSTFIX = "tenant";
    private static final String META_INDEX = "nautilus-meta";

    public static void createMapping(ESConfiguration esConfiguration, ESConnection connection) throws Exception {
        PutIndexTemplateResponse response = connection.client()
                .admin()
                .indices()
                .preparePutTemplate("core")
                .setTemplate(TABLENAME_PREFIX + "*")
                .setOrder(0)
                .addMapping(TypeUtils.typeName(Session.class), mapping(Session.class))
                .addMapping(TypeUtils.typeName(StateTransition.class), mapping(StateTransition.class, Session.class))
                .setCreate(false)
                .execute()
                .actionGet();
        log.info("Create mapping: {}", response.isAcknowledged());
    }

    public static String getMetaIndexName() {
        return ESUtils.META_INDEX;
    }

    public static String getCurrentIndex(final String tenant, long timestamp) {
        //TODO::THROW IF TIMESTAMP IS BEYOND TABLE META.TTL
        String datePostfix = FORMATTER.print(timestamp);
        return String.format("%s-%s-%s-%s", ESUtils.TABLENAME_PREFIX, tenant,
                ESUtils.TABLENAME_POSTFIX, datePostfix);
    }

    public static String getAllIndices(final String table) {
        return String.format("%s-%s-%s-*",
                ESUtils.TABLENAME_PREFIX, table, ESUtils.TABLENAME_POSTFIX);
    }


    public static String sanitizeFieldForAggregation(String field){
        return field.replaceAll(Constants.FIELD_REPLACEMENT_REGEX, Constants.FIELD_REPLACEMENT_VALUE);
    }

    /*public static<T extends AnalyticsOperation<T>> QueryBuilder query(AnalyticsOperation<T> operation) throws Exception {
        return new ESFilterGenerator().build(operation.getFilters(), operation.getWindow());
    }

    public static QueryBuilder query(GraphRequest graphRequest) throws Exception {
        List<Filter> filters = null;
        if(null != graphRequest.getFilters()) {
            filters = graphRequest.getFilters();
        }
        else {
            filters = Collections.emptyList();
        }
        return new ESFilterGenerator().build(filters, graphRequest.getTimeWindow());
    }

    public static FilterBuilder filters(FilteredRequest graphRequest) throws Exception {
        List<Filter> filters = null;
        if(null != graphRequest.getFilters()) {
            filters = graphRequest.getFilters();
        }
        else {
            filters = Collections.emptyList();
        }
        return new ESFilterGenerator().filters(filters, graphRequest.getTimeWindow());
    }*/

    private static XContentBuilder mapping(final Class<?> clazz) throws IOException {
        return mapping(TypeUtils.typeName(clazz));
    }

    private static XContentBuilder mapping(final String type) throws IOException {
        return mapping(type, null);
    }

    private static XContentBuilder mapping(final Class<?> type, final Class<?> parent) throws IOException {
        return mapping(TypeUtils.typeName(type), TypeUtils.typeName(parent));
    }

    private static XContentBuilder mapping(final String type, final String parent) throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field(type)
                    .startObject()
                        /*.field("_source")
                            .startObject()
                                .field("enabled", true)
                            .endObject()*/
                        .field("_all")
                            .startObject()
                                .field("enabled", false)
                            .endObject()
                        /*.field("_timestamp")
                            .startObject()
                                .field("enabled", true)
                                .field("store", false)
                                .field("index", "not_analyzed")
                                .field("doc_values", true)
                            .endObject()*/;
                        //.array("dynamic_date_formats", "epoch_millis");
        if(!Strings.isNullOrEmpty(parent)) {
            builder
                .field("_parent")
                .startObject()
                    .field("type", parent)
                .endObject();
        }
        builder
                .field("dynamic_templates")
                    .startArray()
                        .startObject()
                            .field("template_timestamp")
                            .startObject()
                                .field("match", "timestamp")
                                .field("mapping")
                                .startObject()
                                    .field("store", false)
                                    .field("index", "not_analyzed")
                                    .field("type", "date")
                                    .field("doc_values", true)
                                    .field("format", "epoch_millis")
                                .endObject()
                            .endObject()
                        .endObject()
                        .startObject()
                            .field("template_location")
                                .startObject()
                                    .field("match", "location")
                                    .field("mapping")
                                    .startObject()
                                        .field("store", false)
                                        .field("index", "not_analyzed")
                                        .field("type", "geo_point")
                                        .field("lat_lon", true)
                                        .field("doc_values", true)
                                    .endObject()
                                .endObject()
                        .endObject()
                        .startObject()
                            .field("template_no_store")
                            .startObject()
                                .field("match_mapping_type", "*")
                                .field("mapping")
                                    .startObject()
                                        .field("store", false)
                                        .field("index", "not_analyzed")
                                        .field("doc_values", true)
                                    .endObject()
                            .endObject()
                        .endObject()
                    .endArray()
            .endObject()
        .endObject();
        return builder;
    }


}
