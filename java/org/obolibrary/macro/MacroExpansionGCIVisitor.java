package org.obolibrary.macro;

import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;

import java.util.Set;

public class MacroExpansionGCIVisitor {

	private static final Logger log = Logger.getLogger(MacroExpansionGCIVisitor.class);
	private static final boolean DEBUG = log.isDebugEnabled();
	
	private OWLOntology inputOntology;
	private OWLOntologyManager outputManager;
	private OWLOntology outputOntology;

	private ManchesterSyntaxTool manchesterSyntaxTool;
	private GCIVisitor visitor;
	
	public MacroExpansionGCIVisitor(OWLOntology inputOntology) {
		super();
		this.inputOntology = inputOntology;
		this.visitor = new GCIVisitor(inputOntology);
		this.manchesterSyntaxTool = new ManchesterSyntaxTool(inputOntology);
		
		outputManager = OWLManager.createOWLOntologyManager();
		
		try{
			outputOntology = outputManager.createOntology(inputOntology.getOntologyID());
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}

	}

	private void output(OWLAxiom axiom){
		if (axiom == null) {
			log.error("no axiom");
			return;
		}
		//System.out.println("adding:"+axiom);
		AddAxiom addAx = new AddAxiom(outputOntology, axiom);
		try {
			outputManager.applyChange(addAx);
		}
		catch (Exception e) {			
			log.error("COULD NOT TRANSLATE AXIOM", e);
		}
		
	}
	
	public OWLOntology createGCIOntology() {
		for (OWLAxiom ax : inputOntology.getAxioms()) {

			if (ax instanceof OWLSubClassOfAxiom) {
				visitor.visit((OWLSubClassOfAxiom)ax);
			}
			else if (ax instanceof OWLEquivalentClassesAxiom) {
				visitor.visit((OWLEquivalentClassesAxiom)ax);
			}else if(ax instanceof OWLAnnotationAssertionAxiom){
				expand((OWLAnnotationAssertionAxiom)ax);
			}
		}
		return outputOntology;
	}
	
	private void expand(OWLAnnotationAssertionAxiom ax){
		
		OWLAnnotationProperty prop = ax.getProperty();
		
		String expandTo = visitor.expandAssertionToMap.get(prop.getIRI());
		if(expandTo != null){
			if(DEBUG)
				log.debug("Template to Expand" + expandTo);
			
			expandTo = expandTo.replaceAll("\\?X", manchesterSyntaxTool.getId((IRI) ax.getSubject()));
			expandTo = expandTo.replaceAll("\\?Y", manchesterSyntaxTool.getId((IRI) ax.getValue()));

			if(DEBUG)
				log.debug("Expanding " + expandTo);
		
			try{
				Set<OntologyAxiomPair> setAxp =  manchesterSyntaxTool.parseManchesterExpressionFrames(expandTo);
				for(OntologyAxiomPair axp: setAxp){
					output(axp.getAxiom());
				}
				
			}catch(Exception ex){
				log.error(ex.getMessage(), ex);
			}
		}
	}
	
	private class GCIVisitor extends AbstractMacroExpansionVisitor {
		
		GCIVisitor(OWLOntology inputOntology) {
			super(inputOntology, MacroExpansionGCIVisitor.log);
		}

		@Override
		OWLClassExpression expandOWLObjSomeVal(OWLClassExpression filler, OWLObjectPropertyExpression p) {
			OWLClassExpression gciRHS = expandObject(filler, p);
			if (gciRHS != null) {
				OWLClassExpression gciLHS = dataFactory.getOWLObjectSomeValuesFrom(p, filler);
				OWLEquivalentClassesAxiom ax = 
					this.dataFactory.getOWLEquivalentClassesAxiom(gciLHS, gciRHS);
				output(ax);
			}
			return gciRHS;
		}
	
		@Override
		OWLClassExpression expandOWLObjHasVal(OWLObjectHasValue desc, OWLIndividual filler,
				OWLObjectPropertyExpression p) {
			OWLClassExpression gciRHS = expandObject(filler, p);
			if (gciRHS != null) {
				OWLClassExpression gciLHS = dataFactory.getOWLObjectHasValue(p, filler);
				OWLEquivalentClassesAxiom ax = 
						this.dataFactory.getOWLEquivalentClassesAxiom(gciLHS, gciRHS);
				output(ax);
			}
			return gciRHS;
		}

		private OWLClassExpression expandObject(Object filler, OWLObjectPropertyExpression p) {
			OWLClassExpression result = null;
			IRI iri = ((OWLObjectProperty)p).getIRI();
			IRI templateVal = null;
			if (expandExpressionMap.containsKey(iri)) {
				System.out.println("svf "+p+" "+filler);
				if (filler instanceof OWLObjectOneOf) {
					Set<OWLIndividual> inds = ((OWLObjectOneOf)filler).getIndividuals();
					if (inds.size() == 1) {
						OWLIndividual ind = inds.iterator().next();
						System.out.println("**svf "+p+" "+ind);
						if (ind instanceof OWLNamedIndividual) {
							templateVal = ((OWLNamedObject)ind).getIRI();
						}
					}
					
				}
				if (filler instanceof OWLNamedObject) {
					 templateVal =  ((OWLNamedObject)filler).getIRI();
				}
				if (templateVal != null) {
					System.out.println("TEMPLATEVAL: "+templateVal.toString());
	
					String tStr = expandExpressionMap.get(iri);
					
					System.out.println("t: "+tStr);
					String exStr = tStr.replaceAll("\\?Y", manchesterSyntaxTool.getId( templateVal));
					System.out.println("R: "+exStr);
	
					try {
						result = manchesterSyntaxTool.parseManchesterExpression(exStr);
					} catch (ParserException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
			return result;
		}
	}
}


