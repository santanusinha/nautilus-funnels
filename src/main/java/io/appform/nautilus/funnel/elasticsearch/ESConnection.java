package io.appform.nautilus.funnel.elasticsearch;

import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;

import java.net.InetAddress;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Slf4j
public class ESConnection implements Managed {

    private Node node;
    private Client client = null;
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
            node.start();
            log.info("Elasticsearch started in embedded mode...");
        }
        else {
            Settings settings = Settings.builder()
                    .put("cluster.name", configuration.getCluster()).build();

            TransportClient esClient = TransportClient.builder().settings(settings).build();
            for (String host : configuration.getHosts()) {
                esClient.addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
                log.info(String.format("Added Elasticsearch Node : %s", host));
            }
            client = esClient;
            log.info("Elasticsearch connected to in cluster mode...");
        }
    }

    @Override
    public void stop() throws Exception {
        if(null != node) {
            node.close();
            log.info("Elasticsearch shut down");
        }
    }

    public Client client() {
        if(null != client) {
            return client;
        }
        return node.client();
    }
}
