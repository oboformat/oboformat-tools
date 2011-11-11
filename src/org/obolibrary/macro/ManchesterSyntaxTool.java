package org.obolibrary.macro;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;


/**
 * wrapper for parsing Manchester Syntax
 * 
 * @author heiko
 *
 */
class ManchesterSyntaxTool {

	private static final Logger log = Logger.getLogger(ManchesterSyntaxTool.class);
	private static final boolean DEBUG = log.isDebugEnabled();
	
	private final IRIShortFormProvider iriShortFormProvider;
	
	private final OWLDataFactory dataFactory;
	private OWLEntityChecker entityChecker;

	ManchesterSyntaxTool(OWLOntology inputOntology) {
		super();
		OWLOntologyManager manager = inputOntology.getOWLOntologyManager();
		this.dataFactory = manager.getOWLDataFactory();

		// re-use the same short form provider for translation and parsing 
		iriShortFormProvider = new SimpleIRIShortFormProvider();
		ShortFormProvider shortFormProvider = new ShortFormProvider() {

			public void dispose() {
				// do nothing
			}

			public String getShortForm(OWLEntity owlEntity) {
				return iriShortFormProvider.getShortForm(owlEntity.getIRI());
			}
		};
		
		entityChecker = new ShortFormEntityChecker(
                new BidirectionalShortFormProviderAdapter(
                        manager,
                        Collections.singleton(inputOntology),
                        shortFormProvider));
	}
	
	Set<OntologyAxiomPair> parseManchesterExpressionFrames(String expression) throws ParserException {
		
		ManchesterOWLSyntaxEditorParser parser = createParser(expression);
        Set<OntologyAxiomPair> set =  parser.parseFrames();
		return set;
	}

	OWLClassExpression parseManchesterExpression(String expression) throws ParserException {
		
		ManchesterOWLSyntaxEditorParser parser = createParser(expression);
		OWLClassExpression ce = parser.parseClassExpression();
		return ce;
	}
	
	
	private ManchesterOWLSyntaxEditorParser createParser(String expression) {
		ManchesterOWLSyntaxEditorParser parser = 
			new ManchesterOWLSyntaxEditorParser(dataFactory, expression);
	
	    parser.setOWLEntityChecker(entityChecker);
		
	    if(DEBUG)
	    	log.debug("parsing:"+expression);
		return parser;
	}

	/**
	 * Translate the {@link IRI} into the short form as expected by the parser.
	 * 
	 * @param iri
	 * @return short form
	 */
	String getId(IRI iri){
		return iriShortFormProvider.getShortForm(iri);
		
	}
}
