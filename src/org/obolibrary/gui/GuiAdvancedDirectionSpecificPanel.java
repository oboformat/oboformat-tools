package org.obolibrary.gui;

import static org.obolibrary.gui.GuiTools.addRowGap;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.obolibrary.gui.GuiTools.GBHelper;
import org.obolibrary.gui.GuiTools.SizedJPanel;

/**
 * GUI elements for the advanced settings of the converter
 */
public class GuiAdvancedDirectionSpecificPanel extends SizedJPanel {

	// generated
	private static final long serialVersionUID = -6706259947812407420L;
	
	final JCheckBox danglingCheckbox;
	final JCheckBox followImportsCheckBox;
	final JCheckBox expandMacrosCheckbox;
	final JLabel formatLabel;
	final JRadioButton formatRDFButton;
	final JRadioButton formatOWLXMLButton;
	final JRadioButton formatManchesterButton;

	/**
	 * Create GUI panel for advanced settings specific for either 
	 * conversion direction with the given default values.
	 * 
	 * @param allowDanglingDefault
	 * @param expandMacrosDefault
	 * @param followImports
	 * @param isObo2Owl
	 */
	public GuiAdvancedDirectionSpecificPanel(boolean allowDanglingDefault, 
			boolean expandMacrosDefault,
			boolean followImports, 
			boolean isObo2Owl)
	{
		super();
		danglingCheckbox = new JCheckBox("Allow Dangling", allowDanglingDefault);
		followImportsCheckBox = new JCheckBox("Follow Imports", followImports);
		expandMacrosCheckbox = new JCheckBox("Expand OWL Macros", expandMacrosDefault);
		formatRDFButton = new JRadioButton("RDF/XML", true);
		formatOWLXMLButton = new JRadioButton("OWL/XML");
		formatManchesterButton = new JRadioButton("Manchester");
		formatLabel = new JLabel("OWL Format");
		
		setLayout(new GridBagLayout());
		GBHelper pos = new GBHelper();
	
		//----------- OBO2OWL specific options
		add(new JLabel("OBO2OWL"), pos);
		addRowGap(this, pos, 10);
		
		// ontology format panel
		createOntologyFormatPanel(pos);
		addRowGap(this, pos, 5);

		// flag for allow dangling
		createDanglingPanel(pos);
		addRowGap(this, pos, 5);

		// flag for allow dangling
		createFollowImportsPanel(pos);
		addRowGap(this, pos, 20);
		
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setSize(new Dimension(-1, 1));
		add(separator, pos.nextRow().width(2).fill().expandW());
		addRowGap(this, pos, 10);
		
		// advanced label
		add(new JLabel("OWL2OBO"), pos.nextRow());
		addRowGap(this, pos, 10);
		
		// flag for expand macros
		createExpandMacros(pos);
		
		setObo2Owl(isObo2Owl);
	}

	/**
	 * Layout for the ontology format buttons.
	 * 
	 * @param pos
	 */
	private void createOntologyFormatPanel(GBHelper pos) {
		add(formatLabel, pos.nextRow().nextCol());
		ButtonGroup formatGroup = new ButtonGroup();
		formatGroup.add(formatRDFButton);
		formatGroup.add(formatOWLXMLButton);
		formatGroup.add(formatManchesterButton);
		JPanel formatPanel = new JPanel(new GridLayout(1,0));
		formatPanel.add(formatRDFButton);
		formatPanel.add(formatOWLXMLButton);
		formatPanel.add(formatManchesterButton);
		add(formatPanel, pos.nextRow().nextCol());
	}
	
	/**
	 * Layout for the dangling check box.
	 * 
	 * @param pos
	 */
	private void createDanglingPanel(GBHelper pos) {
		add(danglingCheckbox, pos.nextRow().nextCol());
	}

	/**
	 * Layout for the follow imports check box.
	 * 
	 * @param pos
	 */
	private void createFollowImportsPanel(GBHelper pos) {
		add(followImportsCheckBox, pos.nextRow().nextCol());
	}
	
	/**
	 * Layout for the expand macros check box.
	 * 
	 * @param pos
	 */
	private void createExpandMacros(GBHelper pos) {
		add(expandMacrosCheckbox, pos.nextRow().nextCol().expandH());
	}
	
	/**
	 * Set the advanced tab options to reflect 
	 * the selected conversion direction
	 * 
	 * @param isObo2Owl
	 */
	void setObo2Owl(boolean isObo2Owl) {
		//OBO2OWL
		this.formatLabel.setEnabled(isObo2Owl);
		this.formatRDFButton.setEnabled(isObo2Owl);
		this.formatOWLXMLButton.setEnabled(isObo2Owl);
		this.formatManchesterButton.setEnabled(isObo2Owl);
		this.danglingCheckbox.setEnabled(isObo2Owl);
		this.followImportsCheckBox.setEnabled(isObo2Owl);
		
		//OWL2OBO
		this.expandMacrosCheckbox.setEnabled(!isObo2Owl);
	}
}
