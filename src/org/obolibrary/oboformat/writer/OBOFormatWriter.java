package org.obolibrary.oboformat.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.parser.OBOFormatParser;

/**
 * 
 * @author Shahid Manzoor
 *
 */
public class OBOFormatWriter {
	
	private static Logger LOG = Logger.getLogger(OBOFormatWriter.class);
	
	private static HashSet<String> tagsInformative = buildTagsInformative();
	
	private OBODoc oboDoc;
	
	public OBOFormatWriter(){
		oboDoc = null;
	}
	
	private static HashSet<String> buildTagsInformative(){
		HashSet<String> set = new HashSet<String>();
		
		
		set.add(OboFormatTag.TAG_IS_A.getTag());
		set.add( OboFormatTag.TAG_RELATIONSHIP.getTag());
		set.add( OboFormatTag.TAG_DISJOINT_FROM.getTag());
		set.add( OboFormatTag.TAG_INTERSECTION_OF.getTag());
		set.add( OboFormatTag.TAG_UNION_OF.getTag());
		set.add( OboFormatTag.TAG_EQUIVALENT_TO.getTag());
		set.add( OboFormatTag.TAG_REPLACED_BY.getTag());
		set.add( OboFormatTag.TAG_PROPERTY_VALUE.getTag());
		set.add( OboFormatTag.TAG_DOMAIN.getTag());
		set.add( OboFormatTag.TAG_RANGE.getTag());
		set.add( OboFormatTag.TAG_INVERSE_OF.getTag());
		set.add( OboFormatTag.TAG_TRANSITIVE_OVER.getTag());
		set.add( OboFormatTag.TAG_HOLDS_OVER_CHAIN.getTag());
		set.add( OboFormatTag.TAG_EQUIVALENT_TO_CHAIN.getTag());
		set.add( OboFormatTag.TAG_DISJOINT_OVER.getTag());
		
		return set;
		
		
	}
	
	public void write(String fn, BufferedWriter writer) throws IOException{
		if(fn.startsWith("http:")){
			write(new URL(fn), writer);
		}else{
			BufferedReader reader = new BufferedReader(new FileReader(new File(fn)));
			write(reader, writer);
		}
	}

	public void write(URL url, BufferedWriter writer) throws IOException{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(url.openStream()));
		
		write(reader, writer);
		
	}
	
	
	public void write(BufferedReader reader, BufferedWriter writer) throws IOException{
		OBOFormatParser parser = new OBOFormatParser();
	 	OBODoc doc = parser.parse(reader);
	 	
	 	write(doc, writer);
	}
	

	public void write(OBODoc doc, BufferedWriter writer) throws IOException{
		Frame headerFrame = doc.getHeaderFrame();
		
		this.oboDoc = doc;
		writeHeader(headerFrame, writer);

		for(Frame f: doc.getTermFrames()){
			write(f, writer);
		}
		
		for(Frame f: doc.getTypedefFrames()){
			write(f, writer);
		}
	}
	
	private void writeLine(String ln, BufferedWriter writer) throws IOException{
		writer.write(ln+"\n");
	}
	
	private List<String> duplicateTags(Set<String> src){
		List<String> tags = new ArrayList<String>(src.size());
		
		for(String tag: src){
			tags.add(tag);
		}
		
		return tags;
	}
	
	public void writeHeader(Frame frame, BufferedWriter writer) throws IOException{
	
		Clause c = frame.getClause("format-version");
		if(c != null)
			write(c, writer);
		
		List<String> tags = duplicateTags(frame.getTags());
		
		Collections.sort(tags, new HeaderTagsComparator());
		
		for(String tag: tags){

			if(tag.equals("format-version"))
				continue;
			
			for(Clause claue: frame.getClauses(tag)){
				write(claue, writer);
			}
		}
		
		writeLine("", writer);
		
		
	}
	
	public void write(Frame frame, BufferedWriter writer) throws IOException{

		Comparator<String> comparator = null;
		
		if(frame.getType() == FrameType.TERM){
			writeLine("[Term]", writer);
		
			comparator = new TermsTagsComparator();
		}else if (frame.getType() == FrameType.TYPEDEF){
			writeLine("[Typedef]", writer);
			comparator = new TypeDefTagsComparator();
		}
		
		if(frame.getId() != null){
			writeLine("id: " + frame.getId(), writer);
		}
		
		List<String> tags = duplicateTags(frame.getTags());
		Collections.sort(tags, comparator);
		
		for(String tag: tags){
			
			for( Clause clause : frame.getClauses(tag)){

				if(clause.getTag().equals("id"))
					continue;
				else if(clause.getTag().equals("def"))
					writeDef(clause, writer);
				else if(clause.getTag().equals("synonym"))
					writeSynonym(clause, writer);
				else if(OboFormatTag.TAG_PROPERTY_VALUE.getTag().equals(clause.getTag()))
					writePropertyValue(clause, writer);
				else if(OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(clause.getTag()) || OboFormatTag.TAG_EXPAND_ASSERTION_TO.getTag().equals(clause.getTag()))
					writeClauseWithQuotedString(clause, writer);
				else
					write(clause, writer);
			}
		
		
		
		}
		
		writeLine("", writer);
		
	}
	
	
	private void writeClauseWithQuotedString(Clause clause, BufferedWriter writer) throws IOException{
		String line = clause.getTag() + ": ";
		
		boolean  first = true;
		Iterator<Object> valuesIterator = clause.getValues().iterator();
        while (valuesIterator.hasNext()) {
            if (first) { line += "\""; }
            line += valuesIterator.next();
            if (first) { line += "\""; }
            if (valuesIterator.hasNext()) { line += " "; }
            first = false;
        }
		
		Collection<Xref> xrefs = clause.getXrefs();
		
		// if the xrefs value is null, then there should *never* be xrefs at this location
		// not that the value may be a non-null empty list - here we still want to write []
		if(xrefs != null){
		
			line += " [";
			
			Iterator<Xref> xrefsIterator = xrefs.iterator();
			while (xrefsIterator.hasNext()) {
			    line += xrefsIterator.next().getIdref();
			    if (xrefsIterator.hasNext()) {
			        line += ", ";
			    }
			}
	
			line +="]";
		}else if(OboFormatTag.TAG_DEF.getTag().equals(clause.getTag()) || OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(clause.getTag()) ||
				OboFormatTag.TAG_EXPAND_ASSERTION_TO.getTag().equals(clause.getTag())){
			line  += " []";
		}
		
		writeLine(line, writer);
		
	}
	
	public void writeDef(Clause clause, BufferedWriter writer) throws IOException{
		
		writeClauseWithQuotedString(clause, writer);
	}

	public void writePropertyValue(Clause clause, BufferedWriter writer) throws IOException{
		
		String line = clause.getTag() + ": ";

		Collection cols = clause.getValues();
		
		if(cols.size()<2){
			LOG.warn("The " + OboFormatTag.TAG_PROPERTY_VALUE.getTag() + " has incorrect number of values: " + clause);
			return;
		}
			
		Iterator itr = cols.iterator();
		line +=  itr.next() + " ";
		
		String val = itr.next().toString();
		
		if(val.contains(" ") || !val.contains(":"))
			val = " \"" + val + "\"";
		
		line += val;
		
		writeLine(line, writer);
	}
	
	
	public void writeSynonym(Clause clause, BufferedWriter writer) throws IOException{
		writeClauseWithQuotedString(clause, writer);
	}
	
	public void write(Clause clause, BufferedWriter writer) throws IOException{
		String line = clause.getTag() + ": ";
		
		Iterator<Object> valuesIterator = clause.getValues().iterator();
		String idsLabel = this.oboDoc != null && tagsInformative.contains(clause.getTag()) ? "" : null;
		
		while (valuesIterator.hasNext()) {
			String value = valuesIterator.next() + "";
			if(idsLabel != null){
				Frame f= oboDoc.getTermFrame(value);
				if(f == null){
					f = oboDoc.getTermFrame(value);
				}
				
				if(f != null){
					Clause cl = f.getClause(OboFormatTag.TAG_NAME.getTag());
					if(cl != null){
						if(idsLabel.length()>0)
							idsLabel += "\t";
						idsLabel += cl.getValue();
					}
						
				}
			}
		    line += value;
		    if (valuesIterator.hasNext()) {
		        line += " ";
		    }
		}
		
		Collection<Xref> xrefs = clause.getXrefs();
		
		
		
		if(xrefs != null){
		
		//	if(!xrefs.isEmpty())
				line += " [";
			
			Iterator<Xref> xrefsIterator = xrefs.iterator();
            while (xrefsIterator.hasNext()) {
                line += xrefsIterator.next().getIdref();
                if (xrefsIterator.hasNext()) {
                    line += ", ";
                }
            }
	
		//	if(!xrefs.isEmpty())
				line +="]";
		}
		
		if(idsLabel != null){
			line += " !" +idsLabel;
		}
	
		
		writeLine(line, writer);
		
	}
	
	private static class HeaderTagsComparator implements Comparator<String>{

		private static Hashtable<String, Integer> tagsPriorities = buildTagsPriorities();
		
		private static Hashtable<String, Integer> buildTagsPriorities(){
			Hashtable<String, Integer> table = new Hashtable<String, Integer>();
			
			table.put("ontology",5);
			table.put("data-version",10);
			table.put("date",15);
			table.put("saved-by",20);
			table.put("auto-generated-by",25);
			table.put("import",30);
			table.put("subsetdef",35);
			table.put("synonymtypedef",40);
			table.put("default-namespace",45);
			table.put("idspace",50);
			table.put("treat-xrefs-as-equivalent",55);
			table.put("treat-xrefs-as-genus-differentia",60);
			table.put("treat-xrefs-as-relationship",65);
			table.put("treat-xrefs-as-is_a",70);
			table.put("remark",75);
	        
	        
			return table;
		}
		
		public int compare(String o1, String o2) {
			Integer i1 = tagsPriorities.get(o1);
			Integer i2 = tagsPriorities.get(o2);

			if(i1 == null || i2 == null){
				return -1;
			}
			
			
			return i1.compareTo(i2);
		}
		
	}
	
	
	
	
	private static class TermsTagsComparator implements Comparator<String>{

		private static Hashtable<String, Integer> tagsPriorities = buildTagsPriorities();
		
		private static Hashtable<String, Integer> buildTagsPriorities(){
			Hashtable<String, Integer> table = new Hashtable<String, Integer>();
			
	        table.put("id",5);
	        table.put("is_anonymous",10);
	        table.put("name",15);
	        table.put("namespace",20); 
	        table.put("alt_id",25);
	        table.put("def",30);
	        table.put("comment",35);
	        table.put("subset",40);
	        table.put("synonym",45);
	        table.put("xref",50);
	        table.put("builtin",55);
	        table.put("property_value",60); 
	        table.put("is_a",65);
	        table.put("intersection_of",70); 
	        table.put("intersection_of",75);
	        table.put("union_of",80);
	        table.put("equivalent_to",85);
	        table.put("disjoint_from",90);
	        table.put("relationship",95);
	        table.put("created_by",100);
	        table.put("creation_date",105);
	        table.put("is_obsolete",110);
	        table.put("replaced_by",115);
	        table.put("consider",120);
	        
			return table;
		}
		
		public int compare(String o1, String o2) {
			Integer i1 = tagsPriorities.get(o1);
			Integer i2 = tagsPriorities.get(o2);

			if(i1 == null || i2 == null){
				return -1;
			}
			
			return i1.compareTo(i2);
		}
		
	}

	
	
	private static class TypeDefTagsComparator implements Comparator<String>{

		private static Hashtable<String, Integer> tagsPriorities = buildTagsPriorities();
		
		private static Hashtable<String, Integer> buildTagsPriorities(){
			Hashtable<String, Integer> table = new Hashtable<String, Integer>();

			table.put("id",0);
			table.put("is_anonymous",0);
			table.put("name",0);
			table.put("namespace",0); 
			table.put("alt_id",0);
			table.put("def",0);
			table.put("comment",0);
			table.put("subset",0); 
			table.put("synonym",0); 
			table.put("xref",0); 
			table.put("property_value",0); 
			table.put("domain",0); 
			table.put("range",0); 
			table.put("builtin",0);
			table.put("is_anti_symmetric",0);
			table.put("is_cyclic",0);
			table.put("is_reflexive",0);
			table.put("is_symmetric",0);
			table.put("is_transitive",0);
			table.put("is_functional",0);
			table.put("is_inverse_functional",0);
			table.put("is_a",0);
			table.put("intersection_of",0); 
			table.put("union_of",0);
			table.put("equivalent_to",0); 
			table.put("disjoint_from",0); 
			table.put("inverse_of",0); 
			table.put("transitive_over",0);
			table.put("holds_over_chain",0);
			table.put("equivalent_to_chain",0);
			table.put("disjoint_over",0); 
			table.put("relationship",0); 
			table.put("created_by",0); 
			table.put("creation_date",0); 
			table.put("is-obsolete",0);
			table.put("replaced_by",0); 
			table.put("consider",0);
			table.put("expand_assertion_to",0); 
			table.put("expand_expression_to",0); 
			table.put("is_metadata_tag",0);
			table.put("is_class_level_tag",0);      

			return  table;
		}
		
		public int compare(String o1, String o2) {
			Integer i1 = tagsPriorities.get(o1);
			Integer i2 = tagsPriorities.get(o2);

			if(i1 == null || i2 == null){
				return -1;
			}
			
			return i1.compareTo(i2);
		}
		
	}
	
	
	
	
	
	
}
