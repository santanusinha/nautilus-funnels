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

package io.appform.nautilus.funnel.resources;

import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.model.session.SessionActivitySet;
import io.appform.nautilus.funnel.sessionmanagement.SessionActivityHandler;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

/**
 * Created by santanu.s on 14/01/16.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/activities")
@Slf4j
public class ActivityResource {
    private final SessionActivityHandler activityHandler;

    public ActivityResource(SessionActivityHandler activityHandler) {
        this.activityHandler = activityHandler;
    }

    @Path("/{tenant}")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ApiResponse save(@PathParam("tenant") final String tenant,
                            @Valid SessionActivitySet sessionActivitySet) {
        try {
            activityHandler.handle(tenant, sessionActivitySet);
        } catch (NautilusException e) {
            log.error("Error ingesting activity for: {}", tenant, e);
            return ApiResponse
                    .builder()
                    .error(true)
                    .data(Collections.singletonMap("message", "Could not ingest event"))
                    .build();
        }
        return ApiResponse
                .builder()
                .error(false)
                .data(Collections.singletonMap(sessionActivitySet.getActivities(), true))
                .build();
    }

    /*@Path("/{tenant}/{session}/bulk")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ApiResponse save(@PathParam("tenant") final String tenant, @PathParam("session") final String session,
                            @Valid List<SessionActivity> activities) {
        try {
            activityHandler.handle(tenant, session, activities);
        } catch (NautilusException e) {
            log.error("Error ingesting activity for: {}", tenant, e);
            return ApiResponse
                    .builder()
                    .error(true)
                    .data(Collections.singletonMap("message", "Could not ingest event"))
                    .build();
        }
        return ApiResponse
                .builder()
                .error(false)
                .data(Collections.singletonMap(session, true))
                .build();
    }*/
}
