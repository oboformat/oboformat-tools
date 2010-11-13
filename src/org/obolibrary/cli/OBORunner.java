package org.obolibrary.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * Adapted from Matthew Horridge's OWLAPI examples by Chris Mungall.
 * 
 * See: http://wiki.geneontology.org/index.php/OBO-Edit:Reasoner_Benchmarks
 */
public class OBORunner {

	protected final static Logger logger = Logger.getLogger(OBORunner.class);
	static Set<String> ontsToDownload = new HashSet<String>();
	static Set<String> omitOntsToDownload = new HashSet<String>();

	public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {

		Collection<String> paths = new Vector<String>();
		String outFile = null;
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		boolean buildObo = false;
		String buildDir = null;

		int i=0;

		while (i < args.length) {
			String opt = args[i];
			System.out.println("processing arg: "+opt);
			i++;
			if (opt.equals("-h") || opt.equals("--help")) {
				usage();
				System.exit(0);
			}
			else if (opt.equals("-o") || opt.equals("--out")) {
				outFile = args[i];
				i++;
			}
			else if (opt.equals("--download")) {
				ontsToDownload.add(args[i]);
				i++;
			}
			else if (opt.equals("--omit-download")) {
				omitOntsToDownload.add(args[i]);
				i++;
			}
			else if (opt.equals("-b") || opt.equals("--build")) {
				buildObo = true;
				buildDir = args[i];
				i++;

			}
			else if (opt.equals("-t") || opt.equals("--to")) {
				String to = args[i];
				i++;
				if (to.equals("owlxml")) {
					format = new OWLXMLOntologyFormat();
				}
				else if (to.contains("manchester")) {
					format = new ManchesterOWLSyntaxOntologyFormat();
				}
				else {
					System.err.println("don't know format '"+to+"' -- reverting to default: "+format);
				}
			}
			else {
				paths.add(opt);
			}
		}

		if (ontsToDownload.size() > 0 && !buildObo) {
			System.err.println("must specify dir with -b DIR");
			System.exit(1);
		}

		if (buildObo) {
			buildAllOboOwlFiles(buildDir);
		}

		for (String iri : paths) {
			//showMemory();
			OBOFormatParser p = new OBOFormatParser();
			OBODoc obodoc = p.parse(iri);

			Obo2Owl bridge = new Obo2Owl();
			OWLOntologyManager manager = bridge.getManager();
			OWLOntology ontology = bridge.convert(obodoc);
			IRI outputStream = IRI.create(outFile);
			//format = new OWLXMLOntologyFormat();
			System.out.println("saving to "+outputStream+" via "+format);
			manager.saveOntology(ontology, format, outputStream);
			//OWLXMLOntologyFormat owlFormat = new OWLXMLOntologyFormat();
		}

	}

	private static void usage() {
		System.out.println("obolib-obo2owl [--to SYNTAX] -o FILEPATH-URI OBO-FILE");
		System.out.println("obolib-obo2owl -b BUILDPATH-URI");
		System.out.println("\n");
		System.out.println("Converts obo files to OWL. If -b option is used, entire\n");
		System.out.println("obo repository is converted\n");
		System.out.println("\n");
		System.out.println("Example:\n");
		System.out.println(" obolib-obo2owl -o file://`pwd`/my.owl my.obo\n");
		System.out.println("Example:\n");
		System.out.println(" obolib-obo2owl -b file://`pwd`\n");
		System.out.println("Example:\n");
		System.out.println(" obolib-obo2owl -b file://`pwd` --download FBBT\n");

	}

	public static void showMemory() {
		System.gc();
		System.gc();
		System.gc();
		long tm = Runtime.getRuntime().totalMemory();
		long fm = Runtime.getRuntime().freeMemory();
		long mem = tm-fm;
		System.out.println("Memory total:"+tm+" free:"+fm+" diff:"+mem+" (bytes) diff:"+(mem/1000000)+" (mb)");
	}


	/**
	 * makes OWL from all selected ontologies
	 * 
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private static void buildAllOboOwlFiles(String dir) throws IOException {
		Map<String, String> ontmap = getOntDownloadMap();
		Vector<String> fails = new Vector<String>();
		for (String ont : ontmap.keySet()) {
			if (ontsToDownload.size() > 0 && !ontsToDownload.contains(ont))
				continue;
			if (omitOntsToDownload.size() > 0 && omitOntsToDownload.contains(ont))
				continue;
			if (ontmap.containsKey(ont)) {
				try {
					String url = ontmap.get(ont);
					System.out.println("converting: "+ont+" from: "+url);
					long initTime = System.nanoTime();
					String ontId = ont.toLowerCase();
					Obo2Owl.convertURL(url,dir+"/"+ontId+".owl",ontId);
					long totalTime = System.nanoTime() - initTime;
					showMemory(); // useless

					System.out.println("TIME_TO_CONVERT "+ont+" "+
							+ (totalTime / 1000000d) + " ms");

				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fails.add(ont);
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fails.add(ont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fails.add(ont);
				} catch (Error e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fails.add(ont);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fails.add(ont);
				}
			}
			else {
				fails.add(ont);
				System.out.println("did not convert: "+ont);
			}
		}
		System.out.println("DONE!");
		for (String fail : fails)
			System.out.println("FAIL:"+fail);
	}

	/**
	 * 
	 * find download URLs using obo foundry metadata file
	 * 
	 * @return mapping from ID-space (e.g. GO) to source download URL
	 * @throws IOException
	 */
	private static Map<String,String> getOntDownloadMap() throws IOException {
		return getOntDownloadMap(new URL("http://obo.cvs.sourceforge.net/viewvc/*checkout*/obo/obo/website/cgi-bin/ontologies.txt"));
	}

	private static Map<String,String> getOntDownloadMap(String fn) throws IOException {
		return getOntDownloadMap(new BufferedReader(new FileReader(fn)));
	}
	private static Map<String,String> getOntDownloadMap(URL url) throws IOException {
		return getOntDownloadMap(new BufferedReader(new InputStreamReader(url.openStream())));
	}

	private static Map<String,String> getOntDownloadMap(BufferedReader in) throws IOException {
		Map<String,String> urlmap = new HashMap<String,String>();
		String line;
		String ns = null;
		while ( true ) {
			line = in.readLine();
			if (line == null)
				break;
			if (line.length() == 0) {
				ns = null;
				continue;
			}
			String[] parts = line.split("\t");
			if (parts.length < 2) {
				continue;
			}
			String tag = parts[0];
			if (tag.equals("namespace")) {
				ns = parts[1];
			}
			else if (tag.equals("download")) {
				if (parts[1] != "") {
					urlmap.put(ns, parts[1]);
				}
			}
			else if (tag.equals("source")) {
				if (parts[1] != "" && !urlmap.containsKey(ns)) {
					urlmap.put(ns, parts[1]);
				}
			}
			else if (tag.equals("is_obsolete")) {
				if (urlmap.containsKey(ns))
					urlmap.remove(ns);
				ns = null;
			}
			else if (tag.equals("format")) {
				// danger or circularity, just for testing now
				if (!parts[1].equals("obo"))
					urlmap.put(ns, "http://purl.org/obo/obo/"+ns+".obo");
			}

		}
		return urlmap;

	}
}
