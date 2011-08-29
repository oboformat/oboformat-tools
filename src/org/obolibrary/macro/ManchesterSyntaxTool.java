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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;


/**
 * wrapper for parsing Manchester Syntax
 * 
 * @author heiko
 *
 */
class ManchesterSyntaxTool {

	private static final Logger log = Logger.getLogger(ManchesterSyntaxTool.class);
	private static final boolean DEBUG = log.isDebugEnabled();
	
	private final OWLDataFactory dataFactory;
	private OWLEntityChecker entityChecker;

	ManchesterSyntaxTool(OWLDataFactory dataFactory, OWLOntologyManager manager, OWLOntology inputOntology) {
		super();
		this.dataFactory = dataFactory;
		entityChecker = new ShortFormEntityChecker(
                new BidirectionalShortFormProviderAdapter(
                        manager,
                        Collections.singleton(inputOntology),
                        new SimpleShortFormProvider()));
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
	 * TODO: document behavior
	 * 
	 * @param iri
	 * @return
	 */
	static String getId(IRI iri){
		String iriString = iri.toString();
		String id = null;
		
		String s[] =iriString.split("#");
		
		if(s.length>1){
			id = s[1];
		}else{
			int index = iriString.lastIndexOf("/");
			id = iriString.substring(index+1);
		}
		
		return id;
		
	}
}
