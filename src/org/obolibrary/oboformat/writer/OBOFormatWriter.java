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
		Collections.sort(termFrames, FramesComparator.instance);

		List<Frame> typeDefFrames = new ArrayList<Frame>();
		typeDefFrames.addAll(doc.getTypedefFrames());
		Collections.sort(typeDefFrames, FramesComparator.instance);


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
		Collections.sort(tags, HeaderTagsComparator.instance);

		write(new Clause(OboFormatTag.TAG_FORMAT_VERSION.getTag(), "1.2"), writer);
		
		for(String tag: tags){

			if(tag.equals(OboFormatTag.TAG_FORMAT_VERSION.getTag()))
				continue;

			List<Clause> clauses = new ArrayList<Clause>(frame.getClauses(tag));
			Collections.sort(clauses, ClauseComparator.instance);
			
			for(Clause clause: clauses){
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

			comparator = TermsTagsComparator.instance;
		}else if (frame.getType() == FrameType.TYPEDEF){
			writeLine("[Typedef]", writer);
			comparator = TypeDefTagsComparator.instance;
		}

		if(frame.getId() != null){
			writeLine("id: " + frame.getId(), writer);
		}

		List<String> tags = duplicateTags(frame.getTags());
		Collections.sort(tags, comparator);

		for(String tag: tags){
			List<Clause> clauses = new ArrayList<Clause>(frame.getClauses(tag));
			Collections.sort(clauses, ClauseComparator.instance);
			for( Clause clause : clauses){

				if(OboFormatTag.TAG_ID.getTag().equals(clause.getTag()))
					continue;
				else if(OboFormatTag.TAG_DEF.getTag().equals(clause.getTag()))
					writeDef(clause, writer);
				else if(OboFormatTag.TAG_SYNONYM.getTag().equals(clause.getTag()))
					writeSynonym(clause, writer);
				else if(OboFormatTag.TAG_PROPERTY_VALUE.getTag().equals(clause.getTag()))
					writePropertyValue(clause, writer);
				else if(OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(clause.getTag()) || OboFormatTag.TAG_EXPAND_ASSERTION_TO.getTag().equals(clause.getTag()))
					writeClauseWithQuotedString(clause, writer);
				else if (OboFormatTag.TAG_XREF.getTag().equals(clause.getTag()))
					writeXRefClause(clause, writer);
				else
					write(clause, writer);
			}
		}
		writeEmptyLine(writer);
	}


	private void writeXRefClause(Clause clause, BufferedWriter writer) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(clause.getTag());
		sb.append(": ");
		Object value = clause.getValue();
		if (value != null && value instanceof Xref) {
			Xref xref = (Xref) value;
			if (xref.getIdref() != null) {
				sb.append(xref.getIdref());
				String annotation = xref.getAnnotation();
				if (annotation != null) {
					sb.append(" \"");
					sb.append(escapeOboString(annotation));
					sb.append('"');
				}
			}
		}
		appendQualifiers(sb, clause);
		writeLine(sb, writer);
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
			appendXrefs(sb, xrefs);
		}else if(OboFormatTag.TAG_DEF.getTag().equals(clause.getTag()) || OboFormatTag.TAG_EXPAND_EXPRESSION_TO.getTag().equals(clause.getTag()) ||
				OboFormatTag.TAG_EXPAND_ASSERTION_TO.getTag().equals(clause.getTag())){
			sb.append(" []");
		}

		writeLine(sb, writer);
	}

	private void appendXrefs(StringBuilder sb, Collection<Xref> xrefs) {
		List<Xref> sortedXrefs = new ArrayList<Xref>(xrefs);
		Collections.sort(sortedXrefs, XrefComparator.instance);
		sb.append(" [");

		Iterator<Xref> xrefsIterator = sortedXrefs.iterator();
		while (xrefsIterator.hasNext()) {
			sb.append(xrefsIterator.next().getIdref());
			if (xrefsIterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("]");
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
			if(idsLabel != null && !valuesIterator.hasNext()){
				// only try to resolve the last value as id
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
			sb.append(escapeOboString(value));
			if (valuesIterator.hasNext()) {
				sb.append(' ');
			}
		}

		Collection<Xref> xrefs = clause.getXrefs();



		if(xrefs != null){
			appendXrefs(sb, xrefs);
		}
		
		appendQualifiers(sb, clause);
		
		if(idsLabel != null && idsLabel.length() > 0) {
			String trimmed = idsLabel.toString().trim();
			if (trimmed.length() > 0) {
				sb.append(" ! ");
				sb.append(trimmed);
			}
		}
		writeLine(sb, writer);
	}

	private void appendQualifiers(StringBuilder sb, Clause clause) {
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
			else if (c == '{') {
				modfied = true;
				sb.append("\\{");
			}
			else if (c == '}') {
				modfied = true;
				sb.append("\\}");
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

		static final HeaderTagsComparator instance = new HeaderTagsComparator();
		
		private static Hashtable<String, Integer> tagsPriorities = buildTagsPriorities();

		private static Hashtable<String, Integer> buildTagsPriorities(){
			Hashtable<String, Integer> table = new Hashtable<String, Integer>();

			table.put(OboFormatTag.TAG_FORMAT_VERSION.getTag(),0);
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
			table.put("ontology",85); // moved from pos 5 to emulate OBO-Edit behavior

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

		static final TermsTagsComparator instance = new TermsTagsComparator();
		
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

		static final TypeDefTagsComparator instance = new TypeDefTagsComparator();
		
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

		static final FramesComparator instance = new FramesComparator();
		
		public int compare(Frame f1, Frame f2) {
			return f1.getId().compareTo(f2.getId());
		}

	}

	private static class ClauseComparator implements Comparator<Clause> {
		
		static final ClauseComparator instance = new ClauseComparator();
		
		public int compare(Clause o1, Clause o2) {
			int comp = compareValues(o1.getValue(), o2.getValue());
			if (comp != 0) {
				return comp;
			}
			return compareValues(o1.getValue2(), o2.getValue2());
		}
	
		private int compareValues(Object o1, Object o2) {
			String s1 = toStringRepresentation(o1);
			String s2 = toStringRepresentation(o2);
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			return s1.compareToIgnoreCase(s2);
		}
		
		private String toStringRepresentation(Object obj) {
			String s = null;
			if (obj != null) {
				if (obj instanceof Xref) {
					Xref xref = (Xref) obj;
					s = xref.getIdref()+" "+xref.getAnnotation();
				}
				else if (obj instanceof String) {
					s = (String) obj;
				}
				else {
					s = obj.toString();
				}
			}
			return s;
		}
	}

	private static class XrefComparator implements Comparator<Xref> {
	
		static final XrefComparator instance = new XrefComparator();
		
		public int compare(Xref x1, Xref x2) {
			String idref1 = x1.getIdref();
			String idref2 = x2.getIdref();
			if (idref1 == null && idref2 == null) {
				return 0;
			}
			if (idref1 == null) {
				return -1;
			}
			if (idref2 == null) {
				return 1;
			}
			return idref1.compareToIgnoreCase(idref2);
		}
	}

}
