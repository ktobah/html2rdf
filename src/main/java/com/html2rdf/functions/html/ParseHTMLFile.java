package com.html2rdf.functions.html;


import com.html2rdf.gui.Interface;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.universalchardet.UniversalDetector;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 03/06/16
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */


public class ParseHTMLFile extends Thread{

    private static JFileChooser choose;
    private static int increment = 0;
    private static String tableName;
    private static ArrayList<String> columnNames;
    private static HTMLTableSchema htmlTableSchema;
    private static DefaultTableModel tableModel;
    static Logger logger = Logger.getLogger(ParseHTMLFile.class);

    public void run(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                choose = new JFileChooser();
                choose.setAcceptAllFileFilterUsed(false);
                choose.setLocale(Locale.ENGLISH);
                choose.setApproveButtonText("Open");
                choose.setDialogType(JFileChooser.OPEN_DIALOG);
                choose.setDialogTitle("Open an HTML file");
                choose.setApproveButtonToolTipText("Open an HTML file");
                choose.addChoosableFileFilter(new FilterHTML());
                int decision = choose.showDialog(Interface.getJXFrame(), "Open");


                if (decision == choose.APPROVE_OPTION) {
                    logger.info("File selected : "+choose.getSelectedFile().getAbsolutePath());
                    Interface.setProgressBarVisible(true);
                    File htmlFile = new File(choose.getSelectedFile().getAbsolutePath());
                    InputStreamReader stream;
                    Document doc;
                    try {
                        logger.info("Loading content...");
                        byte[] buffer = new byte[4096];

                        UniversalDetector detector = new UniversalDetector(null);
                        FileInputStream file = new FileInputStream(htmlFile);
                        int nread;
                        while ((nread = file.read(buffer)) > 0 && !detector.isDone()) {
                            detector.handleData(buffer, 0, nread);
                        }

                        detector.dataEnd();

                        String encoding = detector.getDetectedCharset();

                        if (encoding != null) {
                            if(encoding.equalsIgnoreCase("UTF-8"))
                                doc = Jsoup.parse(htmlFile, "UTF-8");
                            else doc = Jsoup.parse(htmlFile, "ISO-8859-1");
                        } else {
                            doc = Jsoup.parse(htmlFile, "UTF-8");
                        }

                        detector.reset();
                        file = null;

                        Elements tables = doc.getElementsByTag("table");

                        for (Element table : tables) {
                            Interface.setProgressBarVisible(true);
                            logger.info("Extracting table name...");
                            if(!table.attr("name").isEmpty()){
                                tableName = table.attr("name");
                                logger.info("Name found : "+tableName);
                            }else if(!table.attr("id").isEmpty()){
                                tableName = table.attr("id");
                                logger.info("Name found : "+tableName);
                            }else{
                                tableName = "Table"+increment;
                                logger.info("No name found, one is generated : "+tableName);
                            }
                            Elements columns = table.getElementsByTag("th");
                            columnNames = new ArrayList<String>();

                            logger.info("Extracting columns...");
                            tableModel = new DefaultTableModel();
                            for(Element column:columns){
                                columnNames.add(column.text().replaceAll("\\s",""));
                                tableModel.addColumn(column.text().replaceAll("\\s", ""));
                            }
                            Elements rows = table.getElementsByTag("tr");
                            logger.info("Extracting data...");
                            for(Element row : rows){
                                Elements rowTD = row.getElementsByTag("td");
                                Vector vector = new Vector();
                                for(Element td:rowTD){
                                    vector.addElement(td.text());
                                }
                                if(!vector.isEmpty()) tableModel.addRow(vector);
                            }

                            Interface.setProgressBarVisible(false);
                            htmlTableSchema = new HTMLTableSchema(tableModel, tableName, columnNames);
                            htmlTableSchema.run();
                            increment++;
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(new JFrame(), "Error occurs while reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }

        });
    }
}

class FilterHTML extends FileFilter {
    public final static String html = "html", htm = "htm";
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(html) | extension.equals(htm)) {
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
        return "HTML file";
    }
}
