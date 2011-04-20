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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

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
	
	final JList inputFileJList;
	final JRadioButton obo2owlButton;
	final JRadioButton owl2oboButton;
	final JTextField outputFolderTextField;
	final JTextField outputFileTextField;

	// Can be replaced with FileNameExtensionFilter in JAVA6
	private final static FileFilter OBO_OWL_FILE_FILTER = new FileFilter() {
		
		private final Set<String> extensions = new HashSet<String>(Arrays.asList("obo","owl"));
		
		@Override
		public String getDescription() {
			return "OBO & OWL files";
		}
		
		@Override
		public boolean accept(File f) {
			if (f != null) {
	            if (f.isDirectory()) {
	                return true;
	            }
	            String fileName = f.getName();
	            int i = fileName.lastIndexOf('.');
	            if (i > 0 && i < fileName.length() - 1) {
	                String extension = fileName.substring(i + 1).toLowerCase();
	                return extensions.contains(extension);
	            }
	        }
	        return false;
		}
	};
	
	/**
	 * Constructor allows to build a panel with default values
	 * 
	 * @param defaultInputFiles
	 * @param defaultOutputFolder
	 * @param defaultOutputFileName
	 * @param obo2owl
	 */
	public GuiMainPanel(Iterable<String> defaultInputFiles, String defaultOutputFolder, String defaultOutputFileName, boolean obo2owl) {
		super();

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
		
		// use file chooser dialog for select input files
		final JFileChooser fc = new JFileChooser();
		String defaultInputFolder = ".";
		if (lastAddedFile != null) {
			File latestFile = files.get(lastAddedFile);
			if (latestFile != null) {
				defaultInputFolder = latestFile.getParentFile().getAbsolutePath();
			}
		}
		fc.setCurrentDirectory(new File(defaultInputFolder));
		fc.setDialogTitle("Input ontology file choose dialog");
		fc.setFileFilter(OBO_OWL_FILE_FILTER);
		JScrollPane scrollPane = new JScrollPane(inputFileJList);
		inputFileJList.setPreferredSize(new Dimension(350, 60));
		
		JButton fileDialogAddButton = new JButton("Add");
		
		// add listener for adding a file to the list model
		fileDialogAddButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(GuiMainPanel.this);
	
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					File old = files.put(file.getAbsolutePath(), file);
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
		// file chooser for folders
		final JFileChooser folderFC = new JFileChooser();
		folderFC.setCurrentDirectory(new File(outputFolderTextField.getText()));
		folderFC.setDialogTitle("Work directory choose dialog");
		folderFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		folderFC.setAcceptAllFileFilterUsed(false);
		
		// file choosers for output file names
		final JFileChooser fileFC = new JFileChooser();
		fileFC.setFileFilter(OBO_OWL_FILE_FILTER);
		
		final JButton folderButton = new JButton("Select");
		final JButton fileButton = new JButton("Select");
		
		// add listener for selecting an output folder
		folderButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int returnVal = folderFC.showOpenDialog(GuiMainPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = folderFC.getSelectedFile();
					// set new folder and remove any previous file names
					setOutputNames(file, null);
				}
			}
		});
		
		// add listener for selecting an output file name
		fileButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				fileFC.setCurrentDirectory(folderFC.getSelectedFile());
				int returnVal = fileFC.showOpenDialog(GuiMainPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileFC.getSelectedFile();
					// if an output file is selected, use also its folder for the output.
					setOutputNames(file.getParentFile(), file.getName());
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
	
	private void setOutputNames(File folder, String name) {
		String absolutePath = folder.getAbsolutePath();
		outputFolderTextField.setText(absolutePath);
		outputFileTextField.setText(name == null ? "" : name);
	}

	private String lastAddedFile = null;

	/**
	 * Update the list model with a new list of files. 
	 * Keeps also the {@link #lastAddedFile} value up-to-date.
	 * 
	 * @param fileList
	 * @param files
	 */
	private void updateInputFileData(JList fileList, Map<String, File> files) {
		DefaultListModel listModel = new DefaultListModel();
		lastAddedFile = null;
		for (String name : files.keySet()) {
			listModel.addElement(name);
			lastAddedFile = name;
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
	}
}
