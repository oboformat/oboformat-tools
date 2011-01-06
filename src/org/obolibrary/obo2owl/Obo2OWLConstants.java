package org.obolibrary.obo2owl;

import org.semanticweb.owlapi.model.IRI;

/**
 * 
 * @author Shahid Manzoor
 *
 */
public class Obo2OWLConstants {

	public static final String DEFAULT_IRI_PREFIX = "http://purl.obolibrary.org/obo/";
	
	
	public enum Obo2OWLVocabulary{
	
		IRI_IAO_0000424(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "IAO_0000424"),
		IRI_IAO_0000425(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "IAO_0000425"),
		IRI_IAO_0000115(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "IAO_0000115"),
		IRI_IAO_0000118(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "IAO_0000118"),
		IRI_IAO_0000427(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "IAO_0000427"),
		IRI_IAO_0100001(Obo2OWLConstants.DEFAULT_IRI_PREFIX, "IAO_0100001");
		
		IRI iri;
		String namespace;
		String shortName;
	
	
		
		Obo2OWLVocabulary(String namespce, String shortName){
			iri = IRI.create(namespce + shortName);
			this.shortName = shortName;
			this.namespace = namespce;
		}
		
		public String getShortName(){
			return shortName;
		}
		
		public String getNamespace(){
			return namespace;
		}
		
		public IRI getIRI(){
			return iri;
		}
	}
	
	
}
