package com.html2rdf.extraction.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.html2rdf.functions.tree.FillTree;
import org.apache.commons.httpclient.URIException;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 22/05/16
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
public class TableGraph extends Thread{

    public TableGraph(Model model, JTable table, String base) {

        TableColumnModel tableColumnModel = table.getColumnModel();
        ArrayList<String> columns = new ArrayList<String>();

        for(int i=0; i<tableColumnModel.getColumnCount(); i++){
            columns.add(tableColumnModel.getColumn(i).getHeaderValue().toString());
        }

        ArrayList tempVector = new ArrayList();
        ArrayList<String> primaryKeys = getPrimaryKeys(table.getName());
        ArrayList<String> foreignKeys = getForeignKeys(table.getName());
        ArrayList<String> primaryKeysValues = new ArrayList<String>();
        if(primaryKeys != null){
            for(int i=0; i<table.getRowCount(); i++){
                for(int j=0; j<columns.size(); j++){
                    if(primaryKeys.contains(columns.get(j))){
                        primaryKeysValues.add(table.getValueAt(i, j).toString());
                    }
                    tempVector.add(table.getValueAt(i, j).toString());
                }
                if(TableGraph.currentThread().isInterrupted()) break;
                try {
                    new RowGraph(model, table.getName(), columns, tempVector, primaryKeys, primaryKeysValues, foreignKeys, base);
                } catch (URIException e) {
                    e.printStackTrace();
                }
                tempVector.clear();
                primaryKeysValues.clear();
            }
        }else{
            for(int i=0; i<table.getRowCount(); i++){
                for(int j=0; j<columns.size(); j++){
                    tempVector.add(table.getValueAt(i, j).toString());
                }
                if(TableGraph.currentThread().isInterrupted()) break;
                try {
                    new RowGraph(model,  table.getName(), columns, tempVector, primaryKeys, primaryKeysValues, foreignKeys, base);
                } catch (URIException e) {
                    e.printStackTrace();
                }
                tempVector.clear();
                primaryKeysValues.clear();
            }
        }
    }

    public static DefaultMutableTreeNode find(DefaultMutableTreeNode root, String tableName, String searched) {

        Enumeration rootChildren = root.breadthFirstEnumeration();
        while (rootChildren.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootChildren.nextElement();
            if (node.getUserObject().toString().equals("Table : " + tableName)) {
                Enumeration nodeChildren = node.breadthFirstEnumeration();
                while (rootChildren.hasMoreElements()) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)nodeChildren.nextElement();
                    if (childNode.getUserObject().toString().contains(searched)) {
                        return childNode;
                    }
                }
            }
        }
        return null;
    }

    private static boolean hasPrimary(String tableName){
        String result = find(FillTree.getRoot(), tableName, "Primary keys").getUserObject().toString();
        if(result.contains("yes")) return true;
        else return false;
    }

    private static boolean hasForeign(String tableName){
        String result = find(FillTree.getRoot(), tableName, "Foreign keys").getUserObject().toString();
        if(result.contains("yes")) return true;
        else return false;
    }

    private static ArrayList<String> getPrimaryKeys(String tableName){

        ArrayList<String> primaryKeys = null;
        if(hasPrimary(tableName)){
            primaryKeys = new ArrayList<String>();
            DefaultMutableTreeNode resultNode = find(FillTree.getRoot(), tableName, "Primary keys");
            Enumeration children = resultNode.children();
            while(children.hasMoreElements()){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)children.nextElement();
                String result = node.getUserObject().toString();
                result = result.substring(result.lastIndexOf(' ')).trim();
                primaryKeys.add(result);
            }
        }
        return primaryKeys;
    }

    public static ArrayList<String> getForeignKeys(String tableName){

        ArrayList<String> foreignKeys = null;
        if(hasForeign(tableName)){
            foreignKeys = new ArrayList<String>();
            DefaultMutableTreeNode resultNode = find(FillTree.getRoot(), tableName, "Foreign keys");
            Enumeration children = resultNode.children();
            while(children.hasMoreElements()){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)children.nextElement();
                String result = node.getUserObject().toString();
                if(result.contains("Foreign table")){
                    result = result.substring(result.lastIndexOf(' ')).trim();
                    foreignKeys.add(result);
                }else if (result.contains("References")){
                    Enumeration nodeChildren = node.children();
                    while (nodeChildren.hasMoreElements()) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)nodeChildren.nextElement();
                        result = childNode.getUserObject().toString();
                        foreignKeys.add(result);
                    }
                }
            }
        }
        return foreignKeys;
    }

}
