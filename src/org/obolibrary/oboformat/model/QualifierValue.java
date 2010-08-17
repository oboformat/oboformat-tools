package org.obolibrary.oboformat.model;

public class QualifierValue {
	protected String qualifier;
	protected Object value;
	
	public QualifierValue(String q, String v) {
		qualifier = q;
		value = v;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return "{"+qualifier+"="+value+"}";
	}

}
