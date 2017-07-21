package com.html2rdf.schema;

import com.html2rdf.functions.tree.CreateTable;
import com.html2rdf.gui.Interface;
import org.apache.commons.beanutils.DynaBean;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 28/04/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class ExtractData extends SwingWorker<String, Void> {

    static Logger logger = Logger.getLogger(ExtractData.class);
    static String database;
    private static JTable jtable;

    public ExtractData(final String database){
        this.database = database;
    }

    @Override
    protected String doInBackground() throws Exception {
        Platform platform = ConnectDB.getPlatform();
        Database db = platform.readModelFromDatabase(database);
        Table[] tables = db.getTables();
        Iterator it;
        DynaBean dina;
        Interface.deleteTabs();
        Interface.updateTabbedPane();

        for(Table table : tables){
            logger.info("Extracting data from table : " + table.getName());
            it = platform.query(db, "SELECT * FROM "+table.getName());

            Column[] columns = table.getColumns();
            DefaultTableModel tableModel = new DefaultTableModel();
            for(Column column : columns) {
                tableModel.addColumn(column.getName());
            }

            while(it.hasNext()){
                dina = (DynaBean)it.next();
                Vector vector = new Vector();
                for(int i=0; i < table.getColumnCount(); i++) {
                    if(dina.get(table.getColumn(i).getName()) == null) vector.addElement("");
                    else vector.addElement(dina.get(table.getColumn(i).getName()));
                }
                tableModel.addRow(vector);
            }
            logger.info("Creating table : " + table.getName());
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jtable = new JTable();
                }
            });
            CreateTable createTable = new CreateTable(jtable, tableModel, table.getName());
            createTable.execute();
        }

        return null;
    }

    @Override
    protected void done(){
        Interface.changeExtractDataIcon(new ImageIcon(getClass().getResource("/icons/process24.png")));
        Interface.setExtractDataEnabled(false);
        Interface.setExtractRDFEnabled(true);
    }
}
