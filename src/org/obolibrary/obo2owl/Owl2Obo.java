package org.obolibrary.obo2owl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.obolibrary.oboformat.model.*;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class Owl2Obo {

	private static Logger LOG = Logger.getLogger(Owl2Obo.class);

	private static final String DEFAULT_IRI_PREFIX = "http://purl.obolibrary.org/obo/";
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLOntology owlOntology;
	OWLDataFactory fac;
	OBODoc obodoc;
	Map<String, String> idSpaceMap;
	// Map<String,IRI> annotationPropertyMap;
	Map<String, String> annotationPropertyMap;
	Set<OWLAnnotationProperty> apToDeclare;

	private void init() {
		idSpaceMap = new HashMap<String, String>();
		manager = OWLManager.createOWLOntologyManager();
		fac = manager.getOWLDataFactory();
		initAnnotationPropertyMap();
		apToDeclare = new HashSet<OWLAnnotationProperty>();
	}

	private void initAnnotationPropertyMap() {
		annotationPropertyMap = new HashMap<String, String>();
		annotationPropertyMap.put(OWLRDFVocabulary.RDFS_LABEL.getIRI()
				.toString(), "name");
		annotationPropertyMap.put(OWLRDFVocabulary.RDFS_COMMENT.getIRI()
				.toString(), "comment");
		annotationPropertyMap.put(DEFAULT_IRI_PREFIX + "IAO_0000424",
				"expand_expression_to");
		annotationPropertyMap.put(DEFAULT_IRI_PREFIX + "IAO_0000425",
				"expand_assertion_to");
		annotationPropertyMap.put(DEFAULT_IRI_PREFIX + "IAO_0000115", "def");
		annotationPropertyMap
				.put(DEFAULT_IRI_PREFIX + "IAO_0000118", "synonym");
		annotationPropertyMap.put(DEFAULT_IRI_PREFIX + "IAO_0000427",
				"is_anti_symmetric");
		annotationPropertyMap.put(DEFAULT_IRI_PREFIX + "IAO_0100001",
				"replaced_by");
		annotationPropertyMap
				.put(DEFAULT_IRI_PREFIX + "remark", "data-version");

	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	public OBODoc getObodoc() {
		return obodoc;
	}

	public void setObodoc(OBODoc obodoc) {
		this.obodoc = obodoc;
	}

	public OBODoc convert(OWLOntology ont) throws OWLOntologyCreationException {
		this.owlOntology = ont;
		init();
		return tr();
	}

	private String getIdentifier(OWLObject obj) {
		if (obj instanceof OWLEntity)
			return getIdentifier(((OWLEntity) obj).getIRI());

		return null;
	}

	private String getIdentifier(IRI iriId) {
		String iri = iriId.toString();
		if (iri.startsWith("http://purl.obolibrary.org/obo/")) {
			iri = iri.replace("http://purl.obolibrary.org/obo/", "");
			int p = iri.indexOf('_');

			if (p >= 0) {
				iri = iri.substring(0, p) + ":" + iri.substring(p + 1);
			}
		}

		return iri;

	}

	private OBODoc tr() throws OWLOntologyCreationException {
		obodoc = new OBODoc();

		tr(owlOntology);

		for (OWLAxiom ax : owlOntology.getAxioms()) {
			if (ax instanceof OWLDeclarationAxiom) {
				tr((OWLDeclarationAxiom) ax);
			} else if (ax instanceof OWLSubClassOfAxiom) {
				tr((OWLSubClassOfAxiom) ax);
			} else if (ax instanceof OWLDisjointClassesAxiom) {
				tr((OWLDisjointClassesAxiom) ax);
			} else if (ax instanceof OWLEquivalentClassesAxiom) {
				tr((OWLEquivalentClassesAxiom) ax);
			}else if (ax instanceof OWLClassAssertionAxiom){
				tr((OWLClassAssertionAxiom)ax);
			}else {
				LOG.warn("Cann't Translate axiom: " + ax);
			}
			// tr(ax);
		}
		return obodoc;
	}

	private void add(Frame f) {
		if (f != null) {
			try {
				this.obodoc.addFrame(f);
			} catch (Exception ex) {
				LOG.error("", ex);
			}
		}
	}

	private void tr(OWLAnnotationAssertionAxiom aanAx, Frame frame) {

		OWLAnnotationProperty prop = aanAx.getProperty();
		String tag = propToTag(prop);

		if (tag != null) {
			String value = ((OWLLiteral) aanAx.getValue()).getLiteral();
			if ("id".equals(tag))
				frame.setId(value);

			Clause clause = new Clause();
			clause.setTag(tag);
			clause.addValue(value);
			frame.addClause(clause);
			
			if("def".equals(tag) || "synonym".equals(tag)){
				for(OWLAnnotation aan: aanAx.getAnnotations()){
					String propId = propToTag(aan.getProperty());
					
					if("xref".equals(propId)){
						String xrefValue = ((OWLLiteral) aan.getValue()).getLiteral();
						Xref xref = new Xref(xrefValue);
						clause.addXref(xref);
					}
				}
			}else if("synonym".equals(tag)){
				String scope = null;
				String type = null;
				for(OWLAnnotation aan: aanAx.getAnnotations()){
					String propId = propToTag(aan.getProperty());
					
					if("xref".equals(propId)){
						String xrefValue = ((OWLLiteral) aan.getValue()).getLiteral();
						Xref xref = new Xref(xrefValue);
						clause.addXref(xref);
					}else if("scope".equals(propId)){
						scope = ((OWLLiteral) aan.getValue()).getLiteral();
					}else if("type".equals(propId)){
						type = ((OWLLiteral) aan.getValue()).getLiteral();
					}
					
					if(scope != null){
						clause.addValue(scope);
						
						if(type != null){
							clause.addValue(type);
						}
					}
					
					
				}
			}

			
			
		}

	}

	private void tr(OWLOntology ontology) {
		Frame f = new Frame(FrameType.HEADER);

		this.obodoc.setHeaderFrame(f);

		String id = getIdentifier(ontology.getOntologyID().getOntologyIRI());

		Clause c = new Clause();
		c.setTag("ontology");
		c.setValue(id);
		f.addClause(c);

		for (OWLAnnotationAssertionAxiom aanAx : ontology
				.getAnnotationAssertionAxioms(ontology.getOntologyID()
						.getOntologyIRI())) {
			tr(aanAx, f);
		}

	}

	private void tr(OWLEquivalentClassesAxiom ax) {

		List<OWLClassExpression> list = ax.getClassExpressionsAsList();

		OWLClassExpression ce1 = list.get(0);
		OWLClassExpression ce2 = list.get(1);

		String cls2 = getIdentifier(ce2);

		Frame f = getTermFrame((OWLEntity) ce1);

		if (f == null) {
			LOG.warn("Cann't Translate axion: " + ax);
			return;
		}

		if (cls2 != null) {
			Clause c = new Clause();
			c.setTag("equivalent_to");
			c.setValue(cls2);
			f.addClause(c);
		} else if (ax instanceof OWLObjectUnionOf) {
			List<OWLClassExpression> list2 = ((OWLObjectUnionOf) ax)
					.getOperandsAsList();
			Clause c = new Clause();
			c.setTag("union_of");
			c.setValue(getIdentifier(list2.get(0)));
			f.addClause(c);
		} else if (ax instanceof OWLObjectIntersectionOf) {

			List<OWLClassExpression> list2 = ((OWLObjectIntersectionOf) ax).getOperandsAsList();
			Clause c = new Clause();
			c.setTag("intersection_of");
			OWLClassExpression ce = list2.get(0);
			String r = null;
			cls2 = getIdentifier(list.get(0));
			if(ce instanceof OWLObjectSomeValuesFrom ){
				OWLObjectSomeValuesFrom ristriction = (OWLObjectSomeValuesFrom)ce;
				r = getIdentifier(ristriction.getProperty());
				cls2 = getIdentifier(ristriction.getFiller());
			}
			
			if(r != null)
				c.addValue(r);
			
			c.addValue(cls2);
			f.addClause(c);
		}

	}

	private void tr(OWLDisjointClassesAxiom ax) {
		List<OWLClassExpression> list = ax.getClassExpressionsAsList();
		String cls2 = getIdentifier(list.get(1));

		Frame f = getTermFrame((OWLEntity) list.get(0));
		Clause c = new Clause();
		c.setTag("disjoint_from");
		c.setValue(cls2);
		f.addClause(c);
	}

	private void tr(OWLDeclarationAxiom axiom) {
		OWLEntity entity = axiom.getEntity();

		Frame f = null;
		if (entity instanceof OWLClass) {
			f = new Frame(FrameType.TERM);
		} else if (entity instanceof OWLObjectProperty) {
			f = new Frame(FrameType.TYPEDEF);
		}

		if (f != null) {
			for (OWLAnnotationAssertionAxiom aanAx : entity
					.getAnnotationAssertionAxioms(this.owlOntology)) {
				
				tr(aanAx, f);
			}

			add(f);
		}

	}

	private String propToTag(OWLAnnotationProperty prop) {
		String iri = prop.getIRI().toString();
		String tag = annotationPropertyMap.get(iri);

		if (tag == null) {
			String prefix = DEFAULT_IRI_PREFIX + "IAO_";
			if (iri.startsWith(prefix)) {
				tag = iri.substring(prefix.length());
			}
		}
		return tag;
	}

	private Frame getTermFrame(OWLEntity entity) {
		String id = getIdentifier(entity.getIRI());
		Frame f = this.obodoc.getTermFrame(id);

		if (f == null) {
			f = new Frame(FrameType.TERM);
			f.setId(id);
			add(f);
		}

		return f;
	}

	
	private void tr(OWLClassAssertionAxiom ax){
		String clsId = getIdentifier(ax.getClassExpression());
		
		if("synonymtypedef".equals(clsId)){
			Frame f = this.obodoc.getHeaderFrame();
			Clause c = new Clause();
			c.setTag("synonymtypedef");

			OWLNamedIndividual indv =(OWLNamedIndividual) ax.getIndividual();
			String indvId = getIdentifier(indv);
			c.addValue(indvId);
			
			String nameValue = "";
			String scopeValue = null;
			for(OWLAnnotation ann: indv.getAnnotations(owlOntology)){
				String propId = propToTag(ann.getProperty());
				String value = ((OWLLiteral) ann.getValue()).getLiteral();

				if("name".equals(propId)){
					nameValue = value;
				}else
					scopeValue = value;
			}
			
				c.addValue(nameValue);
				
				if(scopeValue != null){
					c.addValue(scopeValue);
				}
			
			f.addClause(c);
		}else if("subsetdef".equals(clsId)){
			Frame f = this.obodoc.getHeaderFrame();
			Clause c = new Clause();
			c.setTag("subsetdef");

			OWLNamedIndividual indv =(OWLNamedIndividual) ax.getIndividual();
			String indvId = getIdentifier(indv);
			c.addValue(indvId);
			
			String nameValue = "";
			for(OWLAnnotation ann: indv.getAnnotations(owlOntology)){
				String propId = propToTag(ann.getProperty());
				String value = ((OWLLiteral) ann.getValue()).getLiteral();

				if("name".equals(propId)){
					nameValue = value;
				}
			}
			
				c.addValue(nameValue);
				
			f.addClause(c);
		}else{
			//TODO: individual
		}
			
	}
	
	private void tr(OWLSubClassOfAxiom ax) {
		OWLSubClassOfAxiom a = (OWLSubClassOfAxiom) ax;
		OWLClassExpression sub = a.getSubClass();
		OWLClassExpression sup = a.getSuperClass();
		if (sub instanceof OWLClass) {
			Frame f = getTermFrame((OWLEntity) sub);

			if (sup instanceof OWLClass) {
				Clause c = new Clause();
				c.setTag("is_a");
				c.setValue(getIdentifier(sup));
				f.addClause(c);
			} else if (sup instanceof OWLObjectSomeValuesFrom) {
				OWLQuantifiedRestriction r = (OWLQuantifiedRestriction) sup;

				Clause c = new Clause();
				c.setTag("relationship");
				c.addValue(getIdentifier(r.getProperty()));
				;
				c.addValue(getIdentifier(sup));

				f.addClause(c);
			} else {
				LOG.warn("Cann't translate axiom: " + ax);
			}
		} else {
			LOG.warn("Cann't translate axiom: " + ax);
		}
	}



}
