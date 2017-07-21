package com.html2rdf.functions.rdf.save;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.html2rdf.gui.Base;
import com.html2rdf.gui.Interface;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 02/06/16
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */

public class SaveRDFXML implements Runnable{

    private Model model;
    private String base;
    private static JFileChooser choose;
    private static String fileName;

    public SaveRDFXML(Model model, String base){
        this.model = model;
        this.base = base;
    }

    @Override
    public void run() {
        EventQueue.invokeLater(new Runnable() {
        public void run() {
            choose = new JFileChooser(){
                    @Override
                    public void approveSelection(){
                        File f = getSelectedFile();
                        if(f.exists()){
                            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                            switch(result){
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION:
                                    return;
                                case JOptionPane.CLOSED_OPTION:
                                    return;
                                case JOptionPane.CANCEL_OPTION:
                                    cancelSelection();
                                    return;
                            }
                        }
                        super.approveSelection();
                    }
                };

                choose.setAcceptAllFileFilterUsed(false);
                choose.setLocale(Locale.ENGLISH);
                choose.setApproveButtonText("Save");
                choose.setDialogTitle("Saving RDF triples");
                choose.setDialogType(JFileChooser.SAVE_DIALOG);
                choose.setApproveButtonToolTipText("Save the extracted RDF triples as RDF/XML");
                choose.addChoosableFileFilter(new FilterRDFXML());
                int decision = choose.showDialog(Interface.getJXFrame(), "Save");
                if (decision == choose.APPROVE_OPTION) {
                    FileOutputStream file;
                    RDFWriter writer = model.getWriter("RDF/XML");
                    try {
                        fileName = choose.getSelectedFile().getCanonicalPath();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(new JFrame(), "There was an error while trying to retrieve the path.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        if(fileName.contains(".rdf")) file = new FileOutputStream(fileName);
                        else file = new FileOutputStream(fileName+".rdf");
                        model.setNsPrefix(Base.getBasePrefix(), base);
                        writer.write(model, file, base); //"N3-TRIPLE"
                        JOptionPane.showMessageDialog(new JFrame(), "\"" + fileName.substring(fileName.lastIndexOf('\\') + 1) + "\" was successfully saved.", "Saving...", JOptionPane.INFORMATION_MESSAGE);
                        file.close();
                        Interface.setSaved(true);
                        Interface.setSaveRDF(false);
                    } catch (FileNotFoundException e) {
                        //JOptionPane.showMessageDialog(new JFrame(), "The file " + fileName.substring(fileName.lastIndexOf('\\') + 1) + " doesn't exist.", "File not found", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(new JFrame(), "The following error occurred while saving : \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}


class FilterRDFXML extends FileFilter {
    public final static String rdf = "rdf";
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(rdf)) {
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
        return "RDF/XML file";
    }
}
