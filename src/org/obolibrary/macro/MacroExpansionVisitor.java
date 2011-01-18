package org.obolibrary.macro;

import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxOWLParser;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author cjm
 *
 * TODO - allow use of prefixes
 */
public class MacroExpansionVisitor implements OWLClassExpressionVisitorEx<OWLClassExpression>, OWLDataVisitorEx<OWLDataRange>, OWLAxiomVisitorEx<OWLAxiom> {

	private boolean negated;

	private OWLDataFactory dataFactory;
	private OWLOntology ontology;

	private Map<IRI,String> expandAssertionToMap;
	private Map<IRI,String> expandExpressionMap;
	private BidirectionalShortFormProvider bidiShortFormProvider;

	public MacroExpansionVisitor(OWLDataFactory dataFactory, OWLOntology ontology) {
		this.dataFactory = dataFactory;
		this.ontology = ontology;
		seedMaps();
	}


	public void seedMaps() {
		expandExpressionMap = new HashMap<IRI,String>();
		expandAssertionToMap = new HashMap<IRI,String>();
		OWLAnnotationProperty expandExpressionAP =
			dataFactory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000424"));
		for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature()) {
			for (OWLAnnotation a : p.getAnnotations(ontology, expandExpressionAP)) {
				OWLAnnotationValue v = a.getValue();
				if (v instanceof OWLLiteral) {
					String str = ((OWLLiteral)v).getLiteral();
					System.out.println("mapping "+p+" to "+str);
					expandExpressionMap.put(p.getIRI(), str);
				}
			}
		}
	}

	public OWLClassExpression parseManchesterExpression(String expression) throws ParserException {
		
		ManchesterOWLSyntaxEditorParser parser = 
			new ManchesterOWLSyntaxEditorParser(dataFactory, expression);
		parser.setDefaultOntology(ontology);
				//OWLEntityChecker entityChecker = 
		//	new DefaultEntityChecker();

		//parser.setOWLEntityChecker(entityChecker);
		
		System.out.println("parsing:"+expression);
		
		parser.parseClassAtom();
	//	OWLClassExpression ce = parser.parseClassExpression();
	//	return ce;
		return null;
		
	}

	public void expandAll() {
		for (OWLAxiom ax : ontology.getAxioms()) {
			if (ax instanceof OWLSubClassOfAxiom) {
				this.visit((OWLSubClassOfAxiom)ax);
			}
			else if (ax instanceof OWLEquivalentClassesAxiom) {
				this.visit((OWLEquivalentClassesAxiom)ax);
			}
			else {
				
			}
		}
	}

	public OWLClassExpression visit(OWLClass desc) {
		return desc;
	}


	public OWLClassExpression visit(OWLObjectIntersectionOf desc) {
		Set<OWLClassExpression> ops = new HashSet<OWLClassExpression>();
		for (OWLClassExpression op : desc.getOperands()) {
			ops.add(op.accept(this));
		}
		return dataFactory.getOWLObjectIntersectionOf(ops);
	}


	public OWLClassExpression visit(OWLObjectUnionOf desc) {
		Set<OWLClassExpression> ops = new HashSet<OWLClassExpression>();
		for (OWLClassExpression op : desc.getOperands()) {
			ops.add(op.accept(this));
		}

		return dataFactory.getOWLObjectUnionOf(ops);
		
	}

	public OWLClassExpression visit(OWLObjectComplementOf desc) {
		return dataFactory.getOWLObjectComplementOf( desc.getOperand().accept(this));
	}

	public OWLClassExpression visit(OWLObjectSomeValuesFrom desc) {
		OWLClassExpression filler = desc.getFiller();
		OWLObjectPropertyExpression p = desc.getProperty();
		if (p instanceof OWLObjectProperty) {
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
					 templateVal = ((OWLNamedObject)filler).getIRI();
				}
				if (templateVal != null) {
					System.out.println("TEMPLATEVAL: "+templateVal.toString());

					String tStr = expandExpressionMap.get(iri);
					//tStr = "SubClassOf(CL:0000034 SomeValuesFrom(bearer_of,AllValuesFrom(realized_by,GO:0017145)))";
					
					
					tStr = "<http://purl.obolibrary.org/obo/CARO_0000069> ";

					System.out.println("t: "+tStr);
					String exStr = tStr.replaceAll("\\?Y", templateVal.toString());
					try {
						OWLClassExpression ce = parseManchesterExpression(exStr);
						return  dataFactory.getOWLObjectSomeValuesFrom(desc.getProperty(), ce);

					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		return dataFactory.getOWLObjectSomeValuesFrom(desc.getProperty(), filler.accept(this));
	}


	public OWLClassExpression visit(OWLObjectAllValuesFrom desc) {
		return desc.getFiller().accept(this);
	}


	public OWLClassExpression visit(OWLObjectHasValue desc) {
		return desc.asSomeValuesFrom().accept(this);
	}


	public OWLClassExpression visit(OWLObjectMinCardinality desc) {

		OWLClassExpression filler = desc.getFiller().accept(this);
		return dataFactory.getOWLObjectMinCardinality(desc.getCardinality(),
				desc.getProperty(), filler);
	}


	public OWLClassExpression visit(OWLObjectExactCardinality desc) {
		return desc.asIntersectionOfMinMax().accept(this);
	}


	public OWLClassExpression visit(OWLObjectMaxCardinality desc) {
		OWLClassExpression filler = desc.getFiller().accept(this);
		return dataFactory.getOWLObjectMaxCardinality(desc.getCardinality(),
				desc.getProperty(), filler);
	}


	public OWLClassExpression visit(OWLObjectHasSelf desc) {
		return desc;
	}


	public OWLClassExpression visit(OWLObjectOneOf desc) {
		return desc;
	}


	public OWLClassExpression visit(OWLDataSomeValuesFrom desc) {
		OWLDataRange filler = desc.getFiller().accept(this);
		return dataFactory.getOWLDataSomeValuesFrom(desc.getProperty(), filler);
	}

	public OWLClassExpression visit(OWLDataAllValuesFrom desc) {
		OWLDataRange filler = desc.getFiller().accept(this);
		return dataFactory.getOWLDataAllValuesFrom(desc.getProperty(), filler);
	}


	public OWLClassExpression visit(OWLDataHasValue desc) {
		return desc.asSomeValuesFrom().accept(this);
	}


	public OWLClassExpression visit(OWLDataExactCardinality desc) {
		return desc.asIntersectionOfMinMax().accept(this);
	}

	public OWLClassExpression visit(OWLDataMaxCardinality desc) {
		int card = desc.getCardinality();
		OWLDataRange filler = desc.getFiller().accept(this);
		return dataFactory.getOWLDataMaxCardinality(card, desc.getProperty(), filler);
	}

	public OWLClassExpression visit(OWLDataMinCardinality desc) {
		int card = desc.getCardinality();
		OWLDataRange filler = desc.getFiller().accept(this);
		return dataFactory.getOWLDataMinCardinality(card, desc.getProperty(), filler);
	}


	public OWLDataRange visit(OWLDatatype node) {
		return node;
	}


	public OWLDataRange visit(OWLDataComplementOf node) {
		return node;
	}


	public OWLDataRange visit(OWLDataOneOf node) {
		// Encode as a data union of and return result
		Set<OWLDataOneOf> oneOfs = new HashSet<OWLDataOneOf>();
		for (OWLLiteral lit : node.getValues()) {
			oneOfs.add(dataFactory.getOWLDataOneOf(lit));
		}
		return dataFactory.getOWLDataUnionOf(oneOfs).accept(this);
	}

	public OWLDataRange visit(OWLDataIntersectionOf node) {
		Set<OWLDataRange> ops = new HashSet<OWLDataRange>();
		for (OWLDataRange op : node.getOperands()) {
			ops.add(op.accept(this));
		}
		return dataFactory.getOWLDataIntersectionOf(ops);
	}

	public OWLDataRange visit(OWLDataUnionOf node) {
		Set<OWLDataRange> ops = new HashSet<OWLDataRange>();
		for (OWLDataRange op : node.getOperands()) {
			ops.add(op.accept(this));
		}
		return dataFactory.getOWLDataUnionOf(ops);
	}

	public OWLAxiom visit(OWLHasKeyAxiom axiom) {
		return null;
	}

	public OWLDataRange visit(OWLDatatypeRestriction node) {
		return node;
	}


	/*public OWLDataRange visit(OWLTypedLiteral node) {
		return null;
	}


	public OWLDataRange visit(OWLStringLiteral node) {
		return null;
	}*/


	public OWLDataRange visit(OWLFacetRestriction node) {
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//  Conversion of non-class expressions to MacroExpansionVisitor
	//
	///////////////////////////////////////////////////////////////////////////////////////////////


	public OWLAxiom visit(OWLSubClassOfAxiom axiom) {
		return dataFactory.getOWLSubClassOfAxiom(axiom.getSubClass().accept(this),
				axiom.getSuperClass().accept(this));
	}


	public OWLAxiom visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDisjointClassesAxiom axiom) {
		Set<OWLClassExpression> ops = new HashSet<OWLClassExpression>();
		for (OWLClassExpression op : axiom.getClassExpressions()) {
			ops.add(op.accept(this));
		}
		return dataFactory.getOWLDisjointClassesAxiom(ops);
	}


	public OWLAxiom visit(OWLDataPropertyDomainAxiom axiom) {
		return dataFactory.getOWLDataPropertyDomainAxiom(axiom.getProperty(),
				axiom.getDomain().accept(this));
	}

	public OWLAxiom visit(OWLObjectPropertyDomainAxiom axiom) {
		return dataFactory.getOWLObjectPropertyDomainAxiom(axiom.getProperty(),
				axiom.getDomain().accept(this));
	}


	public OWLAxiom visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDifferentIndividualsAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDisjointDataPropertiesAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDisjointObjectPropertiesAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLObjectPropertyRangeAxiom axiom) {
		return dataFactory.getOWLObjectPropertyRangeAxiom(axiom.getProperty(),
				axiom.getRange().accept(this));
	}


	public OWLAxiom visit(OWLObjectPropertyAssertionAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLFunctionalObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLSubObjectPropertyOfAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDisjointUnionAxiom axiom) {
		Set<OWLClassExpression> descs = new HashSet<OWLClassExpression>();
		for (OWLClassExpression op : axiom.getClassExpressions()) {
			descs.add(op.accept(this));
		}
		return dataFactory.getOWLDisjointUnionAxiom(axiom.getOWLClass(), descs);
	}


	public OWLAxiom visit(OWLDeclarationAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLAnnotationAssertionAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDataPropertyRangeAxiom axiom) {
		return dataFactory.getOWLDataPropertyRangeAxiom(axiom.getProperty(),
				axiom.getRange().accept(this));
	}


	public OWLAxiom visit(OWLFunctionalDataPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLEquivalentDataPropertiesAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLClassAssertionAxiom axiom) {
		if (axiom.getClassExpression().isAnonymous()) {
			return dataFactory.getOWLClassAssertionAxiom(axiom.getClassExpression().accept(this), axiom.getIndividual());
		} else {
			return axiom;
		}
	}


	public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLClassExpression> ops = new HashSet<OWLClassExpression>();
		for (OWLClassExpression op : axiom.getClassExpressions()) {
			ops.add(op.accept(this));
		}
		return dataFactory.getOWLEquivalentClassesAxiom(ops);
	}


	public OWLAxiom visit(OWLDataPropertyAssertionAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLSubDataPropertyOfAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLSameIndividualAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLSubPropertyChainOfAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLInverseObjectPropertiesAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(SWRLRule rule) {
		return rule;
	}

	public OWLAxiom visit(OWLAnnotationPropertyDomainAxiom axiom) {
		return axiom;
	}

	public OWLAxiom visit(OWLAnnotationPropertyRangeAxiom axiom) {
		return axiom;
	}

	public OWLAxiom visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		return axiom;
	}


	public OWLAxiom visit(OWLDatatypeDefinitionAxiom axiom) {
		return axiom;
	}


	public OWLDataRange visit(OWLLiteral node) {
		// TODO Auto-generated method stub
		return null;
	}
}




