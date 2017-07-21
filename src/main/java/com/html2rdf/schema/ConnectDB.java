package com.html2rdf.schema;

import com.html2rdf.functions.db.OpenDB;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.ddlutils.DdlUtilsException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 27/04/13
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
public class ConnectDB extends Thread{

    static MysqlDataSource ds;
    static Platform platform;
    static String port;
    static String vendor;
    static Logger logger = Logger.getLogger(ConnectDB.class);

    public ConnectDB(final String username, final String password, final String port, final String vendor){

        this.port = port;
        this.vendor = vendor;

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ds = new MysqlDataSource();
                if(vendor.equals("MySQL")){
                    ds.setURL("jdbc:mysql://localhost:"+port+"/");
                    ds.setUser(username);
                    ds.setPassword(password);
                }else if (vendor.equals("Oracle")){}

                logger.info("connecting to " + vendor + " : on " + ds.getURL() + ", username : " + username);

                try {
                    platform = PlatformFactory.createNewPlatformInstance(ds);
                    Statement st = platform.borrowConnection().createStatement();
                    ResultSet rs = st.executeQuery("SHOW databases");
                    while(rs.next()){
                         OpenDB.addElement(rs.getString(1));
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            OpenDB.setLoading(false);
                        }
                    });
                    OpenDB.enableButton(true);
                    OpenDB.connectionEstablished();
                    logger.info("Connection Established.");
                }catch (DdlUtilsException e){
                    logger.error("There was en error while trying to connect to "+vendor+".\n"+e.getMessage());
                    JOptionPane.showMessageDialog(new JFrame(), "There was en error while trying to connect to "+vendor+".\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            OpenDB.setLoading(false);
                        }
                    });
                }catch (SQLException e) {
                    logger.error("The following error occurred : \n" + e.getMessage());
                    JOptionPane.showMessageDialog(new JFrame(), "The following error occurred : \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            OpenDB.setLoading(false);
                        }
                    });
                }
            }
        });
    }

    public static Database getDatabase(String database){
        if(vendor.equals("MySQL")) ds.setURL("jdbc:mysql://localhost:"+port+"/"+database);
        else if(vendor.equals("Oracle")) {}
        Database db = platform.readModelFromDatabase(database);
        return db;
    }

    public static Platform getPlatform(){
        return platform;
    }
}
