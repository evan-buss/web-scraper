package com.evanbuss.webscraper.ui.controllers;

import com.evanbuss.webscraper.models.ParsedPagesModel;
import com.evanbuss.webscraper.utils.WebUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;

import java.util.EnumSet;

public class GraphController {

    @FXML
    private AnchorPane graphPane;
    @FXML
    private Label nodeLabel;
    private FxViewPanel viewPanel;
    // static so that close() is invoked from Application class. otherwise the app never closes
    public static FxViewer viewer;

    public GraphController() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        // Initialize graph
        Graph graph = ParsedPagesModel.getInstance().getGraph();
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Initialize graph view
        viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        viewPanel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());
        AnchorPane.setBottomAnchor(viewPanel, 0.0);
        AnchorPane.setTopAnchor(viewPanel, 0.0);
        AnchorPane.setRightAnchor(viewPanel, 0.0);
        AnchorPane.setLeftAnchor(viewPanel, 0.0);
    }

    @FXML
    public void initialize() {
        graphPane.getChildren().add(viewPanel);
        // Control camera zoom with mouse scroll wheel
        graphPane.setOnScroll(event -> {
            // Adjust the zoom factor as per your requirement
            double zoomFactor;
            if (event.getDeltaY() < 0) {
                zoomFactor = 0.90f;
            } else {
                zoomFactor = 1.05f;
            }
            viewPanel.getCamera().setViewPercent(viewPanel.getCamera().getViewPercent() * zoomFactor);

            Camera cam = viewPanel.getCamera();
            double zoom = cam.getViewPercent() * zoomFactor;
            Point2 pxCenter = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
            Point3 guClicked = cam.transformPxToGu(event.getX(), event.getY());
            double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu / zoomFactor;
            double x = guClicked.x + (pxCenter.x - event.getX()) / newRatioPx2Gu;
            double y = guClicked.y - (pxCenter.y - event.getY()) / newRatioPx2Gu;
            cam.setViewCenter(x, y, 0);
            cam.setViewPercent(zoom);
        });

        graphPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Element e = viewPanel.findGraphicElementAt(
                        EnumSet.of(InteractiveElement.NODE),
                        event.getX(),
                        event.getY());
                if (e != null) {
                    nodeLabel.setText(e.getId());
                }
            }
        });
    }

    public void openInBrowser() {
        WebUtils.openInBrowser(nodeLabel.getText());
    }
}
