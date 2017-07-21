package com.html2rdf.functions.rdf.open;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 03/06/16
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.html2rdf.extraction.graph.DirectGraph;
import com.html2rdf.gui.Interface;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class OpenRDF extends Thread {

    private static Model model;
    private static JFileChooser choose;
    private static String fileName;
    private static boolean isOpen = false;

    public void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                choose = new JFileChooser();
                choose.setAcceptAllFileFilterUsed(false);
                choose.setLocale(Locale.ENGLISH);
                choose.setApproveButtonText("Save");
                choose.setDialogType(JFileChooser.SAVE_DIALOG);
                choose.setDialogTitle("Open an RDF document");
                choose.setApproveButtonToolTipText("Open an RDF document");
                choose.addChoosableFileFilter(new FilterLog());
                int decision = choose.showDialog(Interface.getJXFrame(), "Open");
                if (decision == choose.APPROVE_OPTION) {

                    try {
                        fileName = choose.getSelectedFile().getCanonicalPath();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(new JFrame(), "There was an error while trying to retrieve the path.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    model = FileManager.get().loadModel(choose.getSelectedFile().getAbsolutePath().replace("\\", "/").toLowerCase()) ;
                    if(!Interface.getExtractedTriplesContent().isEmpty()) Interface.emptyTriples();
                    if(fileName.contains(".rdf")) {
                        model.write(DirectGraph.getOutputStream(), "RDF/XML");
                        isOpen = true;
                    }else if(fileName.contains(".n3")) {
                        model.write(DirectGraph.getOutputStream(), "N3");
                        isOpen = true;
                    }else if(fileName.contains(".ttl")) {
                        model.write(DirectGraph.getOutputStream(), "TURTLE");
                        isOpen = true;
                    }else if(fileName.contains(".nt")) {
                        model.write(DirectGraph.getOutputStream(), "N-TRIPLE");
                        isOpen = true;
                    }
                }
            }
        });
    }

    public static boolean isOpen(){
        return isOpen;
    }

    public static Model getModel(){
        return model;
    }
}

class FilterLog extends javax.swing.filechooser.FileFilter {
    public final static String rdf = "rdf", rdfN3 = "n3", rdfNTriple = "nt", rdfTurtle = "ttl";
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(rdf) | extension.equals(rdfN3) | extension.equals(rdfNTriple) | extension.equals(rdfTurtle)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public String getDescription() {
        return "RDF file";
    }
}
