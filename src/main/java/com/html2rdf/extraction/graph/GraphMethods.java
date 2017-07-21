package com.html2rdf.extraction.graph;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.html2rdf.functions.tree.FillTree;
import com.html2rdf.gui.Interface;
import org.apache.commons.httpclient.URIException;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 22/05/16
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class GraphMethods {


    private static String base ;
    private static List<Statement> statements = null;
    private static Map<Integer, String> bNodes = new HashMap<Integer, String>();
    private static ArrayList<Integer> rowCount = new ArrayList<Integer>();
    private static int count = 0;
    private static int countTypeNode = 1;
    private static JTable table;
    private static ArrayList<Component> allTables;
    private static ArrayList<String> referencedPrimaryKeysValues;

    public GraphMethods(){
        statements = new ArrayList<Statement>();
    }

    public static void rowTypeTriple(Model model, String tableName, ArrayList<String> primaryKeys, ArrayList<String> primaryKeyValues) throws URIException {

        count = countTypeNode;
        String subject = rowNode(tableName, primaryKeys, primaryKeyValues);
        count--;

        String object = percentEncoded(tableName);
        Resource objectResource = ResourceFactory.createResource(base+object);
        Statement statement;
        Resource subjectResource;
        if(subject.contains("_:id")){
            subjectResource = model.createResource(AnonId.create(subject));
            statement = model.createStatement(subjectResource, RDF.type, objectResource);
            statements.add(statement);
        }else{
            subjectResource = ResourceFactory.createResource(base+subject);
            statement = model.createStatement(subjectResource, RDF.type, objectResource);
            statements.add(statement);
        }

        model.add(statement);
        //logger.info(statement);
        countTypeNode++;
    }

    public static void referenceTriple(Model model, String tableName, ArrayList<String> columns, ArrayList rowData, ArrayList<String> primaryKeys, ArrayList<String> primaryKeysValues,ArrayList<String> foreignKeys) throws URIException {

        String subject = rowNode(tableName, primaryKeys, primaryKeysValues);
        Property predicate = ResourceFactory.createProperty(base, referencePropertyIRI(tableName, foreignKeys));

        referencedPrimaryKeysValues = getReferencedPrimaryKeysValues(columns, rowData, foreignKeys);
        ArrayList<String> referencedPrimaryKeys = getReferencedPrimaryKeys(foreignKeys);
        String referencedTableName = getReferencedTableName(foreignKeys);
        String object = rowNode(referencedTableName, referencedPrimaryKeys, referencedPrimaryKeysValues);


        Statement statement;
        Resource subjectResource;
        if(subject.contains("_:id")){
            subjectResource = model.createResource(AnonId.create(subject));
        }else{
            subjectResource = ResourceFactory.createResource(base+subject);
        }
        Resource objectResource;
        if(object.contains("_:id")){
            objectResource = model.createResource(AnonId.create(object));
        }else{
            objectResource = ResourceFactory.createResource(base+object);
        }

        statement = model.createStatement(subjectResource, predicate, objectResource);

        model.add(statement);
        //logger.info(statement);
    }

    public static void literalTriple(Model model, String tableName, ArrayList<String> columns, ArrayList rowData, ArrayList<String> primaryKeys, ArrayList<String> primaryKeysValues) throws URIException {

        if(primaryKeys == null) count++;
        for(String column : columns){
            if(!rowData.get(columns.indexOf(column)).toString().isEmpty()){
                String subject = rowNode(tableName, primaryKeys, primaryKeysValues);
                Property predicate = ResourceFactory.createProperty(base, literalPropertyIRI(tableName, column));
                Literal object;

                if(getType(tableName, column).equals("INTEGER")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDint);
                }else if(getType(tableName, column).equals("REAL")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDfloat);
                }else if(getType(tableName, column).equals("VARCHAR")){
                    object = model.createLiteral(rowData.get(columns.indexOf(column)).toString());
                }else if(getType(tableName, column).equals("DATE")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDdate);
                }else if(getType(tableName, column).equals("DATETIME")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDdateTime);
                }else if(getType(tableName, column).equals("TIME")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDtime);
                }else if(getType(tableName, column).equals("TIMESTAMP")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDtime);
                }else if(getType(tableName, column).equals("YEAR")){
                    object = model.createTypedLiteral(rowData.get(columns.indexOf(column)), XSDDatatype.XSDgYear);
                }else{
                    object = model.createLiteral(rowData.get(columns.indexOf(column)).toString());
                }

                Resource subjectResource;
                if(subject.contains("_:id")){
                    subjectResource = model.createResource(AnonId.create(subject));
                    Statement statement = model.createStatement(subjectResource, predicate, object);
                    statements.add(statement);
                }else{
                    subjectResource = ResourceFactory.createResource(base+subject);
                    Statement statement = model.createStatement(subjectResource, predicate, object);
                    statements.add(statement);
                }
            }
        }

        model.add(statements);
        //logger.info(statements);
    }

    private static String rowNode(String tableName, ArrayList<String> primaryKey, ArrayList<String> primaryKeyValue) throws URIException {

        String returned;
        if(primaryKey != null){
            ArrayList<String> keyEncoded = new ArrayList<String>();
            for(String key : primaryKey) keyEncoded.add(percentEncoded(key));

            ArrayList<String> keyValue = new ArrayList<String>();
            for(String value : primaryKeyValue) keyValue.add(percentEncoded(value));

            returned = tableIRI(tableName).concat("/");

            for(int i=0; i<keyEncoded.size(); i++){
                if(!keyValue.get(i).isEmpty()){
                    if(!keyEncoded.get(i).equals(keyEncoded.get(keyEncoded.size()-1)))
                        returned = returned.concat(keyEncoded.get(i)).concat("=").concat(keyValue.get(i)).concat(";");
                    else returned = returned.concat(keyEncoded.get(i)).concat("=").concat(keyValue.get(i));
                }
            }
        }else{
            if(rowCount.contains(count)){
                returned = bNodes.get(count);
            }else{
                returned = "_:id".concat(""+count);
                bNodes.put(count, returned);
                rowCount.add(count);
            }
        }

        return returned;
    }

    private static String tableIRI(String tableName) throws URIException {
        return percentEncoded(tableName);
    }

    private static String literalPropertyIRI(String tableName, String columns) throws URIException {
        return percentEncoded(tableName).concat("#").concat(percentEncoded(columns));
    }

    private static String referencePropertyIRI(String tableName, ArrayList<String> foreignColumns) throws URIException {
        String result = percentEncoded(tableName).concat("#ref-");
        ArrayList<String> foreignEncoded = new ArrayList<String>();
        ArrayList<String> onlyForeignKeys = getForeignKeys(foreignColumns);

        for(String foreign : onlyForeignKeys) foreignEncoded.add(percentEncoded(foreign));

        for(int i=0; i<foreignEncoded.size(); i++){
            if(!foreignEncoded.get(i).equals(foreignEncoded.get(foreignEncoded.size()-1)))
                result = result.concat(foreignEncoded.get(i)).concat(";");
            else result = result.concat(foreignEncoded.get(i));
        }

        return result;
    }

    private static ArrayList getForeignKeys(ArrayList<String> foreignColumns){
        ArrayList<String> onlyForeignKeys = new ArrayList<String>();
        for(String foreignColumn : foreignColumns){
            if(foreignColumn.contains("->")){
                foreignColumn = foreignColumn.substring(0, foreignColumn.indexOf("->") - 1);
                onlyForeignKeys.add(foreignColumn);
            }
        }
        return onlyForeignKeys;
    }

    private static String percentEncoded(String string) throws URIException {

        string = base.concat(string);

        /*System.out.println("Using the URIUtil : ");
        String stringURI = URIUtil.encodePath(string, "UTF-8");
        System.out.println(stringURI);*/

        //System.out.println("Using the URL : ");
        URL iri;
        URI uri = null;

        try {
            iri = new URL(string);
            uri = new URI(iri.getProtocol(), iri.getUserInfo(), iri.getHost(), iri.getPort(), iri.getPath(), iri.getQuery(), iri.getRef());
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(new JFrame(), "There was an error while creating URI : \n"+e.getMessage(), "URI creation error", JOptionPane.ERROR_MESSAGE);
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(new JFrame(), "URI syntax error : \n"+e.getMessage(), "URI creation error", JOptionPane.ERROR_MESSAGE);
        }
        return uri.toString().replaceFirst(base, "").trim();
    }

    public void setBase(String base){
        this.base = base;
    }

    private static String getType(String tableName, String columnName){

        String result = TableGraph.find(FillTree.getRoot(), tableName, columnName).getUserObject().toString();
        if(result.contains("INTEGER") | result.contains("INT") | result.contains("SMALLINT")| result.contains("DECIMAL") | result.contains("NUMERIC")) return "INTEGER";
        else if(result.contains("VARCHAR") | result.contains("CHAR") | result.contains("TEXT")) return "VARCHAR";
        else if(result.contains("REAL") | result.contains("FLOAT") | result.contains("DOUBLE PRECISION")) return "REAL";
        else if(result.contains("DATE")) return "DATE";
        else if(result.contains("DATETIME")) return "DATETIME";
        else if(result.contains("TIME")) return "TIME";
        else if(result.contains("TIMESTAMP")) return "TIMESTAMP";
        else if(result.contains("YEAR")) return "YEAR";
        else return "NONE";
    }

    private static int getColumnIndex(JTable tableName, String column){
        for(int i=0; i< tableName.getColumnCount(); i++){
            if(tableName.getColumnName(i).equals(column)) return i;
        }
        return -1;
    }

    private static ArrayList<String> getReferencedPrimaryKeysValues(ArrayList<String> columns, ArrayList rowData, ArrayList<String> foreignKeys){

        if(referencedPrimaryKeysValues == null) referencedPrimaryKeysValues = new ArrayList<String>();
        if(referencedPrimaryKeysValues.size() != 0) referencedPrimaryKeysValues.clear();

        ArrayList<String> onlyForeignKeys = getForeignKeys(foreignKeys);

        Map<String, Object> columnValues = new HashMap<String, Object>();
        for(String col : columns){
            columnValues.put(col, rowData.get(columns.indexOf(col)));
        }

        ArrayList<String> foreignKeysValues = new ArrayList<String>();

        for(String data : onlyForeignKeys){
            foreignKeysValues.add(columnValues.get(data).toString());
        }

        ArrayList<String> referencedPrimaryKeys = getReferencedPrimaryKeys(foreignKeys);

        allTables = Interface.getTables(Interface.getJXFrame(), new ArrayList<Component>());
        for(Component component : allTables){
            if(component.getName().equalsIgnoreCase(foreignKeys.get(0))){
                table = (JTable)component;
            }
        }

        String value;
        int index;
        String foreignCol;
        boolean equal = false;

        for(int i=0; i<table.getRowCount(); i++){
            for(String col : referencedPrimaryKeys){
                index = getColumnIndex(table, col);
                if(index != -1){
                    value = table.getValueAt(i, index).toString();
                    foreignCol = onlyForeignKeys.get(referencedPrimaryKeys.indexOf(col));
                    if(value.equals(columnValues.get(foreignCol))){
                        equal = true;
                    }else{
                        equal = false;
                    }
                }
            }
            if(equal){
                for (String str : onlyForeignKeys){
                    referencedPrimaryKeysValues.add(columnValues.get(str).toString());
                }
                break;
            }
        }

        return referencedPrimaryKeysValues;
    }

    public static ArrayList<String> getReferencedPrimaryKeys(ArrayList<String> foreignKeys){

        ArrayList<String> referencedPrimaryKeys = new ArrayList<String>();
        for(String foreignColumn : foreignKeys){
            if(foreignColumn.contains("->")){
                foreignColumn = foreignColumn.substring(foreignColumn.indexOf('>') + 2, foreignColumn.length());
                referencedPrimaryKeys.add(foreignColumn);
            }
        }
        return referencedPrimaryKeys;
    }

    private static String getReferencedTableName(ArrayList<String> foreignKeys){
        return foreignKeys.get(0);
    }
}
