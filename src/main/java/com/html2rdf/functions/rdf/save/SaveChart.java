package com.html2rdf.functions.rdf.save;

import com.html2rdf.gui.Interface;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 17/06/16
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class SaveChart implements Runnable{

    private static JFileChooser choose;
    private static String fileName;
    private File file;
    private JFreeChart chart;

    public SaveChart(JFreeChart chart){
        this.chart = chart;
    }

    @Override
    public void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                choose = new JFileChooser() {
                    @Override
                    public void approveSelection() {
                        File f = getSelectedFile();
                        if (f.exists()) {
                            int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                            switch (result) {
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
                choose.setDialogType(JFileChooser.SAVE_DIALOG);
                choose.setDialogTitle("Save the chart");
                choose.setApproveButtonToolTipText("Save the chart");
                choose.addChoosableFileFilter(new FilterImage());
                int decision = choose.showDialog(Interface.getJXFrame(), "Save");
                if (decision == choose.APPROVE_OPTION) {
                    try {
                        fileName = choose.getSelectedFile().getCanonicalPath();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(new JFrame(), "There was an error while trying to retrieve the path.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        if (fileName.contains(".png")) file = new File(fileName);
                        else file = new File(fileName + ".png");

                        ChartUtilities.saveChartAsPNG(file, chart, 500, 300);

                        //outputStream = new FileOutputStream(file.getAbsoluteFile());

                        JOptionPane.showMessageDialog(new JFrame(), "\"" + fileName.substring(fileName.lastIndexOf('\\') + 1) + "\" was successfully saved.", "Saving...", JOptionPane.INFORMATION_MESSAGE);
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


class FilterImage extends javax.swing.filechooser.FileFilter {
    public final static String png = "png";
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(png)) {
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
        return "PNG Image";
    }
}
