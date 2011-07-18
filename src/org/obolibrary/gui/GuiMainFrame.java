package org.obolibrary.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;

import org.obolibrary.cli.OBORunnerConfiguration;
import org.obolibrary.cli.OBORunnerConfiguration.Variable;
import org.obolibrary.gui.GuiTools.SizedJPanel;

/**
 * GUI main frame, calls all constructors for the sub components.
 */
public class GuiMainFrame extends JFrame {

	// generated
	private static final long serialVersionUID = 8368019495775583185L;
	
	private JPanel allPanel;
	private GuiMainPanel mainPanel;
	private GuiAdvancedPanel advancedPanel;
	private GuiLogPanel logPanel;
	private final BlockingQueue<String> logQueue;
	private final OBORunnerConfiguration config;

	private JTabbedPane tabbedPane;
	
	/**
	 * Default constructor, required only for testing the GUI as bean.
	 */
	public GuiMainFrame() {
		this(new ArrayBlockingQueue<String>(100), new OBORunnerConfiguration());
	}
	
	/**
	 * Main constructor. 
	 * 
	 * @param logQueue Message queue for events to be shown in the log panel
	 * @param config Default configuration, may contain parsed parameters from the command line.
	 */
	public GuiMainFrame(BlockingQueue<String> logQueue, OBORunnerConfiguration config) {
		super();
		this.logQueue = logQueue;
		this.config = config;
		initialize();
	}

	private void initialize() {
		this.setSize(800, 500);
		// put the all panel in a scrollpane
		this.setContentPane(new JScrollPane(getAllPanel()));
		this.setTitle("OBOFormatConverter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Retrieve all panel, create new if it not exists.
	 * 
	 * @return all panel
	 */
	private JPanel getAllPanel() {
		if (allPanel == null) {
			// use tabs to structure the the three panels
			allPanel = new JPanel(new BorderLayout(10, 10));
			JTabbedPane tabbedPane = getTabbedPane();
			allPanel.add(tabbedPane, BorderLayout.CENTER);
			JPanel controlPanel = createControlPanel();
			allPanel.add(controlPanel, BorderLayout.PAGE_END);
			
			
		}
		return allPanel;
	}
	
	/**
	 * Retrieve tabbed pane, create new if it not exists.
	 * 
	 * @return tabbed pane
	 */
	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			addTab(tabbedPane, "Input/Output", getMainPanel());
			addTab(tabbedPane, "Advanced", getAdvancedPanel());
			addTab(tabbedPane, "Logs", getLogPanel());
		}
		return tabbedPane;
	}
	
	private void addTab(JTabbedPane tabbedPane, String title, SizedJPanel panel) {
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab(title, panel);
	}
	
	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
		JButton button = new JButton("Run Conversion");
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// update config
				// continue only if the return value is true
				if(updateConfigurationFromGUI(config)) {
					// switch to log tab
					tabbedPane.setSelectedComponent(logPanel);
					// do work
					executeConversion(config);
				}
			}
		});
		panel.add(button, BorderLayout.LINE_END);
		return panel;
	}

	/**
	 * Update the config values by reading the fields and states from the GUI.
	 * 
	 * @param config 
	 */
	private boolean updateConfigurationFromGUI(OBORunnerConfiguration config) {
		// config from main panel
		// path
		ListModel inputFileModel = mainPanel.inputFileJList.getModel();
		for (int i = 0; i < inputFileModel.getSize(); i++) {
			config.paths.setValue(inputFileModel.getElementAt(i).toString());
		}
		
		// outFile
		config.outFile.setValue(mainPanel.outputFileTextField.getText());
		// outputdir
		config.outputdir.setValue(mainPanel.outputFolderTextField.getText());
		
		if (config.outFile.isEmpty() && config.outputdir.isEmpty()) {
			renderInputError("Configuration error. Please specify at least one fo the following in the main panel: Output Folder OR Output File");
			return false;
		}
		
		// isOboToOwl
		config.isOboToOwl.setRealValue(mainPanel.obo2owlButton.isSelected());
	
		// config from advanced panel
		
		// defaultOnt
		config.defaultOnt.setValue(advancedPanel.defaultOntologyField.getText());
		
		// format (owlxml, manchester, rdf)
		if (advancedPanel.formatOWLXMLButton.isSelected()) {
			config.format.setValue("owlxml");
		}
		else if (advancedPanel.formatManchesterButton.isSelected()) {
			config.format.setValue("manchester");
		}
		else {
			config.format.setValue("RDF");
		}
		
		// version    owl versions
		config.version.setValue(advancedPanel.owlOntologyVersion.getText());
		
		// allowDangling
		config.allowDangling.setRealValue(advancedPanel.danglingCheckbox.isSelected());
		
		//followimports
		config.followImports.setRealValue(advancedPanel.followImportsCheckBox.isSelected());
		
		//strickconversion
		config.strictConversion.setRealValue(advancedPanel.strictCheckBox.isSelected());
		
		// expand Macros
		config.isExpandMacros.setRealValue(advancedPanel.expandMacrosCheckbox.isSelected());
		
		// ontsToDownloads
		if (advancedPanel.downloadOntologiesCheckBox.isSelected()) {
			List<String> strings = GuiTools.getStrings(advancedPanel.downloadOntologies);
			for (String string : strings) {
				config.ontsToDownload.setValue(string);
			}
			// buildDir -> temp dir for download
			String buildDir = advancedPanel.ontologyDownloadFolderField.getText();
			if (strings.size() > 0 && (buildDir == null || buildDir.length() <= 0)) {
				renderInputError("Configuration error. Please specify also a download directory.");
				return false;
			}
			else {
				config.buildDir.setValue(buildDir);
			}
		}
		
		// omitOntsToDownload
		if (advancedPanel.omitDownloadOntologiesCheckBox.isSelected()) {
			List<String> strings = GuiTools.getStrings(advancedPanel.omitDownloadOntologies);
			for (String string : strings) {
				config.omitOntsToDownload.setValue(string);
		    }
		}
		return true;
	}
	
	private void renderInputError(String message) {
		JOptionPane.showMessageDialog(GuiMainFrame.this, message);
	}

	/**
	 * Execute the actual conversion. 
	 * Override this method to implement the call to the conversion methods.
	 * 
	 * @param config
	 */
	protected void executeConversion(OBORunnerConfiguration config) {
		// for tests print all config variables
		Iterable<Variable<?>> variables = config.getVariables();
		StringBuilder sb = new StringBuilder("-------------------------\n");
		for (Variable<?> variable : variables) {
			sb.append(variable.getName());
			sb.append(":   ");
			sb.append(variable.getValue());
			sb.append('\n');
		}
		logQueue.add(sb.toString());
	}
	
	/**
	 * Retrieve main panel, create new if it not exists.
	 * 
	 * @return main panel
	 */
	private SizedJPanel getMainPanel()
	{
		if (mainPanel == null) {
			mainPanel = new GuiMainPanel(this, config.paths.getValue(), config.outputdir.getValue(), config.outFile.getValue(), config.isOboToOwl.getValue());
		}
		return mainPanel;
	}

	/**
	 * Retrieve advanced panel, create new if it not exists.
	 * 
	 * @return advanced panel
	 */
	private SizedJPanel getAdvancedPanel()
	{
		if (advancedPanel == null) {
			advancedPanel = new GuiAdvancedPanel(this, 
					config.allowDangling.getValue(),
					config.isExpandMacros.getValue(),
					config.ontsToDownload.getValue(), 
					config.omitOntsToDownload.getValue(),
					config.defaultOnt.getValue(),
					config.buildDir.getValue(),
					config.version.getValue(),
					config.followImports.getValue(),
					config.strictConversion.getValue()
			);
		}
		return advancedPanel;
	}

	/**
	 * Retrieve log panel, create new if it not exists.
	 * 
	 * @return log panel
	 */
	private SizedJPanel getLogPanel()
	{
		if (logPanel == null) {
			logPanel = new GuiLogPanel(logQueue);
		}
		return logPanel;
	}
} 
