package io.appform.nautilus.funnel.testutils;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by guruprasad.sridharan on 02/12/15.
 */
public class EmbeddedElasticsearchServer {

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticsearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticsearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        Settings.Builder elasticsearchSettings = Settings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.home", dataDirectory);

        node = NodeBuilder.nodeBuilder()
                .clusterName("nautilus")
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
        System.out.println("Starting local elasticsearch");
    }

    public Node getNode() {
        return node;
    }

    public Client getClient() {
        return (node.isClosed()) ? null : node.client();
    }

    public void shutdown() {
        node.client().admin().indices().delete(new DeleteIndexRequest(".*")).actionGet();
        node.close();
        deleteDataDirectory();
        System.out.println("Shutting down local elasticsearch cluster");
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}

