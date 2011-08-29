package org.obolibrary.macro;

import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MacroExpansionGCIVisitor {

	private static final Logger log = Logger.getLogger(MacroExpansionGCIVisitor.class);
	private static final boolean DEBUG = log.isDebugEnabled();
	
	private OWLOntology inputOntology;
	private List<String> gciList;
	private OWLOntologyManager outputManager;
	private OWLOntology outputOntology;

	private ManchesterSyntaxTool manchesterSyntaxTool;
	private GCIVisitor visitor;
	
	public MacroExpansionGCIVisitor(OWLDataFactory dataFactory, OWLOntology inputOntology, OWLOntologyManager manager) {
		super();
		this.inputOntology = inputOntology;
		this.visitor = new GCIVisitor(dataFactory, inputOntology);
		this.manchesterSyntaxTool = new ManchesterSyntaxTool(dataFactory, manager, inputOntology);
		gciList = new ArrayList<String>();
		
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
		if (gciList != null && !gciList.isEmpty()) {
			return outputOntology;
		}
		return null;
	}
	
	public List<String> getGCIList() {
		return gciList;
	}
	
	private void expand(OWLAnnotationAssertionAxiom ax){
		
		OWLAnnotationProperty prop = ax.getProperty();
		
		String expandTo = visitor.expandAssertionToMap.get(prop.getIRI());
		if(expandTo != null){
			if(DEBUG)
				log.debug("Template to Expand" + expandTo);
			
			expandTo = expandTo.replaceAll("\\?X", ManchesterSyntaxTool.getId((IRI) ax.getSubject()));
			expandTo = expandTo.replaceAll("\\?Y", ManchesterSyntaxTool.getId((IRI) ax.getValue()));

			if(DEBUG)
				log.debug("Expanding " + expandTo);
		
			gciList.add(expandTo);
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
		
		GCIVisitor(OWLDataFactory dataFactory, OWLOntology inputOntology) {
			super(dataFactory, inputOntology, MacroExpansionGCIVisitor.log);
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
					String exStr = tStr.replaceAll("\\?Y", ManchesterSyntaxTool.getId( templateVal));
					System.out.println("R: "+exStr);
	
					gciList.add(exStr);
					try {
						result = manchesterSyntaxTool.parseManchesterExpression(exStr);
						
	//					OWLAxiom axiom  = dataFactory.get
	//					output(axiom);
						
					} catch (ParserException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
			return result;
		}
	}
}


