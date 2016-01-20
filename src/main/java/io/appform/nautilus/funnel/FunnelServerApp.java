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

package io.appform.nautilus.funnel;

import io.appform.nautilus.funnel.elasticsearch.ESConnection;
import io.appform.nautilus.funnel.graphmanagement.ESEdgeBasedGraphBuilder;
import io.appform.nautilus.funnel.model.support.Context;
import io.appform.nautilus.funnel.persistence.impl.ESTemporalTypedEntityStore;
import io.appform.nautilus.funnel.resources.ActivityResource;
import io.appform.nautilus.funnel.resources.GraphResource;
import io.appform.nautilus.funnel.sessionmanagement.SessionActivityHandler;
import io.appform.nautilus.funnel.tasks.Initialize;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Main application.
 */
public class FunnelServerApp extends Application<FunnelServerConfiguration> {
    @Override
    public void run(FunnelServerConfiguration funnelServerConfiguration, Environment environment) throws Exception {
        ESConnection esConnection = new ESConnection(funnelServerConfiguration.getElasticsearch());
        environment.lifecycle().manage(esConnection);

        SessionActivityHandler sessionActivityHandler = new SessionActivityHandler(new ESTemporalTypedEntityStore(environment.getObjectMapper(), funnelServerConfiguration.getElasticsearch(), esConnection));

        environment.jersey().register(new ActivityResource(sessionActivityHandler));

        Context context = Context.builder()
                                .esConfiguration(funnelServerConfiguration.getElasticsearch())
                                .esConnection(esConnection)
                                .build();

        environment.jersey().register(new GraphResource(context, new ESEdgeBasedGraphBuilder()));

        environment.admin().addTask(new Initialize(funnelServerConfiguration.getElasticsearch(), esConnection));
        configureCors(environment);
    }

    private void configureCors(Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }
    public static void main(String[] args) throws Exception {
        FunnelServerApp funnelServerApp = new FunnelServerApp();
        funnelServerApp.run(args);
    }
}
