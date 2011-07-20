package org.obolibrary.gui;

import static org.obolibrary.gui.GuiTools.addRowGap;
import static org.obolibrary.gui.GuiTools.createTextField;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.obolibrary.gui.GuiTools.GBHelper;
import org.obolibrary.gui.GuiTools.SizedJPanel;

/**
 * GUI component for the minimum number of configurations required for the conversion.
 */
public class GuiMainPanel extends SizedJPanel {

	/// Generated
	private static final long serialVersionUID = 1281395185956670966L;

	private final static Logger LOGGER = Logger.getLogger(GuiMainPanel.class); 
	
	final GuiMainFrame frame;
	final JList inputFileJList;
	final JRadioButton obo2owlButton;
	final JRadioButton owl2oboButton;
	final JTextField outputFolderTextField;
	final JTextField outputFileTextField;

	/**
	 * Constructor allows to build a panel with default values
	 * 
	 * @param frame
	 * @param defaultInputFiles
	 * @param defaultOutputFolder
	 * @param defaultOutputFileName
	 * @param obo2owl
	 */
	public GuiMainPanel(GuiMainFrame frame, Iterable<String> defaultInputFiles, String defaultOutputFolder, String defaultOutputFileName, boolean obo2owl) {
		super();
		this.frame = frame;
		// create accessible fields
		// add default values to these fields
		
		// ontology file input files
		DefaultListModel inputFilesDataModel = new DefaultListModel();
		if (defaultInputFiles != null) {
			for (String inputFile : defaultInputFiles) {
				inputFilesDataModel.addElement(inputFile);
			}
		}
		inputFileJList = new JList(inputFilesDataModel);
		
		// conversion direction buttons
		obo2owlButton = new JRadioButton("OBO2OWL");
		owl2oboButton = new JRadioButton("OWL2OBO");
		
		// output folder and file
		if (".".equals(defaultOutputFolder)) {
			try {
				defaultOutputFolder  = new File(defaultOutputFolder).getCanonicalPath();
			} catch (IOException e) {
				LOGGER.debug("Problem converting path to canonical representation for path: "+defaultOutputFolder, e);
			}
		}
		outputFolderTextField = createTextField(defaultOutputFolder);
		outputFileTextField = createTextField(defaultOutputFileName);
		
		setLayout(new GridBagLayout());
		GBHelper pos = new GBHelper();
		
		// input panel
		createInputPanel(pos);
		addRowGap(this, pos, 10);
		
		// output panel
		createOutputPanel(pos);
		addRowGap(this, pos, 10);
		
		// direction
		createDirectionPanel(pos, obo2owl);
		addRowGap(this, pos, 10);
	}
	
	/**
	 * Create layout and listeners for the ontology input files to be converted.
	 * 
	 * @param pos
	 */
	private void createInputPanel(GBHelper pos) {
		// store the files in linked hash map, advantage: maintains insert order
		final LinkedHashMap<String, File> files = new LinkedHashMap<String, File>();
		
		
		final SelectDialog dialog = SelectDialog.getFileSelector(frame, "Input ontology file choose dialog", "OBO & OWL files", "obo","owl");
		// use file chooser dialog for select input files
		
		JScrollPane scrollPane = new JScrollPane(inputFileJList);
		inputFileJList.setPreferredSize(new Dimension(350, 60));
		
		JButton fileDialogAddButton = new JButton("Add");
		
		// add listener for adding a file to the list model
		fileDialogAddButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				dialog.show();
				String selected = dialog.getSelectedCanonicalPath();
				if (selected != null) {
					File file = new File(selected);
					File old = files.put(selected, file);
					// only update the model, if the file was not there before
					if (old == null) {
						updateInputFileData(inputFileJList, files);
					}
				}
			}
		});
		
		// add listener for removing files from the list model
		JButton fileRemoveButton = new JButton("Remove");
		fileRemoveButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Object[] values = inputFileJList.getSelectedValues();
				if (values != null && values.length > 0) {
					for (Object object : values) {
						files.remove(object);
					}
					updateInputFileData(inputFileJList, files);
				}
			}
		});
		
		add(new JLabel("Input"), pos);
		addRowGap(this, pos, 10);
		
		add(new JLabel("Ontology Files"), pos.nextRow());
		add(scrollPane, pos.nextCol().expandW().expandH().anchorCenter().height(4).fill());
		add(fileDialogAddButton, pos.nextCol().indentLeft(10));
		pos.nextRow();
		add(fileRemoveButton, pos.nextCol().nextCol().indentLeft(10));
		pos.nextRow();
		pos.nextRow();
	}

	/**
	 * Create layout and listener for the output fields.
	 * 
	 * @param pos
	 */
	private void createOutputPanel(GBHelper pos) {
		final SelectDialog folderDialog = SelectDialog.getFolderSelector(frame, "Work directory choose dialog");
		
		final SelectDialog fileDialog = SelectDialog.getFileSelector(frame, "Output ontology file choose dialog", "OBO & OWL files", "obo","owl");
		// file choosers for output file names
		
		final JButton folderButton = new JButton("Select");
		final JButton fileButton = new JButton("Select");
		
		// add listener for selecting an output folder
		folderButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				folderDialog.show();
				String selected = folderDialog.getSelectedCanonicalPath();
				if (selected != null) {
					// set new folder and remove any previous file names
					setOutputNames(selected, null);
				}
			}
		});
		
		// add listener for selecting an output file name
		fileButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				fileDialog.show();
				String selected = fileDialog.getSelectedCanonicalPath();
				if (selected != null) {
					// if an output file is selected, use also its folder for the output.
					File file = new File(selected);
					setOutputNames(file.getParentFile().getAbsolutePath(), selected);
				}
			}
		});
		
		add(new JLabel("Output"),pos.nextRow());
		addRowGap(this, pos, 10);
		
		add(new JLabel("Folder"),pos.nextRow());
		add(outputFolderTextField,pos.nextCol().expandW().anchorCenter().fill());
		add(folderButton, pos.nextCol().indentLeft(10));
		addRowGap(this, pos, 2);

		add(new JLabel("Filename"), pos.nextRow());
		add(outputFileTextField, pos.nextCol().expandW().anchorCenter().fill());
		add(fileButton, pos.nextCol().indentLeft(10));
	}
	
	private void setOutputNames(String folder, String name) {
		outputFolderTextField.setText(folder);
		outputFileTextField.setText(name == null ? "" : name);
	}

	/**
	 * Update the list model with a new list of files. 
	 * 
	 * @param fileList
	 * @param files
	 */
	private void updateInputFileData(JList fileList, Map<String, File> files) {
		DefaultListModel listModel = new DefaultListModel();
		for (String name : files.keySet()) {
			listModel.addElement(name);
		}
		fileList.setModel(listModel);
	}

	/**
	 * Create layout for ontology conversion buttons
	 * 
	 * @param pos
	 */
	private void createDirectionPanel(GBHelper pos, boolean isObo2Owl) {
		add(new JLabel("Direction"), pos.nextRow());
		ButtonGroup directionButtonGroup = new ButtonGroup();
		directionButtonGroup.add(obo2owlButton);
		directionButtonGroup.add(owl2oboButton);
		JPanel buttonPanel =  new JPanel(new GridLayout(1, 2));
		buttonPanel.add(obo2owlButton);
		buttonPanel.add(owl2oboButton);
		add(buttonPanel, pos.nextCol().expandW().anchorCenter());
		
		// set default value for buttons
		obo2owlButton.setSelected(isObo2Owl);
		obo2owlButton.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				GuiAdvancedDirectionSpecificPanel panel = frame.getSpecificAdvancedPanel();
				panel.setObo2Owl(obo2owlButton.isSelected());
			}
		});
	}
}
