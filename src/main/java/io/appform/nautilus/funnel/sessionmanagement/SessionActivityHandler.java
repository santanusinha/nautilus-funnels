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

package io.appform.nautilus.funnel.sessionmanagement;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import io.appform.nautilus.funnel.common.ErrorMessageTable;
import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.model.session.SessionActivity;
import io.appform.nautilus.funnel.model.session.SessionActivitySet;
import io.appform.nautilus.funnel.model.session.StateTransition;
import io.appform.nautilus.funnel.persistence.impl.ESTemporalTypedEntityStore;
import io.appform.nautilus.funnel.utils.Constants;
import io.appform.nautilus.funnel.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by santanu.s on 14/01/16.
 */
@Slf4j
public class SessionActivityHandler {
    private static final int RETRY_ATTEMPTS = 3;

    private final ESTemporalTypedEntityStore store;
    private Retryer<Boolean> retryer;

    public SessionActivityHandler(ESTemporalTypedEntityStore store) {
        this.store = store;
        retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Predicates.equalTo(false))
                .withStopStrategy(StopStrategies.stopAfterAttempt(RETRY_ATTEMPTS))
                .build();
    }

    public void handle(final String tenant, final SessionActivitySet activitySet) throws NautilusException {

        try {
            retryer.call(() -> {
                Optional<Session> sessionOptional = store.get(tenant, activitySet.getSessionId(), activitySet.getSessionStartTime(), Session.class);
                Session session = null;
                ;
                ;
                //normalizedPathElements;
                ///;
                ;
                List<StateTransition> transitions = Lists.newArrayList();
                String newPath = null;
                List<SessionActivity> activities = activitySet.getActivities();
                if(sessionOptional.isPresent()) {
                    session = sessionOptional.get();
                }
                else {
                    session = Session.builder()
                                    .id(activitySet.getSessionId())
                                    .timestamp(activitySet.getSessionStartTime())
                                    .tenant(tenant)
                                    .path(null)
                                    .normalizedPath("")
                                    .attributes(activitySet.getAttributes())
                                    .build();
                }
                final String oldPath = session.getPath();
                List<String> pathElements = Strings.isNullOrEmpty(oldPath)
                                            ? Lists.newArrayList()
                                            : Lists.newArrayList(oldPath.split(Constants.PATH_STATE_SEPARATOR));
                String lastState = (pathElements.isEmpty())
                                            ? null
                                            : pathElements.get(pathElements.size() - 1);
                String newNormalizedPath = null;
                for(SessionActivity activity : activities) {
                    pathElements.add(activity.getState());
                    List<String> normalizedPathElements = PathUtils.normalise(pathElements);
                    log.debug("Normalized path: {}:{}", session.getId(), normalizedPathElements);
                    newPath = Joiner.on(Constants.PATH_STATE_SEPARATOR).join(pathElements);
                    newNormalizedPath = Joiner.on(Constants.PATH_STATE_SEPARATOR).join(normalizedPathElements);
                    StateTransition transition = StateTransition.builder()
                            .from(lastState)
                            .to(activity.getState())
                            .normalizedPath(newNormalizedPath)
                            .sequence(pathElements.size())
                            .timestamp(activity.getTimestamp())
                            .sessionId(activitySet.getSessionId())
                            .build();
                    transitions.add(transition);
                    lastState = activity.getState();
                }
                session.setPath(newPath);
                session.setNormalizedPath(newNormalizedPath);
                boolean persisted = store.store(tenant, session);
                if(persisted) {
                    store.store(tenant, transitions, activitySet.getSessionId(), true);
                }
                return persisted;
            });
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof NautilusException) {
                throw (NautilusException)cause;
            }
            log.error(ErrorMessageTable.errorMessage(ErrorMessageTable.ErrorCode.ENTITY_SAVE_UNHANDLED_ERROR, activitySet.getSessionId()));
            throw new NautilusException(e, ErrorMessageTable.ErrorCode.ENTITY_SAVE_UNHANDLED_ERROR, activitySet.getSessionId());
        } catch (RetryException e) {
            log.error(ErrorMessageTable.errorMessage(ErrorMessageTable.ErrorCode.ENTITY_SAVE_VERSION_ERROR, activitySet.getSessionId()));
            throw new NautilusException(e, ErrorMessageTable.ErrorCode.ENTITY_SAVE_VERSION_ERROR, activitySet.getSessionId());
        }
    }
}
