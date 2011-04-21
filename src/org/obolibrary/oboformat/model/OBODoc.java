package org.obolibrary.oboformat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.obolibrary.oboformat.model.Frame.FrameType;

/**
 * An OBODoc is a container for a header frame and zero or more entity frames
 *
 */
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


	public void addFrame(Frame f) throws FrameMergeException {
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
	
	public void addTermFrame(Frame f) throws FrameMergeException {
		String id = f.getId();
		if (termFrameMap.containsKey(id)) {
			termFrameMap.get(id).merge(f);
		}
		else {
			termFrameMap.put(id, f);
		}
	}
	
	public void addTypedefFrame(Frame f) throws FrameMergeException {
		String id = f.getId();
		if (typedefFrameMap.containsKey(id)) {
			typedefFrameMap.get(id).merge(f);
		}
		else {
			typedefFrameMap.put(id, f);
		}
	}
	
	public void addInstanceFrame(Frame f) throws FrameMergeException {
		String id = f.getId();
		if (instanceFrameMap.containsKey(id)) {
			instanceFrameMap.get(id).merge(f);
		}
		else {
			instanceFrameMap.put(id, f);
		}
	}

	/**
	 * 
	 * Looks up the ID prefix to IRI prefix mapping.
	 * Header-Tag: idspace
	 * 
	 * @param prefix prefix
	 * @return IRI prefix as string
	 */
	public String getIDSpace(String prefix) {
		// built-in
		if (prefix.equals("RO")) {
			return "http://purl.obolibrary.org/obo/RO_";
		}
		// TODO
		return null;
	}

	public boolean isTreatXrefsAsEquivalent(String prefix) {
		if (prefix != null && prefix.equals("RO")) {
			return true;
		}
		return false;
	}
	
	public void importContents(OBODoc extDoc) throws FrameMergeException {
		for (Frame f : extDoc.getTermFrames())
			addTermFrame(f);
		for (Frame f : extDoc.getTypedefFrames())
			addTypedefFrame(f);
		for (Frame f : extDoc.getInstanceFrames())
			addInstanceFrame(f);
	}
	
	public  void addDefaultOntologyHeader(String defaultOnt) {
		Frame hf = getHeaderFrame();
		Clause ontClause = hf.getClause("ontology");
		if (ontClause == null) {
			ontClause = new Clause();
			ontClause.setTag("ontology");
			ontClause.setValue(defaultOnt);
			hf.addClause(ontClause);
		}
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
