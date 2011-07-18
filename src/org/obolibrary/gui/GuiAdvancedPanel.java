package org.obolibrary.gui;

import static org.obolibrary.gui.GuiTools.addRowGap;
import static org.obolibrary.gui.GuiTools.createTextField;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.obolibrary.gui.GuiTools.GBHelper;
import org.obolibrary.gui.GuiTools.SizedJPanel;

/**
 * GUI elements for the advanced settings of the converter
 */
public class GuiAdvancedPanel extends SizedJPanel {

	// generated
	private static final long serialVersionUID = -1694788715411761694L;
	
	final Frame frame;
	final JCheckBox danglingCheckbox;
	final JCheckBox followImportsCheckBox;
	final JCheckBox expandMacrosCheckbox;
	final JTextArea downloadOntologies;
	final JTextArea omitDownloadOntologies;
	final JCheckBox downloadOntologiesCheckBox;
	final JCheckBox omitDownloadOntologiesCheckBox;
	final JTextField ontologyDownloadFolderField;
	final JTextField defaultOntologyField;
	final JRadioButton formatRDFButton;
	final JRadioButton formatOWLXMLButton;
	final JRadioButton formatManchesterButton;
	final JTextField owlOntologyVersion;
	final JCheckBox strictCheckBox;

	/**
	 * Create GUI panel for advanced settings with the given default values.
	 * 
	 * @param frame
	 * @param allowDanglingDefault
	 * @param expandMacrosDefault
	 * @param defaultDownloadOntologies
	 * @param defaultOmitDownloadOntologies
	 * @param defaultOntologyConfigValue
	 * @param defaultBuildDir
	 * @param defaultOwlOntologyVersion
	 */
	public GuiAdvancedPanel(Frame frame, boolean allowDanglingDefault, 
			boolean expandMacrosDefault,
			Collection<String> defaultDownloadOntologies, 
			Collection<String> defaultOmitDownloadOntologies,
			String defaultOntologyConfigValue, 
			String defaultBuildDir, 
			String defaultOwlOntologyVersion, boolean followImports, boolean strictConversion)
	{
		super();
		this.frame = frame;
		danglingCheckbox = new JCheckBox("Allow Dangling", allowDanglingDefault);
		
		followImportsCheckBox = new JCheckBox("Follow Imports", followImports);

		strictCheckBox = new JCheckBox("Strict Conversion", strictConversion);
		
		expandMacrosCheckbox = new JCheckBox("Expand OWL Macros", expandMacrosDefault);
		
		// create Field for downloadOntologies
		// if values are available set it and activate the field
		downloadOntologies = new JTextArea(0, 1);
		boolean downloadOntologiesCheckBoxIsActive = false;
		if (defaultDownloadOntologies != null && !defaultDownloadOntologies.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String url : defaultDownloadOntologies) {
				sb.append(url);
				sb.append('\n');
			}
			downloadOntologies.setText(sb.toString());
			downloadOntologiesCheckBoxIsActive = true;
		}
		downloadOntologiesCheckBox = new JCheckBox("Download Ontologies", downloadOntologiesCheckBoxIsActive);
		
		// create Field for omitDownloadOntologies
		// if values are available set it and activate the field
		omitDownloadOntologies = new JTextArea(0, 1);
		boolean omitDownloadOntologiesCheckBoxIsActive = false;
		if (defaultOmitDownloadOntologies != null && !defaultOmitDownloadOntologies.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String url : defaultOmitDownloadOntologies) {
				sb.append(url);
				sb.append('\n');
			}
			omitDownloadOntologies.setText(sb.toString());
			omitDownloadOntologiesCheckBoxIsActive = true;
		}
		omitDownloadOntologiesCheckBox = new JCheckBox("Omit Download Ontologies", omitDownloadOntologiesCheckBoxIsActive);
		
		defaultOntologyField = createTextField(defaultOntologyConfigValue);
		
		formatRDFButton = new JRadioButton("RDF/XML", true);
		formatOWLXMLButton = new JRadioButton("OWL/XML");
		formatManchesterButton = new JRadioButton("Manchester");
		
		ontologyDownloadFolderField = createTextField(defaultBuildDir);
		owlOntologyVersion = createTextField(defaultOwlOntologyVersion);
		
		setLayout(new GridBagLayout());
		GBHelper pos = new GBHelper();
	
		// advanced label
		add(new JLabel("ADVANCED"), pos);
		addRowGap(this, pos, 10);
		
		// ontology format panel
		createOntologyFormatPanel(pos);
		addRowGap(this, pos, 5);
		
		// owl version
		createOntologyVersionPanel(pos);
		addRowGap(this, pos, 5);
		
		// flag for allow dangling
		createDanglingPanel(pos);
		addRowGap(this, pos, 5);

		// flag for allow dangling
		createFollowImportsPanel(pos);
		addRowGap(this, pos, 5);
		
		createStrictPanel(pos);
		addRowGap(this, pos, 5);

		// flag for expand macros
		createExpandMacros(pos);
		addRowGap(this, pos, 5);
		
		// download ontologies
		createOntologiesPanel(pos, downloadOntologies, downloadOntologiesCheckBox);
		addRowGap(this, pos, 5);
		
		// work dir for downloading ontologies
		createDownloadDirPanel(pos);
		addRowGap(this, pos, 5);
		
		// omit download ontologies
		createOntologiesPanel(pos, omitDownloadOntologies, omitDownloadOntologiesCheckBox);
		addRowGap(this, pos, 5);
		
		// default ontology
		createDefaultOntologyPanel(pos);
		addRowGap(this, pos, 10);
	}

	
	/**
	 * Layout for the ontology version field
	 * 
	 * @param pos
	 */
	private void createOntologyVersionPanel(GBHelper pos) {
		add(new JLabel("OWL version"),pos.nextRow());
		add(owlOntologyVersion, pos.nextCol().expandW().fill());
	}

	/**
	 * Layout and events for the build dir
	 * 
	 * @param pos
	 */
	private void createDownloadDirPanel(GBHelper pos) {
		final SelectDialog dialog = SelectDialog.getFolderSelector(frame, "Work directory choose dialog"); 
		
		JButton selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				dialog.show();
				String selected = dialog.getSelectedCanonicalPath();
				if (selected != null) {
					ontologyDownloadFolderField.setText(selected);
				}
			}
		});
		
		add(new JLabel("Download Directory"), pos.nextRow());
		add(ontologyDownloadFolderField, pos.nextCol().expandW().fill());
		add(selectButton,pos.nextCol().indentLeft(10));
	}


	/**
	 * Layout for the ontology format buttons.
	 * 
	 * @param pos
	 */
	private void createOntologyFormatPanel(GBHelper pos) {
		add(new JLabel("Format"), pos.nextRow());
		ButtonGroup formatGroup = new ButtonGroup();
		formatGroup.add(formatRDFButton);
		formatGroup.add(formatOWLXMLButton);
		formatGroup.add(formatManchesterButton);
		JPanel formatPanel = new JPanel(new GridLayout(1,0));
		formatPanel.add(formatRDFButton);
		formatPanel.add(formatOWLXMLButton);
		formatPanel.add(formatManchesterButton);
		add(formatPanel, pos.nextCol());
	}
	
	
	/**
	 * Layout and events for the input of ontologies to be downloaded or omitted.
	 * 
	 * @param pos
	 * @param ontologyList
	 * @param checkBox
	 */
	private void createOntologiesPanel(GBHelper pos, final JTextArea ontologyList, final JCheckBox checkBox) {
		ontologyList.setEditable(checkBox.isSelected());
		ontologyList.setEnabled(checkBox.isSelected());
		final JScrollPane scrollPane = new JScrollPane(ontologyList);
		
		checkBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				boolean selected = checkBox.isSelected();
				ontologyList.setEditable(selected);
				ontologyList.setEnabled(selected);
			}
		});
		
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(checkBox,pos.nextRow());
		add(scrollPane, pos.nextCol().expandW().expandH().anchorCenter().fill().height(3));
		pos.nextRow();
		pos.nextRow();
	}
	
	/**
	 * Layout for the default ontology field.
	 * 
	 * @param pos
	 */
	private void createDefaultOntologyPanel(GBHelper pos) {
		add(new JLabel("DefaultOntology"),pos.nextRow());
		add(defaultOntologyField, pos.nextCol().expandW().fill());
	}

	/**
	 * Layout for the dangling check box.
	 * 
	 * @param pos
	 */
	private void createDanglingPanel(GBHelper pos) {
		add(danglingCheckbox, pos.nextRow());
	}

	private void createFollowImportsPanel(GBHelper pos) {
		add(followImportsCheckBox, pos.nextRow());
	}

	private void createStrictPanel(GBHelper pos) {
		add(strictCheckBox, pos.nextRow());
	}
	

	/**
	 * Layout for the expand macros check box.
	 * 
	 * @param pos
	 */
	private void createExpandMacros(GBHelper pos) {
		add(expandMacrosCheckbox, pos.nextRow());
	}
}
