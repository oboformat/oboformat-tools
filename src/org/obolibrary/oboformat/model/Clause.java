package org.obolibrary.oboformat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class Clause {
	protected String tag;

	protected Collection<Object> values;
	protected Collection<QualifierValue> qualifierValues =
		 new ArrayList<QualifierValue>();



	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Collection<Object> getValues() {
		return values;
	}

	public void setValues(Collection<Object> values) {
		this.values = values;
	}

	public void setValue(Object v) {
		this.values = new ArrayList<Object>(1);
		values.add(v);
	}
	
	public void addValue(Object v) {
		if (values == null)
			values = new ArrayList<Object>(1);
		values.add(v);
		
	}


	public Object getValue() {
		// TODO Auto-generated method stub
		return values.toArray()[0];
	}
	
	public Object getValue2() {
		// TODO Auto-generated method stub
		return values.toArray()[1];
	}
	
	public Collection<QualifierValue> getQualifierValues() {
		return qualifierValues;
	}

	public void setQualifierValues(Collection<QualifierValue> qualifierValues) {
		this.qualifierValues = qualifierValues;
	}

	public void addQualifierValue(QualifierValue qv) {
		if (qualifierValues == null)
			qualifierValues = new Vector<QualifierValue>();
		qualifierValues.add(qv);
	}

	
	public String toString() {
		if (values == null)
			return tag+"=null";
		StringBuffer sb = new StringBuffer();
		for (Object ob : values) {
			sb.append(ob);
		}
		if (qualifierValues != null) {
			for (QualifierValue qv : qualifierValues) {
				sb.append(qv+" ");
			}
		}
		return tag+"("+sb.toString()+")";
	}



}
