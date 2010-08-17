package org.obolibrary.oboformat.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.obolibrary.obo2owl.Obo2Owl;
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


	public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {

		Collection<String> paths = new Vector<String>();
		String outFile = null;
		OWLOntologyFormat format = new RDFXMLOntologyFormat();

		
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
			else if (opt.equals("-t") || opt.equals("--tp")) {
				String to = args[i];
				i++;
				if (to.equals("owlxml")) {
					format = new OWLXMLOntologyFormat();
				}
				else {
					
				}
			}
			else {
				paths.add(opt);
			}
		}


		for (String iri : paths) {
			//showMemory();
			OBOFormatParser p = new OBOFormatParser();
			OBODoc obodoc = p.parse(iri);

			Obo2Owl bridge = new Obo2Owl();
			OWLOntologyManager manager = bridge.getManager();
			OWLOntology ontology = bridge.convert(obodoc);
			IRI outputStream = IRI.create(outFile);
			System.out.println("saving to "+outputStream);
			manager.saveOntology(ontology, format, outputStream);
			OWLXMLOntologyFormat owlFormat = new OWLXMLOntologyFormat();



		}

	}

	private static void usage() {
		// TODO Auto-generated method stub
		
	}
}
