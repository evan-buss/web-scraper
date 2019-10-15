package com.evanbuss.webscraper.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ParsedPagesModel {
    private final ConcurrentMap<String, ResultModel> data = new ConcurrentHashMap<>();
    private long counter;
    private Graph graph;
    private Node prevNode = null;

    private static ParsedPagesModel parsedPagesModel = new ParsedPagesModel();

    public static ParsedPagesModel getInstance() {
        return parsedPagesModel;
    }

    private ParsedPagesModel() {
        graph = new MultiGraph("pages");
    }

    public void addItem(String url, ResultModel model) {
        ResultModel present = data.putIfAbsent(url, model);
        if (present == null) {

            Node newNode = graph.addNode(url);
            newNode.setAttribute("ui.label", url);
            counter++;
            if (prevNode != null) {
                graph.addEdge(prevNode.getId() + "-" + newNode.getId(), newNode, prevNode);
            }
            prevNode = newNode;
        }
    }


    public long getSize() {
        return counter;
    }

    public void clear() {
        data.clear();
        counter = 0;
    }

    public void saveToFile(File selectedFile) {
        Thread writerThread =
                new Thread(
                        () -> {
                            try (PrintWriter writer = new PrintWriter(selectedFile)) {
                                Gson gson =
                                        new GsonBuilder()
                                                .setPrettyPrinting()
                                                .disableHtmlEscaping()
                                                .registerTypeAdapter(ResultModel.class, new ResultModelDataAdapter())
                                                .create();
                                writer.write(gson.toJson(new ArrayList<>(data.values())));
                            } catch (IOException e) {
                                System.out.println("Could not open Print Writer");
                            }
                        });

        writerThread.start();
    }

    public boolean contains(String url) {
        return data.containsKey(url);
    }

    public Graph getGraph() {
        return graph;
    }
}
