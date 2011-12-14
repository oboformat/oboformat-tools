package org.obolibrary.obo2owl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.QualifierValue;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class Obo2OWLOldMapping extends Obo2Owl {

	/*private static Logger LOG = Logger.getLogger(Obo2OWLOldMapping.class);
	private static boolean DEBUG = LOG.isDebugEnabled();
	
	private static final String OBOINOWL = "http://purl.obolibrary.org/obo/";
	
	private static final Hashtable<String, OWLClass> annotationsClasses = new Hashtable<String, OWLClass>();
	
	protected void initAnnotationPropertyMap() {
		annotationPropertyMap = new HashMap<String,IRI>();
		annotationPropertyMap.put("name",OWLRDFVocabulary.RDFS_LABEL.getIRI());
		annotationPropertyMap.put("comment",OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		annotationPropertyMap.put("def",IRI.create(OBOINOWL+"hasDefinition"));
		annotationPropertyMap.put("alt_id",IRI.create(OBOINOWL+"hasAlternativeId"));
		annotationPropertyMap.put("subset",IRI.create(OBOINOWL+"inSubset"));
		annotationPropertyMap.put("xref",IRI.create(OBOINOWL+"hasDbXref"));
		annotationPropertyMap.put("replaced_by",IRI.create(OBOINOWL+"replacedBy"));
		annotationPropertyMap.put("consider",IRI.create(OBOINOWL+"consider"));
		annotationPropertyMap.put("synonym",IRI.create(OBOINOWL+"hasSynonym"));

	}

	
	private OWLAxiom handleDef(OWLAnnotationSubject sub, Clause clause){
		
		//Create OWL class of id "Definition" 
		if(!annotationsClasses.contains("def")){
			OWLClass cls = fac.getOWLClass(IRI.create(OBOINOWL + "Definition")); 
			
			annotationsClasses.put("def", cls);
			add(fac.getOWLDeclarationAxiom(cls));
		}
		
		//get "Definition" OWL class for the tag def
		OWLClass cls = annotationsClasses.get("def");
		
		OWLAnonymousIndividual indvidual = fac.getOWLAnonymousIndividual();
		add( fac.getOWLClassAssertionAxiom(cls,indvidual ) );
		
		add( 
				fac.getOWLAnnotationAssertionAxiom(
						trTagToAnnotationProp("name"), indvidual, trLiteral(clause.getValue())
						)
							);
		
		OWLAnnotationProperty prop =  trTagToAnnotationProp("def");
		
		OWLAxiom ax = fac.getOWLAnnotationAssertionAxiom(prop, sub, indvidual);
		
		return ax;
	}
	
	
	private OWLAxiom handleAnnotationWithClassAssertion(OWLAnnotationSubject sub, String tag, String className,  Clause clause){
		
		if(!annotationsClasses.contains(tag)){
			OWLClass cls = fac.getOWLClass(IRI.create(OBOINOWL + className));
			annotationsClasses.put(tag, cls);
			add(fac.getOWLDeclarationAxiom(cls));
		}
		
		OWLClass cls = annotationsClasses.get(tag);
		
		OWLAnonymousIndividual indvidual = fac.getOWLAnonymousIndividual();
		add( fac.getOWLClassAssertionAxiom(cls,indvidual ) );
		
		add( 
				fac.getOWLAnnotationAssertionAxiom(
						trTagToAnnotationProp("name"), indvidual, trLiteral(clause.getValue())
						)
							);
		
		OWLAnnotationProperty prop =  trTagToAnnotationProp(tag);
		
		OWLAxiom ax = fac.getOWLAnnotationAssertionAxiom(prop, sub, indvidual);
		
		return ax;
	}
	
	@Override
	protected OWLAxiom trGenericClause(OWLAnnotationSubject sub, String tag, Clause clause) {
		
		if (clause.getValue() == null) {
			LOG.error("Problem:"+clause);
		}
		
		
		OWLAxiom ax = null;
		
		if("def".equals(tag)){
			//ax = handleAnnotationWithClassAssertion(sub, tag, "Definition", clause);
			System.out.println("------------");
			ax = handleDef(sub, clause);
			System.out.println("------------");
		}else if("subset".equals(tag)){
			ax = handleAnnotationWithClassAssertion(sub, tag, "Subset", clause);
		}else if("synonym".equals(tag)){
			ax = handleAnnotationWithClassAssertion(sub, tag, "Synonym", clause);
		}else if("xref".equals(tag)){
			ax = handleAnnotationWithClassAssertion(sub, tag, "DbXref", clause);
		}else{

			OWLAnnotationProperty prop = trTagToAnnotationProp(tag);

			if(prop == null)
				return null;
			ax = fac.getOWLAnnotationAssertionAxiom(
					prop,
					sub, 
					trLiteral(clause.getValue()));
		}
		return ax;
	}
	

	private OWLAnnotationProperty trTagToAnnotationProp(String tag) {
		OWLAnnotationProperty ap = null;
		if (annotationPropertyMap.containsKey(tag)) {
			ap = fac.getOWLAnnotationProperty(annotationPropertyMap.get(tag));
		}
		else {
			//ap = fac.getOWLAnnotationProperty(IRI.create(DEFAULT_IRI_PREFIX+"IAO_"+tag));
			LOG.error("Annotation property is not implemented for the tag " + tag);
			return null;
		}
		if (!apToDeclare.contains(ap)) {
			apToDeclare.add(ap);
			add(fac.getOWLDeclarationAxiom(ap));
		}

		return ap;
	}

*/

	
	
}
