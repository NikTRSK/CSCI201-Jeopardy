package client;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class FactoryClientGUI extends JFrame {
	
	public static final long serialVersionUID = 1;
	
	private FactoryPanel factoryPanel;
	private FactoryManager factoryManager;

	private JTextArea messageTextArea;
	private JTable productTable;
	private DefaultTableModel productTableModel;
	private JScrollPane tableScrollPane;
	private JSlider simulationSpeedController;
	
	FactoryClientGUI(Socket socket){
		super(Constants.factoryGUITitleString);
		factoryManager = new FactoryManager();
		initializeVariables();
		createGUI();
		new FactoryClientListener(factoryManager, this, socket);
		addActionAdapters();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void initializeVariables() {
		messageTextArea = new JTextArea(10, 50);
		factoryPanel = new FactoryPanel();
		factoryManager.setFactoryRenderer(factoryPanel);
		
		productTableModel = new DefaultTableModel(null, Constants.tableColumnNames);
		productTable = new JTable(productTableModel);
		productTable.setEnabled(false);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(productTableModel);
		productTable.setRowSorter(sorter);
		tableScrollPane = new JScrollPane(productTable);
		tableScrollPane.setBounds(Constants.factoryGUIwidth - Constants.tableWidth - 10, 0, Constants.tableWidth, Constants.factoryGUIheight - 100);
		
		simulationSpeedController = new JSlider(JSlider.HORIZONTAL,Constants.simulation_0x, Constants.simulation_3x, Constants.simulation_1x);
		simulationSpeedController.addChangeListener(factoryManager);
		simulationSpeedController.setMajorTickSpacing(1);
		simulationSpeedController.setMinorTickSpacing(1);
		simulationSpeedController.setPaintTicks(true);
	}
	
	private void createGUI() {
		setSize(Constants.factoryGUIwidth, Constants.factoryGUIheight);
		setLayout(new BorderLayout());
		JScrollPane messageTextAreaScrollPane = new JScrollPane(messageTextArea);
		
		Box bottomBox = Box.createHorizontalBox();
		bottomBox.add(messageTextAreaScrollPane);
		bottomBox.add(simulationSpeedController);
		
		add(factoryPanel,BorderLayout.CENTER);
		add(bottomBox, BorderLayout.SOUTH);
		add(tableScrollPane,BorderLayout.EAST);
	}
	
	public JTable getTable() {
		return productTable;
	}
	
	private void addActionAdapters() {
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  System.exit(0);
			  }
		});
	}
	
	public void addMessage(String msg) {
		if (messageTextArea.getText().length() != 0) {
			messageTextArea.append("\n");
		}
		messageTextArea.append(msg);
	}
	
}
