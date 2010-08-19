package org.obolibrary.oboformat.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.QualifierValue;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.model.Frame.FrameType;

/**
 * implements the OBO Format 1.4 specification
 *
 */
public class OBOFormatParser {
	
	final String DATA_VERSION = "data-version";
	final String ID = "id";
	final String NAME = "name";
	
	SimpleDateFormat headerDateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm");
	SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

	protected enum ParseState {
		HEADER, BODY
	}
	
	protected enum Tag {
		ID,
		DATA_VERSION,
		NAMESPACE,
		NAME
	}
	
	public HashMap<String,Tag> tagMap = new HashMap<String,Tag>();
	
	protected void initTagMap() {
		tagMap.put("id",Tag.ID);
		
	}
	
	protected class MyStream {
		int pos=0;
		String line;
		BufferedReader reader;
		
		public MyStream() {
			pos = 0;
			
		}

		public MyStream(BufferedReader r) {
			reader = r;
		}
		
		public char peekChar() {
			prepare();
			return line.charAt(pos);
		}
		
		public char nextChar() {
			pos++;
			return line.charAt(pos-1);
		}
		
		public String rest() {
			prepare();
			if (line == null)
				return null;
			return line.substring(pos);
		}
		
		public void advance(int dist) {
			pos += dist;
		}
		
		public void prepare() {
			if (line == null)
				advanceLine();
		}
		
		public void advanceLine() {
			try {
				line = reader.readLine();
				pos = 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void forceEol() {
			if (line == null) {
				return;
			}
			pos = line.length();
		}

		
		public boolean eol() {
			prepare();
			if (line == null) {
				return false;
			}
			return pos == line.length();
		}
		
		public boolean eof() {
			prepare();
			if (line == null) {
				return true;
			}
			return false;
		}
		
		public String getTag() {
			return "";
		}
		
		public boolean consume(String s) {
			String r = rest();
			if (r==null)
				return false;
			if (r.startsWith(s)) {
				pos += s.length();
				return true;
			}
			return false;
		}

		public int indexOf(char c) {
			prepare();
			if (line == null)
				return -1;
			return line.substring(pos).indexOf(c);
		}
		
		public String toString() {
			return line + "//" + pos;
		}

		public boolean peekCharIs(char c) {
			if (eol() || eof())
				return false;
			return peekChar() == c;
		}
	}
	
	protected MyStream s;
	
	public OBOFormatParser() {
		super();
		this.s = new MyStream();
	}
	
	public OBOFormatParser(MyStream s) {
		super();
		this.s = s;
	}
	
	public void setReader(BufferedReader r) {
		this.s.reader = r;
	}

	/**
	 * Parses a local file to an OBODoc
	 * 
	 * @param filename
	 * @return parsed obo document
	 * @throws IOException
	 */
	public OBODoc parse(String fn) throws IOException {
		if (fn.startsWith("http:"))
			return parse(new URL(fn));
		 BufferedReader in
		   = new BufferedReader(new FileReader(fn));
		 return parse(in);
	}	

	/**
	 * Parses a remote URL to an OBODoc
	 * 
	 * @param filename
	 * @return parsed obo document
	 * @throws IOException
	 */
	public OBODoc parse(URL url) throws IOException {
	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    return parse(in);
	}	

	/**
	 * Parses a remote URL to an OBODoc
	 * 
	 * @param filename
	 * @return parsed obo document
	 * @throws IOException
	 */
	public OBODoc parseURL(String urlstr) throws IOException {
		URL url = new URL(urlstr);
	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		 return parse(in);
	}	

	
	/**
	 * 
	 * 
	 * @param reader
	 * @returnparsed obo document
	 * @throws IOException
	 */
	public OBODoc parse(BufferedReader reader) throws IOException {
		setReader(reader);

		OBODoc obodoc = new OBODoc();
		if (parseOBODoc(obodoc))
			return obodoc;
		return null;
	}
	
	// ----------------------------------------
	// GRAMMAR
	// ----------------------------------------


	public boolean parseOBODoc(OBODoc obodoc) {
		Frame h = new Frame(FrameType.HEADER);
		obodoc.setHeaderFrame(h);
		parseHeaderFrame(h);
		while (parseEntityFrame(obodoc)) {
			
		}
		parseZeroOrMoreWsOptCmtNl();
		if (s.eof()) {
			return true;
		}
		else {
			System.out.println("UNPARSED:"+s);
			return false;
		}
	}

	public boolean parseHeaderFrame(Frame h) {
		if (s.peekChar() == '[') 
			return false;
		while (parseHeaderClauseNl(h)) {
			
		}
		return true;
	}

	/**
	 * header-clause ::= format-version-TVP | ... | ...
	 */
	protected boolean parseHeaderClauseNl(Frame h) {
		parseZeroOrMoreWsOptCmtNl();
		if (s.peekChar() == '[') 
			return false;

		if (parseHeaderClause(h)) {
			System.out.println("hdr// "+h);
			return forceParseNlOrEof();
		}
		else {
			return false;
		}
	}
	
	protected boolean parseHeaderClause(Frame h) {
		Clause cl = new Clause();
		String tag = getParseTag();
		if (tag == null)
			return false;
		cl.setTag(tag);
		h.addClause(cl);
		if (tag == "data-version") {
			return parseUnquotedString(cl);
		}
		if (tag == "format-version") {
			return parseUnquotedString(cl);
		}
		// default
		return parseUnquotedString(cl);
	}
	
	public boolean parseEntityFrame(OBODoc obodoc) {
		return parseTermFrame(obodoc) || parseTypedefFrame(obodoc);
	}
	
	// ----------------------------------------
	// [Term] Frames
	// ----------------------------------------


	/**
	 * term-frame ::= nl*  '[Term]' nl  id-Tag Class-ID EOL  { term-frame-clause EOL } 
	 */
	public boolean parseTermFrame(OBODoc obodoc) {
		Frame f = new Frame(FrameType.TERM);
		parseZeroOrMoreWsOptCmtNl();
		if (s.consume("[Term]")) {
			forceParseNlOrEof();
			parseIdLine(f);
			while (parseTermFrameClauseEOL(f)) {
				
			}
			obodoc.addFrame(f);
			//System.out.println("parsed: "+f);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	protected boolean parseTermFrameClauseEOL(Frame f) {
		Clause cl = new Clause();
		if (parseTermFrameClause(cl)) {
			f.addClause(cl);
			return parseEOL(cl);
		}
		else {
			if (cl.getTag() != null) {
				System.out.println("problem parsing tag:"+s);
			}
				
		}
		return false;
	}
	
	public boolean parseTermFrameClause(Clause cl) {
		String tag = getParseTag();
		if (tag == null)
			return false;
		cl.setTag(tag);
		if (tag.equals("is_anonymous")) {
			return parseBoolean(cl);
		}
		if (tag.equals("name")) {
			return parseUnquotedString(cl);
		}
		if (tag.equals("namespace")) {
			return parseIdRef(cl);
		}
		if (tag.equals("alt_id")) {
			return parseIdRef(cl);
		}
		if (tag.equals("def")) {
			return parseDef(cl);
		}
		if (tag.equals("comment")) {
			return parseUnquotedString(cl);
		}
		if (tag.equals("subset")) {
			return parseIdRef(cl);
		}
		if (tag.equals("synonym")) {
			return parseSynonym(cl);
		}
		if (tag.equals("xref") || tag.equals("xref_analog")) {
			return parseDirectXref(cl);
		}
		if (tag.equals("is_a")) {
			return parseIdRef(cl);
		}
		if (tag.equals("intersection_of")) {
			return parseTermIntersectionOf(cl);
		}
		if (tag.equals("union_of")) {
			return parseIdRef(cl);
		}
		if (tag.equals("equivalent_to")) {
			return parseIdRef(cl);
		}
		if (tag.equals("disjoint_from")) {
			return parseIdRef(cl);
		}
		if (tag.equals("relationship")) {
			return parseRelationship(cl);
		}
		if (tag.equals("created_by")) {
			return parsePerson(cl);
		}
		if (tag.equals("creation_date")) {
			return parseISODate(cl);
		}
		if (tag.equals("is_obsolete")) {
			return parseBoolean(cl);
		}
		if (tag.equals("replaced_by")) {
			return parseIdRef(cl);
		}
		if (tag.equals("consider")) {
			return parseIdRef(cl);
		}

		return false;
	}
	
	// ----------------------------------------
	// [Typedef] Frames
	// ----------------------------------------


	/**
	 * Typedef-frame ::= nl*  '[Typedef]' nl  id-Tag Class-ID EOL  { Typedef-frame-clause EOL } 
	 */
	public boolean parseTypedefFrame(OBODoc obodoc) {
		Frame f = new Frame(FrameType.TYPEDEF);
		parseZeroOrMoreWsOptCmtNl();
		if (s.consume("[Typedef]")) {
			forceParseNlOrEof();
			parseIdLine(f);
			while (parseTypedefFrameClauseEOL(f)) {
				
			}
			obodoc.addFrame(f);
			System.out.println("parsed: "+f);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	protected boolean parseTypedefFrameClauseEOL(Frame f) {
		Clause cl = new Clause();
	
		if (parseTypedefFrameClause(cl)) {
			f.addClause(cl);
			return parseEOL(cl);
		}
		return false;
	}
	
	public boolean parseTypedefFrameClause(Clause cl) {
		String tag = getParseTag();
		if (tag == null)
			return false;
		cl.setTag(tag);
		if (tag.equals("is_anonymous")) {
			return parseBoolean(cl);
		}
		if (tag.equals("name")) {
			return parseUnquotedString(cl);
		}
		if (tag.equals("namespace")) {
			return parseIdRef(cl);
		}
		if (tag.equals("alt_id")) {
			return parseIdRef(cl);
		}
		if (tag.equals("def")) {
			return parseDef(cl);
		}
		if (tag.equals("comment")) {
			return parseUnquotedString(cl);
		}
		if (tag.equals("subset")) {
			return parseIdRef(cl);
		}
		if (tag.equals("synonym")) {
			return parseSynonym(cl);
		}
		if (tag.equals("xref") || tag.equals("xref_analog")) {
			return parseDirectXref(cl);
		}
		if (tag.equals("domain")) {
			return parseIdRef(cl);
		}
		if (tag.equals("range")) {
			return parseIdRef(cl);
		}
		if (tag.equals("is_anti_symmetric")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_cyclic")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_reflexive")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_symmetric")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_transitive")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_functional")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_inverse_functional")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_a")) {
			return parseIdRef(cl);
		}
		if (tag.equals("intersection_of")) {
			return parseTypedefIntersectionOf(cl);
		}
		if (tag.equals("union_of")) {
			return parseIdRef(cl);
		}
		if (tag.equals("equivalent_to")) {
			return parseIdRef(cl);
		}
		if (tag.equals("disjoint_from")) {
			return parseIdRef(cl);
		}
		if (tag.equals("inverse_of")) {
			return parseIdRef(cl);
		}
		if (tag.equals("transitive_over")) {
			return parseIdRef(cl);
		}
		if (tag.equals("holds_over_chain")) {
			return parseIdRefPair(cl);
		}
		if (tag.equals("equivalent_to_chain")) {
			return parseIdRefPair(cl);
		}
		if (tag.equals("disjoint_over")) {
			return parseIdRef(cl);
		}
		if (tag.equals("relationship")) {
			return parseRelationship(cl);
		}
		if (tag.equals("created_by")) {
			return parseIdRef(cl);
		}
		if (tag.equals("creation_date")) {
			return parseISODate(cl);
		}
		if (tag.equals("is_obsolete")) {
			return parseBoolean(cl);
		}
		if (tag.equals("replaced_by")) {
			return parseIdRef(cl);
		}
		if (tag.equals("consider")) {
			return parseIdRef(cl);
		}
		if (tag.equals("is_metadata_tag")) {
			return parseBoolean(cl);
		}
		if (tag.equals("is_class_level")) {
			return parseBoolean(cl);
		}
		if (tag.equals("expand_assertion_to")) {
			return parseOwlDef(cl);
		}
		if (tag.equals("expand_expression_to")) {
			return parseOwlDef(cl);
		}

		

		return false;
	}
	
	// ----------------------------------------
	// [Instance] Frames - TODO 
	// ----------------------------------------

	// ----------------------------------------
	// [Annotation] Frames - TODO 
	// ----------------------------------------


	// ----------------------------------------
	// TVP
	// ----------------------------------------

	
	private String getParseTag() {
		int i = s.indexOf(':');
		if (i == -1) {
			return null;
		}
		String tag = s.rest().substring(0, i);
		s.advance(i+1);
		parseWs();
		parseZeroOrMoreWs();
		return tag;
	}
	
	private boolean parseId(Clause cl) {
		return parseIdRef(cl);
	}
	
	private boolean parseIdRef(Clause cl) {
		String id = getParseUntil(" !{");
		if (id == null)
			return false;
		cl.addValue(id);
		
		return true;
	}
	
	private boolean parseIdRefPair(Clause cl) {
		if (parseIdRef(cl)) {
			if (parseOneOrMoreWs()) {
				return parseIdRef(cl);
			}
		}
		return false;
	}

	
	private boolean parsePerson(Clause cl) {
		return parseUnquotedString(cl);
	}


	private boolean parseISODate(Clause cl) {
		String dateStr = getParseUntil(" !{");
		Date date;
		try {
			date = isoDateFormat.parse(dateStr);
			cl.setValue(date);
			return true;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private boolean parseRelationship(Clause cl) {
		return parseIdRef(cl) && parseOneOrMoreWs() && parseIdRef(cl);
	}

	/**
	 * intersection_of-Tag Class-ID | intersection_of-Tag Relation-ID Class-ID 
	 */
	private boolean parseTermIntersectionOf(Clause cl) {
		if (parseIdRef(cl)) {
			if (s.peekCharIs(' ')) {
				parseOneOrMoreWs();
				if (parseIdRef(cl)) {
					// differentia
				}
				
			}
			return true;
		}
		return false;
	}
	
	private boolean parseTypedefIntersectionOf(Clause cl) {
		// single values only
		return parseIdRef(cl);
	}
	
	// ----------------------------------------
	// Synonyms
	// ----------------------------------------


	private boolean parseSynonym(Clause cl) {
		if (s.consume("\"")) {
			String syn = getParseUntilAdv("\"");
			cl.setValue(syn);
			parseZeroOrMoreWs();
			if (s.peekCharIs('[')) {
				return parseXrefList(cl);
			}
			else if (parseSynonymScope(cl)) {
				parseZeroOrMoreWs();
				if (s.peekCharIs('[')) {
					return parseXrefList(cl);
				}
				else if (parseSynontmType(cl)) {
					parseZeroOrMoreWs();
					return parseXrefList(cl);
				}
			}
		}
		return false;
	}

	private boolean parseSynontmType(Clause cl) {
		return parseIdRef(cl);
	}

	private boolean parseSynonymScope(Clause cl) {
		return parseIdRef(cl);
	}

	// ----------------------------------------
	// Definitions
	// ----------------------------------------

	private boolean parseDef(Clause cl) {
		if (s.consume("\"")) {
			String def = getParseUntilAdv("\"");
			cl.setValue(def);
			parseZeroOrMoreWs();
			return parseXrefList(cl);
		}
		return false;
	}
	
	private boolean parseOwlDef(Clause cl) {
		if (s.consume("\"")) {
			String def = getParseUntilAdv("\"");
			cl.setValue(def);
			parseZeroOrMoreWs();
			return parseXrefList(cl);
		}
		return false;
	}

	// ----------------------------------------
	// XrefLists - e.g. [A:1, B:2, ... ]
	// ----------------------------------------

	private boolean parseXrefList(Clause cl) {
		if (s.consume("[")) {
			parseZeroOrMoreXrefs(cl);
			parseZeroOrMoreWs();
			return s.consume("]");
		}		
		return false;
	}

	private boolean parseZeroOrMoreXrefs(Clause cl) {
		if (parseXref(cl)) {
			while (s.consume(",") && parseXref(cl)) {
			
			}
		}
		return true;	
	}

	// an xref that supports a value of values in a clause
	private boolean parseXref(Clause cl) {
		parseZeroOrMoreWs();
		String id = getParseUntil("\",]!{");
		if (id != null) {
			if (id.contains(" ")) {
				System.out.println("accepting bad xref with spaces:"+id);
			}
			Xref xref = new Xref(id);
			cl.addXref(xref);
			parseZeroOrMoreWs();
			if (s.peekCharIs('"')) {
				s.consume("\"");
				xref.setAnnotation(this.getParseUntilAdv("\""));
			}
			return true;
		}
		return false;
	}
	
	// an xref that is a direct value of a clause
	private boolean parseDirectXref(Clause cl) {
		parseZeroOrMoreWs();
		String id = getParseUntil("\",]!{");
		if (id != null) {
			if (id.contains(" ")) {
				System.out.println("accepting bad xref with spaces:"+id);
			}
			Xref xref = new Xref(id);
			//cl.addXref(xref);
			cl.addValue(xref);
			parseZeroOrMoreWs();
			if (s.peekCharIs('"')) {
				s.consume("\"");
				xref.setAnnotation(this.getParseUntilAdv("\""));
			}
			return true;
		}
		return false;
	}

	// ----------------------------------------
	// Qualifier Value blocks - e.g. {a="1",b="foo", ...}
	// ----------------------------------------

	private boolean parseQualifierBlock(Clause cl) {
		if (s.consume("{")) {
			parseZeroOrMoreQuals(cl);
			parseZeroOrMoreWs();
			return s.consume("}");
			
		}
		return false;
	}

	private boolean parseZeroOrMoreQuals(Clause cl) {
		if (parseQual(cl)) {
			while (s.consume(",") && parseQual(cl)) {
			
			}
		}
		return true;	
	}

	private boolean parseQual(Clause cl) {
		parseZeroOrMoreWs();
		String q = getParseUntilAdv("=");
		if (q != null) {
			parseZeroOrMoreWs();
			String v;
			if (s.consume("\"")) {
				 v = getParseUntilAdv("\"");
			}
			else {
				// TODO - warn
				v = getParseUntil(" ,}");
			}
			QualifierValue qv = new QualifierValue(q,v);
			cl.addQualifierValue(qv);
			parseZeroOrMoreWs();
			return true;
		}
		return false;
	}

	// ----------------------------------------
	// Other
	// ----------------------------------------


	private boolean parseBoolean(Clause cl) {
		if (s.consume("true")) {
			cl.setValue(true);
			return true;
		}
		if (s.consume("false")) {
			cl.setValue(false);
			return true;
		}
		// throw
		return false;
	}

	private boolean parseNamespace(Clause cl) {
		return parseIdRef(cl);	
	}

	protected boolean parseIdLine(Frame f) {
		String tag = getParseTag();
		if (!tag.equals(ID)) {
			return false;
		}
		Clause cl = new Clause();
		cl.setTag("id");
		f.addClause(cl);
		String id = getParseUntil(" !{");
		if (id == null)
			return false;
		cl.addValue(id);
		f.setId(id);
		return parseEOL(cl);
	}
	
	// ----------------------------------------
	// End-of-line matter
	// ----------------------------------------

	
	private boolean parseEOL(Clause cl) {
		while (parseWs()) {
			
		}
		parseQualifierBlock(cl);
		parseHiddenComment();
		return forceParseNlOrEof();
	}

	private boolean parseHiddenComment() {
		while (parseWs()) {
			
		}
		if (s.eol() || s.eof())
			return false;
		if (s.peekChar() == '!') {
			s.forceEol();
			return true;
		}
		return false;
		
	}


	
	//
	protected boolean parseUnquotedString(Clause cl) {
		parseZeroOrMoreWs();
		String v = getParseUntil("!{");

		cl.setValue(v);
		//s.advanceLine();
		return true;
	}
	
	// Newlines, whitespace
	
	protected boolean forceParseNlOrEof() {
		while (parseWs()) {
			
		}
		if ( parseNlChar() ) 
			return true;
		if (s.eof())
			return true;
		throw new Error("expected newline");
	}
	
	protected boolean parseZeroOrMoreWsOptCmtNl() {
		while (parseWsOptCmtNl()) {
			
		}
		return true;
	}
	
	protected boolean parseWsOptCmtNl() {
		parseZeroOrMoreWs();
		parseHiddenComment();
		if ( parseNlChar() ) 
			return true;
		return false;
	}


	// non-newline
	protected boolean parseWs() {
		if (s.eol()) {
			return false;
		}
		while (!s.eof() && s.peekChar() == ' ') {
			s.advance(1);
			return true;
		}
		return false;
	}
	
	protected boolean parseOneOrMoreWs() {
		if (s.eol() || s.eof()) {
			return false;
		}
		int n = 0;
		while (s.peekChar() == ' ') {
			s.advance(1);
			n++;
		}
		return n>0;
	}

	protected boolean parseZeroOrMoreWs() {
		if (s.eol() || s.eof()) {
			return true;
		}
		
		while (s.peekChar() == ' ') {
			s.advance(1);
		}
		return true;
	}


	protected boolean parseNlChar() {
		if (s.eol()) {
			s.advanceLine();
			return true;
		}
		return false;
	}
	
	private String getParseUntilAdv(String compl) {
		String ret = getParseUntil(compl);
		s.advance(1);
		return ret;
	}

	private String getParseUntil(String compl) {
		String r = s.rest();
		int i = 0;
		boolean hasEscapedChars = false;
		while (i < r.length()) {
			if (r.charAt(i) == '\\') {
				hasEscapedChars = true;
				i+=2; // Escape
				continue;
			}
			if (compl.contains(r.subSequence(i, i+1))) {
				break;
			}
			i++;
		}
		if (i==0)
			return null;
		String ret = r.substring(0, i);
		// TODO - replace escaped characters
		s.advance(i);
		return ret;
	}

}
