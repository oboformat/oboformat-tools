package org.obolibrary.macro;

import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cjm
 *
 * TODO - allow use of prefixes
 */
public class MacroExpansionVisitor {

	private static final Logger log = Logger.getLogger(MacroExpansionVisitor.class);
	private static final boolean DEBUG = log.isDebugEnabled();
	
	private OWLOntology inputOntology;
	private OWLOntologyManager outputManager;
	private OWLOntology outputOntology;

	private Visitor vistor;
	private ManchesterSyntaxTool manchesterSyntaxTool;
	
	public MacroExpansionVisitor(OWLOntology inputOntology) {
		super();
		this.inputOntology = inputOntology;
		this.vistor = new Visitor(inputOntology);
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

	public OWLOntology expandAll() {
		for (OWLAxiom ax : inputOntology.getAxioms()) {
			
			OWLAxiom exAx = ax;
			if (ax instanceof OWLSubClassOfAxiom) {
				exAx = vistor.visit((OWLSubClassOfAxiom)ax);
			}
			else if (ax instanceof OWLEquivalentClassesAxiom) {
				exAx = vistor.visit((OWLEquivalentClassesAxiom)ax);
			}else if(ax instanceof OWLAnnotationAssertionAxiom){
			 	for(OWLAxiom expandedAx: expand((OWLAnnotationAssertionAxiom)ax)){
			 		output(expandedAx);
			 	}
			}
			/*else if(ax instanceof OWLDeclarationAxiom) {
				exAx = vistor.visit((OWLDeclarationAxiom) ax);
			}*/
			
			output(exAx);
		}
		return outputOntology;
	}
	
	private Set<OWLAxiom> expand(OWLAnnotationAssertionAxiom ax){
		
		OWLAnnotationProperty prop = ax.getProperty();
		
		String expandTo = vistor.expandAssertionToMap.get(prop.getIRI());
		HashSet<OWLAxiom> setAx = new HashSet<OWLAxiom>();
		
		if(expandTo != null){
			if(DEBUG)
				log.debug("Template to Expand" + expandTo);
			
			expandTo = expandTo.replaceAll("\\?X", manchesterSyntaxTool.getId((IRI)ax.getSubject()));
			expandTo = expandTo.replaceAll("\\?Y", manchesterSyntaxTool.getId((IRI)ax.getValue()));

			if(DEBUG)
				log.debug("Expanding " + expandTo);
			
			try{
				Set<OntologyAxiomPair> setAxp =  manchesterSyntaxTool.parseManchesterExpressionFrames(expandTo);
				
				for(OntologyAxiomPair axp: setAxp){
					setAx.add(axp.getAxiom());
				}
				
			}catch(Exception ex){
				log.error(ex.getMessage(), ex);
			}
			//TODO: 
		}
		return setAx;
	}
	
	private class Visitor extends AbstractMacroExpansionVisitor {
		
		Visitor(OWLOntology inputOntology) {
			super(inputOntology, MacroExpansionVisitor.log);
		}

		@Override
		OWLClassExpression expandOWLObjSomeVal(OWLClassExpression filler, OWLObjectPropertyExpression p) {
			return expandObject(filler, p);
		}

		@Override
		OWLClassExpression expandOWLObjHasVal(OWLObjectHasValue desc, OWLIndividual filler,
				OWLObjectPropertyExpression p) {
			OWLClassExpression result = expandObject(filler, p);
			if (result != null) {
				result = dataFactory.getOWLObjectSomeValuesFrom(desc.getProperty(), result);
			}
			return result;
		}
		
		OWLClassExpression expandObject(Object filler, OWLObjectPropertyExpression p) {
			OWLClassExpression result = null;
			IRI iri = ((OWLObjectProperty)p).getIRI();
			IRI templateVal = null;
			if (expandExpressionMap.containsKey(iri)) {
				System.out.println("svf "+p+" "+filler);
				if (filler instanceof OWLObjectOneOf) {
					Set<OWLIndividual> inds = ((OWLObjectOneOf)filler).getIndividuals();
					if (inds.size() == 1) {
						System.out.println("**svf "+p+" "+inds.iterator().next());
						OWLIndividual ind = inds.iterator().next();
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
