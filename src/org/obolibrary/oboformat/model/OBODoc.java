package org.obolibrary.oboformat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.obolibrary.oboformat.model.Frame.FrameType;

public class OBODoc {
	protected Frame headerFrame;
	protected Map<String,Frame> termFrameMap = new HashMap<String,Frame>();
	protected Map<String,Frame> typedefFrameMap = new HashMap<String,Frame>();
	protected Map<String,Frame> instanceFrameMap = new HashMap<String,Frame>();
	protected Collection<Frame> annotationFrames = new LinkedList<Frame>();

	public OBODoc() {
		super();
	}

	public Frame getHeaderFrame() {
		return headerFrame;
	}

	public void setHeaderFrame(Frame headerFrame) {
		this.headerFrame = headerFrame;
	}

	public Collection<Frame> getTermFrames() {
		return termFrameMap.values();
	}
	public Collection<Frame> getTypedefFrames() {
		return typedefFrameMap.values();
	}
	public Collection<Frame> getInstanceFrames() {
		return instanceFrameMap.values();
	}
	public Frame getTermFrame(String id) {
		return termFrameMap.get(id);
	}
	public Frame getTypedefFrame(String id) {
		return typedefFrameMap.get(id);
	}
	public Frame getInstanceFrame(String id) {
		return instanceFrameMap.get(id);
	}


	public void addFrame(Frame f) {
		if (f.getType() == FrameType.TERM) {
			addTermFrame(f);
		}
		else if (f.getType() == FrameType.TYPEDEF) {
			addTypedefFrame(f);
			
		}
		else if (f.getType() == FrameType.INSTANCE) {
			addInstanceFrame(f);
		}
	}
	
	public void addTermFrame(Frame f) {
		String id = f.getId();
		if (termFrameMap.containsKey(id)) {
			// merge
		}
		else {
			termFrameMap.put(id, f);
		}
	}
	
	public void addTypedefFrame(Frame f) {
		String id = f.getId();
		if (typedefFrameMap.containsKey(id)) {
			// merge
		}
		else {
			typedefFrameMap.put(id, f);
		}
	}
	
	public void addInstanceFrame(Frame f) {
		String id = f.getId();
		if (instanceFrameMap.containsKey(id)) {
			// merge
		}
		else {
			instanceFrameMap.put(id, f);
		}
	}

	public String getIDSpace(String prefix) {
		// built-in
		if (prefix.equals("RO")) {
			return "http://purl.obolibrary.org/obo/RO_";
		}
		return null;
	}

	public boolean isTreatXrefsAsEquivalent(String prefix) {
		if (prefix != null && prefix.equals("RO")) {
			return true;
		}
		return false;
	}
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		//for (Frame f : getTermFrames()) {
		//	sb.append(f.toString());
		//}
		//return "OBODoc("+headerFrame+" Frames("+sb.toString()+"))";
		return "OBODoc("+headerFrame+")";
	}

}
