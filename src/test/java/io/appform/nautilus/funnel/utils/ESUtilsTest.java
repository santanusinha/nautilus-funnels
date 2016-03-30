package io.appform.nautilus.funnel.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import io.appform.nautilus.funnel.elasticsearch.ESConfiguration;
import io.appform.nautilus.funnel.elasticsearch.ESConnection;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.model.session.StateTransition;
import io.appform.nautilus.funnel.testutils.EmbeddedElasticsearchServer;
import junit.framework.Assert;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by guruprasad.sridharan on 15/02/16.
 */
public class ESUtilsTest {
    private EmbeddedElasticsearchServer localEs;
    private ESConnection esConnection;
    private ESConfiguration esConfiguration;

    @Before
    public void setup() {
        localEs = new EmbeddedElasticsearchServer();
        esConfiguration = new ESConfiguration();
        esConfiguration.setCluster("nautilus");
        esConfiguration.setEmbedded(true);
        esConfiguration.setDefaultShards(1);
        esConfiguration.setDefaultReplicas(0);
        esConfiguration.setHosts(new Vector<>(Arrays.asList("localhost")));

        esConnection = new ESConnection(esConfiguration, localEs.getNode());
    }

    @Test
    public void testTerms() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ESUtils.createMapping(esConfiguration, esConnection);
        esConnection.client()
                .prepareIndex()
                .setIndex("nautilus-funnels-kahuna-tenant-2016-03-16")
                .setType(TypeUtils.typeName(Session.class))
                .setId("123")
                .setRefresh(true)
                .setSource(mapper.writeValueAsBytes(Session.builder()
                        .id("123")
                        .attributes(Collections.singletonMap("valid", "yes"))
                        .path("A->B->C")
                        .tenant("kahuna")
                        .timestamp(System.currentTimeMillis())
                        .build()))
                .execute()
                .actionGet();
        esConnection.client()
                .prepareIndex()
                .setIndex("nautilus-funnels-kahuna-tenant-2016-03-16")
                .setType(TypeUtils.typeName(StateTransition.class))
                .setId("1234")
                .setRefresh(true)
                .setParent("123")
                .setSource(mapper.writeValueAsBytes(StateTransition.builder()
                        .id("1234")
                        .attributes(Collections.singletonMap("valid", "yes"))
                        .sequence(1)
                        .from("A")
                        .to("B->C")
                        .normalizedPath("A->B->C")
                        .sessionId("123")
                        .timestamp(System.currentTimeMillis())
                        .build()))
                .execute()
                .actionGet();
        esConnection.client()
                .prepareIndex()
                .setIndex("nautilus-funnels-kahuna-tenant-2016-03-16")
                .setType(TypeUtils.typeName(StateTransition.class))
                .setId("1235")
                .setRefresh(true)
                .setParent("123")
                .setSource(mapper.writeValueAsBytes(StateTransition.builder()
                        .id("1235")
                        .attributes(Collections.singletonMap("valid", "yes"))
                        .sequence(1)
                        .from("B")
                        .to("D->E")
                        .normalizedPath("B->D->E")
                        .sessionId("123")
                        .timestamp(System.currentTimeMillis())
                        .build()))
                .execute()
                .actionGet();


        Assert.assertEquals("A,B", Joiner.on(",").join(ESUtils.terms("kahuna", StateTransition.class, "from", esConnection)));
    }

    @After
    public void tearDown() {
        localEs.shutdown();
    }
}
