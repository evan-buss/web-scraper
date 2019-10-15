package com.evanbuss.webscraper.ui.controllers;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import java.util.Random;

public class GraphController {

    public SwingNode swingNode;

    @FXML
    public void initialize() {
        System.setProperty("org.graphstream.ui", "javafx");
        Graph graph = new MultiGraph("embedded");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        Node a = graph.addNode("A");
        a.addAttribute("ui.label", "https://evanbuss.com");
        a.addAttribute("ui.style", "size: 100px;");
        graph.addNode("B");
        graph.addEdge("edge1", "A", "B");
        graph.addNode("C");
        graph.addEdge("edge2", "A", "C");


//        TODO: Close viewer on application close...
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        ViewPanel viewPanel = viewer.addDefaultView(false);
//        ViewerPipe pipe = new ViewerPipe();

        viewer.enableAutoLayout();
        swingNode.setContent(viewPanel);
        swingNode.setOnScroll(event -> {
            // Adjust the zoom factor as per your requirement
            double zoomFactor;
            if (event.getDeltaY() < 0) {
                System.out.println("< 0");
                zoomFactor = 0.93;
            } else {
                zoomFactor = 1.05;
            }
            viewPanel.getCamera().setViewPercent(viewPanel.getCamera().getViewPercent() * zoomFactor);
        });

        swingNode.setOnMouseClicked(event -> {
            Element e = viewPanel.findNodeOrSpriteAt(event.getSceneX(), event.getY());
            if (e != null) {
                Random r = new Random();
                e.setAttribute("ui.style", "fill-color: rgb(" + r.nextInt(256) + "," + r.nextInt(256) + "," + r.nextInt(256) + ");");
            }
        });
    }

}
