package org.obolibrary.obo2owl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.obolibrary.oboformat.model.*;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRestriction;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class Obo2Owl {

	private static final String DEFAULT_IRI_PREFIX = "http://purl.obolibrary.org/obo/";
	OWLOntologyManager manager;
	OWLOntology owlOntology;
	OWLDataFactory fac;
	OBODoc obodoc;
	Map<String,String> idSpaceMap;
	Map<String,IRI> annotationPropertyMap;
	Set<OWLAnnotationProperty> apToDeclare;



	public Obo2Owl() {
		init();
	}



	private void init() {
		idSpaceMap = new HashMap<String,String>();
		manager = OWLManager.createOWLOntologyManager();
		fac = manager.getOWLDataFactory();
		initAnnotationPropertyMap();
		apToDeclare = new HashSet<OWLAnnotationProperty>();
	}


	public static void convertURL(String iri, String outFile) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Obo2Owl bridge = new Obo2Owl();
		OWLOntologyManager manager = bridge.getManager();
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse(new URL(iri));
		OWLOntology ontology = bridge.convert(obodoc);
		IRI outputStream = IRI.create(outFile);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		System.err.println("saving to "+outputStream+" fmt="+format);
		manager.saveOntology(ontology, format, outputStream);

	}

	public static void convertURL(String iri, String outFile, String defaultOnt) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Obo2Owl bridge = new Obo2Owl();
		OWLOntologyManager manager = bridge.getManager();
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse(new URL(iri));
		addDefaultOntologyHeader(obodoc, defaultOnt);
		OWLOntology ontology = bridge.convert(obodoc);
		IRI outputStream = IRI.create(outFile);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		System.err.println("saving to "+outputStream+" fmt="+format);
		manager.saveOntology(ontology, format, outputStream);

	}

	public static void addDefaultOntologyHeader(OBODoc obodoc, String defaultOnt) {
		Frame hf = obodoc.getHeaderFrame();
		Clause ontClause = hf.getClause("ontology");
		if (ontClause == null) {
			ontClause = new Clause();
			ontClause.setTag("ontology");
			ontClause.setValue(defaultOnt);
			hf.addClause(ontClause);
		}
	}


	protected void initAnnotationPropertyMap() {
		annotationPropertyMap = new HashMap<String,IRI>();
		map("is_obsolete",OWLRDFVocabulary.OWL_DEPRECATED);
		map("name",OWLRDFVocabulary.RDFS_LABEL);
		map("comment",OWLRDFVocabulary.RDFS_COMMENT);
		map("expand_expression_to","IAO_0000424");
		map("expand_assertion_to","IAO_0000425");
		map("def","IAO_0000115");
		map("synonym","IAO_0000118");
		map("is_anti_symmetric","IAO_0000427");
		map("replaced_by","IAO_0100001");
	}

	protected void map(String key, String iri) {
		annotationPropertyMap.put(key, IRI.create(DEFAULT_IRI_PREFIX+iri));
	}

	protected void map(String key, OWLRDFVocabulary vocab) {
		annotationPropertyMap.put(key, vocab.getIRI());
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


	public OWLOntology convert(String oboFile) throws OWLOntologyCreationException {
		
		try{
			OBOFormatParser p = new OBOFormatParser();
			
			OBODoc obodoc = p.parse(oboFile);
		
			return convert(obodoc);
		}catch(IOException ex){
			throw new OWLOntologyCreationException("Error Occured while parsing OBO '" + oboFile + "'", ex);
		}

	}
	

	public OWLOntology convert(OBODoc obodoc) throws OWLOntologyCreationException {
		this.obodoc = obodoc;
		init();
		return tr();
	}

	private OWLOntology tr() throws OWLOntologyCreationException {
		Frame hf = obodoc.getHeaderFrame();
		Clause ontClause = hf.getClause("ontology");
		if (ontClause != null) {
			String ontOboId = (String) ontClause.getValue();
			IRI ontIRI;
			if (ontOboId.contains(":")) {
				ontIRI = IRI.create(ontOboId);
			}
			else {
				ontIRI = IRI.create(DEFAULT_IRI_PREFIX+ontOboId+".owl");
			}
			owlOntology = manager.createOntology(ontIRI);
		}
		else {
			owlOntology = manager.createOntology();
		}
		trHeaderFrame(hf);


		for (Frame f : obodoc.getTermFrames()) {
			trTermFrame(f);
		}
		for (Frame f : obodoc.getTypedefFrames()) {
			trTypedefFrame(f);
		}
		// TODO - individuals
		return owlOntology;
	}

	public void trHeaderFrame(Frame headerFrame) {
		IRI ontIRI = manager.getOntologyDocumentIRI(owlOntology);
		for (String tag : headerFrame.getTags()) {
			
			
			if (tag.equals("ontology")) {

				// already processed
			}
			else if (tag.equals("import")) {
				// TODO
				//fac.getOWLImportsDeclaration(importedOntologyIRI);
			//	manager.applyChange(new AddImport(baseOnt, manager.getOWLDataFactory()
				//		.getOWLImportsDeclaration(importedIRI)));
			}
			else if (tag.equals("data-version")) {
				//fac.getOWLVersionInfo();
				Clause clause = headerFrame.getClause(tag);
				
				OWLAnnotationProperty ap = trAnnotationProp("remark");
				OWLAnnotation ann = fac.getOWLAnnotation(ap, trLiteral(clause.getValue()));
				
				OWLAxiom ax = fac.getOWLAnnotationAssertionAxiom(ontIRI, ann);
				
				manager.applyChange(new AddAxiom(owlOntology, ax));
				
				// TODO
			}else{
				Collection<Clause> clauses = headerFrame.getClauses(tag);
				for(Clause clause: clauses){
					add(trGenericClause(ontIRI, tag, clause));
				}
			}
		}		
	}

	public OWLClassExpression trTermFrame(Frame termFrame) {
		OWLClass cls = trClass(termFrame.getId());
		add(fac.getOWLDeclarationAxiom(cls));
		for (String tag : termFrame.getTags()) {
			//System.out.println("tag:"+tag);
			Collection<Clause> clauses = termFrame.getClauses(tag);
			if (tag.equals("intersection_of")) {
				add(trIntersectionOf(cls,clauses));
			}
			else if (tag.equals("union_of")) {
				add(trUnionOf(cls,clauses));
			}
			else {
				for (Clause clause : clauses) {
					add(trTermClause(cls,tag,clause));
				}
			}
		}
		return cls;
	}

	public OWLNamedObject trTypedefFrame(Frame typedefFrame) {
		// TODO - annotation props
		if (typedefFrame.getTagValue("is_metadata_tag") != null &&
				(Boolean)typedefFrame.getTagValue("is_metadata_tag")) {

			OWLAnnotationProperty p = trAnnotationProp(typedefFrame.getId());
			add(fac.getOWLDeclarationAxiom(p));
			for (String tag : typedefFrame.getTags()) {
				for (Clause clause : typedefFrame.getClauses(tag)) {
					//System.out.println(p+" p "+tag+" t "+clause);
					add(trGenericClause(p, tag, clause));					
				}
			}
			return p;
		}
		else {
			OWLObjectProperty p = trObjectProp(typedefFrame.getId());
			add(fac.getOWLDeclarationAxiom(p));

			for (String tag : typedefFrame.getTags()) {
				Collection<Clause> clauses = typedefFrame.getClauses(tag);
				if (tag.equals("intersection_of")) {
					add(trRelationIntersectionOf(p,clauses));
				}
				else if (tag.equals("union_of")) {
					add(trRelationUnionOf(p,clauses));
				}
				else {
					for (Clause clause : clauses) {
						add(trTypedefClause(p,tag,clause));
					}
				}
			}
			return p;
		}
	}


	private OWLAxiom trRelationUnionOf(OWLProperty p, Collection<Clause> clauses) {
		// TODO Auto-generated method stub
		// not expressible in OWL - use APs. SWRL?
		return null;
	}

	private OWLAxiom trRelationIntersectionOf(OWLProperty p,
			Collection<Clause> clauses) {
		// TODO Auto-generated method stub
		// not expressible in OWL - use APs. SWRL?
		return null;
	}

	private OWLAxiom trUnionOf(OWLClass cls, Collection<Clause> clauses) {
		Set<? extends OWLAnnotation> annotations = trAnnotations(clauses);

		Set<OWLClassExpression> eSet;
		eSet = new HashSet<OWLClassExpression>();
		eSet.add(cls);

		Set<OWLClassExpression> iSet;
		iSet = new HashSet<OWLClassExpression>();
		for (Clause clause: clauses) {
			Collection<QualifierValue> qvs = clause.getQualifierValues();
			// TODO - quals
			if (clause.getValues().size() == 1) {
				iSet.add(trClass(clause.getValue()));
			}
			else {
				System.err.println("union_of n-ary slots not is standard - converting anyway");
				iSet.add(trRel((String)clause.getValue(),
						(String)clause.getValue2(),
						qvs));

			}
		}
		//out.println(cls+" CL:"+clauses+" I:"+iSet+" E:"+eSet);
		eSet.add(fac.getOWLObjectUnionOf(iSet));
		// TODO - fix this
		if (annotations == null || annotations.size() == 0)
			return fac.getOWLEquivalentClassesAxiom(eSet);
		else
			return fac.getOWLEquivalentClassesAxiom(eSet, annotations);
	}

	private OWLAxiom trIntersectionOf(OWLClass cls, Collection<Clause> clauses) {
		Set<? extends OWLAnnotation> annotations = trAnnotations(clauses);

		Set<OWLClassExpression> eSet;
		eSet = new HashSet<OWLClassExpression>();
		eSet.add(cls);

		Set<OWLClassExpression> iSet;
		iSet = new HashSet<OWLClassExpression>();
		for (Clause clause: clauses) {
			Collection<QualifierValue> qvs = clause.getQualifierValues();
			// TODO - quals
			if (clause.getValues().size() == 1) {
				iSet.add(trClass(clause.getValue()));
			}
			else {
				iSet.add(trRel((String)clause.getValue(),
						(String)clause.getValue2(),
						qvs));

			}
		}
		//out.println(cls+" CL:"+clauses+" I:"+iSet+" E:"+eSet);
		eSet.add(fac.getOWLObjectIntersectionOf(iSet));
		// TODO - fix this
		if (annotations == null || annotations.size() == 0)
			return fac.getOWLEquivalentClassesAxiom(eSet);
		else
			return fac.getOWLEquivalentClassesAxiom(eSet, annotations);
	}


	protected void add(OWLAxiom axiom) {
		if (axiom == null) {
			System.err.println("no axiom");
			return;
		}
		//System.out.println("adding:"+axiom);
		AddAxiom addAx = new AddAxiom(owlOntology, axiom);
		//System.out.println(addAx);
		try {
			manager.applyChange(addAx);
		}
		catch (Exception e) {			
			System.err.println(e+"\nCOULD NOT TRANSLATE AXIOM");
		}
	}

	private OWLAxiom trTermClause(OWLClass cls, String tag, Clause clause) {
		OWLAxiom ax;
		Collection<QualifierValue> qvs = clause.getQualifierValues();
		Set<? extends OWLAnnotation> annotations = trAnnotations(clause);
		if (tag.equals("is_a")) {
			ax = fac.getOWLSubClassOfAxiom(cls, 
					trClass((String)clause.getValue()), 
					annotations);
		}
		else if (tag.equals("relationship")) {
			// TODO
			ax = fac.getOWLSubClassOfAxiom(cls, 
					this.trRel((String)clause.getValue(),
							(String)clause.getValue2(),
							qvs),
							annotations);
		}
		else if (tag.equals("disjoint_from")) {

			Set<OWLClassExpression> cSet;
			cSet = new HashSet<OWLClassExpression>();
			cSet.add(cls);
			cSet.add(trClass((String)clause.getValue()));
			ax = fac.getOWLDisjointClassesAxiom(
					cSet, 
					annotations);
		}
		else if (tag.equals("equivalent_to")) {

			Set<OWLClassExpression> cSet;
			cSet = new HashSet<OWLClassExpression>();
			cSet.add(cls);
			cSet.add(trClass((String)clause.getValue()));
			ax = fac.getOWLEquivalentClassesAxiom(
					cSet, 
					annotations);
		}

		else {
			return trGenericClause(cls, tag, clause);
		}
		return ax;
	}

	// no data properties in obo
	private OWLAxiom trTypedefClause(OWLObjectProperty p, String tag, Clause clause) {
		OWLAxiom ax = null;
		Object v = clause.getValue();
		Collection<QualifierValue> qvs = clause.getQualifierValues();
		Set<? extends OWLAnnotation> annotations = trAnnotations(clause);
		if (tag.equals("is_a")) {
			ax = fac.getOWLSubObjectPropertyOfAxiom(
					p,
					trObjectProp((String)v), 
					annotations);
		}
		else if (tag.equals("relationship")) {
			/*
			OWLAnnotationProperty metaProp = (OWLAnnotationProperty) p;
			OWLAnnotationSubject sub = (OWLAnnotationSubject) p;
			OWLAnnotationValue val = 
				trProp((String)clause.getValue2());
			ax = fac.getOWLAnnotationAssertionAxiom(metaProp,sub,val,
					annotations);
			 */
			ax = null; // TODO
		}
		else if (tag.equals("disjoint_from")) {

			Set<OWLObjectPropertyExpression> cSet;
			cSet = new HashSet<OWLObjectPropertyExpression>();
			cSet.add( p);
			cSet.add( trObjectProp((String)v));
			ax = fac.getOWLDisjointObjectPropertiesAxiom(
					cSet, 
					annotations);
		}
		else if (tag.equals("equivalent_to")) {

			Set<OWLObjectPropertyExpression> cSet;
			cSet = new HashSet<OWLObjectPropertyExpression>();
			cSet.add(p);
			cSet.add( trObjectProp((String)v));
			ax = fac.getOWLEquivalentObjectPropertiesAxiom(
					cSet, 
					annotations);
		}
		else if (tag.equals("domain")) {
			ax = fac.getOWLObjectPropertyDomainAxiom(p,
					trClass(v), 
					annotations);
		}
		else if (tag.equals("range")) {
			ax = fac.getOWLObjectPropertyRangeAxiom(p,
					trClass(v), 
					annotations);
		}
		else if (tag.equals("holds_over_chain") || tag.equals("equivalent_to_chain")) {
			List<OWLObjectPropertyExpression> chain =
				new Vector<OWLObjectPropertyExpression>();
			chain.add(trObjectProp(v));
			chain.add(trObjectProp(clause.getValue2()));
			ax = fac.getOWLSubPropertyChainOfAxiom(chain , p, annotations);
			//System.out.println("chain:"+ax);
			// TODO - annotations for equivalent to
		}
		else if (tag.equals("is_transitive") && "true".equals(clause.getValue().toString())) {
			ax = fac.getOWLTransitiveObjectPropertyAxiom(p, annotations);
		}
		else if (tag.equals("is_reflexive") && "true".equals(clause.getValue().toString())) {
			ax = fac.getOWLReflexiveObjectPropertyAxiom(p, annotations);
		}
		else if (tag.equals("is_symmetric") && "true".equals(clause.getValue().toString())) {
			ax = fac.getOWLSymmetricObjectPropertyAxiom(p, annotations);
		}
		else if (tag.equals("is_asymmetric") && "true".equals(clause.getValue().toString())) {
			ax = fac.getOWLAsymmetricObjectPropertyAxiom(p, annotations);
		}
		else if (tag.equals("is_functional") && "true".equals(clause.getValue().toString())) {
			ax = fac.getOWLFunctionalObjectPropertyAxiom(p, annotations);
		}
		else if (tag.equals("is_inverse_functional") && "true".equals(clause.getValue().toString())) {
			ax = fac.getOWLInverseFunctionalObjectPropertyAxiom(p, annotations);
		}
		else {
			return trGenericClause(p, tag, clause);
		}
		// TODO - disjointOver
		return ax;
	}


	private OWLAxiom trGenericClause(OWLNamedObject e, String tag, Clause clause) {
		/*Collection<QualifierValue> qvs = clause.getQualifierValues();
		Set<? extends OWLAnnotation> annotations = trAnnotations(clause);

		OWLAnnotationSubject sub = (OWLAnnotationSubject) e.getIRI();
		//System.out.println(e+" ==> "+sub);
		if (clause.getValue() == null) {
			System.err.println("Problem:"+clause);
		}

		OWLAxiom ax = null;
		if (tag.equals("name")) {
			ax = fac.getOWLAnnotationAssertionAxiom(
					trTagToAnnotationProp(tag),
					sub, 
					trLiteral(clause.getValue()), 
					annotations);
		}
		else if (tag.equals("def")) {
			// TODO
			ax = fac.getOWLAnnotationAssertionAxiom(
					trTagToAnnotationProp(tag),
					sub, 
					trLiteral(clause.getValue()), 
					annotations);
		}
		else {
			// generic
			//System.out.println("generic clause:"+clause);
			ax = fac.getOWLAnnotationAssertionAxiom(
					trTagToAnnotationProp(tag),
					sub, 
					trLiteral(clause.getValue()), 
					annotations);
		}
		// TODO synonyms
		return ax;*/
		
		return trGenericClause(e.getIRI(), tag, clause);
	}


	protected OWLAxiom trGenericClause(OWLAnnotationSubject sub, String tag, Clause clause) {
		Collection<QualifierValue> qvs = clause.getQualifierValues();
		Set<? extends OWLAnnotation> annotations = trAnnotations(clause);
		
//		OWLAnnotationSubject sub = (OWLAnnotationSubject) e.getIRI();
		
		if (clause.getValue() == null) {
			System.err.println("Problem:"+clause);
		}

		OWLAxiom ax = null;
		if (tag.equals("name")) {
			ax = fac.getOWLAnnotationAssertionAxiom(
					trTagToAnnotationProp(tag),
					sub, 
					trLiteral(clause.getValue()), 
					annotations);
		}
		else if (tag.equals("def")) {
			// TODO
			ax = fac.getOWLAnnotationAssertionAxiom(
					trTagToAnnotationProp(tag),
					sub, 
					trLiteral(clause.getValue()), 
					annotations);
		}
		else {
			// generic
			//System.out.println("generic clause:"+clause);
			ax = fac.getOWLAnnotationAssertionAxiom(
					trTagToAnnotationProp(tag),
					sub, 
					trLiteral(clause.getValue()), 
					annotations);
		}
		// TODO synonyms
		return ax;
	}
	
	

	private Set<? extends OWLAnnotation> trAnnotations(Clause clause) {
		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		Collection<Xref> xrefs = clause.getXrefs();
		if (xrefs != null) {
			for (Xref x : xrefs) {
				OWLAnnotationProperty ap = trTagToAnnotationProp("xref");
				OWLAnnotation ann = fac.getOWLAnnotation(ap, trLiteral(x));
				anns.add(ann);
			}
		}
		Collection<QualifierValue> qvs = clause.getQualifierValues();
		if (qvs != null) {
			for (QualifierValue qv : qvs) {
				OWLAnnotationProperty ap = trTagToAnnotationProp(qv.getQualifier());
				OWLAnnotation ann = fac.getOWLAnnotation(ap, trLiteral(qv.getValue()));
				anns.add(ann);
			}
		}

		return anns;
	}

	private Set<? extends OWLAnnotation> trAnnotations(
			Collection<Clause> clauses) {
		// TODO Auto-generated method stub
		return null;
	}


	public OWLClassExpression trRel(String relId, String classId, Collection<QualifierValue> quals) {
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
			ex = fac.getOWLObjectExactCardinality(exact,pe,ce);
		}
		else if ((exact != null && exact == 0) ||
				(max != null && max == 0)) {
			OWLObjectComplementOf ceCompl = fac.getOWLObjectComplementOf(ce);
			ex = fac.getOWLObjectAllValuesFrom(pe,
					ceCompl);
		}
		else if (max != null && min != null) {
			ex = fac.getOWLObjectIntersectionOf(
					fac.getOWLObjectMinCardinality(min, pe, ce),
					fac.getOWLObjectMaxCardinality(max, pe, ce));
		}
		else if (min != null) {
			ex = fac.getOWLObjectMinCardinality(min, pe, ce);
		}
		else if (max != null) {
			ex = fac.getOWLObjectMaxCardinality(min, pe, ce);
		}
		else if (allSome != null && allSome &&
				allOnly != null && allOnly) {
			ex = fac.getOWLObjectIntersectionOf(
					fac.getOWLObjectSomeValuesFrom(pe, ce),
					fac.getOWLObjectAllValuesFrom(pe, ce));			
		}
		else if (allOnly != null && allOnly) {
			ex = fac.getOWLObjectAllValuesFrom(pe, ce);

		}
		else if (relFrame != null && relFrame.getTagValue("is_class_level") != null &&
				(Boolean)relFrame.getTagValue("is_class_level")) {
			// pun
			ex = fac.getOWLObjectHasValue(pe,trIndividual(classId));
		}
		else {
			// default
			ex = fac.getOWLObjectSomeValuesFrom(pe,ce);
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
		return trClass((String)v);
	}

	/**
	 * See section "header macros" and treat-xrefs-as-equivalent
	 */
	private String mapPropId(String id) {
		Frame f = obodoc.getTypedefFrame(id);
		if (f != null) {
			Collection<Object> xrefs = f.getTagValues("xref");
			for (Object x : xrefs) {
				String xid = ((Xref)x).getIdref();
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

	private OWLAnnotationProperty trTagToAnnotationProp(String tag) {
		OWLAnnotationProperty ap;
		if (annotationPropertyMap.containsKey(tag)) {
			ap = fac.getOWLAnnotationProperty(annotationPropertyMap.get(tag));
		}
		else {
			ap = fac.getOWLAnnotationProperty(IRI.create(DEFAULT_IRI_PREFIX+"IAO_"+tag));
		}
		if (!apToDeclare.contains(ap)) {
			apToDeclare.add(ap);
			add(fac.getOWLDeclarationAxiom(ap));
		}

		return ap;
	}

	private OWLAnnotationProperty trAnnotationProp(String relId) {
		IRI iri = oboIdToIRI(mapPropId(relId));
		return fac.getOWLAnnotationProperty(iri);	
	}


	private OWLObjectProperty trObjectProp(String relId) {
		IRI iri = oboIdToIRI(mapPropId(relId));
		return fac.getOWLObjectProperty(iri);	
	}


	private OWLObjectPropertyExpression trObjectProp(Object v) {
		IRI iri = oboIdToIRI(mapPropId((String) v));
		return fac.getOWLObjectProperty(iri);	
	}



	protected OWLAnnotationValue trLiteral(Object value) {
		if (value instanceof Xref) {
			value = ((Xref)value).getIdref();
		}
		else if (value instanceof Date) {
			// TODO
			value = ((Date)value).toString();
		}
		else if (! (value instanceof String)) {
			// TODO
			// e.g. boolean
			value = value.toString();
		}
		//System.out.println("v="+value);
		return fac.getOWLTypedLiteral((String)value); // TODO
	}

	private IRI oboIdToIRI(String id) {
		if (id.contains(" ")) {
			System.err.println("id contains space: "+id);
			//throw new UnsupportedEncodingException();
			return null;
		}

		// TODO - treat_xrefs_as_equivalent
		// special case rule for relation xrefs:
		Frame tdf = obodoc.getTypedefFrame(id);
		if (tdf != null) {
			Object xref = tdf.getTagValue("xref");
			if (xref != null) {
				String xid = ((Xref)xref).getIdref();

				// RO and BFO have special status.
				// avoid cycles (in case of self-xref)
				if ((xid.startsWith("RO") ||
						xid.startsWith("BFO")) &&
						!xid.equals(id)) {
					return oboIdToIRI(xid);
				}
			}
		}
		String[] idParts = id.split(":", 2);
		String db;
		String localId;
		if (idParts.length > 1) {
			db = idParts[0];
			localId = idParts[1];
		}
		else if (idParts.length == 0) {
			db = getDefaultIDSpace();
			localId = id;
		}
		else { // ==1
			db = getDefaultIDSpace();
			localId = idParts[0];
		}

		// No conversion is required if this is already an IRI
		if (db.equals("http")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		}
		else if (db.equals("https")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		}
		else if (db.equals("ftp")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		}
		else if (db.equals("urn")) { // TODO - roundtrip from other schemes
			return IRI.create(id);
		}

		String uriPrefix = DEFAULT_IRI_PREFIX+db+"_";
		if (idSpaceMap.containsKey(db)) {
			uriPrefix = idSpaceMap.get(db);
		}

		String safeId;
		try {
			safeId = java.net.URLEncoder.encode(localId,"US-ASCII");
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

		return  iri;	
	}

	private String getDefaultIDSpace() {
		// TODO Auto-generated method stub
		return "TODO";
	}




}
