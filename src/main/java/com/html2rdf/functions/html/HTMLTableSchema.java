package com.html2rdf.functions.html;

import com.html2rdf.functions.tree.CreateTable;
import com.html2rdf.functions.tree.FillTree;
import com.html2rdf.gui.Interface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 08/06/16
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public class HTMLTableSchema {

    private final JPanel contentPanel = new JPanel();
    private JTextField tableNameField;
    private JTextField primaryKeys;
    private JTextField foreignKeys;
    private static JTable jtable;
    static DefaultMutableTreeNode tableNode, columnNode, columnChild, referenceNode, referenceChild, foreignTable;
    private static String tableName;
    private static DefaultTableModel tableModel;
    private static String tableNameReceived;
    private static ArrayList<String> columnsReceived;
    private static JDialog dialog;

    public HTMLTableSchema(final DefaultTableModel tableModel, String tableNameReceived, final ArrayList<String> columnsReceived) {

        this.tableModel = tableModel;
        this.tableNameReceived = tableNameReceived;
        this.columnsReceived = columnsReceived;

        /*this.tableModel = tableModel;
        this.tableNameReceived = tableNameReceived;
        this.columnsReceived = columnsReceived;
        dialog = new JDialog();
        dialog.setTitle("HTML Table Schema");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setBounds(100, 100, 652, 468);
        dialog.setResizable(false);
        dialog.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Table name : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(10, 11, 616, 68);
        contentPanel.add(panel);
        panel.setLayout(null);

        JLabel tableNameLabel = new JLabel("Table name :");
        tableNameLabel.setBounds(23, 30, 98, 14);
        panel.add(tableNameLabel);

        tableNameField = new JTextField(tableNameReceived);
        tableNameField.setBounds(110, 27, 133, 20);
        panel.add(tableNameField);
        tableNameField.setColumns(10);

        JLabel noteLabel = new JLabel("<html>The table name is a required champ, so please fill it.<br><br>N.B : You may leave it as it is if it is not empty.</html>");
        noteLabel.setBounds(285, 11, 321, 46);
        panel.add(noteLabel);

        JPanel primaryKeysPanel = new JPanel();
        primaryKeysPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Primary keys : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        primaryKeysPanel.setBounds(10, 90, 616, 155);
        contentPanel.add(primaryKeysPanel);
        primaryKeysPanel.setLayout(null);

        JLabel primaryKeysLabelNote = new JLabel("<html><p>Please specify the primary keys if applicable. If more than one, separate them with a (;). The available columns are : </p></html>");
        primaryKeysLabelNote.setBounds(10, 25, 596, 40);
        primaryKeysPanel.add(primaryKeysLabelNote);

        JLabel columns = new JLabel(columnsReceived.toString());
        columns.setBounds(10, 69, 596, 40);
        primaryKeysPanel.add(columns);

        JLabel primaryKeysLabel = new JLabel("Primary keys : ");
        primaryKeysLabel.setBounds(49, 123, 96, 14);
        primaryKeysPanel.add(primaryKeysLabel);

        primaryKeys = new JTextField();
        primaryKeys.setBounds(155, 120, 408, 20);
        primaryKeysPanel.add(primaryKeys);
        primaryKeys.setColumns(10);

        JPanel foreignKeysPanel = new JPanel();
        foreignKeysPanel.setBorder(new TitledBorder(null, "Foreign keys : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        foreignKeysPanel.setBounds(10, 256, 616, 132);
        contentPanel.add(foreignKeysPanel);
        foreignKeysPanel.setLayout(null);

        JLabel foreignKeysLabelNote = new JLabel("<html><p>Please specify the foreign keys if applicable. If more than one, separate them with a (;).</p><br><p>Use the following format : ForeignkeyColumn -> ReferencedColumn (ReferencedTable) </p></html>");
        foreignKeysLabelNote.setBounds(10, 23, 596, 45);
        foreignKeysPanel.add(foreignKeysLabelNote);

        foreignKeys = new JTextField();
        foreignKeys.setColumns(10);
        foreignKeys.setBounds(161, 95, 400, 20);
        foreignKeysPanel.add(foreignKeys);

        JLabel foreignKeysLabel = new JLabel("Foreign keys : ");
        foreignKeysLabel.setBounds(51, 98, 96, 14);
        foreignKeysPanel.add(foreignKeysLabel);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FillTree.changeRootName("HTML files");
                tableName = tableNameField.getText();
                if(tableName.isEmpty()){
                    JOptionPane.showMessageDialog(new JFrame(), "The table name can not be empty!", "Table name unspecified", JOptionPane.ERROR_MESSAGE);
                }else{
                    tableNode = new DefaultMutableTreeNode("Table : " + tableName);
                    FillTree.addToRoot(tableNode);
                    columnNode = new DefaultMutableTreeNode("Table columns : " + columnsReceived.size());
                    FillTree.addNode(columnNode, tableNode);

                    for (final String column : columnsReceived) {
                        columnChild = new DefaultMutableTreeNode("Column " + columnsReceived.indexOf(column) + " : " + column + " (VARCHAR)");
                        FillTree.addNode(columnChild, columnNode);
                    }

                    // Primary keys
                    final ArrayList<String> primaryKeyColumns = new ArrayList<String>();
                    String keys = primaryKeys.getText();
                    if(!keys.isEmpty()){
                        if(!keys.contains(";")) primaryKeyColumns.add(keys);
                        else{
                            String[] pks = keys.split(";");
                            for(String s : pks) primaryKeyColumns.add(s);
                        }
                    }

                    if(!primaryKeyColumns.isEmpty()) {
                        columnNode = new DefaultMutableTreeNode("Primary keys : " + primaryKeyColumns.size() +" (yes)");
                        FillTree.addNode(columnNode, tableNode);
                        for (String pkc : primaryKeyColumns) {
                            columnChild = new DefaultMutableTreeNode("Primary Key " + primaryKeyColumns.indexOf(pkc) + " : " + pkc);
                            FillTree.addNode(columnChild, columnNode);
                        }
                    }
                    else {
                        columnNode = new DefaultMutableTreeNode("Primary keys : " + primaryKeyColumns.size() +" (no)");
                        FillTree.addNode(columnNode, tableNode);
                    }

                    // Foreign keys
                    final ArrayList<String> foreignKeyColumns = new ArrayList<String>();
                    String fKeys = foreignKeys.getText();
                    if(!fKeys.isEmpty()){
                        if(!fKeys.contains(";")) foreignKeyColumns.add(fKeys);
                        else{
                            String[] fks = fKeys.split(";");
                            for(String s : fks) foreignKeyColumns.add(s);
                        }
                    }
                    ArrayList<String> referencedAlready = new ArrayList<String>();
                    boolean referenced = false;
                    if(!foreignKeyColumns.isEmpty()){
                        columnNode = new DefaultMutableTreeNode("Foreign keys : " + foreignKeyColumns.size() +" (yes)");
                        FillTree.addNode(columnNode, tableNode);
                        for (String fkNumber : foreignKeyColumns) {
                            String referencedTableName = fkNumber.substring(fkNumber.indexOf("(")+1, fkNumber.indexOf(")"));
                            if(!referencedAlready.contains(referencedTableName)){
                                foreignTable = new DefaultMutableTreeNode("Foreign table : "+referencedTableName);
                                FillTree.addNode(foreignTable, columnNode);
                                referencedAlready.add(referencedTableName);
                            }
                            if(!referenced){
                                referenceNode = new DefaultMutableTreeNode("References : " + foreignKeyColumns.size());
                                FillTree.addNode(referenceNode, columnNode);
                                referenced = true;
                            }
                            referenceChild = new DefaultMutableTreeNode(fkNumber.substring(0, fkNumber.indexOf("(")-1));
                            FillTree.addNode(referenceChild, referenceNode);
                        }
                    }else {
                        columnNode = new DefaultMutableTreeNode("Foreign keys : " + foreignKeyColumns.size() +" (no)");
                        FillTree.addNode(columnNode, tableNode);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            jtable = new JTable();
                        }
                    });
                    CreateTable createTable = new CreateTable(jtable, tableModel, tableName);
                    createTable.execute();
                    Interface.setExtractRDFEnabled(true);

                    dialog.dispose();
                }
            }
        });
        ok.setBounds(180, 399, 89, 23);
        contentPanel.add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        cancel.setBounds(365, 399, 89, 23);
        contentPanel.add(cancel);
        dialog.setVisible(true);     */
    }

    public void run(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog = new JDialog();
                dialog.setTitle("HTML Table Schema");
                dialog.setModal(true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setBounds(100, 100, 652, 468);
                dialog.setResizable(false);
                dialog.getContentPane().setLayout(new BorderLayout());
                contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
                contentPanel.setLayout(null);

                JPanel panel = new JPanel();
                panel.setBorder(new TitledBorder(null, "Table name : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBounds(10, 11, 616, 68);
                contentPanel.add(panel);
                panel.setLayout(null);

                JLabel tableNameLabel = new JLabel("Table name :");
                tableNameLabel.setBounds(23, 30, 98, 14);
                panel.add(tableNameLabel);

                tableNameField = new JTextField(tableNameReceived);
                tableNameField.setBounds(110, 27, 133, 20);
                panel.add(tableNameField);
                tableNameField.setColumns(10);

                JLabel noteLabel = new JLabel("<html>The table name is a required champ, so please fill it.<br><br>N.B : You may leave it as it is if it is not empty.</html>");
                noteLabel.setBounds(285, 11, 321, 46);
                panel.add(noteLabel);

                JPanel primaryKeysPanel = new JPanel();
                primaryKeysPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Primary keys : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                primaryKeysPanel.setBounds(10, 90, 616, 155);
                contentPanel.add(primaryKeysPanel);
                primaryKeysPanel.setLayout(null);

                JLabel primaryKeysLabelNote = new JLabel("<html><p>Please specify the primary keys if applicable. If more than one, separate them with a (;). The available columns are : </p></html>");
                primaryKeysLabelNote.setBounds(10, 25, 596, 40);
                primaryKeysPanel.add(primaryKeysLabelNote);

                JLabel columns = new JLabel(columnsReceived.toString());
                columns.setBounds(10, 69, 596, 40);
                primaryKeysPanel.add(columns);

                JLabel primaryKeysLabel = new JLabel("Primary keys : ");
                primaryKeysLabel.setBounds(49, 123, 96, 14);
                primaryKeysPanel.add(primaryKeysLabel);

                primaryKeys = new JTextField();
                primaryKeys.setBounds(155, 120, 408, 20);
                primaryKeysPanel.add(primaryKeys);
                primaryKeys.setColumns(10);

                JPanel foreignKeysPanel = new JPanel();
                foreignKeysPanel.setBorder(new TitledBorder(null, "Foreign keys : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                foreignKeysPanel.setBounds(10, 256, 616, 132);
                contentPanel.add(foreignKeysPanel);
                foreignKeysPanel.setLayout(null);

                JLabel foreignKeysLabelNote = new JLabel("<html><p>Please specify the foreign keys if applicable. If more than one, separate them with a (;).</p><br><p>Use the following format : ForeignkeyColumn -> ReferencedColumn (ReferencedTable) </p></html>");
                foreignKeysLabelNote.setBounds(10, 23, 596, 45);
                foreignKeysPanel.add(foreignKeysLabelNote);

                foreignKeys = new JTextField();
                foreignKeys.setColumns(10);
                foreignKeys.setBounds(161, 95, 400, 20);
                foreignKeysPanel.add(foreignKeys);

                JLabel foreignKeysLabel = new JLabel("Foreign keys : ");
                foreignKeysLabel.setBounds(51, 98, 96, 14);
                foreignKeysPanel.add(foreignKeysLabel);

                JButton ok = new JButton("Ok");
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FillTree.changeRootName("HTML files");
                        tableName = tableNameField.getText();
                        if(tableName.isEmpty()){
                            JOptionPane.showMessageDialog(new JFrame(), "The table name can not be empty!", "Table name unspecified", JOptionPane.ERROR_MESSAGE);
                        }else{
                            tableNode = new DefaultMutableTreeNode("Table : " + tableName);
                            FillTree.addToRoot(tableNode);
                            columnNode = new DefaultMutableTreeNode("Table columns : " + columnsReceived.size());
                            FillTree.addNode(columnNode, tableNode);

                            for (final String column : columnsReceived) {
                                columnChild = new DefaultMutableTreeNode("Column " + columnsReceived.indexOf(column) + " : " + column + " (VARCHAR)");
                                FillTree.addNode(columnChild, columnNode);
                            }

                            // Primary keys
                            final ArrayList<String> primaryKeyColumns = new ArrayList<String>();
                            String keys = primaryKeys.getText();
                            if(!keys.isEmpty()){
                                if(!keys.contains(";")) primaryKeyColumns.add(keys);
                                else{
                                    String[] pks = keys.split(";");
                                    for(String s : pks) primaryKeyColumns.add(s);
                                }
                            }

                            if(!primaryKeyColumns.isEmpty()) {
                                columnNode = new DefaultMutableTreeNode("Primary keys : " + primaryKeyColumns.size() +" (yes)");
                                FillTree.addNode(columnNode, tableNode);
                                for (String pkc : primaryKeyColumns) {
                                    columnChild = new DefaultMutableTreeNode("Primary Key " + primaryKeyColumns.indexOf(pkc) + " : " + pkc);
                                    FillTree.addNode(columnChild, columnNode);
                                }
                            }
                            else {
                                columnNode = new DefaultMutableTreeNode("Primary keys : " + primaryKeyColumns.size() +" (no)");
                                FillTree.addNode(columnNode, tableNode);
                            }

                            // Foreign keys
                            final ArrayList<String> foreignKeyColumns = new ArrayList<String>();
                            String fKeys = foreignKeys.getText();
                            if(!fKeys.isEmpty()){
                                if(!fKeys.contains(";")) foreignKeyColumns.add(fKeys);
                                else{
                                    String[] fks = fKeys.split(";");
                                    for(String s : fks) foreignKeyColumns.add(s);
                                }
                            }
                            ArrayList<String> referencedAlready = new ArrayList<String>();
                            boolean referenced = false;
                            if(!foreignKeyColumns.isEmpty()){
                                columnNode = new DefaultMutableTreeNode("Foreign keys : " + foreignKeyColumns.size() +" (yes)");
                                FillTree.addNode(columnNode, tableNode);
                                for (String fkNumber : foreignKeyColumns) {
                                    String referencedTableName = fkNumber.substring(fkNumber.indexOf("(")+1, fkNumber.indexOf(")"));
                                    if(!referencedAlready.contains(referencedTableName)){
                                        foreignTable = new DefaultMutableTreeNode("Foreign table : "+referencedTableName);
                                        FillTree.addNode(foreignTable, columnNode);
                                        referencedAlready.add(referencedTableName);
                                    }
                                    if(!referenced){
                                        referenceNode = new DefaultMutableTreeNode("References : " + foreignKeyColumns.size());
                                        FillTree.addNode(referenceNode, columnNode);
                                        referenced = true;
                                    }
                                    referenceChild = new DefaultMutableTreeNode(fkNumber.substring(0, fkNumber.indexOf("(")-1));
                                    FillTree.addNode(referenceChild, referenceNode);
                                }
                            }else {
                                columnNode = new DefaultMutableTreeNode("Foreign keys : " + foreignKeyColumns.size() +" (no)");
                                FillTree.addNode(columnNode, tableNode);
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    jtable = new JTable();
                                }
                            });
                            CreateTable createTable = new CreateTable(jtable, tableModel, tableName);
                            createTable.execute();
                            Interface.setExtractRDFEnabled(true);

                            dialog.dispose();
                        }
                    }
                });
                ok.setBounds(180, 399, 89, 23);
                contentPanel.add(ok);

                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });
                cancel.setBounds(365, 399, 89, 23);
                contentPanel.add(cancel);
                dialog.setVisible(true);
            }
        });
    }

}
