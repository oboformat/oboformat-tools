package org.obolibrary.macro;

import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.OWLExpressionParser;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;

/**
 * @author cjm
 * use MacroExpansionVisitor
 *
 */
@Deprecated
public class MacroExpansionEngine {
	OWLClassExpression ce;
	private OWLOntology ontology;
	OWLDataFactory dataFactory;
    private BidirectionalShortFormProvider bidiShortFormProvider;


	OWLExpressionParser<OWLClassExpression> parser;
	private Map<IRI,String> expandAssertionToMap;
	
	
	
	public MacroExpansionEngine(OWLOntology ontology) {
		super();
		this.ontology = ontology;
		init();
	}



	private void init() {
		
		parser = new ManchesterOWLSyntaxClassExpressionParser(null, null);
	}



	public OWLClassExpression p(String expression) throws ParserException {
		ManchesterOWLSyntaxEditorParser parser = 
				new ManchesterOWLSyntaxEditorParser(dataFactory, expression);
		parser.setDefaultOntology(ontology);
        OWLEntityChecker entityChecker = 
        	new ShortFormEntityChecker(bidiShortFormProvider);

		parser.setOWLEntityChecker(entityChecker);
        ce = parser.parseClassExpression();
		return ce;
	}
	
	public void t() {
		OWLAxiomVisitor visitor;
		for (OWLAxiom ax : ontology.getAxioms()) {
			if (ax instanceof OWLAnnotationAssertionAxiom) {
				OWLAnnotationAssertionAxiom aax = (OWLAnnotationAssertionAxiom)ax;
				OWLAnnotationProperty p = aax.getProperty();
				IRI pi = p.getIRI();
				if (expandAssertionToMap.containsKey(pi)) {
					
				}
			}
			
			// maybe there's a smarter way to do this using visitors...
			if (ax instanceof OWLSubClassOfAxiom) {
				t(((OWLSubClassOfAxiom)ax).getSubClass());
				t(((OWLSubClassOfAxiom)ax).getSuperClass());
			}
			if (ax instanceof OWLEquivalentClassesAxiom) {
				for (OWLClassExpression x : ((OWLEquivalentClassesAxiom)ax).getClassExpressions()) {
					t(x);
				}
			}
			if (ax instanceof OWLDisjointClassesAxiom) {
				for (OWLClassExpression x : ((OWLDisjointClassesAxiom)ax).getClassExpressions()) {
					t(x);
				}
			}
			/*
			Set<OWLEntity> entities = ax.getSignature();
			ax.accept(visitor);
			ax.
			((OWLSubClassOfAxiom)ax).getSignature()
			*/
			
		}
	}



	private void t(OWLClassExpression ex) {
		if (ex instanceof OWLRestriction) {
			OWLRestriction rex = (OWLRestriction)ex;
			OWLPropertyExpression p = rex.getProperty();
			if (rex instanceof OWLObjectSomeValuesFrom) {
				
			}
		}
		// TODO Auto-generated method stub
		
	}
}
