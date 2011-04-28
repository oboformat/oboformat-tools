package org.obolibrary.obo2owl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatConstants;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class Owl2Obo {

	private static Logger LOG = Logger.getLogger(Owl2Obo.class);

	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLOntology owlOntology;
	OWLDataFactory fac;
	OBODoc obodoc;
	Map<String, String> idSpaceMap;
	// Map<String,IRI> annotationPropertyMap;
	public static Map<String, String> annotationPropertyMap = initAnnotationPropertyMap();
	Set<OWLAnnotationProperty> apToDeclare;

	//private String ontologyId;
	
	
	private void init() {
		idSpaceMap = new HashMap<String, String>();
		manager = OWLManager.createOWLOntologyManager();
		fac = manager.getOWLDataFactory();
		apToDeclare = new HashSet<OWLAnnotationProperty>();
	}

	private static HashMap<String, String>  initAnnotationPropertyMap() {
		/*annotationPropertyMap = new HashMap<String, String>();
		annotationPropertyMap.put(OWLRDFVocabulary.RDFS_LABEL.getIRI()
				.toString(), "name");
		annotationPropertyMap.put(OWLRDFVocabulary.RDFS_COMMENT.getIRI()
				.toString(), "comment");
		annotationPropertyMap.put(Obo2Owl.DEFAULT_IRI_PREFIX + "IAO_0000424",
				"expand_expression_to");
		annotationPropertyMap.put(Obo2Owl.DEFAULT_IRI_PREFIX + "IAO_0000425",
				"expand_assertion_to");
		annotationPropertyMap.put(Obo2Owl.DEFAULT_IRI_PREFIX + "IAO_0000115", "def");
		annotationPropertyMap
				.put(Obo2Owl.DEFAULT_IRI_PREFIX + "IAO_0000118", "synonym");
		annotationPropertyMap.put(Obo2Owl.DEFAULT_IRI_PREFIX + "IAO_0000427",
				"is_anti_symmetric");
		annotationPropertyMap.put(OBO2 DEFAULT_IRI_PREFIX + "IAO_0100001",
				"replaced_by");
		annotationPropertyMap
				.put(DEFAULT_IRI_PREFIX + "remark", "data-version");*/
		
		HashMap<String, String> map = new HashMap<String, String>();
		for(String key: Obo2Owl.annotationPropertyMap.keySet()){
			IRI propIRI =Obo2Owl.annotationPropertyMap.get(key);
			map.put(propIRI.toString(), key);
		}
		
		return map;

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
			}else if(ax instanceof OWLEquivalentObjectPropertiesAxiom) {
				tr((OWLEquivalentObjectPropertiesAxiom)ax);
			}else if(ax instanceof OWLSubObjectPropertyOfAxiom){
				tr((OWLSubObjectPropertyOfAxiom)ax);
			}else if(ax instanceof OWLObjectPropertyRangeAxiom){
				tr((OWLObjectPropertyRangeAxiom)ax);
			}else if (ax instanceof OWLFunctionalObjectPropertyAxiom){
				tr((OWLFunctionalObjectPropertyAxiom)ax);
			}else if(ax instanceof OWLSymmetricObjectPropertyAxiom){
				tr((OWLSymmetricObjectPropertyAxiom)ax);
			}else if (ax instanceof OWLAsymmetricObjectPropertyAxiom){
				tr((OWLAsymmetricObjectPropertyAxiom)ax);
			}else if(ax instanceof OWLObjectPropertyDomainAxiom){
				tr((OWLObjectPropertyDomainAxiom)ax);
			}else if(ax instanceof OWLInverseFunctionalObjectPropertyAxiom){
				tr((OWLInverseFunctionalObjectPropertyAxiom)ax);
			}else if(ax instanceof OWLDisjointObjectPropertiesAxiom){
				tr((OWLDisjointObjectPropertiesAxiom)ax);
			}else if (ax instanceof OWLReflexiveObjectPropertyAxiom){
				tr((OWLReflexiveObjectPropertyAxiom)ax);
			}else if(ax instanceof OWLTransitiveObjectPropertyAxiom){
				tr((OWLTransitiveObjectPropertyAxiom)ax);
			}else if(ax instanceof OWLSubPropertyChainOfAxiom){
				tr((OWLSubPropertyChainOfAxiom)ax);
			}else{
				if(!(ax instanceof OWLAnnotationAssertionAxiom)){
					LOG.warn("The axiom is not translated: " + ax);
				}
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
				LOG.error(ex.getMessage(), ex);
			}
		}
	}

	
	private void trObjectProperty(OWLObjectProperty prop, String tag, String value){
		if(prop == null || value == null)
			return;
		
		Frame f = getTypedefFrame(prop);
		Clause clause = new Clause();
		clause.setTag(tag);
		
		clause.addValue(value);
		f.addClause(clause);
		
	}
	
	private void trNaryPropertyAxiom(OWLNaryPropertyAxiom<OWLObjectPropertyExpression> ax, String tag){
		Set<OWLObjectPropertyExpression> set = ax.getProperties();
		
		if(set.size()>1){
			boolean first = true;
			OWLObjectProperty prop = null;
			String disjointFrom = null;
			for(OWLObjectPropertyExpression ex: set){
				if(first){
					first = false;
					if(ex instanceof OWLObjectProperty)
						prop = (OWLObjectProperty)ex;
				}else{
					disjointFrom = getIdentifier(ex);
				}
			}
			
			trObjectProperty(prop, tag, disjointFrom);
			
		}else{
			LOG.warn("Unhandeled axiom: " + ax);
		}
		
	}
	
	private void tr(OWLSubPropertyChainOfAxiom ax){
		System.out.println("...." + ax);
	}
	
	private void tr(OWLEquivalentObjectPropertiesAxiom ax){
		trNaryPropertyAxiom(ax, OboFormatTag.TAG_EQUIVALENT_TO.getTag());
	}
	
	private void tr(OWLTransitiveObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_TRANSITIVE.getTag(), "true");
		}
	}
	
	private void tr(OWLDisjointObjectPropertiesAxiom ax){
		trNaryPropertyAxiom(ax, OboFormatTag.TAG_DISJOINT_FROM.getTag());
		/*Set<OWLObjectPropertyExpression> set = ax.getProperties();
		
		if(set.size()>1){
			boolean first = true;
			OWLObjectProperty prop = null;
			String disjointFrom = null;
			for(OWLObjectPropertyExpression ex: set){
				if(first){
					first = false;
					if(ex instanceof OWLObjectProperty)
						prop = (OWLObjectProperty)ex;
				}else{
					disjointFrom = getIdentifier(ex);
				}
			}
			
			trObjectProperty(prop, OboFormatTag.TAG_DISJOINT_FROM.getTag(), disjointFrom);
			
		}else{
			LOG.warn("Unhandeled axiom: " + ax);
		}*/
	}
	
	private void tr(OWLReflexiveObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_REFLEXIVE.getTag(), "true");
		}
		
	}
	
	private void tr(OWLInverseFunctionalObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_INVERSE_FUNCTIONAL.getTag(), "true");
		}
	}
	
	private void tr(OWLObjectPropertyDomainAxiom ax){
		String range = getIdentifier(ax.getDomain());
		OWLObjectPropertyExpression prop = ax.getProperty();
		
		if(range != null && prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_DOMAIN.getTag(), range);
		}
	}

	private void tr(OWLAsymmetricObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_ASYMMETRIC.getTag(), "true");
		}
		
	}
	
	
	private void tr(OWLSymmetricObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_SYMMETRIC.getTag(), "true");
		}
		
	}
	
	
	private void tr(OWLFunctionalObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_FUNCTIONAL.getTag(), "true");
		}
	}
	
	private void tr(OWLObjectPropertyRangeAxiom ax){
	
		String range = getIdentifier(ax.getRange());
		OWLObjectPropertyExpression prop = ax.getProperty();
		
		if(range != null && prop instanceof OWLObjectProperty){
			trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_RANGE.getTag(), range);
		}
		
	}
	
	
	private void tr(OWLSubObjectPropertyOfAxiom ax){
		
		OWLObjectPropertyExpression sup = ax.getSuperProperty();
		OWLObjectPropertyExpression sub = ax.getSuperProperty();
		
		
		if(sub instanceof OWLObjectProperty && sup instanceof OWLObjectProperty){
		
			String supId = getIdentifier(sup);
			
			Frame f = getTypedefFrame((OWLEntity)ax.getSubProperty());
			Clause clause = new Clause();
			clause.setTag(OboFormatTag.TAG_IS_A.getTag());
			clause.addValue(supId);
			f.addClause(clause);
			
		}else{
			LOG.warn("Unhandled axiom: " + ax);
		}
		
		
	}
	
	private void tr(OWLAnnotationAssertionAxiom aanAx, Frame frame) {

		OWLAnnotationProperty prop = aanAx.getProperty();
		String tag = owlObjectToTag(prop);

		if (tag != null) {
			
			String value = aanAx.getValue().toString();
			if(aanAx.getValue() instanceof OWLLiteral){
				value = ((OWLLiteral) aanAx.getValue()).getLiteral();
			}else if(aanAx.getValue() instanceof IRI){
				value =getIdentifier((IRI)aanAx.getValue());
			}
			
			Clause clause = new Clause();
			clause.setTag(tag);
			clause.addValue(value);
			frame.addClause(clause);
			OboFormatTag _tag = OBOFormatConstants.getTag(tag);
			if(_tag == OboFormatTag.TAG_DEF){
				for(OWLAnnotation aan: aanAx.getAnnotations()){
					String propId = owlObjectToTag(aan.getProperty());
					
					if("xref".equals(propId)){
						String xrefValue = ((OWLLiteral) aan.getValue()).getLiteral();
						Xref xref = new Xref(xrefValue);
						clause.addXref(xref);
					}
				}
			}else if(_tag == OboFormatTag.TAG_REMARK){
			
					String version = ((OWLLiteral) aanAx.getValue()).getLiteral();
					clause.setTag(OboFormatTag.TAG_DATA_VERSION.getTag());
					clause.setValue(version);
					
			}else if(_tag == OboFormatTag.TAG_SYNONYM){
				String scope = null;
				String type = null;
				clause.setXrefs(new Vector<Xref>());
				for(OWLAnnotation aan: aanAx.getAnnotations()){
					String propId = owlObjectToTag(aan.getProperty());
					
					if(OboFormatTag.TAG_XREF.getTag().equals(propId)){
						String xrefValue = ((OWLLiteral) aan.getValue()).getLiteral();
						Xref xref = new Xref(xrefValue);
						clause.addXref(xref);
					}else if("scope".equals(propId)){
						scope = ((OWLLiteral) aan.getValue()).getLiteral();
					}else if("type".equals(propId)){
						type = ((OWLLiteral) aan.getValue()).getLiteral();
					}
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
	
	public static String getOntologyId(OWLOntology ontology){
		String id = getIdentifier(ontology.getOntologyID().getOntologyIRI());

		int index = id.lastIndexOf(".owl");
		if(index>0){
			id = id.substring(0, index);
		}
		
		return id;
	}

	private void tr(OWLOntology ontology) {
		Frame f = new Frame(FrameType.HEADER);

		this.obodoc.setHeaderFrame(f);

		String id = getOntologyId(this.owlOntology);
		//this.ontologyId = id;
		
		Clause c = new Clause();
		c.setTag(OboFormatTag.TAG_ONTOLOGY.getTag());
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
			c.setTag(OboFormatTag.TAG_EQUIVALENT_TO.getTag());
			c.setValue(cls2);
			f.addClause(c);
		} else if (ce2 instanceof OWLObjectUnionOf) {
			List<OWLClassExpression> list2 = ((OWLObjectUnionOf) ce2)
					.getOperandsAsList();
			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_UNION_OF.getTag());
			c.setValue(getIdentifier(list2.get(0)));
			f.addClause(c);
		} else if (ce2 instanceof OWLObjectIntersectionOf) {

			List<OWLClassExpression> list2 = ((OWLObjectIntersectionOf) ce2).getOperandsAsList();
			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_INTERSECTION_OF.getTag());
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
		c.setTag(OboFormatTag.TAG_DISJOINT_FROM.getTag());
		c.setValue(cls2);
		f.addClause(c);
	}

	private void tr(OWLDeclarationAxiom axiom) {
		OWLEntity entity = axiom.getEntity();

		Set<OWLAnnotationAssertionAxiom> set  = entity.getAnnotationAssertionAxioms(this.owlOntology);

		if(set.isEmpty())
			return;
		
		Frame f = null;
		if (entity instanceof OWLClass) {
			//f = new Frame(FrameType.TERM);
			f= getTermFrame(entity);
		} else if (entity instanceof OWLObjectProperty) {
			//f = new Frame(FrameType.TYPEDEF);
			f = getTypedefFrame(entity);
		}

		if (f != null) {
			for (OWLAnnotationAssertionAxiom aanAx : set) {
				
				tr(aanAx, f);
			}

			add(f);
		
		}

	}

	
	public static String getIdentifier(OWLObject obj) {
		if(obj instanceof OWLEntity)
			return getIdentifier(((OWLEntity)obj).getIRI());
		
		return null;
	}

	public static String getIdentifier(IRI iriId) {
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
	
	
	public static String owlObjectToTag(OWLObject obj){
		
		if(!(obj instanceof OWLNamedObject)){
			return null;
		}
		
		String iri = ((OWLNamedObject) obj).getIRI().toString();
	
		String tag = annotationPropertyMap.get(iri);

		if (tag == null) {
			String prefix = Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_";
			if (iri.startsWith(prefix)) {
				tag = iri.substring(prefix.length());
			}
			
			
		}
		return tag;
	}
	
	/*
	public static String propToTag(OWLAnnotationProperty prop) {
		String iri = prop.getIRI().toString();
		String tag = annotationPropertyMap.get(iri);

		if (tag == null) {
			String prefix = Obo2Owl.DEFAULT_IRI_PREFIX + "IAO_";
			if (iri.startsWith(prefix)) {
				tag = iri.substring(prefix.length());
			}
		}
		return tag;
	}*/

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
	
	private Frame getTypedefFrame(OWLEntity entity){
		String id = getIdentifier(entity.getIRI());
		Frame f = this.obodoc.getTypedefFrame(id);
		if (f == null) {
			f = new Frame(FrameType.TYPEDEF);
			f.setId(id);
			add(f);
		}

		return f;
		
	}

	
	private void tr(OWLClassAssertionAxiom ax){
		OWLObject cls = ax.getClassExpression();
		
		if(!(cls instanceof OWLClass))
			return;
		
		String clsIRI = ((OWLClass) cls).getIRI().toString();
		
		
		if(Obo2Owl.IRI_CLASS_SYNONYMTYPEDEF.equals(clsIRI)){
			Frame f = this.obodoc.getHeaderFrame();
			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_SYNONYMTYPEDEF.getTag());

			OWLNamedIndividual indv =(OWLNamedIndividual) ax.getIndividual();
			String indvId = getIdentifier(indv);
			
			// TODO: full specify this in the spec document.
			// we may want to allow full IDs for subsets in future.
			// here we would have a convention that an unprefixed subsetdef/synonymtypedef
			// gets placed in a temp ID space, and only this id space is stripped
			indvId = indvId.replaceFirst(".*:", "");
			c.addValue(indvId);

			c.addValue(indvId);
			
			String nameValue = "";
			String scopeValue = null;
			for(OWLAnnotation ann: indv.getAnnotations(owlOntology)){
				String propId = ann.getProperty().getIRI().toString();
				String value = ((OWLLiteral) ann.getValue()).getLiteral();

				if(OWLRDFVocabulary.RDFS_LABEL.getIRI().toString().equals(propId)){
					nameValue = "\"" +value + "\"";
				}else
					scopeValue = value;
			}
			
				c.addValue(nameValue);
				
				if(scopeValue != null){
					c.addValue(scopeValue);
				}
			
			f.addClause(c);
		}else if(Obo2Owl.IRI_CLASS_SUBSETDEF.equals(clsIRI)){
			Frame f = this.obodoc.getHeaderFrame();
			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_SUBSETDEF.getTag());

			OWLNamedIndividual indv =(OWLNamedIndividual) ax.getIndividual();
			String indvId = getIdentifier(indv);
			
			// TODO: full specify this in the spec document.
			// we may want to allow full IDs for subsets in future.
			// here we would have a convention that an unprefixed subsetdef/synonymtypedef
			// gets placed in a temp ID space, and only this id space is stripped
			indvId = indvId.replaceFirst(".*:", "");
			c.addValue(indvId);
			
			String nameValue = "";
			for(OWLAnnotation ann: indv.getAnnotations(owlOntology)){
				String propId = ann.getProperty().getIRI().toString();
				String value = ((OWLLiteral) ann.getValue()).getLiteral();

				if(OWLRDFVocabulary.RDFS_LABEL.getIRI().toString().equals(propId)){
					nameValue = "\"" +value + "\"";
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
				c.setTag(OboFormatTag.TAG_IS_A.getTag());
				c.setValue(getIdentifier(sup));
				f.addClause(c);
			} else if (sup instanceof OWLObjectSomeValuesFrom ||sup instanceof OWLObjectAllValuesFrom ) {
				OWLQuantifiedRestriction r = (OWLQuantifiedRestriction) sup;

				Clause c = new Clause();
				c.setTag(OboFormatTag.TAG_RELATIONSHIP.getTag());
				c.addValue(getIdentifier(r.getProperty()));
				c.addValue(getIdentifier(r.getFiller()));
				f.addClause(c);
			} else {
				LOG.warn("Cann't translate axiom: " + ax);
			}
		} else {
			LOG.warn("Cann't translate axiom: " + ax);
		}
	}



}
