package com.evanbuss.webscraper.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class ParsedPagesModel {
    private final ConcurrentMap<String, ResultModel> data = new ConcurrentHashMap<>();
    private long counter;
    private Graph graph;
    private int prevDepth = 1;
    private String randomColor = generateNewColor();

    private static ParsedPagesModel parsedPagesModel = new ParsedPagesModel();
    private static Logger log = LoggerFactory.getLogger(ParsedPagesModel.class);

    public static ParsedPagesModel getInstance() {
        return parsedPagesModel;
    }

    private ParsedPagesModel() {
        graph = new MultiGraph("pages");
    }

    public synchronized void addItem(String url, String prevURL, ResultModel model, int depth) {
        if (data.putIfAbsent(url, model) == null) {
            Node newNode = graph.addNode(url);
//            newNode.setAttribute("ui.label", depth);

            if (depth != prevDepth) {
                randomColor = generateNewColor();
                prevDepth = depth;
            }

            if (prevURL != null) { // If it is not the first node, create an edge
                graph.addEdge(prevURL + "-" + newNode.getId(), prevURL, newNode.getId(), true);
                newNode.setAttribute("ui.style", "shape: circle;fill-color: " +
                        randomColor + ";size: 10px; text-alignment: center;");
            } else {
                newNode.setAttribute("ui.style", "shape: triangle;fill-color: " +
                        randomColor + ";size: 20px; text-alignment: center;");
            }

            if (depth != prevDepth) {
                randomColor = generateNewColor();
                prevDepth = depth;
            }
            counter++;
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
                                log.error("Could not write results to file");
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

    private String generateNewColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return "rgb(" + random.nextInt(256) +
                "," + random.nextInt(256) + "," +
                random.nextInt(256) + ")";
    }
}
