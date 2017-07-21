package com.html2rdf.gui;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.html2rdf.functions.db.OpenDB;
import com.html2rdf.functions.html.ParseHTMLFile;
import com.html2rdf.functions.rdf.draw.GraphVisualization;
import com.html2rdf.functions.rdf.draw.Statistics;
import com.html2rdf.functions.rdf.open.OpenRDF;
import com.html2rdf.functions.rdf.save.*;
import com.html2rdf.functions.tree.FillTree;
import com.html2rdf.schema.ExtractData;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Interface {

    private static JButton extractRDF, saveRDF, extractData;
    private static RSyntaxTextArea textArea;
    private static JTabbedPane tabbedPane;
    private JSplitPane splitPane;
    private static JProgressBar progressBar;
    private static JXFrame principal;
    private static JMenuItem itemExtractRDF, itemExtractData, itemDeleteTables, itemStatistics, menuItem, itemSaveRDF, itemNTriples, itemTurtle, itemN3TRIPLE, itemN3PP, itemN3PLAIN, itemXMLNormal, itemXMLAbr;
    private static JTextArea extractedTriples;
    private static Thread extractTriples = null;
    private static Logger logger;
    private static Model generalModel;
    private static boolean saved = false;
    private JLabel statusLabel = new JLabel(" Loading complete.");;


    public static void main(String[] args) {

        /*SplashScreen splash = SplashScreen.getSplashScreen();
        Graphics2D g = splash.createGraphics();
        Dimension dim = splash.getSize();

        g.setComposite(AlphaComposite.Src);
        g.setColor(Color.BLACK);
        g.fillRect(28, dim.height -10 , dim.width - 61, 5);
        splash.update();
        int value = 0;
        for (int i = 0; i < 38; i++) {
            g.setComposite(AlphaComposite.Clear);
            g.setPaintMode();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(28, dim.height -10, value, 5);
            splash.update();
            value += 20;
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
        } */

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //Cute look and feel
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    //UIManager.setLookAndFeel(new WindowsLookAndFeel());
                    //UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel");

                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
                    ///UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");

                    new Interface();
                    logger = Logger.getRootLogger();
                    TextAppender appender = new TextAppender(new PatternLayout("[%d{dd-MM-yyyy HH:mm:ss}] %p :%l %m%n"), textArea);
                    appender.addFilter(new Filter() {
                        @Override
                        public int decide(LoggingEvent loggingEvent) {
                            if (loggingEvent.getLevel().equals(Level.INFO) | loggingEvent.getLevel().equals(Level.ERROR) | loggingEvent.getLevel().equals(Level.WARN)) {
                                return ACCEPT;
                            } else {
                                return DENY;
                            }
                        }
                    });
                    logger.addAppender(appender);
                    logger.info("Application Started...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * The constructor.
     */
    public Interface() {
        initialize();
    }

    private void initialize() {
        principal = new JXFrame();
        principal.setIconImage(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/icons/LinkedData.png")));
        principal.setTitle("HTML2RDF");
        principal.setBounds(30, 25, 1300, 700);
        principal.setDefaultCloseOperation(JXFrame.DO_NOTHING_ON_CLOSE);
        principal.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                int answer = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to exit the application?", "Exit the application", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else if (answer == JOptionPane.NO_OPTION) {
                    return;
                }
                ;
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        // Create a container for the principal interface.
        Container content = principal.getContentPane();

        // Create a toolbar and add it to the Interface container.
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        content.add(toolBar, BorderLayout.NORTH);

        // Creating the elements of the toolbar.
        JButton openDB = new JButton();
        openDB.setToolTipText("Open a Database");
        openDB.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Open a database.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action of opening a file when the user click "Open".
        openDB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new OpenDB();
            }
        });
        openDB.setIcon(new ImageIcon(getClass().getResource("/icons/database24.png")));
        toolBar.add(openDB);

        JButton OpenHTML = new JButton(new ImageIcon(getClass().getResource("/icons/html24.png")));
        OpenHTML.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Open an HTML document.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        OpenHTML.setToolTipText("Open an HTML file");
        // The action of opening a URL when the user click "Open".
        OpenHTML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ParseHTMLFile htmlFile=  new ParseHTMLFile();
                htmlFile.run();
            }
        });
        toolBar.add(OpenHTML);

        //The button to open an RDF document.
        JButton OpenRDF = new JButton();
        OpenRDF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Open an RDF document.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        OpenRDF.setToolTipText("Open an RDF document");
        // The action of opening a URL when the user click "Open".
        OpenRDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new OpenRDF().run();
            }
        });
        OpenRDF.setIcon(new ImageIcon(Interface.class.getResource("/icons/rdf224.png")));
        toolBar.add(OpenRDF);
        toolBar.addSeparator();

        //ToolBar Item "Save RDF"
        saveRDF = new JButton(new ImageIcon(getClass().getResource("/icons/saveRDF24.png")));
        saveRDF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Save the RDF document.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        saveRDF.setEnabled(false);
        saveRDF.setToolTipText("Saving the RDF document");
        saveRDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(!saved) {
                    new SaveRDFXML(generalModel, Base.getBaseIRI()).run();
                }
            }
        });
        toolBar.add(saveRDF);

        //ToolBar Item "Save Log"
        JButton saveLog = new JButton(new ImageIcon(getClass().getResource("/icons/save24.png")));
        saveLog.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Saving the journal events.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        saveLog.setToolTipText("Saving the events of the journal");
        saveLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveLog().run();
            }
        });
        toolBar.add(saveLog);
        toolBar.addSeparator();

        //The button that starts the data extraction
        extractData = new JButton(new ImageIcon(getClass().getResource("/icons/process24.png")));
        extractData.setToolTipText("Start the data extraction process");
        extractData.setEnabled(false);
        extractData.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Extract the data from the database.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        extractData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        changeExtractDataIcon(new ImageIcon(getClass().getResource("/icons/loading24.gif")));
                        ExtractData data = new ExtractData(OpenDB.getDataBaseName());
                        data.execute();
                    }
                });
            }
        });
        toolBar.add(extractData);

        // The button that starts the conversion.
        extractRDF = new JButton(new ImageIcon(getClass().getResource("/icons/extraction24.png")));
        extractRDF.setEnabled(false);
        extractRDF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Start the RDF extraction process.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        extractRDF.setToolTipText("Start the RDF Extraction Process");
        extractRDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(extractTriples == null){
                    generalModel = ModelFactory.createDefaultModel();
                    new Base(generalModel);
                }else{
                    if(!extractTriples.isAlive() | extractTriples.getState().equals(Thread.State.RUNNABLE)){
                        extractTriples.interrupt();
                        setThreadNull();
                    }
                }
            }
        });
        toolBar.add(extractRDF);
        toolBar.addSeparator();

        JButton visualizeGraph = new JButton(new ImageIcon(getClass().getResource("/icons/graph24.png")));
        visualizeGraph.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Draw the RDF graph.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        visualizeGraph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (generalModel != null) {
                    if (com.html2rdf.functions.rdf.open.OpenRDF.isOpen()) {
                        GraphVisualization graphVisualization = new GraphVisualization(com.html2rdf.functions.rdf.open.OpenRDF.getModel());
                        graphVisualization.execute();
                    } else {
                        GraphVisualization graphVisualization = new GraphVisualization(generalModel);
                        graphVisualization.execute();
                    }
                } else {
                    JOptionPane.showMessageDialog(principal, "There is no graph to draw.", "Graph missing", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        toolBar.add(visualizeGraph);

        //ToolBar Item "statistics"
        JButton statistics = new JButton(new ImageIcon(getClass().getResource("/icons/statistics24.png")));
        statistics.setToolTipText("Show extraction statistics");
        statistics.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Show extraction statistics.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        statistics.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Statistics("Extraction Statistics", "Number of triples by predicate", generalModel);
                    }
                });
            }
        });
        toolBar.add(statistics);
        toolBar.addSeparator();

        //ToolBar Item "Exit the app"
        JButton aboutApp = new JButton(new ImageIcon(getClass().getResource("/icons/info24.png")));
        aboutApp.setToolTipText("About the application");
        aboutApp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" About the application.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        aboutApp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new About();
            }
        });
        toolBar.add(aboutApp);

        //ToolBar Item "Exit the app"
        JButton exitApp = new JButton(new ImageIcon(getClass().getResource("/icons/exit24.png")));
        exitApp.setToolTipText("Exit the application");
        exitApp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Exit the application.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        exitApp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                close();
            }
        });
        toolBar.add(exitApp);

        // Creating a panel that contains all our components.
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10,10));
        content.add(panel, BorderLayout.CENTER);

        // Creating a TabbedPane that contains our panelLink.
        JTabbedPane tabbedPaneLink = new JTabbedPane(JTabbedPane.TOP);
        Dimension d = new Dimension();
        d.width = 300;

        // Creating the JTree and adding the root item.
        FillTree fillTree = new FillTree();
        JTree tree = fillTree.createTree();
        JScrollPane scrollPaneLink = new JScrollPane(tree);

        // Creating the left panel that contains a JTree.
        JPanel panelLink = new JPanel();
        panelLink.setPreferredSize(d);
        panelLink.add(scrollPaneLink);
        panelLink.setLayout(new BoxLayout(panelLink, BoxLayout.X_AXIS));
        tabbedPaneLink.addTab("Opened Schemas", null, panelLink);

        JPanel tabbedPanePanel = new JPanel();

        //Creating the panel which will contain the extracted triples.
        extractedTriples = new JTextArea();
        JScrollPane extractedTriplesScrollPane = new JScrollPane(extractedTriples);

        // Creating a TabbedPane that contains our different Tabs.
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFocusable(true);

        // Creating the right panel that contains our Editor.
        JPanel panel_Tab1 = new JPanel();
        panel_Tab1.setLayout(new BoxLayout(panel_Tab1, BoxLayout.X_AXIS));

        // The container where the content of the files is shown.
        textArea = new RSyntaxTextArea();

        try {
            Theme theme = Theme.load(getClass().getResourceAsStream("/themes/dark.xml"));
            theme.apply(textArea);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Customizing the container to support syntax highlighting.
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setLineWrap(true);
        textArea.setAnimateBracketMatching(true);

        //Creating a Popup menu
        JPopupMenu popup = new JPopupMenu();

        // The "cut" popup menu item.
        menuItem = new JMenuItem("Cut", new ImageIcon(getClass().getResource("/icons/cut.png")));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        });
        popup.add(menuItem);

        // The "copy" popup menu item
        menuItem = new JMenuItem("Copy", new ImageIcon(getClass().getResource("/icons/copy.png")));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        });
        popup.add(menuItem);

        // The "Paste" popup menu item
        menuItem = new JMenuItem("Paste", new ImageIcon(getClass().getResource("/icons/paste.png")));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        });
        popup.add(menuItem);
        popup.addSeparator();

        // The "SelectAll" popup menu item
        menuItem = new JMenuItem("Select All");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.selectAll();
            }
        });
        popup.add(menuItem);

        textArea.setPopupMenu(popup);

        // Adding a scrollPane so you can scroll inside the JEditorPane.
        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setFoldIndicatorEnabled(true);

        panel_Tab1.add(scrollPane);
        tabbedPane.addTab("Event's journal", null, panel_Tab1);

        tabbedPanePanel.setLayout(new BorderLayout());
        tabbedPanePanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel containCollapsible = new JPanel();
        Dimension dimen = new Dimension();
        dimen.height = 200;
        containCollapsible.setPreferredSize(dimen);
        containCollapsible.setLayout(new BorderLayout());
        containCollapsible.setMinimumSize(dimen);
        containCollapsible.setBorder(new TitledBorder(null, "Extracted Triples : ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        containCollapsible.add(extractedTriplesScrollPane, BorderLayout.CENTER);

        JSplitPane containBoth = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPanePanel, containCollapsible);
        containBoth.setDividerLocation(350);
        containBoth.setOneTouchExpandable(true);

        //Creating a Split Pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPaneLink, containBoth);
        splitPane.setDividerLocation(305);
        splitPane.setOneTouchExpandable(true);
        tabbedPaneLink.setMinimumSize(d);
        panel.add(splitPane);

        // Letting a space between the components [Editor & The principal interface].
        Component rigidArea_2 = Box.createRigidArea(new Dimension(5, 226));
        content.add(rigidArea_2, BorderLayout.EAST);

        // This panel will contain some statistics.
        JPanel panel_1 = new JPanel(new MigLayout());
        //panel_1.setLayout(null);
        d.height = 40;
        panel_1.setPreferredSize(d);
        //panel.add(panel_1, BorderLayout.SOUTH);

        // Creating the status bar
        JXStatusBar statusBar = new JXStatusBar();
        statusBar.setBorder(new EmptyBorder(3, 0, 3, 2));
        statusBar.setPreferredSize(new Dimension(principal.getWidth(), 25));
        principal.setStatusBar(statusBar);

        JXStatusBar.Constraint c1 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
        statusBar.add(statusLabel, c1);

        //Creating a JProgressbar.
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(120, 5));
        progressBar.setBorder(new EmptyBorder(1, 0, 1, 0));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED, new InsetsUIResource(2, 0, 0, 2));
        c2.setFixedWidth(150);
        statusBar.add(progressBar, c2);

        //The memory usage indicator
        MemoryUsage memoryUsage = new MemoryUsage();
        memoryUsage.setVisible(true);
        memoryUsage.setBorder(new EmptyBorder(1, 0, 1, 0));
        memoryUsage.setPreferredSize(new Dimension(100, 5));
        JXStatusBar.Constraint c4 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED, new InsetsUIResource(2, 0, 0, 2));
        c4.setFixedWidth(100);
        statusBar.add(memoryUsage, c4);

        // The timer
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        final JLabel clock = new JLabel(dateFormat.format(now.getTime()));
        clock.setPreferredSize(new Dimension(115, 15));
        clock.setVerticalTextPosition(SwingConstants.CENTER);
        clock.setHorizontalTextPosition(SwingConstants.CENTER);
        JXStatusBar.Constraint c3 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED, new InsetsUIResource(2, 0, 0, 2));
        c3.setFixedWidth(105);
        statusBar.add(clock, c3);

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar now = Calendar.getInstance();
                clock.setText(dateFormat.format(now.getTime()));
            }
        }).start();

        // Letting a space between the components [Conversion button & Editor(Log)].
        Component rigidArea_3 = Box.createRigidArea(new Dimension(30, 15));
        panel_1.add(rigidArea_3);

        // Letting a space between the components [Tree & Principal interface].
        Component rigidArea_1 = Box.createRigidArea(new Dimension(4, 226));
        content.add(rigidArea_1, BorderLayout.WEST);

        // A JMenuBar that contains our menus.
        JMenuBar menuBar = new JMenuBar();
        principal.setJMenuBar(menuBar);

        // The Menu File.
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        // Menu item "Open a database".
        JMenuItem itemOpen = new JMenuItem("Open a Database...", new ImageIcon(getClass().getResource("/icons/database16.png")));
        itemOpen.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Open a database.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Open a file" is fired.
        itemOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new OpenDB();
            }
        });
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK));
        menuFile.add(itemOpen);

        // Menu item "Open an hTML  file".
        JMenuItem itemOpenHTML = new JMenuItem("Open an HTML file...", new ImageIcon(getClass().getResource("/icons/html16.png")));
        itemOpenHTML.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Open an HTML file.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Open a file" is fired.
        itemOpenHTML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ParseHTMLFile htmlFile=  new ParseHTMLFile();
                htmlFile.run();
            }
        });
        itemOpenHTML.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        menuFile.add(itemOpenHTML);

        // Menu item "Open an RDF document".
        JMenuItem openRDF = new JMenuItem("Open an RDF document...", new ImageIcon(getClass().getResource("/icons/rdf216.png")));
        openRDF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Open an RDF document.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        openRDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OpenRDF().run();
            }
        });
        openRDF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menuFile.add(openRDF);
        menuFile.addSeparator();

        //Menu Item "Save RDF"
        itemSaveRDF = new JMenuItem("Save...", new ImageIcon(getClass().getResource("/icons/saveRDF16.png")));
        itemSaveRDF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Save the RDF triples.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemSaveRDF.setEnabled(false);
        itemSaveRDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveRDFXML(generalModel, Base.getBaseIRI()).run();
                /*file = new FileOutputStream("C:\\Users\\AHMED\\Desktop\\file.n3");
                generalModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                generalModel.write(file, "N3-TRIPLE", Base.getBaseIRI());
                System.out.println("Written");         */
            }
        });
        itemSaveRDF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuFile.add(itemSaveRDF);

        //Menu Item "Save Log"
        JMenuItem itemSavelog = new JMenuItem("Save Log...", new ImageIcon(getClass().getResource("/icons/save16.png")));
        itemSavelog.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Save the journal events.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemSavelog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveLog();
            }
        });
        itemSavelog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK));
        menuFile.add(itemSavelog);

        //Menu Item "Export"
        JMenu export = new JMenu("Export");

        //Menu Item "Export as Turtle format"
        itemTurtle = new JMenuItem("Turtle", new ImageIcon(getClass().getResource("/icons/turtle16.png")));
        itemTurtle.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as Turtle format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemTurtle.setEnabled(false);
        itemTurtle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveRDFTURTLE(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemTurtle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        export.add(itemTurtle);

        //Menu Item containing the different RDF/XML formats
        JMenu itemXML = new JMenu("RDF/XML");
        itemXML.setEnabled(true);
        export.add(itemXML);

        // Menu item "Export as RDF-XML"
        itemXMLNormal = new JMenuItem("RDF/XML", new ImageIcon(getClass().getResource("/icons/xml16.png")));
        itemXMLNormal.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as RDF/XML format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemXMLNormal.setEnabled(false);
        itemXMLNormal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SaveRDFXML(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemXML.add(itemXMLNormal);

        // Menu item "Export as RDF/XML-Abr"
        itemXMLAbr = new JMenuItem("RDF/XML-Abbreviated", new ImageIcon(getClass().getResource("/icons/xmlAbr.png")));
        itemXMLAbr.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as RDF/XML-Abbreviated format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemXMLAbr.setEnabled(false);
        itemXMLAbr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SaveRDFXMLABR(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemXML.add(itemXMLAbr);

        //Menu Item "Export as N-Triples format"
        itemNTriples = new JMenuItem("N-Triples", new ImageIcon(getClass().getResource("/icons/n-triple16.png")));
        itemNTriples.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as N-Triples format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemNTriples.setEnabled(false);
        itemNTriples.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveRDFNTRIPLES(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemNTriples.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        export.add(itemNTriples);

        //Menu Item containing N3 formats
        JMenu itemN3 = new JMenu("N3");
        export.add(itemN3);

        //Menu Item "Export as N3-PP format"
        itemN3PP = new JMenuItem("N3-PP", new ImageIcon(getClass().getResource("/icons/n3pp.png")));
        itemN3PP.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as N3-PP format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemN3PP.setEnabled(false);
        itemN3PP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveRDFN3PP(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemN3.add(itemN3PP);

        //Menu Item "Export as N3-PLAIN format"
        itemN3PLAIN = new JMenuItem("N3-PLAIN", new ImageIcon(getClass().getResource("/icons/n3plain.png")));
        itemN3PLAIN.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as N3-PLAIN format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemN3PLAIN.setEnabled(false);
        itemN3PLAIN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveRDFN3PLAIN(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemN3.add(itemN3PLAIN);

        //Menu Item "Export as N3-TRIPLE format"
        itemN3TRIPLE = new JMenuItem("N3-TRIPLE", new ImageIcon(getClass().getResource("/icons/n3triple.png")));
        itemN3TRIPLE.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Export the RDF triples as N3-TRIPLE format.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemN3TRIPLE.setEnabled(false);
        itemN3TRIPLE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SaveRDFN3TRIPLE(generalModel, Base.getBaseIRI()).run();
            }
        });
        itemN3.add(itemN3TRIPLE);

        menuFile.add(export);
        menuFile.addSeparator();


        // Menu item "Exit".
        JMenuItem itemExit = new JMenuItem("Exit", new ImageIcon(getClass().getResource("/icons/exit.png")));
        itemExit.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Exit the application.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Exit" is fired.
        itemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                close();
            }
        });
        itemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        menuFile.add(itemExit);

        // The Menu "Edit".
        JMenu menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);

        // Menu item "Start Extraction".
        itemExtractData = new JMenuItem("Start Data Extraction", new ImageIcon(getClass().getResource("/icons/process16.png")));
        itemExtractData.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Start the extraction process.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemExtractData.setEnabled(false);
        itemExtractData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        changeExtractDataIcon(new ImageIcon(getClass().getResource("/icons/loading24.gif")));
                        ExtractData data = new ExtractData(OpenDB.getDataBaseName());
                        data.execute();
                    }
                });
            }
        });
        itemExtractData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        menuEdit.add(itemExtractData);

        // Menu item "Start Extraction".
        itemExtractRDF = new JMenuItem("Start Extraction", new ImageIcon(getClass().getResource("/icons/extraction16.png")));
        itemExtractRDF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Start the extraction process.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemExtractRDF.setEnabled(false);
        itemExtractRDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(extractTriples == null){
                    generalModel = ModelFactory.createDefaultModel();
                    new Base(generalModel);
                }else{
                    if(!extractTriples.isAlive() | extractTriples.getState().equals(Thread.State.RUNNABLE)){
                        extractTriples.interrupt();
                        setThreadNull();
                    }
                }
            }
        });
        itemExtractRDF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        menuEdit.add(itemExtractRDF);

        // Menu item "Start Extraction".
        itemDeleteTables = new JMenuItem("Delete Tables", new ImageIcon(getClass().getResource("/icons/extraction16.png")));
        itemDeleteTables.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Delete the opened tables.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemDeleteTables.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Component> tables = getTables(principal, new ArrayList<Component>());
                if (tables.size() > 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FillTree.deleteAllNodes();
                            } catch (InvocationTargetException e1) {
                                //
                            } catch (InterruptedException e1) {
                                //e1.printStackTrace();
                            }
                            Interface.deleteTabs();
                            Interface.updateTabbedPane();
                        }
                    });
                }
            }
        });
        menuEdit.add(itemDeleteTables);

        menuEdit.addSeparator();


        // Menu item "Visualize Graph"
        JMenuItem itemVisualizeGraph = new JMenuItem("Visualize the Graph", new ImageIcon(getClass().getResource("/icons/graph16.png")));
        itemVisualizeGraph.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Draw the RDF graph.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });

        itemVisualizeGraph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(generalModel != null){
                    if(com.html2rdf.functions.rdf.open.OpenRDF.isOpen()){
                        GraphVisualization graphVisualization = new GraphVisualization(com.html2rdf.functions.rdf.open.OpenRDF.getModel());
                        graphVisualization.execute();
                    }else{
                        GraphVisualization graphVisualization = new GraphVisualization(generalModel);
                        graphVisualization.execute();
                    }
                }else{
                    JOptionPane.showMessageDialog(principal, "There is no graph to draw.", "Graph missing", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        itemVisualizeGraph.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuEdit.add(itemVisualizeGraph);

        //MenuItem "Statistics"
        itemStatistics = new JMenuItem("Show extraction statistics", new ImageIcon(getClass().getResource("/icons/statistics16.png")));
        itemStatistics.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Show extraction statistics.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemStatistics.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Statistics("Extraction Statistics", "Number of triples by predicate", generalModel);
                    }
                });
            }
        });
        menuEdit.add(itemStatistics);
        menuEdit.addSeparator();

        // Menu item "Cut".
        JMenuItem itemCut = new JMenuItem("Cut", new ImageIcon(getClass().getResource("/icons/cut.png")));
        itemCut.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Cut the journal events content.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Cut" is fired.
        itemCut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        });
        itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuEdit.add(itemCut);

        // Menu item "Copy".
        JMenuItem itemCopy = new JMenuItem("Copy", new ImageIcon(getClass().getResource("/icons/copy.png")));
        itemCopy.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Copy the journal events content.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Copy" is fired.
        itemCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        });
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuEdit.add(itemCopy);

        // Menu item "Paste".
        System.out.println(getClass().getResource("/icons/paste.png"));
        JMenuItem itemPaste = new JMenuItem("Paste", new ImageIcon(getClass().getResource("/icons/paste.png")));
        itemPaste.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Paste the clipboard content into the journal events.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Paste" is fired.
        itemPaste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        });
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        menuEdit.add(itemPaste);
        menuEdit.addSeparator();

        // Menu item "Select All"
        JMenuItem itemSelectAll = new JMenuItem("Select All");
        itemSelectAll.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" Select all the journal events content.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        // The action performed when the menu item "Paste" is fired.
        itemSelectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.selectAll();
            }
        });
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuEdit.add(itemSelectAll);

        JMenu menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);

        JMenuItem itemAbout = new JMenuItem("About...", new ImageIcon(getClass().getResource("/icons/info16.png")));
        itemAbout.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(" About the application.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(" Ready.");
            }
        });
        itemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new About();
            }
        });
        menuHelp.add(itemAbout);
        principal.setVisible(true);
    }

    public static String getContent(){
        return textArea.getText();
    }

    public static void emptySelection(){
        tabbedPane.setTitleAt(0, "Journal events");
    }

    public static void setProgressBarVisible(boolean visible){
        progressBar.setVisible(visible);
    }

    public static JTabbedPane getTabbedPane(){
        return tabbedPane;
    }

    private static void close(){
        int answer = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to exit the application?", "Exit the application", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else if (answer == JOptionPane.NO_OPTION) {
            return;
        };
    }

    public static void deleteTabs(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                int j = tabbedPane.getTabCount();
                if(j > 1){
                    while((j = tabbedPane.getTabCount())>1){
                        tabbedPane.removeTabAt(j-1);
                    }
                }
            }
        });
    }

    public static void updateTabbedPane(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                tabbedPane.updateUI();
            }
        });
    }

    public static ArrayList<Component> getTables(final Component comp, final ArrayList<Component>  list) {
        if(comp instanceof JTable){
            list.add(comp);
        }
        if (comp instanceof Container){
            Component[] comps = ((Container)comp).getComponents();
            for(int x = 0, y = comps.length; x < y; x++){
                getTables(comps[x], list);
            }
        }
        return list;
    }

    public static JXFrame getJXFrame(){
        return principal;
    }

    public static void changeImageAndToolTip(Icon icon24, Icon icon16, String tooltip){
        extractRDF.setIcon(icon24);
        extractRDF.setToolTipText(tooltip);
        itemExtractRDF.setIcon(icon16);
        itemExtractRDF.setText(tooltip);
    }

    public static void setItemsEnabled(){
        saveRDF.setEnabled(true);
        itemSaveRDF.setEnabled(true);
        itemXMLNormal.setEnabled(true);
        itemXMLAbr.setEnabled(true);
        itemN3PLAIN.setEnabled(true);
        itemN3PP.setEnabled(true);
        itemN3TRIPLE.setEnabled(true);
        itemNTriples.setEnabled(true);
        itemTurtle.setEnabled(true);
    }

    public static void setExtractDataEnabled(Boolean enabled){
        extractData.setEnabled(enabled);
        itemExtractData.setEnabled(enabled);
    }

    public static void changeExtractDataIcon(ImageIcon icon){
        extractData.setIcon(icon);
    }

    public static void setExtractRDFEnabled(boolean enabled){
        extractRDF.setEnabled(enabled);
        itemExtractRDF.setEnabled(enabled);
    }

    public static JButton getPosition(){
        return extractRDF;
    }

    public static void showTriple(final String statement){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                extractedTriples.append(statement.toString());
            }
        });
    }

    public static void emptyTriples(){
        extractedTriples.setText("");
    }

    public static String getExtractedTriplesContent(){
        return extractedTriples.getText();
    }

    public static void setThreadNull(){
        extractTriples = null;
    }

    public static void setThread(Thread thread){
        extractTriples = thread;
    }

    public static void setSaved(boolean done){
        saved = done;
    }
    public static void setSaveRDF(boolean enabled){
        saveRDF.setEnabled(enabled);
    }

}
