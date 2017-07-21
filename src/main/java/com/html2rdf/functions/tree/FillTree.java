package com.html2rdf.functions.tree;

import com.html2rdf.gui.Interface;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: AHMED
 * Date: 26/04/13
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class FillTree {

    private static DefaultTreeModel model;
    private static DefaultMutableTreeNode root, node;
    private static JTree tree;
    private static JMenuItem emptySelection;
    private static int numChild;

    public static JTree createTree(){
        root = new DefaultMutableTreeNode("Schema of :");

        model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.expandRow(0);

        //Create the popup for the tree.
        JPopupMenu popup = new JPopupMenu();
        popup.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setEnabledNodes(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                numChild = root.getChildCount();
                if ((numChild != 0)){
                    setEnabledNodes(true);
                }else{
                    setEnabledNodes(false);
                }
            }
        });



        // The "Clear selection" popup menu item
        emptySelection = new JMenuItem("Clear selection", new ImageIcon(ToolTipManager.class.getResource("/icons/emptySelection.png")));
        emptySelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        emptySelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                emptySelection();
            }
        });
        emptySelection.setEnabled(false);
        popup.add(emptySelection);


        tree.setComponentPopupMenu(popup);
        tree.setShowsRootHandles(true);
        tree.putClientProperty("JTree.lineStyle", "Horizontal");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    setEnabledNodes(false);
                    return;
                }

                Object path = node.getUserObject();

                if(path.toString().isEmpty()){
                    return;
                }else {
                    if(!node.equals(root)){
                        if (Interface.getTabbedPane().getTabCount()>1 & node.getParent().equals(root) & path.toString().contains("Table")) {
                            setEnabledNodes(true);
                            Thread showShoosedNode = new Thread(new ShowSelection(Interface.getTabbedPane(), root.getIndex(node)+1));
                            showShoosedNode.start();
                        } else {
                            setEnabledNodes(true);
                            return;
                        }
                    }
                }
            }
        });
        return tree;
    }

    public static void addNode(final DefaultMutableTreeNode firstNode, final DefaultMutableTreeNode parentNode){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                parentNode.add(firstNode);
                model.insertNodeInto(firstNode, parentNode, parentNode.getChildCount()-1);
            }
        });
    };

    public static void addToRoot(final DefaultMutableTreeNode firstNode){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                root.add(firstNode);
                model.insertNodeInto(firstNode, root, root.getChildCount()-1);
            }
        });
        //return root;
    };

    public static void emptySelection(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                tree.clearSelection();
                Interface.getTabbedPane().setSelectedIndex(0);
            }
        });
    }

    public static void setEnabledNodes(boolean empty){
        if(empty){
            emptySelection.setEnabled(true);
        }else{
            emptySelection.setEnabled(false);
        }
    }

    public static void changeRootName(String databaseName){
        root.setUserObject("Schema of : " + databaseName);
    }

    public static void expandAll(){
        final TreePath treePath = new TreePath(root.getPath());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tree.expandPath(treePath);
                tree.fireTreeExpanded(treePath);
            }
        });
    }

    public static void deleteAllNodes() throws InvocationTargetException, InterruptedException {
        if(root.getChildCount() != 0){
            /*SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {

                }
            });       */
            root.removeAllChildren();
            tree.updateUI();
            emptySelection();
        }
    }

    public static DefaultMutableTreeNode getRoot(){
        return root;
    }
}
