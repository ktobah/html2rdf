package com.html2rdf.extraction.graph;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.httpclient.URIException;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 22/05/16
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
public class RowGraph {

    private static GraphMethods graphMethods = null;

    public RowGraph(Model model, String tableName, ArrayList<String> columns, ArrayList rowData, ArrayList<String> primaryKeys, ArrayList<String> primaryKeysValues,ArrayList<String> foreignKeys, String base) throws URIException {

        graphMethods = new GraphMethods();
        graphMethods.setBase(base);

        graphMethods.rowTypeTriple(model, tableName, primaryKeys, primaryKeysValues);
        graphMethods.literalTriple(model, tableName, columns, rowData, primaryKeys, primaryKeysValues);
        if(foreignKeys != null)
            graphMethods.referenceTriple(model, tableName, columns, rowData, primaryKeys, primaryKeysValues, foreignKeys);

    }

}
