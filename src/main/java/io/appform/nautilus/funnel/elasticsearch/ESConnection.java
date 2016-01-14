package io.appform.nautilus.funnel.elasticsearch;

import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Slf4j
public class ESConnection implements Managed {

    private Node node;
    private ESConfiguration configuration;

    @VisibleForTesting
    public ESConnection(ESConfiguration configuration, Node node) {
        this.configuration = configuration;
        this.node = node;
    }

    public ESConnection(ESConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void start() throws Exception {
        if(configuration.isEmbedded()) {
            node = nodeBuilder()
                    .build();
            log.info("Elasticsearch started in embedded mode...");
        }
        else {
            node = nodeBuilder()
                    .client(true)
                    .clusterName(configuration.getCluster())
                    .data(false)
                    .build();
            log.info("Elasticsearch connected to in cluster mode...");
        }
        node.start();
    }

    @Override
    public void stop() throws Exception {
        if(null != node) {
            node.close();
            log.info("Elasticsearch shut down");
        }
    }

    public Client client() {
        return node.client();
    }
}
