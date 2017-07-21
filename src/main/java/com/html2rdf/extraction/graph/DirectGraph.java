package com.html2rdf.extraction.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.html2rdf.gui.Interface;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 22/05/16
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class DirectGraph extends SwingWorker<Model,Void>{

    static Logger logger = Logger.getLogger(DirectGraph.class);
    private static Model temporaryModel = null, model;
    private static String base;
    private static boolean isInterrupted = false;

    private static OutputStream out = new OutputStream() {
        @Override
        public void write(int b) throws IOException {
              Interface.showTriple(String.valueOf((char) b));
        }
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
              Interface.showTriple(new String(b, off, len));
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }
    };
    private static TableGraph tableGraph;
    private static PrintStream printStream;


    public DirectGraph(Model model, String base) throws URIException {  //Model model
        logger.info("Setting the base : "+base);
        this.base = base;
        this.model = model;
    }


    @Override
    protected Model doInBackground() throws Exception {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String startTime = formatter.format(Calendar.getInstance().getTimeInMillis());
        long startTimeInMilli = Calendar.getInstance().getTimeInMillis();

        Interface.setProgressBarVisible(true);
        temporaryModel = ModelFactory.createDefaultModel();
        Interface.emptyTriples();
        logger.info("Extraction started, this process may take a while, please be patient...");
        logger.info("Start Time : "+startTime);
        Icon icon24 = new ImageIcon(getClass().getResource("/icons/pause24.png"));
        Icon icon16 = new ImageIcon(getClass().getResource("/icons/pause16.png"));
        Interface.changeImageAndToolTip(icon24, icon16, "Stop Extraction.");
        ArrayList<Component> allTables = Interface.getTables(Interface.getJXFrame(), new ArrayList<Component>());
        System.setProperty("usePropertySymbols", "false");
        temporaryModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        printStream = new PrintStream(out, true);
        Interface.showTriple("@base <"+base+">\n");

        for(int i=0; i < allTables.size(); i++){
            logger.info("Extracting triples from table : "+allTables.get(i).getName());
            logger.info("********************************************************************************");
            if(Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                tableGraph.interrupt();
                break;
            }
            tableGraph = new TableGraph(temporaryModel, (JTable)allTables.get(i), base);
        }
        String endTime = formatter.format(Calendar.getInstance().getTimeInMillis());
        long endTimeInMilli = Calendar.getInstance().getTimeInMillis();
        logger.info("End Time : "+endTime);
        logger.info("Time elapsed during extraction : "+formatter.format(endTimeInMilli - startTimeInMilli));
        return null;
    }


    @Override
    protected void done(){
        Interface.setProgressBarVisible(false);
        if(isInterrupted) logger.warn("Extraction process is canceled");
        else logger.info("Extraction process has terminated successfully");

        Icon icon24 = new ImageIcon(getClass().getResource("/icons/extraction24.png"));
        Icon icon16 = new ImageIcon(getClass().getResource("/icons/extraction16.png"));
        Interface.changeImageAndToolTip(icon24, icon16, "Start Extraction.");
        Interface.setItemsEnabled();

        logger.info("Appending extracted triples to the graph");
        System.setProperty("usePropertySymbols", "false");

        temporaryModel.write(printStream, "N3-TRIPLE");
        model.add(temporaryModel);

        Interface.setThreadNull();
    }

    public static OutputStream getOutputStream(){
        return out;
    }
}
