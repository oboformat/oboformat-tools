package org.obolibrary.oboformat.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameMergeException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

public class XrefExpander {
	OBODoc sourceOBODoc;
	OBODoc targetOBODoc;
	String targetBase;
	Map<String,Rule> treatMap = new HashMap<String,Rule>();
	Map<String,OBODoc> targetDocMap = new HashMap<String,OBODoc>();
	//Obo2Owl obo2owl;
	//Map<String,Clause> expandMap = new HashMap<String,Clause>();

	public XrefExpander(OBODoc src) throws InvalidXrefMapException {
		sourceOBODoc = src;
		Frame shf = src.getHeaderFrame();
		String ontId = shf.getClause(OboFormatTag.TAG_ONTOLOGY.getTag()).getValue().toString();
		String tgtOntId = ontId + "/xref_expansions";
		targetOBODoc = new OBODoc();
		Frame thf = new Frame(FrameType.HEADER);
		thf.addClause(new Clause(OboFormatTag.TAG_ONTOLOGY.getTag(), tgtOntId));
		targetOBODoc.setHeaderFrame(thf);
		sourceOBODoc.addImportedOBODoc(targetOBODoc);
		setUp();		
	}
	public XrefExpander(OBODoc src, String targetBase) throws InvalidXrefMapException {
		sourceOBODoc = src;
		Frame shf = src.getHeaderFrame();
		this.targetBase = targetBase;
		setUp();		
	}
	public XrefExpander(OBODoc src, OBODoc tgt) throws InvalidXrefMapException {
		sourceOBODoc = src;
		targetOBODoc = tgt;
		setUp();
	}

	public void setUp() throws InvalidXrefMapException {
		// required for translation of IDs
		//obo2owl = new Obo2Owl();
		//obo2owl.setObodoc(sourceOBODoc);

		for (Clause c : sourceOBODoc.getHeaderFrame().getClauses()) {
			String [] parts;
			parts = c.getValue().toString().split("\\s");
			String idSpace = parts[0];
			if (c.getTag().equals(OboFormatTag.TAG_TREAT_XREFS_AS_EQUIVALENT.getTag())) {
				addRule(parts[0], new EquivalenceExpansion());
				//				addMacro(idSpace,"is_specific_equivalent_of","Class: ?X EquivalentTo: ?Y and "+oboIdToIRI(parts[1])+" some "+oboIdToIRI(parts[2]));
			}
			else if (c.getTag().equals(OboFormatTag.TAG_TREAT_XREFS_AS_GENUS_DIFFERENTIA.getTag())) {
				addRule(idSpace, new GenusDifferentiaExpansion(parts[1],parts[2]));
				//				addMacro(idSpace,"is_generic_equivalent_of","Class: ?Y EquivalentTo: ?X and "+oboIdToIRI(parts[1])+" some "+oboIdToIRI(parts[2]));
			}
			else if (c.getTag().equals(OboFormatTag.TAG_TREAT_XREFS_AS_REVERSE_GENUS_DIFFERENTIA.getTag())) {
				addRule(idSpace, new ReverseGenusDifferentiaExpansion(parts[1],parts[2]));
				//				addMacro(idSpace,"is_generic_equivalent_of","Class: ?Y EquivalentTo: ?X and "+oboIdToIRI(parts[1])+" some "+oboIdToIRI(parts[2]));
			}
			else if (c.getTag().equals(OboFormatTag.TAG_TREAT_XREFS_AS_HAS_SUBCLASS.getTag())) {
				addRule(idSpace, new HasSubClassExpansion());
			}
			else if (c.getTag().equals(OboFormatTag.TAG_TREAT_XREFS_AS_IS_A.getTag())) {
				addRule(idSpace, new IsaExpansion());
			}
			else if (c.getTag().equals(OboFormatTag.TAG_TREAT_XREFS_AS_RELATIONSHIP.getTag())) {
				addRule(idSpace, new RelationshipExpansion(parts[1]));
			}
			else {
				continue;
			}
			
			if (targetBase != null) {
				// create a new bridge ontology for every expansion macro
				OBODoc tgt = new OBODoc();
				Frame thf = new Frame(FrameType.HEADER);
				thf.addClause(new Clause(OboFormatTag.TAG_ONTOLOGY.getTag(), targetBase + "-" + idSpace.toLowerCase()));
				tgt.setHeaderFrame(thf);
				targetDocMap.put(idSpace, tgt);
				sourceOBODoc.addImportedOBODoc(tgt);
			}

		}
	}
	
	public OBODoc getTargetDoc(String idSpace) {
		if (targetOBODoc != null)
			return targetOBODoc;
		return targetDocMap.get(idSpace);
	}

	//	private String oboIdToIRI(String id) {
	//		return obo2owl.oboIdToIRI(id).toQuotedString();
	//	}

	/*
	private void addMacro(String idSpace, String prefix, String macro) throws FrameMergeException {
		String id = prefix+"_"+idSpace;
		Frame f = new Frame(Frame.FrameType.TYPEDEF);
		f.setId(id);
		Clause c = new Clause();
		c.setTag(OboFormatTag.TAG_EXPAND_ASSERTION_TO.toString());
		c.setValue(macro);
		sourceOBODoc.addFrame(f );		
	}
	 */

	private void addRule(String db, Rule rule) throws InvalidXrefMapException {
		if (treatMap.containsKey(db)) {
			throw new InvalidXrefMapException(db);
		}
		rule.idSpace = db;
		treatMap.put(db, rule);
	}

	public void expandXrefs() {
		for (Frame f : sourceOBODoc.getTermFrames()) {
			String id = f.getClause("id").getValue().toString();
			Collection<Clause> clauses = f.getClauses(OboFormatTag.TAG_XREF.toString());
			for (Clause c : clauses) {
				String x = c.getValue().toString();
				String s = getIDSpace(x);
				if (treatMap.containsKey(s)) {
					treatMap.get(s).expand(f, id, x);
				}
			}
		}
	}

	private String getIDSpace(String x) {
		String[] parts = x.split(":", 2);
		return parts[0];
	}

	public abstract class Rule {
		protected String xref;
		public String idSpace;

		public abstract void expand(Frame sf, String id, String xref);

		protected Frame getTargetFrame(String id) {
			Frame f = getTargetDoc(idSpace).getTermFrame(id);
			if (f == null) {
				try {
					f = new Frame();
					f.setId(id);
					getTargetDoc(idSpace).addTermFrame(f);
				} catch (FrameMergeException e) {
					// this should be impossible
					e.printStackTrace();
				}
			}
			return f;
		}


	}

	public class EquivalenceExpansion extends Rule {
		public void expand(Frame sf, String id, String xref) {
			Clause c = new Clause(OboFormatTag.TAG_EQUIVALENT_TO.toString(), xref);
			sf.addClause(c);
		}

	}

	public class HasSubClassExpansion extends Rule {
		public void expand(Frame sf, String id, String xref) {
			Clause c = new Clause(OboFormatTag.TAG_IS_A.toString(), id);
			getTargetFrame(xref).addClause(c);
		}			
	}

	public class GenusDifferentiaExpansion extends Rule {

		protected String rel;
		protected String tgt;
		public GenusDifferentiaExpansion(String rel, String tgt) {
			this.rel = rel;
			this.tgt = tgt;
		}

		public void expand(Frame sf, String id, String xref) {
			Clause gc = new Clause(OboFormatTag.TAG_INTERSECTION_OF.toString(), xref);
			Clause dc = new Clause(OboFormatTag.TAG_INTERSECTION_OF.toString());
			dc.setValue(rel);
			dc.addValue(tgt);
			getTargetFrame(id).addClause(gc);
			getTargetFrame(id).addClause(dc);
		}			
	}

	public class ReverseGenusDifferentiaExpansion extends Rule {

		protected String rel;
		protected String tgt;
		public ReverseGenusDifferentiaExpansion(String rel, String tgt) {
			this.rel = rel;
			this.tgt = tgt;
		}

		public void expand(Frame sf, String id, String xref) {
			Clause gc = new Clause(OboFormatTag.TAG_INTERSECTION_OF.toString(), id);
			Clause dc = new Clause(OboFormatTag.TAG_INTERSECTION_OF.toString());
			dc.setValue(rel);
			dc.addValue(tgt);
			getTargetFrame(xref).addClause(gc);
			getTargetFrame(xref).addClause(dc);
		}			
	}

	public class IsaExpansion extends Rule {

		public void expand(Frame sf, String id, String xref) {
			Clause c = new Clause(OboFormatTag.TAG_IS_A.toString(), xref);
			getTargetFrame(id).addClause(c);
		}			
	}

	public class RelationshipExpansion extends Rule {

		protected String rel;
		public RelationshipExpansion(String rel) {
			this.rel = rel;
		}
		public void expand(Frame sf, String id, String xref) {
			Clause c = new Clause(OboFormatTag.TAG_RELATIONSHIP.toString(), rel);
			c.addValue(xref);
			getTargetFrame(id).addClause(c);
		}			
	}

}
