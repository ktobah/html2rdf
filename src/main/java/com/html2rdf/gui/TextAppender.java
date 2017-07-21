package com.html2rdf.gui;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 28/04/13
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
public class TextAppender extends WriterAppender {

    private static RSyntaxTextArea textArea;
    private static PatternLayout patternLayout;

    public TextAppender(PatternLayout patternLayout, RSyntaxTextArea textArea){
        this.textArea = textArea;
        this.patternLayout = patternLayout;
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void append(final LoggingEvent loggingEvent) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.append(patternLayout.format(loggingEvent));
            }
        });
    }
}
