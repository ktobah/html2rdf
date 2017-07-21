package com.html2rdf.functions.db;

import com.html2rdf.gui.Interface;
import com.html2rdf.schema.ConnectDB;
import com.html2rdf.schema.ExtractSchema;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 26/04/13
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
public class OpenDB extends JDialog{

    private JPanel contentPane;
    private static JComboBox vendor, database;
    private static JTextField username, port;
    private static JPasswordField password;
    private String passWord;
    private static DefaultComboBoxModel modelDB;
    private static JButton Ok, Cancel, Connect;
    private static JLabel iconLabel;
    private static String databaseName;
    private static Logger logger = Logger.getLogger(OpenDB.class);

    public OpenDB() {
        setTitle("Select a database");
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 555, 332);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel ConfigPanel = new JPanel();
        ConfigPanel.setBorder(new TitledBorder(null, "Configuration :", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        ConfigPanel.setBounds(10, 11, 525, 244);
        contentPane.add(ConfigPanel);
        ConfigPanel.setLayout(null);

        JPanel vendorPanel = new JPanel();
        vendorPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Vendor :", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        vendorPanel.setBounds(10, 27, 504, 50);
        ConfigPanel.add(vendorPanel);
        vendorPanel.setLayout(null);

        vendor = new JComboBox();
        vendor.setModel(new DefaultComboBoxModel(new String[]{"", "MySQL", "Oracle"}));
        vendor.setBounds(252, 19, 232, 20);
        vendorPanel.add(vendor);

        JLabel labelVendor = new JLabel("Please select a vendor from the list :");
        labelVendor.setBounds(10, 22, 261, 14);
        vendorPanel.add(labelVendor);

        JPanel propertiesPanel = new JPanel();
        propertiesPanel.setBorder(new TitledBorder(null, "Properties :", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        propertiesPanel.setBounds(10, 88, 504, 54);
        ConfigPanel.add(propertiesPanel);
        propertiesPanel.setLayout(null);

        JLabel lblUsername = new JLabel("Username :");
        lblUsername.setBounds(10, 25, 71, 14);
        propertiesPanel.add(lblUsername);

        username = new JTextField();
        username.setBounds(81, 22, 100, 20);
        propertiesPanel.add(username);
        username.setColumns(10);

        JLabel lblPassword = new JLabel("Password :");
        lblPassword.setBounds(202, 25, 76, 14);
        propertiesPanel.add(lblPassword);

        JLabel lblPort = new JLabel("Port :");
        lblPort.setBounds(390, 25, 46, 14);
        propertiesPanel.add(lblPort);

        port = new JTextField();
        port.setBounds(435, 22, 59, 20);
        propertiesPanel.add(port);
        port.setColumns(10);

        password = new JPasswordField();
        password.setBounds(272, 22, 100, 20);
        propertiesPanel.add(password);

        JPanel DBPanel = new JPanel();
        DBPanel.setBorder(new TitledBorder(null, "Database :", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        DBPanel.setBounds(10, 183, 504, 50);
        ConfigPanel.add(DBPanel);
        DBPanel.setLayout(null);

        JLabel labelDB = new JLabel("Please select a database :");
        labelDB.setBounds(10, 21, 232, 14);
        DBPanel.add(labelDB);

        database = new JComboBox();
        modelDB = new DefaultComboBoxModel(new String[] {""});
        database.setModel(modelDB);
        database.setEnabled(false);
        database.setBounds(252, 18, 232, 20);
        DBPanel.add(database);

        Connect = new JButton("Connect");
        Connect.setBounds(217, 153, 89, 23);
        Connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passWord = new String(password.getPassword());
                if(vendor.getSelectedItem().equals("")){
                    logger.warn("\"No vendor is selected, please select one.\"");
                    JOptionPane.showMessageDialog(new JFrame(), "No vendor is selected, please select one.", "Warning", JOptionPane.WARNING_MESSAGE);
                }else{
                    if(username.getText().isEmpty()){
                        logger.warn("\"Username can not be empty.\"");
                        JOptionPane.showMessageDialog(new JFrame(), "Username can not be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else if(passWord.isEmpty()){
                        logger.warn("\"Password can not be empty.\"");
                        JOptionPane.showMessageDialog(new JFrame(), "Password can not be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else if(port.getText().isEmpty()){
                        logger.warn("\"port can not be empty.\"");
                        JOptionPane.showMessageDialog(new JFrame(), "Port can not be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else if(!username.getText().isEmpty() & !passWord.isEmpty() & !port.getText().isEmpty()){
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                OpenDB.setLoading(true);
                            }
                        });
                        if(vendor.getSelectedItem().equals("MySQL")){
                            Thread connect = new ConnectDB(username.getText(), passWord, port.getText(), "MySQL");
                            connect.start();
                        }else{
                            Thread connect = new ConnectDB(username.getText(), passWord, port.getText(), "Oracle");
                            connect.start();
                        }
                    }
                }
            }
        });
        ConfigPanel.add(Connect);

        iconLabel = new JLabel("");
        iconLabel.setBounds(316, 158, 198, 14);
        ConfigPanel.add(iconLabel);

        Ok = new JButton("OK");
        Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(database.getSelectedItem().equals("")){
                    logger.warn("\"Please select a database.\"");
                    JOptionPane.showMessageDialog(new JFrame(), "Please select a database.", "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    databaseName = database.getSelectedItem().toString();
                    ExtractSchema worker = new ExtractSchema(databaseName);
                    worker.execute();
                    Interface.setExtractDataEnabled(true);
                    Interface.setExtractRDFEnabled(false);
                    dispose();
                }
            }
        });
        Ok.setBounds(166, 266, 89, 23);
        Ok.setEnabled(false);
        contentPane.add(Ok);

        Cancel = new JButton("Cancel");
        Cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        Cancel.setBounds(310, 266, 89, 23);
        contentPane.add(Cancel);
        setVisible(true);
    }

    public static void addElement(String item){
        modelDB.addElement(item);
    }

    public static void enableButton(boolean bool){
        Ok.setEnabled(bool);
        database.setEnabled(bool);
        vendor.setEnabled(false);
        username.setEnabled(false);
        password.setEnabled(false);
        port.setEnabled(false);
        Connect.setEnabled(false);
    }

    public static void setLoading(boolean bool){
        if(bool) iconLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(OpenDB.class.getResource("/icons/loading.gif"))));
        else iconLabel.setIcon(null);
    }

    public static void connectionEstablished(){
        iconLabel.setText("Connection Established.");
        iconLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(OpenDB.class.getResource("/icons/true16.png"))));
        iconLabel.setForeground(Color.green);
    }

    public static String getDataBaseName(){
        return databaseName;
    }
}
