package org.obolibrary.oboformat.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
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
import org.obolibrary.oboformat.model.QualifierValue;
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

	private boolean isCheckStructure = true;

	public OBOFormatWriter(){
		oboDoc = null;
	}
	
	

	public boolean isCheckStructure() {
		return isCheckStructure;
	}



	public void setCheckStructure(boolean isCheckStructure) {
		this.isCheckStructure = isCheckStructure;
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

	public void write(OBODoc doc, String outFile) throws IOException, URISyntaxException{

		FileOutputStream os = new FileOutputStream(new File( outFile )); 
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		write(doc,bw);
		bw.close();
	}


	public void write(OBODoc doc, BufferedWriter writer) throws IOException{

		if (isCheckStructure) {
			doc.check();
		}
		Frame headerFrame = doc.getHeaderFrame();

		this.oboDoc = doc;

		writeHeader(headerFrame, writer);

		List<Frame> termFrames = new ArrayList<Frame>();
		termFrames.addAll(doc.getTermFrames());
		Collections.sort(termFrames, new FramesComparator());

		List<Frame> typeDefFrames = new ArrayList<Frame>();
		typeDefFrames.addAll(doc.getTypedefFrames());
		Collections.sort(typeDefFrames, new FramesComparator());


		for(Frame f: termFrames){
			write(f, writer);
		}

		for(Frame f: typeDefFrames){
			write(f, writer);
		}
	}

	private void writeLine(StringBuilder ln, BufferedWriter writer) throws IOException{
		ln.append('\n');
		writer.write(ln.toString());
	}
	
	private void writeLine(String ln, BufferedWriter writer) throws IOException{
		writer.write(ln+"\n");
	}
	
	private void writeEmptyLine(BufferedWriter writer) throws IOException{
		writer.write("\n");
	}

	private List<String> duplicateTags(Set<String> src){
		List<String> tags = new ArrayList<String>(src.size());

		for(String tag: src){
			tags.add(tag);
		}

		return tags;
	}

	public void writeHeader(Frame frame, BufferedWriter writer) throws IOException{

		List<String> tags = duplicateTags(frame.getTags());
		Collections.sort(tags, new HeaderTagsComparator());

		write(new Clause(OboFormatTag.TAG_FORMAT_VERSION.getTag(), "1.2"), writer);
		
		for(String tag: tags){

			if(tag.equals(OboFormatTag.TAG_FORMAT_VERSION.getTag()))
				continue;

			for(Clause clause: frame.getClauses(tag)){
				if(tag.equals(OboFormatTag.TAG_SUBSETDEF.getTag())){
					writeSynonymtypedef(clause, writer);
				}else if(tag.equals(OboFormatTag.TAG_SYNONYMTYPEDEF.getTag())){
					writeSynonymtypedef(clause, writer);
				}else
					write(clause, writer);
			}
		}

		writeEmptyLine(writer);


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
		writeEmptyLine(writer);
	}


	private void writeSynonymtypedef(Clause clause, BufferedWriter writer) throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append(clause.getTag());
		sb.append(": ");

		Iterator<Object> valuesIterator = clause.getValues().iterator();
		Collection<?> values = clause.getValues();
		for(int i=0;i<values.size();i++) {
			String value = valuesIterator.next().toString();
			if (i==1) { sb.append('"'); }
			sb.append(escapeOboString(value));
			if (i==1) { sb.append('"'); }
			if (valuesIterator.hasNext()) { sb.append(' '); }
		}
		writeLine(sb, writer);
	}

	private void writeClauseWithQuotedString(Clause clause, BufferedWriter writer) throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append(clause.getTag());
		sb.append(": ");

		boolean  first = true;
		Iterator<Object> valuesIterator = clause.getValues().iterator();
		while (valuesIterator.hasNext()) {
			if (first) { sb.append('"'); }
			String value = valuesIterator.next().toString();
			sb.append(escapeOboString(value));
			if (first) { sb.append('"'); }
			if (valuesIterator.hasNext()) { sb.append(' ');}
			first = false;
		}

		Collection<Xref> xrefs = clause.getXrefs();

		// if the xrefs value is null, then there should *never* be xrefs at this location
		// not that the value may be a non-null empty list - here we still want to write []
		if(xrefs != null){

			sb.append(" [");

			Iterator<Xref> xrefsIterator = xrefs.iterator();
			while (xrefsIterator.hasNext()) {
				sb.append(xrefsIterator.next().getIdref());
				if (xrefsIterator.hasNext()) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}else if(OboFormatTag.TAG_DEF.getTag().equals(clause.getTag()) || OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(clause.getTag()) ||
				OboFormatTag.TAG_EXPAND_ASSERTION_TO.getTag().equals(clause.getTag())){
			sb.append(" []");
		}

		writeLine(sb, writer);
	}


	public void writeDef(Clause clause, BufferedWriter writer) throws IOException{

		writeClauseWithQuotedString(clause, writer);
	}

	public void writePropertyValue(Clause clause, BufferedWriter writer) throws IOException{

		StringBuilder sb = new StringBuilder();
		sb.append(clause.getTag());
		sb.append(": ");

		Collection<?> cols = clause.getValues();

		if(cols.size()<2){
			LOG.warn("The " + OboFormatTag.TAG_PROPERTY_VALUE.getTag() + " has incorrect number of values: " + clause);
			return;
		}

		Iterator<?> itr = cols.iterator();
		sb.append(itr.next());
		sb.append(" ");

		String val = itr.next().toString();

		if(val.contains(" ") || !val.contains(":")) {
			sb.append(" \"");
			sb.append(val);
			sb.append("\"");
		}
		else {
			sb.append(val);
		}
		writeLine(sb, writer);
	}


	public void writeSynonym(Clause clause, BufferedWriter writer) throws IOException{
		writeClauseWithQuotedString(clause, writer);
	}

	public void write(Clause clause, BufferedWriter writer) throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append(clause.getTag());
		sb.append(": ");

		Iterator<Object> valuesIterator = clause.getValues().iterator();
		StringBuilder idsLabel = null;
		if (this.oboDoc != null && tagsInformative.contains(clause.getTag())) {
			idsLabel = new StringBuilder();
		}

		while (valuesIterator.hasNext()) {
			String value = valuesIterator.next().toString();
			if(idsLabel != null){
				Frame f= oboDoc.getTermFrame(value);
				if(f == null){
					f = oboDoc.getTypedefFrame(value);
				}

				if(f != null){
					Clause cl = f.getClause(OboFormatTag.TAG_NAME.getTag());
					if(cl != null){
						if(idsLabel.length() > 0)
							idsLabel.append(" ");
						idsLabel.append(cl.getValue());
					}

				}
			}
			sb.append(value);
			if (valuesIterator.hasNext()) {
				sb.append(' ');
			}
		}

		Collection<Xref> xrefs = clause.getXrefs();



		if(xrefs != null){

			//	if(!xrefs.isEmpty())
			sb.append(" [");

			Iterator<Xref> xrefsIterator = xrefs.iterator();
			while (xrefsIterator.hasNext()) {
				sb.append(xrefsIterator.next().getIdref());
				if (xrefsIterator.hasNext()) {
					sb.append(", ");
				}
			}

			//	if(!xrefs.isEmpty())
			sb.append("]");
		}
		
		Collection<QualifierValue> qvs = clause.getQualifierValues();
		if (qvs != null && qvs.size() > 0) {
			sb.append(" {");
			Iterator<QualifierValue> qvsIterator = qvs.iterator();
			while (qvsIterator.hasNext()) {
				QualifierValue qv = qvsIterator.next();
				sb.append(qv.getQualifier());
				sb.append("=\"");
				sb.append(escapeOboString(qv.getValue().toString()));
				sb.append("\"");
				if (qvsIterator.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append("}");
		}
		
		if(idsLabel != null && idsLabel.length() > 0) {
			String trimmed = idsLabel.toString().trim();
			if (trimmed.length() > 0) {
				sb.append(" ! ");
				sb.append(trimmed);
			}
		}
		writeLine(sb, writer);
	}

	private CharSequence escapeOboString(String in) {
		boolean modfied = false;
		StringBuilder sb = new StringBuilder();
		int length = in.length();
		for (int i = 0; i < length; i++) {
			char c = in.charAt(i);
			if (c == '\n') {
				modfied = true;
				sb.append("\\n");
			}
			else if (c == '"') {
				modfied = true;
				sb.append("\\\"");
			}
			else {
				sb.append(c);
			}
		}
		if (modfied) {
			return sb;
		}
		return in;
	}
	
	private static class HeaderTagsComparator implements Comparator<String>{

		private static Hashtable<String, Integer> tagsPriorities = buildTagsPriorities();

		private static Hashtable<String, Integer> buildTagsPriorities(){
			Hashtable<String, Integer> table = new Hashtable<String, Integer>();

			table.put(OboFormatTag.TAG_FORMAT_VERSION.getTag(),0);
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

			if(i1 == null)
				i1 = 10000;

			if(i2 == null)
				i2 = 10000;

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

			if(i1 == null)
				i1 = 10000;

			if(i2 == null)
				i2 = 10000;

			return i1.compareTo(i2);
		}

	}



	private static class TypeDefTagsComparator implements Comparator<String>{

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
			table.put("property_value",55); 
			table.put("domain",60); 
			table.put("range",65); 
			table.put("builtin",70);
			table.put("is_anti_symmetric",75);
			table.put("is_cyclic",80);
			table.put("is_reflexive",85);
			table.put("is_symmetric",90);
			table.put("is_transitive",100);
			table.put("is_functional",105);
			table.put("is_inverse_functional",110);
			table.put("is_a",115);
			table.put("intersection_of",120); 
			table.put("union_of",125);
			table.put("equivalent_to",130); 
			table.put("disjoint_from",135); 
			table.put("inverse_of",140); 
			table.put("transitive_over",145);
			table.put("holds_over_chain",150);
			table.put("equivalent_to_chain",155);
			table.put("disjoint_over",160); 
			table.put("relationship",165); 
			table.put("created_by",170); 
			table.put("creation_date",175); 
			table.put("is-obsolete",180);
			table.put("replaced_by",185); 
			table.put("consider",190);
			table.put("expand_assertion_to",195); 
			table.put("expand_expression_to",200); 
			table.put("is_metadata_tag",205);
			table.put("is_class_level_tag",210);      

			return  table;
		}

		public int compare(String o1, String o2) {
			Integer i1 = tagsPriorities.get(o1);
			Integer i2 = tagsPriorities.get(o2);

			if(i1 == null)
				i1 = 10000;

			if(i2 == null)
				i2 = 10000;

			return i1.compareTo(i2);
		}

	}


	private static class FramesComparator implements Comparator<Frame>{

		public int compare(Frame f1, Frame f2) {
			return f1.getId().compareTo(f2.getId());
		}

	}



}
