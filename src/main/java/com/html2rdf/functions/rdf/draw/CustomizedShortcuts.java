package com.html2rdf.functions.rdf.draw;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 18/06/16
 * Time: 01:59
 * To change this template use File | Settings | File Templates.
 */

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.ShortcutManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CustomizedShortcuts implements ShortcutManager {
    // Attributes

    /**
     * The viewer to control.
     */
    protected View view;

    protected double viewPercent = 1;

    protected Point3 viewPos = new Point3();

    protected double rotation = 0;

    // Construction

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        view.addKeyListener(this);
    }

    public void release() {
        view.removeKeyListener(this);
    }

    // Events

    /**
     * A key has been pressed.
     *
     * @param event
     *            The event that generated the key.
     */
    public void keyPressed(KeyEvent event) {
        Camera camera = view.getCamera();

        if (event.getKeyCode() == MouseEvent.BUTTON3 ) {
            camera.setViewPercent(camera.getViewPercent() - 0.05f);
        } else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            camera.setViewPercent(camera.getViewPercent() + 0.05f);
        } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            if ((event.getModifiers() & KeyEvent.ALT_MASK) != 0) {
                double r = camera.getViewRotation();
                camera.setViewRotation(r - 5);
            } else {
                double delta = 0;

                if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
                    delta = camera.getGraphDimension() * 0.1f;
                else
                    delta = camera.getGraphDimension() * 0.01f;

                Point3 p = camera.getViewCenter();
                camera.setViewCenter(p.x - delta, p.y, 0);
            }
        } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            if ((event.getModifiers() & KeyEvent.ALT_MASK) != 0) {
                double r = camera.getViewRotation();
                camera.setViewRotation(r + 5);
            } else {
                double delta = 0;

                if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
                    delta = camera.getGraphDimension() * 0.1f;
                else
                    delta = camera.getGraphDimension() * 0.01f;

                Point3 p = camera.getViewCenter();
                camera.setViewCenter(p.x + delta, p.y, 0);
            }
        } else if (event.getKeyCode() == KeyEvent.VK_UP) {
            double delta = 0;

            if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
                delta = camera.getGraphDimension() * 0.1f;
            else
                delta = camera.getGraphDimension() * 0.01f;

            Point3 p = camera.getViewCenter();
            camera.setViewCenter(p.x, p.y + delta, 0);
        } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            double delta = 0;

            if ((event.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
                delta = camera.getGraphDimension() * 0.1f;
            else
                delta = camera.getGraphDimension() * 0.01f;

            Point3 p = camera.getViewCenter();
            camera.setViewCenter(p.x, p.y - delta, 0);
        }
    }

    /**
     * A key has been pressed.
     *
     * @param event
     *            The event that generated the key.
     */
    public void keyReleased(KeyEvent event) {
    }

    /**
     * A key has been typed.
     *
     * @param event
     *            The event that generated the key.
     */
    public void keyTyped(KeyEvent event) {
        if (event.getKeyChar() == 'R') {
            view.getCamera().resetView();
        }
        // else if( event.getKeyChar() == 'B' )
        // {
        // view.setModeFPS( ! view.getModeFPS() );
        // }
    }
}