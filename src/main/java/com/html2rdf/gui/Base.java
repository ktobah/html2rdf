package com.html2rdf.gui;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.html2rdf.extraction.graph.DirectGraph;
import org.apache.commons.httpclient.URIException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by AHMED on 29/05/13.
 */
public class Base extends JDialog {

    private static String basePrefix;
    private JTextField baseField;
    private static String baseIRI;
    private static Thread threadBase;
    private static Model temporaryModel;

    public Base(final Model model) {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("Base IRI");
        setModal(true);
        setSize(388, 159);
        setLocationRelativeTo(this.getParent());
        getContentPane().setLayout(null);
        setResizable(false);

        JButton ok = new JButton("OK");
        ok.setBounds(122, 85, 71, 23);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String base = baseField.getText();
                if(base.isEmpty()) JOptionPane.showMessageDialog(new JFrame(), "No base IRI is specified, please type one.", "Error", JOptionPane.ERROR_MESSAGE);
                else{
                    if(base.contains("#") | base.contains("?") | !base.endsWith("/")) JOptionPane.showMessageDialog(new JFrame(), "Base IRI syntax error. Please be sure that the base IRI does not \n include \"#\" or \"?\" characters and ends with \"/\" character.", "Syntax error", JOptionPane.ERROR_MESSAGE);
                    else{
                        try {
                            URL url = new URL(base);
                            baseIRI = base;
                            basePrefix = url.getHost().replace(".com", "").replace("www."," ").trim();
                            temporaryModel = ModelFactory.createDefaultModel();
                            temporaryModel = model;
                            threadBase = new Thread(new DirectGraph(temporaryModel, base)); //, temporaryModel
                            Interface.setThread(threadBase);
                            threadBase.start();
                            dispose();
                        } catch (MalformedURLException e1) {
                            JOptionPane.showMessageDialog(new JFrame(), "Not a valid base IRI, please check your syntax.", "IRI syntax error", JOptionPane.ERROR_MESSAGE);
                        } catch (URIException e1) {
                            JOptionPane.showMessageDialog(new JFrame(), "Not a valid base IRI, please check your syntax.", "IRI syntax error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        getContentPane().add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(236, 85, 71, 23);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getContentPane().add(cancel);

        JLabel baseLabel = new JLabel("Please specify the base IRI :");
        baseLabel.setBounds(84, 28, 271, 14);
        getContentPane().add(baseLabel);

        baseField = new JTextField();
        baseField.setBounds(84, 54, 271, 23);
        getContentPane().add(baseField);
        baseField.setColumns(10);

        JLabel icon = new JLabel("");
        icon.setIcon(new ImageIcon(getClass().getResource("/icons/question.png")));
        icon.setBounds(10, 28, 64, 46);
        getContentPane().add(icon);
        setVisible(true);
    }

    public static String getBaseIRI(){
        return baseIRI;
    }

    public static String getBasePrefix(){
        return basePrefix;
    }
}
