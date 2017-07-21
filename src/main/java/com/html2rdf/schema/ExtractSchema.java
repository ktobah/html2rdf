package com.html2rdf.schema;

import com.html2rdf.functions.tree.FillTree;
import com.html2rdf.gui.Interface;
import org.apache.ddlutils.model.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.InvocationTargetException;


/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 26/04/13
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class ExtractSchema extends SwingWorker<String, Void>{

    static DefaultMutableTreeNode tableNode, columnNode, columnChild, referenceNode, referenceChild, foreignTable;
    static Logger logger = Logger.getLogger(ExtractSchema.class);
    static String database;

    public ExtractSchema(final String database){
        this.database = database;
    }

    @Override
    protected String doInBackground() throws Exception {
        Interface.setProgressBarVisible(true);
        Database db = ConnectDB.getDatabase(database);
        FillTree.changeRootName(db.getName());

        logger.info("Database \"" + database + "\" selected.");
        logger.info("Schema Extraction process started...");

        Table[] ar = db.getTables();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    FillTree.deleteAllNodes();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Interface.deleteTabs();
                Interface.updateTabbedPane();
            }
        });

        for (final Table table : ar) {
            logger.info("************************************************");
            logger.info("Table name : " + table.getName());
            logger.info("Table columns : " + table.getColumnCount());
            tableNode = new DefaultMutableTreeNode("Table : " + table.getName());
            FillTree.addToRoot(tableNode);
            columnNode = new DefaultMutableTreeNode("Table columns : " + table.getColumnCount());
            FillTree.addNode(columnNode, tableNode);
            Column[] columns = table.getColumns();
            for (final Column c : columns) {
                logger.info("Column " + table.getColumnIndex(c) + " : " + c.getName());
                columnChild = new DefaultMutableTreeNode("Column " + table.getColumnIndex(c) + " : " + c.getName() + " ("+c.getType()+")");
                FillTree.addNode(columnChild, columnNode);
            }
            // Primary keys
            final Column[] primaryKeyColumns = table.getPrimaryKeyColumns();
            if(table.hasPrimaryKey()) columnNode = new DefaultMutableTreeNode("Primary keys : " + primaryKeyColumns.length +" (yes)");
            else columnNode = new DefaultMutableTreeNode("Primary keys : " + primaryKeyColumns.length +" (no)");
            FillTree.addNode(columnNode, tableNode);
            logger.info("Primary keys : " + primaryKeyColumns.length);
            for (final Column pkc : primaryKeyColumns) {
                logger.info("Primary Key " + table.getColumnIndex(pkc) + " : " + pkc.getName());
                columnChild = new DefaultMutableTreeNode("Primary Key " + table.getColumnIndex(pkc) + " : " + pkc.getName());
                FillTree.addNode(columnChild, columnNode);
            }
            // Foreign keys
            if(table.getForeignKeyCount() > 0) columnNode = new DefaultMutableTreeNode("Foreign keys : " + table.getForeignKeyCount() +" (yes)");
            else columnNode = new DefaultMutableTreeNode("Foreign keys : " + table.getForeignKeyCount() +" (no)");
            FillTree.addNode(columnNode, tableNode);
            logger.info("Foreign keys : " + table.getForeignKeyCount());
            ForeignKey[] fk = table.getForeignKeys();
            for (final ForeignKey fkNumber : fk) {
                columnChild = new DefaultMutableTreeNode("Foreign Key : "+fkNumber.getName());
                FillTree.addNode(columnChild, columnNode);
                foreignTable = new DefaultMutableTreeNode("Foreign table : "+fkNumber.getForeignTableName());
                FillTree.addNode(foreignTable, columnNode);
                referenceNode = new DefaultMutableTreeNode("References : " + fkNumber.getReferenceCount());
                FillTree.addNode(referenceNode, columnNode);
                logger.info(fkNumber.toString());
                logger.info("References : " + fkNumber.getReferenceCount());
                Reference[] rf = fkNumber.getReferences();
                for (final Reference rfChild : rf) {
                    logger.info(rfChild.toString());
                    referenceChild = new DefaultMutableTreeNode(rfChild);
                    FillTree.addNode(referenceChild, referenceNode);
                }
            }
        }
        FillTree.expandAll();
        Interface.setProgressBarVisible(false);
        //FillTree.refreshTree();
        return null;
    }
}
