package com.html2rdf.functions.tree;

import com.html2rdf.gui.Interface;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 01/05/16
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class CreateTable extends SwingWorker<String, Void>{

    JTable table;
    DefaultTableModel tableModel;
    String paneName;
    JTabbedPane tabbedPane;
    TableColumn tableColumn;
    JScrollPane scrollPane;
    JPanel panel;

    public CreateTable(JTable table, DefaultTableModel tableModel, String tableName){
        this.table = table;
        this.tableModel = tableModel;
        this.paneName = tableName;
    }

    @Override
    protected String doInBackground() throws Exception {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                table  = new JTable(tableModel);
                table.setName(paneName);
                DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
                cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                cellRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
                for(int i=0; i < table.getColumnCount(); i++){
                    tableColumn = table.getColumnModel().getColumn(i);
                    tableColumn.setCellRenderer(cellRenderer);
                }
                if(table.getColumnCount() >= 10){
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    TableColumn tableColumn;
                    for(int i=0; i<table.getColumnCount();i++){
                        tableColumn = table.getColumnModel().getColumn(i);
                        tableColumn.setMaxWidth(100);
                        tableColumn.setMinWidth(100);
                    }
                }
                scrollPane = new JScrollPane(table);
                scrollPane.setName("scroll"+paneName);
                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                panel.add(scrollPane);

                tabbedPane = Interface.getTabbedPane();
                tabbedPane.addTab("Table : "+paneName, null, panel);
            }
        });
        return null;

    }


}
