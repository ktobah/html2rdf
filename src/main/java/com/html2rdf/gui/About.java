package com.html2rdf.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 28/05/13
 * Time: 01:23
 * To change this template use File | Settings | File Templates.
 */

public class About implements MouseListener{

    private static JWindow about;
    /**
     * Create the dialog.
     */
    public About() {
        about = new JWindow();
        about.setFont(new Font("Arabic Typesetting", Font.PLAIN, 13));
        about.setAlwaysOnTop(true);
        about.addMouseListener(this);
        int x = Interface.getPosition().getWidth()+350;
        int y = Interface.getPosition().getHeight()+150;
        about.setLocation(x, y);
        about.setSize(500, 300);
        about.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 3));
        panel.setBounds(0, 0, 500, 300);
        about.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel gpl = new JLabel("<html>Copyright \u00A9 2013. Distributed under GNU General Public Licence (GPL).</html>");
        gpl.setBounds(8, 275, 370, 14);
        panel.add(gpl);
        gpl.setForeground(Color.BLACK);
        gpl.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        JLabel developer = new JLabel("<html>Developed by : Ahmed Ktob</html>");
        developer.setForeground(Color.WHITE);
        developer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        developer.setBounds(24, 73, 201, 14);
        panel.add(developer);

        JLabel department = new JLabel("<html>Department of Mathematics and Computer Science</html>");
        department.setForeground(new Color(0, 0, 0));
        department.setForeground(Color.WHITE);
        department.setFont(new Font("Segoe UI", Font.BOLD, 11));
        department.setBounds(24, 98, 423, 14);
        panel.add(department);

        JLabel software = new JLabel("<html>HTML2RDF v0.1</html>");
        software.setForeground(Color.WHITE);
        software.setFont(new Font("Segoe UI", Font.BOLD, 14));
        software.setBounds(24, 41, 165, 21);
        panel.add(software);

        JLabel university = new JLabel("<html>University Dr. Taher Moulay of Saida</html>");
        university.setForeground(Color.WHITE);
        university.setFont(new Font("Segoe UI", Font.BOLD, 11));
        university.setBounds(24, 123, 423, 14);
        panel.add(university);

        JLabel description = new JLabel("<html><p><b>Description :</b> </p><p>HTML2RDF is a software developed to extract Linked Data (RDF) from HTML Web pages & relational databases. This software is based on the W3C Direct Mapping specification.</p></html>");
        description.setForeground(Color.WHITE);
        description.setFont(new Font("Segoe UI", Font.BOLD, 11));
        //description.setFont(new Font("Tahoma", Font.PLAIN, 12));  style="text-indent: 15px;"
        description.setBounds(24, 163, 466, 90);
        panel.add(description);

        JLabel image = new JLabel("");
        image.setBounds(3, 3, 494, 294);
        panel.add(image);
        image.setOpaque(true);
        panel.setOpaque(false);
        image.setIcon(new ImageIcon(getClass().getResource("/icons/aboutBack.jpg")));
        about.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        about.dispose();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
