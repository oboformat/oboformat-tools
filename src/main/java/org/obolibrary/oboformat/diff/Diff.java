package org.obolibrary.oboformat.diff;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;

public class Diff {

	String type;
	String frameType;
	Frame frame1;
	Frame frame2;
	Clause clause1;
	Clause clause2;
	int frameNum;

	public Diff(String ftype, String type, Frame f1, int n) {
		this(ftype, type, f1, null, null, n);
	}

	public Diff(String ftype, String type, Frame f1, Frame f2, Clause c, int n) {
		this.type = type;
		frame1 = f1;
		frame2 = f2;
		clause1 = c;
		frameNum = n;
		frameType = ftype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Frame getFrame1() {
		return frame1;
	}

	public void setFrame1(Frame frame1) {
		this.frame1 = frame1;
	}

	public Frame getFrame2() {
		return frame2;
	}

	public void setFrame2(Frame frame2) {
		this.frame2 = frame2;
	}

	public Clause getClause1() {
		return clause1;
	}

	public void setClause1(Clause clause1) {
		this.clause1 = clause1;
	}

	public Clause getClause2() {
		return clause2;
	}

	public void setClause2(Clause clause2) {
		this.clause2 = clause2;
	}

	public String toString() {
		return type + " " + frameType +
		" Frame1="+(frame1 == null ? "-" : frame1.getId()) +
		" Frame2="+(frame2 == null ? "-" : frame2.getId()) +
		" Clause="+(clause1 == null ? "-" : clause1)+
		" In=Frame"+frameNum;
		
	}

}
