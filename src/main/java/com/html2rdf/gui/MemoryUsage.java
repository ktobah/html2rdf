package com.html2rdf.gui;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 17/06/13
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MemoryUsage extends JProgressBar implements ActionListener{

    private long totalMemory;
    private final Runtime runtime;
    private final long meg = 1000000;

    public MemoryUsage(){
        super(0, 0);
        setStringPainted(true);
        runtime = Runtime.getRuntime();
        showMemoryStatus();
    }

    private void showMemoryStatus(){

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                totalMemory = runtime.totalMemory() / meg;
                final long free = runtime.freeMemory() / meg;
                final long current = totalMemory - free;
                final String value = current + "M of " + totalMemory + "M";
                setString(value);
                setMaximum((int) totalMemory);
                setValue((int) current);
            }
        }).start();
    }

    public void actionPerformed(ActionEvent e){
        showMemoryStatus();
    }
}


