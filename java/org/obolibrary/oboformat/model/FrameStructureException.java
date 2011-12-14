package org.obolibrary.oboformat.model;

public class FrameStructureException extends DocumentStructureException {
	Frame frame;
	public FrameStructureException(String msg) {
		super(msg);
	}

	public FrameStructureException(Frame frame, String msg) {
		super(msg + "in frame:" + frame.toString());
	}

}
