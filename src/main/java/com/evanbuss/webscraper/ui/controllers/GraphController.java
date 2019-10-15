package com.evanbuss.webscraper.ui.controllers;

import com.evanbuss.webscraper.models.ParsedPagesModel;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.util.InteractiveElement;

import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GraphController {

    //    public Label nodeLabel;
    public AnchorPane graphPane;
    private Graph graph;
    private FxViewPanel viewPanel;
    private ThreadLocalRandom random = ThreadLocalRandom.current();
    private MainController mainController;

    public GraphController() {
        System.out.println("graph controller consturctor");
        // Initialize graph
        graph = ParsedPagesModel.getInstance().getGraph();
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Initialize graph view
        FxViewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        viewPanel = (FxViewPanel) viewer.addDefaultView(false);
        AnchorPane.setBottomAnchor(viewPanel, 0.0);
        AnchorPane.setTopAnchor(viewPanel, 0.0);
        AnchorPane.setRightAnchor(viewPanel, 0.0);
        AnchorPane.setLeftAnchor(viewPanel, 0.0);
    }

    @FXML
    public void initialize() {
        System.out.println("graph controller init");
        graphPane.getChildren().add(viewPanel);
//        Control camera zoom with mouse scroll wheel
        graphPane.setOnScroll(event -> {
            // Adjust the zoom factor as per your requirement
            double zoomFactor;
            if (event.getDeltaY() < 0) {
                zoomFactor = 0.90f;
            } else {
                zoomFactor = 1.05f;
            }
            viewPanel.getCamera().setViewPercent(viewPanel.getCamera().getViewPercent() * zoomFactor);
        });

        graphPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Element e = viewPanel.findGraphicElementAt(
                        EnumSet.of(InteractiveElement.NODE),
                        event.getX(),
                        event.getY());

                if (e != null) {
                    System.out.println(e.getId());
                    Random r = new Random();
                    e.setAttribute("ui.style", "fill-color: rgb(" + r.nextInt(256) + "," + r.nextInt(256) + "," + r.nextInt(256) + ");");
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                Node n = graph.addNode(String.valueOf(random.nextInt(10000)));
            }
        });
    }

    void inject(MainController mainController) {
        this.mainController = mainController;
    }
}
