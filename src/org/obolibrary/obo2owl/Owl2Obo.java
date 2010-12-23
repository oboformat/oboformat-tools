package org.obolibrary.obo2owl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.coode.owlapi.obo.parser.DisjointFromHandler;
import org.obolibrary.oboformat.model.*;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLRestriction;
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

	public class NoTranslation extends Exception {

		public NoTranslation() {
			super();
			// TODO Auto-generated constructor stub
		}

		public NoTranslation(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public NoTranslation(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		public NoTranslation(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}

	}

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

	/*
	 * private void initAnnotationPropertyMap() { annotationPropertyMap = new
	 * HashMap<String,IRI>(); map("name",OWLRDFVocabulary.RDFS_LABEL);
	 * map("comment",OWLRDFVocabulary.RDFS_COMMENT);
	 * map("expand_expression_to","IAO_0000424");
	 * map("expand_assertion_to","IAO_0000425"); map("def","IAO_0000115");
	 * map("synonym","IAO_0000118"); map("is_anti_symmetric","IAO_0000427");
	 * map("replaced_by","IAO_0100001"); }
	 * 
	 * private void map(String key, String iri) { annotationPropertyMap.put(key,
	 * IRI.create(DEFAULT_IRI_PREFIX+iri)); }
	 * 
	 * private void map(String key, OWLRDFVocabulary vocab) {
	 * annotationPropertyMap.put(key, vocab.getIRI()); }
	 */

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
			} else {
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
			clause.setValue(value);
			frame.addClause(clause);
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

	/*
	 * private Clause tr(OWLAxiom ax) { Clause c = null; String tag = null; if
	 * (ax instanceof OWLSubClassOfAxiom) { OWLSubClassOfAxiom a =
	 * (OWLSubClassOfAxiom)ax; OWLClassExpression sub = a.getSubClass();
	 * OWLClassExpression sup = a.getSuperClass(); if (sub instanceof OWLClass)
	 * { if (sup instanceof OWLClass) { tag = "is_a"; } else { if (sup
	 * instanceof OWLRestriction) { OWLRestriction r = (OWLRestriction)sup; c =
	 * tr(r); tag = "relationship"; } } } else { // cannot do anything with GCIs
	 * yet } } if (c == null) { return null; } if (tag == null) { return null; }
	 * c.setTag(tag); return c; }
	 * 
	 * private Clause tr(OWLRestriction r) { Clause c = new Clause(); String
	 * relId = trId(r.getProperty()); if (relId == null) { return null; }
	 * c.addValue(relId);
	 * 
	 * if (r instanceof OWLObjectSomeValuesFrom) { String tgtId =
	 * trId(((OWLObjectSomeValuesFrom)r).getFiller()); if (tgtId != null) {
	 * c.addValue(tgtId); } } else if (r instanceof OWLObjectAllValuesFrom) {
	 * String tgtId = trId(((OWLObjectAllValuesFrom)r).getFiller()); } return c;
	 * }
	 * 
	 * private String trId(OWLClassExpression filler) { // TODO Auto-generated
	 * method stub return null; }
	 * 
	 * private String trId(OWLAnnotationProperty property) { String iri =
	 * property.getIRI().toString(); String tag =
	 * annotationPropertyMap.get(iri);
	 * 
	 * if(tag == null){ if(iri.startsWith(DEFAULT_IRI_PREFIX)){ tag =
	 * iri.substring(DEFAULT_IRI_PREFIX.length()); } } return tag; }
	 */

	/*
	 * public OWLClassExpression trTermFrame(Frame termFrame) { OWLClass cls =
	 * trClass(termFrame.getId()); add(fac.getOWLDeclarationAxiom(cls)); for
	 * (String tag : termFrame.getTags()) { System.out.println("tag:"+tag);
	 * Collection<Clause> clauses = termFrame.getClauses(tag); if
	 * (tag.equals("intersection_of")) { add(trIntersectionOf(cls,clauses)); }
	 * else if (tag.equals("union_of")) { add(trUnionOf(cls,clauses)); } else {
	 * for (Clause clause : clauses) { add(trTermClause(cls,tag,clause)); } } }
	 * return cls; }
	 * 
	 * public OWLProperty trTypedefFrame(Frame typedefFrame) { // TODO -
	 * annotation props OWLObjectProperty p =
	 * trObjectProp(typedefFrame.getId()); add(fac.getOWLDeclarationAxiom(p));
	 * 
	 * for (String tag : typedefFrame.getTags()) { Collection<Clause> clauses =
	 * typedefFrame.getClauses(tag); if (tag.equals("intersection_of")) {
	 * add(trRelationIntersectionOf(p,clauses)); } else if
	 * (tag.equals("union_of")) { add(trRelationUnionOf(p,clauses)); } else {
	 * for (Clause clause : clauses) { add(trTypedefClause(p,tag,clause)); } } }
	 * return p; }
	 * 
	 * 
	 * private OWLAxiom trRelationUnionOf(OWLProperty p, Collection<Clause>
	 * clauses) { // TODO Auto-generated method stub return null; }
	 * 
	 * private OWLAxiom trRelationIntersectionOf(OWLProperty p,
	 * Collection<Clause> clauses) { // TODO Auto-generated method stub return
	 * null; }
	 * 
	 * private OWLAxiom trUnionOf(OWLClass cls, Collection<Clause> clauses) { //
	 * TODO Auto-generated method stub return null; }
	 * 
	 * private OWLAxiom trIntersectionOf(OWLClass cls, Collection<Clause>
	 * clauses) { Set<? extends OWLAnnotation> annotations =
	 * trAnnotations(clauses);
	 * 
	 * Set<OWLClassExpression> eSet; eSet = new HashSet<OWLClassExpression>();
	 * eSet.add(cls);
	 * 
	 * Set<OWLClassExpression> iSet; iSet = new HashSet<OWLClassExpression>();
	 * for (Clause clause: clauses) { Collection<QualifierValue> qvs =
	 * clause.getQualifierValues(); // TODO - quals if
	 * (clause.getValues().size() == 1) { iSet.add(trClass(clause.getValue()));
	 * } else { iSet.add(trRel((String)clause.getValue(),
	 * (String)clause.getValue2(), qvs));
	 * 
	 * } } System.out.println(cls+" CL:"+clauses+" I:"+iSet+" E:"+eSet);
	 * eSet.add(fac.getOWLObjectIntersectionOf(iSet)); // TODO - fix this if
	 * (annotations == null || annotations.size() == 0) return
	 * fac.getOWLEquivalentClassesAxiom(eSet); else return
	 * fac.getOWLEquivalentClassesAxiom(eSet, annotations); }
	 * 
	 * 
	 * private void add(OWLAxiom axiom) { if (axiom == null) {
	 * System.out.println("no axiom"); return; } System.out.println("adding: ");
	 * //System.out.println(axiom); AddAxiom addAx = new AddAxiom(owlOntology,
	 * axiom); //System.out.println(addAx); manager.applyChange(addAx); }
	 * 
	 * private OWLAxiom trTermClause(OWLClass cls, String tag, Clause clause) {
	 * OWLAxiom ax; Collection<QualifierValue> qvs =
	 * clause.getQualifierValues(); Set<? extends OWLAnnotation> annotations =
	 * trAnnotations(clause); if (tag.equals("is_a")) { ax =
	 * fac.getOWLSubClassOfAxiom(cls, trClass((String)clause.getValue()),
	 * annotations); } else if (tag.equals("relationship")) { // TODO ax =
	 * fac.getOWLSubClassOfAxiom(cls, this.trRel((String)clause.getValue(),
	 * (String)clause.getValue2(), qvs), annotations); } else if
	 * (tag.equals("disjoint_from")) {
	 * 
	 * Set<OWLClassExpression> cSet; cSet = new HashSet<OWLClassExpression>();
	 * cSet.add(cls); cSet.add(trClass((String)clause.getValue())); ax =
	 * fac.getOWLDisjointClassesAxiom( cSet, annotations); } else if
	 * (tag.equals("equivalent_to")) {
	 * 
	 * Set<OWLClassExpression> cSet; cSet = new HashSet<OWLClassExpression>();
	 * cSet.add(cls); cSet.add(trClass((String)clause.getValue())); ax =
	 * fac.getOWLEquivalentClassesAxiom( cSet, annotations); }
	 * 
	 * else { return trGenericClause(cls, tag, clause); } return ax; }
	 */

	// no data properties in obo
	/*
	 * private OWLAxiom trTypedefClause(OWLObjectProperty p, String tag, Clause
	 * clause) { OWLAxiom ax = null; Object v = clause.getValue();
	 * Collection<QualifierValue> qvs = clause.getQualifierValues(); Set<?
	 * extends OWLAnnotation> annotations = trAnnotations(clause); if
	 * (tag.equals("is_a")) { ax = fac.getOWLSubObjectPropertyOfAxiom( p,
	 * trObjectProp((String)v), annotations); } else if
	 * (tag.equals("relationship")) { ax = null; // TODO } else if
	 * (tag.equals("disjoint_from")) {
	 * 
	 * Set<OWLObjectPropertyExpression> cSet; cSet = new
	 * HashSet<OWLObjectPropertyExpression>(); cSet.add( p); cSet.add(
	 * trObjectProp((String)v)); ax = fac.getOWLDisjointObjectPropertiesAxiom(
	 * cSet, annotations); } else if (tag.equals("equivalent_to")) {
	 * 
	 * Set<OWLObjectPropertyExpression> cSet; cSet = new
	 * HashSet<OWLObjectPropertyExpression>(); cSet.add(p); cSet.add(
	 * trObjectProp((String)v)); ax = fac.getOWLEquivalentObjectPropertiesAxiom(
	 * cSet, annotations); } else if (tag.equals("domain")) { ax =
	 * fac.getOWLObjectPropertyDomainAxiom(p, trClass(v), annotations); } else
	 * if (tag.equals("range")) { ax = fac.getOWLObjectPropertyRangeAxiom(p,
	 * trClass(v), annotations); } else if (tag.equals("holds_over_chain") ||
	 * tag.equals("equivalent_to_chain")) { List<OWLObjectPropertyExpression>
	 * chain = new Vector<OWLObjectPropertyExpression>();
	 * chain.add(getObjectProp(v));
	 * chain.add(getObjectProp(clause.getValue2())); ax =
	 * fac.getOWLSubPropertyChainOfAxiom(chain , p, annotations); // TODO -
	 * annotations for equivalent to } else if (tag.equals("is_transitive")) {
	 * ax = fac.getOWLTransitiveObjectPropertyAxiom(p, annotations); } else if
	 * (tag.equals("is_symmetric")) { ax =
	 * fac.getOWLSymmetricObjectPropertyAxiom(p, annotations); } else if
	 * (tag.equals("is_asymmetric")) { ax =
	 * fac.getOWLAsymmetricObjectPropertyAxiom(p, annotations); } else if
	 * (tag.equals("is_functional")) { ax =
	 * fac.getOWLFunctionalObjectPropertyAxiom(p, annotations); } else if
	 * (tag.equals("is_inverse_functional")) { ax =
	 * fac.getOWLInverseFunctionalObjectPropertyAxiom(p, annotations); } else {
	 * return trGenericClause(p, tag, clause); } // TODO - disjointOver return
	 * ax; }
	 */

	private OWLObjectPropertyExpression getObjectProp(Object v) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * private OWLAxiom trGenericClause(OWLNamedObject e, String tag, Clause
	 * clause) { Collection<QualifierValue> qvs = clause.getQualifierValues();
	 * Set<? extends OWLAnnotation> annotations = trAnnotations(clause);
	 * 
	 * OWLAnnotationSubject sub = (OWLAnnotationSubject) e.getIRI();
	 * 
	 * OWLAxiom ax = null; if (tag.equals("name")) { ax =
	 * fac.getOWLAnnotationAssertionAxiom( trAnnotationProp(tag), sub,
	 * trLiteral(clause.getValue()), annotations); } else if (tag.equals("def"))
	 * { // TODO ax = fac.getOWLAnnotationAssertionAxiom( trAnnotationProp(tag),
	 * sub, trLiteral(clause.getValue()), annotations); } else { // generic ax =
	 * fac.getOWLAnnotationAssertionAxiom( trAnnotationProp(tag), sub,
	 * trLiteral(clause.getValue()), annotations); } return ax; }
	 * 
	 * 
	 * 
	 * private Set<? extends OWLAnnotation> trAnnotations(Clause clause) {
	 * Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>(); Collection<Xref>
	 * xrefs = clause.getXrefs(); if (xrefs != null) { for (Xref x : xrefs) {
	 * OWLAnnotationProperty ap = trAnnotationProp("xref"); OWLAnnotation ann =
	 * fac.getOWLAnnotation(ap, trLiteral(x)); anns.add(ann); } }
	 * Collection<QualifierValue> qvs = clause.getQualifierValues(); if (qvs !=
	 * null) { for (QualifierValue qv : qvs) { OWLAnnotationProperty ap =
	 * trAnnotationProp(qv.getQualifier()); OWLAnnotation ann =
	 * fac.getOWLAnnotation(ap, trLiteral(qv.getValue())); anns.add(ann); } }
	 * 
	 * return anns; }
	 */

	private Set<? extends OWLAnnotation> trAnnotations(
			Collection<Clause> clauses) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClassExpression trRel(String relId, String classId,
			Collection<QualifierValue> quals) {
		OWLClassExpression ex;
		Frame relFrame = obodoc.getTypedefFrame(relId);
		OWLObjectPropertyExpression pe = trObjectProp(relId);
		OWLClassExpression ce = trClass(classId);
		Integer exact = getQVInt("exactCardinality", quals);
		Integer min = getQVInt("minCardinality", quals);
		Integer max = getQVInt("maxCardinality", quals);
		Boolean allSome = getQVBoolean("all_some", quals);
		Boolean allOnly = getQVBoolean("all_only", quals);

		if (exact != null && exact > 0) {
			ex = fac.getOWLObjectExactCardinality(exact, pe, ce);
		} else if ((exact != null && exact == 0) || (max != null && max == 0)) {
			OWLObjectComplementOf ceCompl = fac.getOWLObjectComplementOf(ce);
			ex = fac.getOWLObjectAllValuesFrom(pe, ceCompl);
		} else if (max != null && min != null) {
			ex = fac.getOWLObjectIntersectionOf(
					fac.getOWLObjectMinCardinality(min, pe, ce),
					fac.getOWLObjectMaxCardinality(max, pe, ce));
		} else if (min != null) {
			ex = fac.getOWLObjectMinCardinality(min, pe, ce);
		} else if (max != null) {
			ex = fac.getOWLObjectMaxCardinality(min, pe, ce);
		} else if (allSome != null && allSome && allOnly != null && allOnly) {
			ex = fac.getOWLObjectIntersectionOf(
					fac.getOWLObjectSomeValuesFrom(pe, ce),
					fac.getOWLObjectAllValuesFrom(pe, ce));
		} else if (allOnly != null && allOnly) {
			ex = fac.getOWLObjectAllValuesFrom(pe, ce);

		} else if (relFrame != null
				&& relFrame.getTagValue("is_class_level") != null
				&& (Boolean) relFrame.getTagValue("is_class_level")) {
			// pun
			ex = fac.getOWLObjectHasValue(pe, trIndividual(classId));
		} else {
			// default
			ex = fac.getOWLObjectSomeValuesFrom(pe, ce);
		}
		return ex;
	}

	private Boolean getQVBoolean(String q, Collection<QualifierValue> quals) {
		for (QualifierValue qv : quals) {
			if (qv.getQualifier().equals(q)) {
				Object v = qv.getValue();
				return Boolean.valueOf((String) v);
			}
		}
		return null;
	}

	private Integer getQVInt(String q, Collection<QualifierValue> quals) {
		for (QualifierValue qv : quals) {
			if (qv.getQualifier().equals(q)) {
				Object v = qv.getValue();
				return Integer.valueOf((String) v);
			}
		}
		return null;
	}

	private OWLClass trClass(String classId) {
		IRI iri = oboIdToIRI(classId);
		return fac.getOWLClass(iri);
	}

	private OWLClassExpression trClass(Object v) {
		return trClass((String) v);
	}

	/**
	 * See section "header macros" and treat-xrefs-as-equivalent
	 */
	private String mapPropId(String id) {
		Frame f = obodoc.getTypedefFrame(id);
		if (f != null) {
			Collection<Object> xrefs = f.getTagValues("xref");
			for (Object x : xrefs) {
				String xid = ((Xref) x).getIdref();
				if (obodoc.isTreatXrefsAsEquivalent(getIdPrefix(xid))) {
					return xid;
				}
			}
		}
		return id;
	}

	private String getIdPrefix(String x) {
		String[] parts = x.split(":", 2);
		return parts[0];
	}

	private OWLIndividual trIndividual(String instId) {
		IRI iri = oboIdToIRI(instId);
		return fac.getOWLNamedIndividual(iri);
	}

	/*
	 * private OWLAnnotationProperty trAnnotationProp(String tag) {
	 * OWLAnnotationProperty ap; if (annotationPropertyMap.containsKey(tag)) {
	 * ap = fac.getOWLAnnotationProperty(annotationPropertyMap.get(tag)); } else
	 * { ap =
	 * fac.getOWLAnnotationProperty(IRI.create(DEFAULT_IRI_PREFIX+"IAO_"+tag));
	 * } if (!apToDeclare.contains(ap)) { apToDeclare.add(ap);
	 * add(fac.getOWLDeclarationAxiom(ap)); }
	 * 
	 * return ap; }
	 */

	private OWLObjectProperty trObjectProp(String relId) {
		IRI iri = oboIdToIRI(mapPropId(relId));
		return fac.getOWLObjectProperty(iri);
	}

	private OWLAnnotationValue trLiteral(Object value) {
		if (value instanceof Xref) {
			value = ((Xref) value).getIdref();
		}
		System.out.println("v=" + value);
		return fac.getOWLTypedLiteral((String) value); // TODO
	}

	private IRI oboIdToIRI(String id) {
		if (id.contains(" ")) {
			System.err.println("id contains space: " + id);
			// throw new UnsupportedEncodingException();
			return null;
		}
		String[] idParts = id.split(":", 2);
		String db;
		String localId;
		if (idParts.length > 1) {
			db = idParts[0];
			localId = idParts[1];
		} else if (idParts.length == 0) {
			db = getDefaultIDSpace();
			localId = id;
		} else { // ==1
			db = getDefaultIDSpace();
			localId = idParts[0];
		}

		// No conversion is required if this is already an IRI
		if (db.equals("http")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		} else if (db.equals("https")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		} else if (db.equals("ftp")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		} else if (db.equals("urn")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		}

		String uriPrefix = DEFAULT_IRI_PREFIX + db + "_";
		if (idSpaceMap.containsKey(db)) {
			uriPrefix = idSpaceMap.get(db);
		}

		String safeId;
		try {
			safeId = java.net.URLEncoder.encode(localId, "US-ASCII");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			return null;
		}

		if (safeId.contains(" "))
			safeId = safeId.replace(" ", "_");
		IRI iri = null;
		try {
			iri = IRI.create(uriPrefix + safeId);
		} catch (IllegalArgumentException e) {
			// TODO - define new exception class for this
			// throw new UnsupportedEncodingException();
			return null;
		}

		return iri;
	}

	private String getDefaultIDSpace() {
		// TODO Auto-generated method stub
		return "TODO";
	}

}
