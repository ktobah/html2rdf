package com.html2rdf.functions.tree;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 20/05/16
 * Time: 19:02
 * To change this template use File | Settings | File Templates.
 */
public class ShowSelection implements Runnable{

    JTabbedPane tabbedPane;
    int index;

    public ShowSelection(JTabbedPane tabbedPane, int index){
        this.tabbedPane = tabbedPane;
        this.index = index;
    }

    @Override
    public void run() {
        tabbedPane.setSelectedIndex(index);
    }
}
