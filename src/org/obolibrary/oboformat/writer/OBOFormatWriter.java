package org.obolibrary.oboformat.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;

/**
 * 
 * @author Shahid Manzoor
 *
 */
public class OBOFormatWriter {
	
	public OBOFormatWriter(){
		
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
	
	public void writeHeader(Frame frame, BufferedWriter writer) throws IOException{
	
		Clause c = frame.getClause("format-version");
		if(c != null)
			write(c, writer);
		
		
		for(String tag: frame.getTags()){

			if(tag.equals("format-version"))
				continue;
			
			for(Clause claue: frame.getClauses(tag)){
				write(claue, writer);
			}
		}
		
		writeLine("", writer);
		
		
	}
	
	public void write(Frame frame, BufferedWriter writer) throws IOException{
		
		if(frame.getType() == FrameType.TERM){
			writeLine("[Term]", writer);
		}else if (frame.getType() == FrameType.TYPEDEF){
			writeLine("[Typedef]", writer);
		}
		
		if(frame.getId() != null){
			writeLine("id: " + frame.getId(), writer);
		}
		
		for(Clause clause: frame.getClauses()){
			if(clause.getTag().equals("id"))
				continue;
			else if(clause.getTag().equals("def"))
				writeDef(clause, writer);
			else if(clause.getTag().equals("synonym"))
				writeSynonym(clause, writer);
			else
				write(clause, writer);
		}
		
		writeLine("", writer);
		
	}
	
	
	private void writeClauseWithQoutedString(Clause clause, BufferedWriter writer) throws IOException{
		String line = clause.getTag() + ": ";
		
		boolean  first = true;
		for(Object value: clause.getValues()){
			if(first)
				line += "\"";

			line += value;
			
			if(first)
				line += "\"";
			
			line += " ";
			
			first = false;
		}
		
		Collection<Xref> xrefs = clause.getXrefs();
		
		if(xrefs != null){
		
			if(!xrefs.isEmpty())
				line +="[";
			
			for(Xref xref: xrefs){
				line += xref.getIdref();
			}
	
			if(!xrefs.isEmpty())
				line +="]";
		}
		
		writeLine(line, writer);
		
	}
	
	public void writeDef(Clause clause, BufferedWriter writer) throws IOException{
		
		writeClauseWithQoutedString(clause, writer);
	}
	
	public void writeSynonym(Clause clause, BufferedWriter writer) throws IOException{
		writeClauseWithQoutedString(clause, writer);
	}
	
	public void write(Clause clause, BufferedWriter writer) throws IOException{
		String line = clause.getTag() + ": ";
		
		for(Object value: clause.getValues()){
			line += value + " ";
		}
		
		Collection<Xref> xrefs = clause.getXrefs();
		
		if(xrefs != null){
		
			if(!xrefs.isEmpty())
				line +="[";
			
			for(Xref xref: xrefs){
				line += xref.getIdref();
			}
	
			if(!xrefs.isEmpty())
				line +="]";
		}
		
		writeLine(line, writer);
		
	}
}
