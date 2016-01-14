package org.obolibrary.owl;

import org.semanticweb.owlapi.formats.LabelFunctionalDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLStorer;
import org.semanticweb.owlapi.util.OWLStorerFactoryImpl;

public class LabelFunctionalSyntaxStorerFactory extends OWLStorerFactoryImpl {

	// generated
	private static final long serialVersionUID = 8497423548162171219L;

	public LabelFunctionalSyntaxStorerFactory() {
		super(new LabelFunctionalDocumentFormatFactory());
	}

	@Override
	public OWLStorer createStorer() {
		return new LabelFunctionalSyntaxStorer();
	}

}
