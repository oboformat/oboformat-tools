package org.obolibrary.oboformat.writer;
//package org.obolibrary.oboparser;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//
//import org.obolibrary.oboparser.Frame.FrameType;
//
//public class OBOFormatWriter {
//	//BufferedReader reader;
//	
//	
//	final String DATA_VERSION = "data-version";
//	final String ID = "id";
//	final String NAME = "name";
//	
//	SimpleDateFormat headerDateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm");
//	SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
//
//	protected enum generateState {
//		HEADER, BODY
//	}
//	
//	protected enum Tag {
//		ID,
//		DATA_VERSION,
//		NAMESPACE,
//		NAME
//	}
//	
//	public HashMap<String,Tag> tagMap = new HashMap<String,Tag>();
//	
//	protected void initTagMap() {
//		tagMap.put("id",Tag.ID);
//		
//	}
//	
//	protected class OboOutStream {
//		int pos=0;
//		String line;
//		BufferedReader reader;
//		
//		public OboOutStream() {
//			pos = 0;
//			
//		}
//
//		public OboOutStream(BufferedReader r) {
//			reader = r;
//		}
//		
//		public char peekChar() {
//			prepare();
//			return line.charAt(pos);
//		}
//		
//		public char nextChar() {
//			pos++;
//			return line.charAt(pos-1);
//		}
//		
//		public String rest() {
//			prepare();
//			if (line == null)
//				return null;
//			return line.substring(pos);
//		}
//		
//		public void advance(int dist) {
//			pos += dist;
//		}
//		
//		public void prepare() {
//			if (line == null)
//				advanceLine();
//		}
//		
//		public void advanceLine() {
//			try {
//				line = reader.readLine();
//				pos = 0;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		public void forceEol() {
//			if (line == null) {
//				return;
//			}
//			pos = line.length();
//		}
//
//		
//		public boolean eol() {
//			prepare();
//			if (line == null) {
//				return false;
//			}
//			return pos == line.length();
//		}
//		
//		public boolean eof() {
//			prepare();
//			if (line == null) {
//				return true;
//			}
//			return false;
//		}
//		
//		public String getTag() {
//			return "";
//		}
//		
//		public boolean consume(String s) {
//			String r = rest();
//			if (r==null)
//				return false;
//			if (r.startsWith(s)) {
//				pos += s.length();
//				return true;
//			}
//			return false;
//		}
//
//		public int indexOf(char c) {
//			prepare();
//			if (line == null)
//				return -1;
//			return line.substring(pos).indexOf(c);
//		}
//		
//		public String toString() {
//			return line + "//" + pos;
//		}
//
//		public boolean peekCharIs(char c) {
//			if (eol() || eof())
//				return false;
//			return peekChar() == c;
//		}
//	}
//	
//	protected OboOutStream s;
//	
//	public OBOFormatWriter() {
//		super();
//		this.s = new OboOutStream();
//	}
//	
//	public OBOFormatWriter(OboOutStream s) {
//		super();
//		this.s = s;
//	}
//	
//	public void setReader(BufferedReader r) {
//		this.s.reader = r;
//	}
//
//	public OBODoc generate(String fn) throws IOException {
//		 BufferedReader in
//		   = new BufferedReader(new FileReader(fn));
//		 return generate(in);
//	}	
//	
//	public OBODoc generateURL(String urlstr) throws IOException {
//		URL url = new URL(urlstr);
//	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//		 return generate(in);
//	}	
//
//	
//	public void generate(BufferedReader reader, OBODoc obodoc) throws IOException {
//		setReader(reader);
//
//		generateOBODoc(obodoc);
//	}
//	
//	// ----------------------------------------
//	// GRAMMAR
//	// ----------------------------------------
//
//
//	public void generateOBODoc(OBODoc obodoc) {
//		generateHeaderFrame(obodoc.getHeaderFrame());
//		while (generateEntityFrame(obodoc)) {
//			
//		}
//	}
//
//	public void generateHeaderFrame(Frame h) {
//		if (s.peekChar() == '[') 
//			return false;
//		while (generateHeaderClauseNl(h)) {
//			
//		}
//		return true;
//	}
//
//	/**
//	 * header-clause ::= format-version-TVP | ... | ...
//	 */
//	protected void generateHeaderClauseNl(Frame h) {
//		generateZeroOrMoreWsNl();
//		if (s.peekChar() == '[') 
//			return false;
//
//		if (generateHeaderClause(h)) {
//			System.out.println("hdr// "+h);
//			return forcegenerateNlOrEof();
//		}
//		else {
//			return false;
//		}
//	}
//	
//	protected void generateHeaderClause(Frame h) {
//		Clause cl = new Clause();
//		String tag = getgenerateTag();
//		if (tag == null)
//			return false;
//		cl.setTag(tag);
//		h.addClause(cl);
//		if (tag == "data-version") {
//			return generateUnquotedString(cl);
//		}
//		if (tag == "format-version") {
//			return generateUnquotedString(cl);
//		}
//		// default
//		return generateUnquotedString(cl);
//	}
//	
//	public void generateEntityFrame(OBODoc obodoc) {
//		return generateTermFrame(obodoc) || generateTypedefFrame(obodoc);
//	}
//	
//	// ----------------------------------------
//	// [Term] Frames
//	// ----------------------------------------
//
//
//	/**
//	 * term-frame ::= nl*  '[Term]' nl  id-Tag Class-ID EOL  { term-frame-clause EOL } 
//	 */
//	public void generateTermFrame(OBODoc obodoc) {
//		Frame f = new Frame(FrameType.TERM);
//		generateZeroOrMoreWsNl();
//		if (s.consume("[Term]")) {
//			forcegenerateNlOrEof();
//			generateIdLine(f);
//			while (generateTermFrameClauseEOL(f)) {
//				
//			}
//			obodoc.addFrame(f);
//			//System.out.println("generated: "+f);
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 
//	 */
//	protected void generateTermFrameClauseEOL(Frame f) {
//		Clause cl = new Clause();
//		if (generateTermFrameClause(cl)) {
//			f.addClause(cl);
//			return generateEOL(cl);
//		}
//		return false;
//	}
//	
//	public void generateTermFrameClause(Clause cl) {
//		String tag = getgenerateTag();
//		if (tag == null)
//			return false;
//		cl.setTag(tag);
//		if (tag.equals("is_anonymous")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("name")) {
//			return generateUnquotedString(cl);
//		}
//		if (tag.equals("namespace")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("alt_id")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("def")) {
//			return generateDef(cl);
//		}
//		if (tag.equals("comment")) {
//			return generateUnquotedString(cl);
//		}
//		if (tag.equals("subset")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("synonym")) {
//			return generateSynonym(cl);
//		}
//		if (tag.equals("xref")) {
//			return generateXref(cl);
//		}
//		if (tag.equals("is_a")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("intersection_of")) {
//			return generateTermIntersectionOf(cl);
//		}
//		if (tag.equals("union_of")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("equivalent_to")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("disjoint_from")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("relationship")) {
//			return generateRelationship(cl);
//		}
//		if (tag.equals("created_by")) {
//			return generatePerson(cl);
//		}
//		if (tag.equals("creation_date")) {
//			return generateISODate(cl);
//		}
//		if (tag.equals("is_obsolete")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("replaced_by")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("consider")) {
//			return generateIdRef(cl);
//		}
//
//		return false;
//	}
//	
//	// ----------------------------------------
//	// [Typedef] Frames
//	// ----------------------------------------
//
//
//	/**
//	 * Typedef-frame ::= nl*  '[Typedef]' nl  id-Tag Class-ID EOL  { Typedef-frame-clause EOL } 
//	 */
//	public void generateTypedefFrame(OBODoc obodoc) {
//		Frame f = new Frame(FrameType.TYPEDEF);
//		generateZeroOrMoreWsNl();
//		if (s.consume("[Typedef]")) {
//			forcegenerateNlOrEof();
//			generateIdLine(f);
//			while (generateTypedefFrameClauseEOL(f)) {
//				
//			}
//			obodoc.addFrame(f);
//			System.out.println("generated: "+f);
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 
//	 */
//	protected void generateTypedefFrameClauseEOL(Frame f) {
//		Clause cl = new Clause();
//	
//		if (generateTypedefFrameClause(cl)) {
//			f.addClause(cl);
//			return generateEOL(cl);
//		}
//		return false;
//	}
//	
//	public void generateTypedefFrameClause(Clause cl) {
//		String tag = getgenerateTag();
//		if (tag == null)
//			return false;
//		cl.setTag(tag);
//		if (tag.equals("is_anonymous")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("name")) {
//			return generateUnquotedString(cl);
//		}
//		if (tag.equals("namespace")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("alt_id")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("def")) {
//			return generateDef(cl);
//		}
//		if (tag.equals("comment")) {
//			return generateUnquotedString(cl);
//		}
//		if (tag.equals("subset")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("synonym")) {
//			return generateSynonym(cl);
//		}
//		if (tag.equals("xref")) {
//			return generateXref(cl);
//		}
//		if (tag.equals("domain")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("range")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("is_anti_symmetric")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_cyclic")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_reflexive")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_symmetric")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_transitive")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_functional")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_inverse_functional")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_a")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("intersection_of")) {
//			return generateTypedefIntersectionOf(cl);
//		}
//		if (tag.equals("union_of")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("equivalent_to")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("disjoint_from")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("transitive_over")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("holds_over_chain")) {
//			return generateIdRefPair(cl);
//		}
//		if (tag.equals("equivalent_to_chain")) {
//			return generateIdRefPair(cl);
//		}
//		if (tag.equals("disjoint_over")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("relationship")) {
//			return generateRelationship(cl);
//		}
//		if (tag.equals("created_by")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("creation_date")) {
//			return generateISODate(cl);
//		}
//		if (tag.equals("is_obsolete")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("replaced_by")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("consider")) {
//			return generateIdRef(cl);
//		}
//		if (tag.equals("is_metadata_tag")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("is_class_level")) {
//			return generateBoolean(cl);
//		}
//		if (tag.equals("expand_assertion_to")) {
//			return generateOwlDef(cl);
//		}
//		if (tag.equals("expand_expression_to")) {
//			return generateOwlDef(cl);
//		}
//
//		
//
//		return false;
//	}
//
//	// ----------------------------------------
//	// TVP
//	// ----------------------------------------
//
//	
//	private String getgenerateTag() {
//		int i = s.indexOf(':');
//		if (i == -1) {
//			return null;
//		}
//		String tag = s.rest().substring(0, i);
//		s.advance(i+1);
//		generateWs();
//		generateZeroOrMoreWs();
//		return tag;
//	}
//	
//	private void generateId(Clause cl) {
//		return generateIdRef(cl);
//	}
//	
//	private void generateIdRef(Clause cl) {
//		String id = getgenerateUntil(" !{");
//		cl.addValue(id);
//		
//		return true;
//	}
//	
//	private void generateIdRefPair(Clause cl) {
//		if (generateIdRef(cl)) {
//			if (generateOneOrMoreWs()) {
//				return generateIdRef(cl);
//			}
//		}
//		return false;
//	}
//
//	
//	private void generatePerson(Clause cl) {
//		return generateUnquotedString(cl);
//	}
//
//
//	private void generateISODate(Clause cl) {
//		String dateStr = getgenerateUntil(" !{");
//		Date date;
//		try {
//			date = isoDateFormat.generate(dateStr);
//			cl.setValue(date);
//			return true;
//		} catch (generateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	private void generateRelationship(Clause cl) {
//		return generateIdRef(cl) && generateOneOrMoreWs() && generateIdRef(cl);
//	}
//
//	/**
//	 * intersection_of-Tag Class-ID | intersection_of-Tag Relation-ID Class-ID 
//	 */
//	private void generateTermIntersectionOf(Clause cl) {
//		if (generateIdRef(cl)) {
//			if (s.peekCharIs(' ')) {
//				generateOneOrMoreWs();
//				generateIdRef(cl);
//			}
//			return true;
//		}
//		return false;
//	}
//	
//	private void generateTypedefIntersectionOf(Clause cl) {
//		return generateIdRef(cl);
//	}
//
//	private void generateSynonym(Clause cl) {
//		if (s.consume("\"")) {
//			String syn = getgenerateUntilAdv("\"");
//			cl.setValue(syn);
//			generateZeroOrMoreWs();
//			if (s.peekCharIs('[')) {
//				return generateXrefList(cl);
//			}
//			else if (generateSynonymScope(cl)) {
//				generateZeroOrMoreWs();
//				if (s.peekCharIs('[')) {
//					return generateXrefList(cl);
//				}
//				else if (generateSynontmType(cl)) {
//					generateZeroOrMoreWs();
//					return generateXrefList(cl);
//				}
//			}
//		}
//		return false;
//	}
//
//	private void generateSynontmType(Clause cl) {
//		return generateIdRef(cl);
//	}
//
//	private void generateSynonymScope(Clause cl) {
//		return generateIdRef(cl);
//	}
//
//	private void generateDef(Clause cl) {
//		if (s.consume("\"")) {
//			String def = getgenerateUntilAdv("\"");
//			cl.setValue(def);
//			generateZeroOrMoreWs();
//			return generateXrefList(cl);
//		}
//		return false;
//	}
//	
//	private void generateOwlDef(Clause cl) {
//		if (s.consume("\"")) {
//			String def = getgenerateUntilAdv("\"");
//			cl.setValue(def);
//			generateZeroOrMoreWs();
//			return generateXrefList(cl);
//		}
//		return false;
//	}
//
//
//
//	private void generateXrefList(Clause cl) {
//		if (s.consume("[")) {
//			generateZeroOrMoreXrefs(cl);
//			generateZeroOrMoreWs();
//			return s.consume("]");
//		}		
//		return false;
//	}
//
//	private void generateZeroOrMoreXrefs(Clause cl) {
//		if (generateXref(cl)) {
//			while (s.consume(",") && generateXref(cl)) {
//			
//			}
//		}
//		return true;	
//	}
//
//	private void generateXref(Clause cl) {
//		generateZeroOrMoreWs();
//		String id = getgenerateUntil(" \",]!{");
//		if (id != null) {
//			Xref xref = new Xref(id);
//			cl.addValue(xref);
//			generateZeroOrMoreWs();
//			if (s.peekCharIs('"')) {
//				s.consume("\"");
//				xref.setAnnotation(this.getgenerateUntilAdv("\""));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	private void generateBoolean(Clause cl) {
//		if (s.consume("true")) {
//			cl.setValue(true);
//			return true;
//		}
//		if (s.consume("false")) {
//			cl.setValue(false);
//			return true;
//		}
//		// throw
//		return false;
//	}
//
//	private void generateNamespace(Clause cl) {
//		return generateIdRef(cl);	
//	}
//
//	protected void generateIdLine(Frame f) {
//		String tag = getgenerateTag();
//		if (!tag.equals(ID)) {
//			return false;
//		}
//		Clause cl = new Clause();
//		cl.setTag("id");
//		f.addClause(cl);
//		String id = getgenerateUntil(" !{");
//		if (id == null)
//			return false;
//		cl.addValue(id);
//		f.setId(id);
//		return generateEOL(cl);
//	}
//	
//	private void generateEOL(Clause cl) {
//		while (generateWs()) {
//			
//		}
//		generateQualifierBlock(cl);
//		generateHiddenComment();
//		return forcegenerateNlOrEof();
//	}
//
//	private void generateHiddenComment() {
//		while (generateWs()) {
//			
//		}
//		if (s.eol() || s.eof())
//			return false;
//		if (s.peekChar() == '!') {
//			s.forceEol();
//			return true;
//		}
//		return false;
//		
//	}
//
//	private void generateQualifierBlock(Clause cl) {
//		return false;		
//	}
//
//	
//	//
//	protected void generateUnquotedString(Clause cl) {
//		generateZeroOrMoreWs();
//		String v = getgenerateUntil("!{");
//
//		cl.setValue(v);
//		//s.advanceLine();
//		return true;
//	}
//	
//	// Newlines, whitespace
//	
//	protected void forcegenerateNlOrEof() {
//		while (generateWs()) {
//			
//		}
//		if ( generateNlChar() ) 
//			return true;
//		if (s.eof())
//			return true;
//		throw new Error("expected newline");
//	}
//	
//	protected void generateZeroOrMoreWsNl() {
//		while (generateWsNl()) {
//			
//		}
//		return true;
//	}
//	
//	protected void generateWsNl() {
//		generateZeroOrMoreWs();
//		if ( generateNlChar() ) 
//			return true;
//		return false;
//	}
//
//
//	// non-newline
//	protected void generateWs() {
//		if (s.eol()) {
//			return false;
//		}
//		while (!s.eof() && s.peekChar() == ' ') {
//			s.advance(1);
//			return true;
//		}
//		return false;
//	}
//	
//	protected void generateOneOrMoreWs() {
//		if (s.eol() || s.eof()) {
//			return false;
//		}
//		int n = 0;
//		while (s.peekChar() == ' ') {
//			s.advance(1);
//			n++;
//		}
//		return n>0;
//	}
//
//	protected void generateZeroOrMoreWs() {
//		if (s.eol() || s.eof()) {
//			return true;
//		}
//		
//		while (s.peekChar() == ' ') {
//			s.advance(1);
//		}
//		return true;
//	}
//
//
//	protected void generateNlChar() {
//		if (s.eol()) {
//			s.advanceLine();
//			return true;
//		}
//		return false;
//	}
//	
//	private String getgenerateUntilAdv(String compl) {
//		String ret = getgenerateUntil(compl);
//		s.advance(1);
//		return ret;
//	}
//
//	private String getgenerateUntil(String compl) {
//		String r = s.rest();
//		int i = 0;
//		boolean hasEscapedChars = false;
//		while (i < r.length()) {
//			if (r.charAt(i) == '\\') {
//				hasEscapedChars = true;
//				i+=2; // Escape
//				continue;
//			}
//			if (compl.contains(r.subSequence(i, i+1))) {
//				break;
//			}
//			i++;
//		}
//		String ret = r.substring(0, i);
//		// TODO - replace escaped characters
//		s.advance(i);
//		return ret;
//	}
//
//}
