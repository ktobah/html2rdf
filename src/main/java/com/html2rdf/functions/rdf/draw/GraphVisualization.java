package com.html2rdf.functions.rdf.draw;

import com.hp.hpl.jena.rdf.model.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.ViewerListener;
import org.graphstream.ui.swingViewer.ViewerPipe;
import org.graphstream.ui.swingViewer.util.Camera;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 04/06/16
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */

public class GraphVisualization extends SwingWorker<Object, Void> implements ViewerListener{

    protected String style = "graph { fill-color: #FFF, #BBB; fill-mode: gradient-radial; padding: 30px; icon: url('file:///C:/Users/AHMED/IdeaProjects/MasterThesisProject/src/icons/graph24.png'); } edge { shape: cubic-curve; size: 1px; fill-color: black; arrow-shape: arrow; text-style: bold-italic; } node.literal { size-mode: fit; shape: box; fill-color: white; stroke-mode: plain; padding: 4px; text-color: black; text-style: bold; shadow-mode: plain; shadow-width: 2px; shadow-color: blue; shadow-offset: 0px;} node.uri { size-mode: fit; shape: circle; padding: 20px; size: 40px, 20px; fill-color: #2BF; text-style: bold-italic; shadow-mode: plain; shadow-width: 2px; shadow-color: #9C02A7; shadow-offset: 0px;} node.bNode { shape: circle; padding: 20px; size: 40px, 20px; text-mode:hidden; fill-color: white; shadow-mode: plain; shadow-width: 2px; shadow-color: #FF7A00; shadow-offset: 0px;} ";
    private Model model;
    private static Graph graph;
    private static ViewerPipe fromViewer;
    private static boolean working = true;
    private static View view;

    public GraphVisualization(Model model) {
        this.model = model;
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new DefaultGraph("Visualize Graph") ;
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.title", "Graph Visualization");
        graph.addAttribute("ui.icon", "url('"+getClass().getResource("/icons/graph24.png")+"')");
        graph.addAttribute("ui.stylesheet", style);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        Viewer viewer = graph.display();

        viewer.enableAutoLayout();
        view = viewer.getDefaultView();

        view.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                Camera camera = view.getCamera();

                if (notches < 0) {
                    camera.setViewPercent(camera.getViewPercent() - 0.05f);
                } else {
                    camera.setViewPercent(camera.getViewPercent() + 0.05f);
                }
            }
        });
        view.resizeFrame(1200, 700);
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);
    }

    @Override
    protected Object doInBackground() throws Exception {

        StmtIterator iterator = model.listStatements();
        Statement statement;
        Resource subject;
        Property predicate;
        RDFNode object;
        int increment = 0;

        Node subjectNode, objectNode;

        while(iterator.hasNext() & working){
            fromViewer.pump();
            statement = iterator.nextStatement();
            subject = statement.getSubject();
            if(subject.isAnon()){
                subjectNode = graph.addNode(subject.toString());
                subjectNode.addAttribute("ui.class", "bNode");
            }else{
                subjectNode = graph.addNode(subject.toString());
                subjectNode.addAttribute("ui.class", "uri");
            }

            predicate = statement.getPredicate();

            object = statement.getObject();
            if(object.isAnon()){
                objectNode = graph.addNode(object.toString());
                objectNode.addAttribute("ui.class", "bNode");
            }else if(object.isLiteral()){
                objectNode = graph.addNode(object.toString());
                objectNode.addAttribute("ui.class", "literal");
            }else{
                objectNode = graph.addNode(object.toString());
                objectNode.addAttribute("ui.class", "uri");
            }

            graph.addEdge(increment + predicate.toString(), subjectNode.getId(), objectNode.getId(), true);
            increment++;
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        for(Edge edge: graph.getEachEdge()) {
            edge.addAttribute("ui.label", edge.getId().substring(edge.getId().indexOf('h'), edge.getId().length()));
        }
        return null;
    }

    @Override
    public void viewClosed(String s) {
        working = false;
    }

    @Override
    public void buttonPushed(String s) {
        System.out.println("Button pushed on node "+s);
    }

    @Override
    public void buttonReleased(String s) {
        System.out.println("Button released on node "+s);
    }
}