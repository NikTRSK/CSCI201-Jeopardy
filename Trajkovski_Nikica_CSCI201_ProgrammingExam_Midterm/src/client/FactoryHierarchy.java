package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import resource.Factory;
import resource.Product;
import resource.Resource;

public class FactoryHierarchy extends JFrame implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Factory mFactory;
	
	JButton refreshBtn;
	JTree productTree, resourceTree;
	DefaultMutableTreeNode resourceRoot, productRoot; 
	
	FactoryHierarchy(Factory inFactory) {
		super("Factory Hierarchy");
		
		mFactory = inFactory;
		
		addKeyListener(this);
		initComponents();
		createGUI();
		addEvents();
	}

	private void addEvents() {
		// TODO Auto-generated method stub
		refreshBtn.addActionListener((ActionEvent e) -> {
			resourceRoot.setUserObject("Resources");
			resourceTree.setRootVisible(true);
			for (Resource resource : mFactory.getResources()) {
				DefaultMutableTreeNode r = new DefaultMutableTreeNode(resource.getName());
				resourceRoot.add(r);
			}
			resourceTree.expandRow(0);
			productRoot.setUserObject("Products");
			for (Product product : mFactory.getProducts()) {
				System.out.println(product.getName());
				DefaultMutableTreeNode p = new DefaultMutableTreeNode(product.getName());
				productRoot.add(p);
			}
			productTree.expandRow(0);
		});
		
	}

	private void createGUI() {
		// TODO Auto-generated method stub
		setSize(400,380);
		setBackground(Color.ORANGE);
		
		JPanel treePanel = new JPanel(new GridBagLayout());
		treePanel.setBackground(Color.orange);
//		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.X_AXIS));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,10);
		treePanel.add(resourceTree, gbc);
		treePanel.add(productTree);
		
		refreshBtn.setMnemonic('r');
		add(treePanel, BorderLayout.CENTER);
		add(refreshBtn, BorderLayout.SOUTH);
		
		
		
		
		setVisible(true);
	}

	private void initComponents() {
		refreshBtn = new JButton("Refresh");
		
		
		productRoot = new DefaultMutableTreeNode("Click Refresh");
		productTree = new JTree(productRoot);
		productTree.setPreferredSize(new Dimension(100, 200));
		
		
		resourceRoot = new DefaultMutableTreeNode("Click Refresh");
		resourceTree = new JTree(resourceRoot);
		resourceTree.setPreferredSize(new Dimension(100, 200));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_R) {
			// TODO Auto-generated method stub
			resourceRoot.setUserObject("Resources");
			resourceTree.setRootVisible(true);
			for (Resource resource : mFactory.getResources()) {
				DefaultMutableTreeNode r = new DefaultMutableTreeNode(resource.getName());
				resourceRoot.add(r);
			}
			resourceTree.expandRow(0);
			productRoot.setUserObject("Products");
			for (Product product : mFactory.getProducts()) {
				System.out.println(product.getName());
				DefaultMutableTreeNode p = new DefaultMutableTreeNode(product.getName());
				productRoot.add(p);
			}
			productTree.expandRow(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
