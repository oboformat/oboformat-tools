package org.obolibrary.oboformat.model;

public class Xref {
	String idref;
	String annotation;
	
	public Xref(String idref) {
		super();
		this.idref = idref;
	}
	public String getIdref() {
		return idref;
	}
	public void setIdref(String idref) {
		this.idref = idref;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	
	public String toString() {
		if (annotation == null)
			return "<"+idref+">";
		return "<"+idref+" \""+annotation+"\">";
	}
}
