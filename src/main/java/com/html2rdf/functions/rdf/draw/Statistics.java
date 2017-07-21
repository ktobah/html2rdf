package com.html2rdf.functions.rdf.draw;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 17/06/16
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Statistics extends JFrame {

    private static final long serialVersionUID = 1L;

    public Statistics(String applicationTitle, String chartTitle, Model model) {
        super(applicationTitle);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Statistics.class.getResource("/icons/statistics24.png")));

        // This will create the dataset
        PieDataset dataset = createDataset(model);
        // based on the dataset we create the chart
        final JFreeChart chart = createChart(dataset, chartTitle);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
        pack();
        setVisible(true);
    }

    /**
     * Creates a sample dataset
     */
    private  PieDataset createDataset(Model model) {
        DefaultPieDataset result = new DefaultPieDataset();

        Map<String, MutableInt> predicates = new HashMap<String, MutableInt>();
        int subjects = 0;

        StmtIterator iterator = model.listStatements();
        Statement statement;
        while(iterator.hasNext()){
            statement = iterator.nextStatement();
            MutableInt count = predicates.get(statement.getPredicate().toString());
            if (count == null) {
                predicates.put(statement.getPredicate().toString(), new MutableInt());
            }
            else {
                count.increment();
            }

        }
        Collection collection = predicates.entrySet();
        Iterator<Map.Entry<String, MutableInt>> it = collection.iterator();
        while(it.hasNext()){
            Map.Entry<String, MutableInt> entry = it.next();
            result.setValue(entry.getKey().substring(entry.getKey().indexOf('#')+1, entry.getKey().length()), (entry.getValue().get()*100)/model.size());
        }

        return result;
    }


    /**
     * Creates a chart
     */

    private JFreeChart createChart(PieDataset dataset, String title) {

        JFreeChart chart = ChartFactory.createPieChart3D(title,          // chart title
                dataset,                // data
                true,                   // include legend
                true,
                false);

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.8f);
        return chart;

    }
}

class MutableInt {
    int value = 1; // note that we start at 1 since we're counting
    public void increment () { ++value;      }
    public int  get ()       { return value; }
}