package org.obolibrary.obo2owl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.QualifierValue;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatConstants;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
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
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
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
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

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

	private String ontologyId;
	//private String ontologyId;

	private boolean strictConversion;

	private void init() {
		idSpaceMap = new HashMap<String, String>();
		idSpaceMap.put("http://www.obofoundry.org/ro/ro.owl#", "OBO_REL");

		manager = OWLManager.createOWLOntologyManager();
		fac = manager.getOWLDataFactory();
		apToDeclare = new HashSet<OWLAnnotationProperty>();
	}

	public Owl2Obo(){
		init();
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

	public void setStrictConversion(boolean b){
		this.strictConversion = b;
	}

	public boolean getStrictConversion(){
		return this.strictConversion;
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
		if(ont != null)
			this.ontologyId = getOntologyId(ont);
		else
			this.ontologyId = "TODO";
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
			}else if(ax instanceof OWLSubAnnotationPropertyOfAxiom){
				tr((OWLSubAnnotationPropertyOfAxiom)ax);
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
			}else if(ax instanceof OWLInverseObjectPropertiesAxiom){
				tr((OWLInverseObjectPropertiesAxiom)ax);
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
					String logErr = "the axiom is not translated : " + ax;
					String err = "The conversion is halted as the axiom is not translated: " + ax;
					if(strictConversion){
						logErr = err;
					}

					LOG.warn(logErr);

					if(strictConversion)
						throw new RuntimeException(err);
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


	private boolean trObjectProperty(OWLObjectProperty prop, String tag, String value){
		if(prop == null || value == null)
			return false;

		Frame f = getTypedefFrame(prop);
		Clause clause = new Clause();
		clause.setTag(tag);

		clause.addValue(value);
		f.addClause(clause);
		
		return true;

	}

	private boolean trObjectProperty(OWLObjectProperty prop, String tag, Boolean value){
		if(prop == null || value == null)
			return false;

		Frame f = getTypedefFrame(prop);
		Clause clause = new Clause();
		clause.setTag(tag);

		clause.addValue(value);
		f.addClause(clause);

		return true;
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
					disjointFrom = this.getIdentifier(ex); //getIdentifier(ex);
				}
			}

			if( trObjectProperty(prop, tag, disjointFrom) ){
				return;
			}

		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);


	}

	private void tr(OWLSubPropertyChainOfAxiom ax){

		OWLObjectPropertyExpression p= ax.getSuperProperty();

		List<OWLObjectPropertyExpression> list = ax.getPropertyChain();

		if(list.size()!=2){
			LOG.warn("The axiom '" + ax + "' is not translated.");
			return;
		}

		Frame f = getTypedefFrame((OWLObjectProperty)p);

		String rel1 = getIdentifier(list.get(0));
		String rel2 = getIdentifier(list.get(1));

		if(rel1 == null || rel2 == null){
			String logErr = "the axiom is not translated : " + ax;
			String err = "The conversion is halted as the axiom is not translated: " + ax;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);

			return;
		}

		OboFormatTag tag = OboFormatTag.TAG_HOLDS_OVER_CHAIN;

		for(OWLAnnotation ann: ax.getAnnotations()){

			if(Obo2Owl.IRI_PROP_isReversiblePropertyChain.equals(ann.getProperty().getIRI().toString())){
				tag = OboFormatTag.TAG_EQUIVALENT_TO_CHAIN;
				break;
			}
		}


		Clause clause = new Clause();
		clause.setTag(tag.getTag());
		clause.addValue(rel1);
		clause.addValue(rel2);
		f.addClause(clause);



	}

	private void tr(OWLEquivalentObjectPropertiesAxiom ax){
		trNaryPropertyAxiom(ax, OboFormatTag.TAG_EQUIVALENT_TO.getTag());
	}

	private void tr(OWLTransitiveObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_TRANSITIVE.getTag(), Boolean.TRUE) )
				return;
		}
		
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		
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
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_REFLEXIVE.getTag(), Boolean.TRUE) )
				return;
		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		

	}

	private void tr(OWLInverseFunctionalObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_INVERSE_FUNCTIONAL.getTag(), Boolean.TRUE) )
				return;
		}
		
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		
	}
	private void tr(OWLInverseObjectPropertiesAxiom ax){
		OWLObjectPropertyExpression prop1 = ax.getFirstProperty();
		OWLObjectPropertyExpression prop2 = ax.getSecondProperty();

		if(prop1 instanceof OWLObjectProperty && prop2 instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop1, OboFormatTag.TAG_INVERSE_OF.getTag(), this.getIdentifier(prop2)) )
				return;
		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		
	}


	private void tr(OWLObjectPropertyDomainAxiom ax){
		String range = this.getIdentifier(ax.getDomain()); //getIdentifier(ax.getDomain());
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(range != null && prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_DOMAIN.getTag(), range) ){
				return;
			}
		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		
		
	}

	private void tr(OWLAsymmetricObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_ASYMMETRIC.getTag(), Boolean.TRUE) ){
				return;
			}
		}

		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		
		
	}


	private void tr(OWLSymmetricObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_SYMMETRIC.getTag(), Boolean.TRUE) )
				return;
		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		

	}


	private void tr(OWLFunctionalObjectPropertyAxiom ax){
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_IS_FUNCTIONAL.getTag(), Boolean.TRUE) )
				return;
		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		
	}

	private void tr(OWLObjectPropertyRangeAxiom ax){

		String range = this.getIdentifier(ax.getRange()); //getIdentifier(ax.getRange());
		OWLObjectPropertyExpression prop = ax.getProperty();

		if(range != null && prop instanceof OWLObjectProperty){
			if( trObjectProperty((OWLObjectProperty)prop, OboFormatTag.TAG_RANGE.getTag(), range) )
				return;
		}
		
		String logErr = "the axiom is not translated : " + ax;
		String err = "The conversion is halted as the axiom is not translated: " + ax;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		

	}


	private void tr(OWLSubObjectPropertyOfAxiom ax){

		OWLObjectPropertyExpression sup = ax.getSuperProperty();
		OWLObjectPropertyExpression sub = ax.getSubProperty();


		if(sub instanceof OWLObjectProperty && sup instanceof OWLObjectProperty){

			String supId = this.getIdentifier(sup); //getIdentifier(sup);

			if(supId.startsWith("owl:")){
				return;
			}

			Frame f = getTypedefFrame((OWLEntity)ax.getSubProperty());
			Clause clause = new Clause();
			clause.setTag(OboFormatTag.TAG_IS_A.getTag());
			clause.addValue(supId);
			f.addClause(clause);

		}else{
			String logErr = "the axiom is not translated : " + ax;
			String err = "The conversion is halted as the axiom is not translated: " + ax;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);

		}


	}

	private void tr(OWLSubAnnotationPropertyOfAxiom ax){

		OWLAnnotationProperty sup = ax.getSuperProperty();
		OWLAnnotationProperty sub = ax.getSubProperty();

		String _tag = owlObjectToTag(sup);
		if (_tag .equals(OboFormatTag.TAG_SYNONYMTYPEDEF.getTag())) {
			String name = "";
			String scope = "";
			for(OWLAnnotationAssertionAxiom axiom: sub.getAnnotationAssertionAxioms(owlOntology)){
				String tg = owlObjectToTag(axiom.getProperty());
				if(OboFormatTag.TAG_NAME.getTag().equals(tg)){
					name = getLiteral((OWLLiteral)axiom.getValue());
				}else if(OboFormatTag.TAG_SCOPE.getTag().equals(tg)){
					scope = owlObjectToTag(axiom.getValue());
				}
			}

			Frame hf = obodoc.getHeaderFrame();
			Clause clause = new Clause();
			clause.setTag(OboFormatTag.TAG_SYNONYMTYPEDEF.getTag());
			clause.addValue(this.getIdentifier(sub));
			clause.addValue(name);
			clause.addValue(scope);
			hf.addClause(clause);
			return;

		}
		else if (_tag .equals(OboFormatTag.TAG_SUBSETDEF.getTag())) {
			String comment = "";
			for(OWLAnnotationAssertionAxiom axiom: sub.getAnnotationAssertionAxioms(owlOntology)){
				String tg = owlObjectToTag(axiom.getProperty());
				if(OboFormatTag.TAG_COMMENT.getTag().equals(tg)){
					comment = getLiteral((OWLLiteral)axiom.getValue());
					break;
				}
			}

			Frame hf = obodoc.getHeaderFrame();
			Clause clause = new Clause();
			clause.setTag(OboFormatTag.TAG_SUBSETDEF.getTag());
			clause.addValue(this.getIdentifier(sub));
			clause.addValue(comment);
			hf.addClause(clause);
			return;

		}

		if(sub instanceof OWLObjectProperty && sup instanceof OWLObjectProperty){

			String supId = this.getIdentifier(sup); //getIdentifier(sup);

			if(supId.startsWith("owl:")){
				return;
			}

			Frame f = getTypedefFrame((OWLEntity)ax.getSubProperty());
			Clause clause = new Clause();
			clause.setTag(OboFormatTag.TAG_IS_A.getTag());
			clause.addValue(supId);
			f.addClause(clause);

		}else{
			String logErr = "the axiom is not translated : " + ax;
			String err = "The conversion is halted as the axiom is not translated: " + ax;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);

		}


	}


	private Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");

	private String getLiteral(OWLLiteral literal){
		String val = literal.getLiteral();


		Matcher m = CRLF.matcher(val);
		if(m.find())
			val= m.replaceAll("\\\\n");

		//val = val.replaceAll("\n|\r", "\\\\\n");
		//	val = val.replaceAll("[\\]", "\\\\\\\\");
		//val = val.replace("\\", "\\\\");
		//val.re

		return val;
	}

	private Pattern absoulteURLPattern = Pattern.compile("<\\s*http.*?>");

	private void tr(OWLAnnotationAssertionAxiom aanAx, Frame frame) {
		tr(aanAx.getProperty(), aanAx.getValue(), aanAx.getAnnotations(), frame);
		
		/*if( !tr(aanAx.getProperty(), aanAx.getValue(), aanAx.getAnnotations(), frame)){
			String logErr = "the axiom is not translated : " + aanAx;
			String err = "The conversion is halted as the axiom is not translated: " + aanAx;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);
				
		}*/
	}	

	private boolean tr(OWLAnnotationProperty prop, OWLAnnotationValue annVal, Set<OWLAnnotation> qualifiers,  Frame frame) {

		//		OWLAnnotationProperty prop = aanAx.getProperty();
		String tag = owlObjectToTag(prop);


		//		OboFormatTag _tag = OBOFormatConstants.getTag(tag);

		if (tag != null) {

			String value = annVal.toString();

			if(annVal instanceof OWLLiteral){
				value = getLiteral((OWLLiteral) annVal);
			}else if(annVal instanceof IRI){
				value = getIdentifier((IRI)annVal); //getIdentifier((IRI)aanAx.getValue());
			}

			if(OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(tag)){
				//value = aanAx.getValue().toString();
				//value = getLiteral((OWLLiteral)aanAx.getValue());
				Matcher matcher = absoulteURLPattern.matcher(value);
				while(matcher.find()){
					String m = matcher.group();
					m = m.replace("<", "");
					m = m.replace(">", "");
					int i = m.lastIndexOf("/");
					m = m.substring(i+1);

					value = value.replace(matcher.group(), m);
				}

			}


			OboFormatTag _tag = OBOFormatConstants.getTag(tag);
			if(_tag == null){
				Clause clause = new Clause();
				clause.setTag(OboFormatTag.TAG_PROPERTY_VALUE.getTag());
				String propId = this.getIdentifier(prop); //getIdentifier(prop);
				clause.addValue(propId);
				clause.addValue(value);
				frame.addClause(clause);

				//				if(propId.endsWith("0000426"))
				//				System.out.println(propId+"----- " + value);
			}else if (value.trim().length()>0) {
				Clause clause = new Clause();
				clause.setTag(tag);
				clause.addValue(value);
				frame.addClause(clause);
				if(_tag == OboFormatTag.TAG_DEF){

					for(OWLAnnotation aan: qualifiers){
						String propId = owlObjectToTag(aan.getProperty());

						if("xref".equals(propId)){
							String xrefValue = ((OWLLiteral) aan.getValue()).getLiteral();
							Xref xref = new Xref(xrefValue);
							clause.addXref(xref);

						}
					}
				}
				else if (_tag == OboFormatTag.TAG_XREF) {
					clause.setValue(new Xref(value));
				}
				else if(_tag == OboFormatTag.TAG_EXACT ||
						_tag == OboFormatTag.TAG_NARROW ||
						_tag == OboFormatTag.TAG_BROAD ||
						_tag == OboFormatTag.TAG_RELATED){
					clause.setTag(OboFormatTag.TAG_SYNONYM.getTag());
					String scope = _tag.getTag();
					String type = null;
					clause.setXrefs(new Vector<Xref>());
					for(OWLAnnotation aan: qualifiers){
						String propId = owlObjectToTag(aan.getProperty());
						//System.out.println("Qual: "+aan+" PropID: '"+propId+"' X:'"+OboFormatTag.TAG_HAS_SYNONYM_TYPE.);
						if(OboFormatTag.TAG_XREF.getTag().equals(propId)){
							String xrefValue = ((OWLLiteral) aan.getValue()).getLiteral();
							Xref xref = new Xref(xrefValue);
							clause.addXref(xref);

						}else if(OboFormatTag.TAG_HAS_SYNONYM_TYPE.getTag().equals(propId)){
							type = getIdentifier(aan.getValue());
						}
					}


					if(scope != null){
						clause.addValue(scope);

						if(type != null){
							clause.addValue(type);
						}
					}
					else {

						return false;
					}

				}else
					return false;
			}else{
				return false;
			}



		}else
			return false;
		
		
		return true;

	}

	public static String getOntologyId(OWLOntology ontology){
		//	String id = getIdentifier(ontology.getOntologyID().getOntologyIRI());

		String iri = ontology.getOntologyID().getOntologyIRI().toString();
		int index = iri.lastIndexOf("/");

		String id = iri.substring(index+1);

		index = id.lastIndexOf(".owl");
		if(index>0){
			id = id.substring(0, index);
		}

		return id;
	}

	private void tr(OWLOntology ontology) {
		Frame f = new Frame(FrameType.HEADER);
		this.obodoc.setHeaderFrame(f);

		for(IRI iri: ontology.getDirectImportsDocuments()){

			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_IMPORT.getTag());
			c.setValue(iri.toString());
			f.addClause(c);
		}


		String id = getOntologyId(this.owlOntology);
		//this.ontologyId = id;

		Clause c = new Clause();
		c.setTag(OboFormatTag.TAG_ONTOLOGY.getTag());
		c.setValue(id);
		f.addClause(c);



		/*		for (OWLAnnotationAssertionAxiom aanAx : ontology
				.getAnnotationAssertionAxioms(ontology.getOntologyID()
						.getOntologyIRI())) {
			tr(aanAx, f);
		}
		 */
		for(OWLAnnotation ann: ontology.getAnnotations()){
			tr(ann.getProperty(), ann.getValue(), new HashSet<OWLAnnotation>(), f);
		}

	}

	private void tr(OWLEquivalentClassesAxiom ax) {

		List<OWLClassExpression> list = ax.getClassExpressionsAsList();

		OWLClassExpression ce1 = list.get(0);
		OWLClassExpression ce2 = list.get(1);

		String cls2 = this.getIdentifier(ce2); //getIdentifier(ce2);

		Frame f = getTermFrame((OWLEntity) ce1);

		if (f == null) {
			String logErr = "the axiom is not translated : " + ax;
			String err = "The conversion is halted as the axiom is not translated: " + ax;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);

			return;
		}

		boolean isUntranslateable = false;
		List<Clause> equivalenceAxiomClauses = new ArrayList<Clause>();


		if (cls2 != null) {
			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_EQUIVALENT_TO.getTag());
			c.setValue(cls2);
			f.addClause(c);
		} else if (ce2 instanceof OWLObjectUnionOf) {
			List<OWLClassExpression> list2 = ((OWLObjectUnionOf) ce2)
			.getOperandsAsList();

			for(OWLClassExpression oce: list2){
				String id = this.getIdentifier(oce);
				Clause c = new Clause();
				c.setTag(OboFormatTag.TAG_UNION_OF.getTag());

				if(id == null){
					isUntranslateable = true;
					String logErr = "the axiom is not translated : " + ax;
					String err = "The conversion is halted as the axiom is not translated: " + ax;
					if(strictConversion){
						logErr = err;
					}
	
					LOG.warn(logErr);
	
					if(strictConversion)
						throw new RuntimeException(err);
	
					return;
				}
				else{
					c.setValue(id);
					equivalenceAxiomClauses.add(c);
				}
			}
			
			
			
		} else if (ce2 instanceof OWLObjectIntersectionOf) {

			List<OWLClassExpression> list2 = ((OWLObjectIntersectionOf) ce2).getOperandsAsList();

			for( OWLClassExpression ce : list2){
				String r = null;
				//			cls2 = getIdentifier(list.get(0));
				cls2 = this.getIdentifier(ce);


				if(ce instanceof OWLObjectSomeValuesFrom ){
					OWLObjectSomeValuesFrom ristriction = (OWLObjectSomeValuesFrom)ce;
					r = this.getIdentifier(ristriction.getProperty());
					cls2 = this.getIdentifier(ristriction.getFiller());
				}

				if(cls2 != null){

					Clause c = new Clause();
					c.setTag(OboFormatTag.TAG_INTERSECTION_OF.getTag());

					if(r != null)
						c.addValue(r);

					c.addValue(cls2);
					equivalenceAxiomClauses.add(c);
				}
				else if (f.getClauses(OboFormatTag.TAG_INTERSECTION_OF).size() > 0) {

					String logErr = "the axiom is not translated (maximimum one IntersectionOf EquivalenceAxiom : " + ax;
					String err = "The conversion is halted as the axiom is not translated: " + ax;
					if(strictConversion){
						logErr = err;
					}

					LOG.warn(logErr);

					if(strictConversion)
						throw new RuntimeException(err);
					
				}
				else{
					isUntranslateable = true;

					String logErr = "the axiom is not translated : " + ax;
					String err = "The conversion is halted as the axiom is not translated: " + ax;
					if(strictConversion){
						logErr = err;
					}

					LOG.warn(logErr);

					if(strictConversion)
						throw new RuntimeException(err);
				}
			}


		}

		// Only add clauses if the *entire* equivalence axiom can be translated
		if (!isUntranslateable) {
			for (Clause c : equivalenceAxiomClauses) {
				f.addClause(c);
			}
		}

	}

	private void tr(OWLDisjointClassesAxiom ax) {
		List<OWLClassExpression> list = ax.getClassExpressionsAsList();
		String cls2 = this.getIdentifier(list.get(1));

		if(cls2 == null){
			
			String logErr = "the axiom is not translated : " + ax;
			String err = "The conversion is halted as the axiom is not translated: " + ax;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);

			return;
		}	
		
		
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
			f= getTermFrame(entity);
		} else if (entity instanceof OWLObjectProperty) {
			f = getTypedefFrame(entity);
		}else if (entity instanceof OWLAnnotationProperty){

			for(OWLAnnotationAssertionAxiom ax: set){
				OWLAnnotationProperty prop = ax.getProperty();
				String tag = owlObjectToTag(prop);
				if(OboFormatTag.TAG_IS_METADATA_TAG.getTag().equals(tag)){
					f = getTypedefFrame(entity);
					break;
				}
			}


		}

		if (f != null) {
			for (OWLAnnotationAssertionAxiom aanAx : set) {

				tr(aanAx, f);
			}

			add(f);

			return;
		}
		
		/*
		if(entity.getIRI().toString().startsWith("http://www.geneontology.org/formats/oboInOwl#"))
			return;
		
		String logErr = "the axiom is not translated : " + axiom;
		String err = "The conversion is halted as the axiom is not translated: " + axiom;
		if(strictConversion){
			logErr = err;
		}

		LOG.warn(logErr);

		if(strictConversion)
			throw new RuntimeException(err);
		*/

	}

	public  String getIdentifier(OWLObject obj) {
		return getIdentifierFromObject(obj, this.owlOntology);
	}	

	public static String getIdentifierFromObject(OWLObject obj, OWLOntology ont) {

		if(obj instanceof OWLObjectProperty){
			OWLObjectProperty prop = (OWLObjectProperty) obj;
			for(OWLAnnotationAssertionAxiom ax: prop.getAnnotationAssertionAxioms(ont)){
				String propId = getIdentifierFromObject(ax.getProperty(), ont);

				// see BFOROXrefTest
				// 5.9.3. Special Rules for Relations
				if(propId.equals("shorthand")){
					return ((OWLLiteral)ax.getValue()).getLiteral();
				}

			}
		}

		if(obj instanceof OWLEntity)
			return getIdentifier(((OWLEntity)obj).getIRI());
		if(obj instanceof IRI)
			return getIdentifier((IRI)obj);

		return null;
	}

	/*public String iriToOBoId(IRI iriId){
		String id = getIdentifier(iriId);

		if(id.contains("_")){
			id = id.replace("_", ":");
		}else{
			id = ontologyId + ":" + id;
		}


		return id;
	}

	public String iriToOBoId(OWLObject obj){
		if(obj instanceof OWLEntity)
			return iriToOBoId(((OWLEntity)obj).getIRI());

		return null;

	}*/


	public static String getIdentifier(IRI iriId) {
		String id = _getIdentifier(iriId);
		if(id != null && id.startsWith("Error")){
			id = null;
		}

		return id;
	}	

	private static String _getIdentifier(IRI iriId) {


		if(iriId == null)
			return null;

		String iri = iriId.toString();

		int indexSlash = iri.lastIndexOf("/");


		String prefixURI = null;
		String id = null;

		if(indexSlash>-1){
			prefixURI = iri.substring(0, indexSlash+1);
			id = iri.substring(indexSlash+1);
		}else
			id = iri;

		String s[]= id.split("#_");

		if(s.length>1){
			return s[0] + ":" + s[1];
		}

		s= id.split("#");
		if(s.length>1){
			//			prefixURI = prefixURI + s[0] + "#";

			//			if(!(s[1].contains("#") || s[1].contains("_"))){
			String prefix = "";

			if("owl".equals(s[0]) || "rdf".equals(s[0]) || "rdfs".equals(s[0])){
				prefix = s[0] + ":";
			}

			return prefix + s[1];
		}
		//		}



		s= id.split("_");

		if(s.length==2 && !id.contains("#") && !s[1].contains("_")){
			return s[0] + ":" + s[1];
		}
		if(s.length > 2 && !id.contains("#")) {
			if (s[s.length-1].replaceAll("[0-9]","").length() == 0) {
				StringBuffer sb = new StringBuffer();
				for (int i=0; i < s.length; i++) {
					if (i > 0) {
						if (i == s.length -1) {
							sb.append(":");
						}
						else {
							sb.append("_");						
						}
					}
					sb.append(s[i]);
				}
				return sb.toString();
			}
		}


		return iri;







		/*

		if(iriId == null)
			return null;

		String iri = iriId.toString();

		int indexSlash = iri.lastIndexOf("/");


		String prefixURI = null;
		String id = null;

		if(indexSlash>-1){
			prefixURI = iri.substring(0, indexSlash+1);
			id = iri.substring(indexSlash+1);
		}else
			id = iri;


		String s[]= id.split("#");

		if(s.length>1){
			prefixURI = prefixURI + s[0] + "#";
			id = s[1];
		}

		String prefix =null;

		if(prefixURI != null)
			prefix = idSpaceMap.get(prefixURI);

		if(prefix == null && s.length>1){
			prefix = s[0];
		}

		String prefix2 = null;

		int uderscoreIndex = id.indexOf("_");
		String id2 = null;
		if(uderscoreIndex>-1){
			prefix2 = id.substring(0, uderscoreIndex);
			id2 =  id.substring(uderscoreIndex+1);
		}else{
			id2 = id;
			if(prefix != null)
				prefix2 = prefix;
			else
				prefix2 = this.ontologyId;
		}

		if(prefix2 != ontologyId && prefix != null && !prefix2.equals(prefix)){
			 id2 = id;
			 prefix2 = prefix;
		}

		if(!"http://purl.obolibrary.org/obo/".equals(prefixURI) && prefix != ontologyId){
			idSpaceMap.put(prefixURI, prefix);
		}


		return prefix2 + ":" + id2;



		 */

		/*
		if(iriId == null)
			return null;

		String iri = iriId.toString();

		if(iri.contains("topObjectProperty")){
			System.out.println("-------------------");
		}

		int indexSlash = iri.lastIndexOf("/");


		String prefixURI = null;
		String id = null;

		String s[]= iri.split("#");

		if(s.length>1){
			prefixURI = s[0] + "#";
			id = s[1];
		}else if(indexSlash>-1){
			prefixURI = iri.substring(0, indexSlash+1);
			id = iri.substring(indexSlash+1);
		}else
			id = iri;

		String prefix = idSpaceMap.get(prefixURI);


		String prefix2 = null;
		s = id.split("_");
		String id2 = null;
		if(s.length>1){
			prefix2 = s[0];
			id2 = s[1];
		}else{
			id2 = id;
			prefix2 = this.ontologyId;
		}

		if(prefix2 != ontologyId && prefix != null && !prefix2.equals(prefix)){
			 id2 = id;
			 prefix2 = prefix;
		}

		if(!"http://purl.obolibrary.org/obo/".equals(prefixURI) && prefix != ontologyId){
			idSpaceMap.put(prefixURI, prefix);
		}


		return prefix2 + ":" + id2;

		 */


	}


	public static String owlObjectToTag(OWLObject obj){

		IRI iriObj = null;
		if((obj instanceof OWLNamedObject)){
			iriObj =((OWLNamedObject) obj).getIRI();
		}else if(obj instanceof IRI){
			iriObj = (IRI)obj;
		}

		if(iriObj == null)
			return null;

		String iri = iriObj.toString();

		String tag = annotationPropertyMap.get(iri);

		if (tag == null) {
			
			// hardcoded values for legacy annotation properties: (TEMPORARY)
			if (iri.startsWith(Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_")) {
				String legacyId = iri.replace(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "");
				if (legacyId.equals("IAO_xref")) {
					return OboFormatTag.TAG_XREF.getTag();
				}
				if (legacyId.equals("IAO_id")) {
					return OboFormatTag.TAG_ID.getTag();
				}
				if (legacyId.equals("IAO_namespace")) {
					return OboFormatTag.TAG_NAMESPACE.getTag();
				}
				
			}
			
			//String prefix = Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_";
			String prefix = Obo2OWLConstants.OIOVOCAB_IRI_PREFIX;

			if (iri.startsWith(prefix)) {
				tag =  iri.substring(prefix.length());
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
		String id = this.getIdentifier(entity);
		Frame f = this.obodoc.getTypedefFrame(id);
		if (f == null) {
			f = new Frame(FrameType.TYPEDEF);
			f.setId(id);
			add(f);
		}

		return f;

	}


	private void tr(OWLClassAssertionAxiom ax){
		/*OWLObject cls = ax.getClassExpression();

		if(!(cls instanceof OWLClass))
			return;

		String clsIRI = ((OWLClass) cls).getIRI().toString();


		if(Obo2Owl.IRI_CLASS_SYNONYMTYPEDEF.equals(clsIRI)){
			Frame f = this.obodoc.getHeaderFrame();
			Clause c = new Clause();
			c.setTag(OboFormatTag.TAG_SYNONYMTYPEDEF.getTag());

			OWLNamedIndividual indv =(OWLNamedIndividual) ax.getIndividual();
			String indvId = this.getIdentifier(indv);

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
			String indvId = this.getIdentifier(indv);

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
		}*/

	}

	private void tr(OWLSubClassOfAxiom ax) {
		OWLSubClassOfAxiom a = (OWLSubClassOfAxiom) ax;
		OWLClassExpression sub = a.getSubClass();
		OWLClassExpression sup = a.getSuperClass();
		Set<QualifierValue> qvs = new HashSet<QualifierValue>();
		
		// 5.2.2
		boolean isRewrittenToGCI = false;
		if (sub instanceof OWLObjectIntersectionOf) {
			Set<OWLClassExpression> xs = ((OWLObjectIntersectionOf)sub).getOperands();
			
			// obo-format is limited to very restricted GCIs - the LHS of the axiom
			// must correspond to ObjectIntersectionOf(cls ObjectSomeValuesFrom(p filler))
			if (xs.size() == 2) {
				OWLClass c = null;
				OWLObjectSomeValuesFrom r = null;
				OWLObjectProperty p = null;
				OWLClass filler = null;
				for (OWLClassExpression x : xs) {
					if (x instanceof OWLClass)
						c = (OWLClass) x;
					if (x instanceof OWLObjectSomeValuesFrom) {
						r = (OWLObjectSomeValuesFrom)x;
						if (r.getProperty() instanceof OWLObjectProperty) {
							if (r.getFiller() instanceof OWLClass) {
								p = (OWLObjectProperty) r.getProperty();
								filler = (OWLClass) r.getFiller();
							}
						}
					}
				}
				if (c != null && p != null && filler != null) {
					
					isRewrittenToGCI = true;
					sub = c;
					qvs.add(new QualifierValue("gci_relation",getIdentifier(p)));
					qvs.add(new QualifierValue("gci_filler",getIdentifier(filler)));
				}
			}
		}
		
		if (sub instanceof OWLClass) {
			Frame f = getTermFrame((OWLEntity) sub);

			if (sup instanceof OWLClass) {
				Clause c = new Clause();
				c.setTag(OboFormatTag.TAG_IS_A.getTag());
				c.setValue(this.getIdentifier(sup));
				c.setQualifierValues(qvs);
				f.addClause(c);
			} else if (sup instanceof OWLObjectSomeValuesFrom ||sup instanceof OWLObjectAllValuesFrom ) {
				OWLQuantifiedRestriction r = (OWLQuantifiedRestriction) sup;
				String fillerId = this.getIdentifier(r.getFiller());

				if(fillerId == null){
					String logErr = "the axiom is not translated : " + ax;
					String err = "The conversion is halted as the axiom is not translated: " + ax;
					if(strictConversion){
						logErr = err;
					}

					LOG.warn(logErr);

					if(strictConversion)
						throw new RuntimeException(err);
					
					return;
				}

				Clause c = new Clause();
				c.setTag(OboFormatTag.TAG_RELATIONSHIP.getTag());
				c.addValue(this.getIdentifier(r.getProperty()));
				c.addValue(fillerId);
				c.setQualifierValues(qvs);

				f.addClause(c);
			} else {
				String logErr = "the axiom is not translated : " + ax;
				String err = "The conversion is halted as the axiom is not translated: " + ax;
				if(strictConversion){
					logErr = err;
				}

				LOG.warn(logErr);

				if(strictConversion)
					throw new RuntimeException(err);
				
				return;

			}
		} else {
			String logErr = "the axiom is not translated : " + ax;
			String err = "The conversion is halted as the axiom is not translated: " + ax;
			if(strictConversion){
				logErr = err;
			}

			LOG.warn(logErr);

			if(strictConversion)
				throw new RuntimeException(err);
			
			return;

		}
	}



}
