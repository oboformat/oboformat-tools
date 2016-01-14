package org.obolibrary.owl;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.LabelFunctionalDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Example for how-to use the LabelFunctionalSyntax to serialize an ontology.
 */
public class WriteLabels {

    public static void main(String[] args) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final OWLDocumentFormat format = new LabelFunctionalDocumentFormat();
        manager.getOntologyStorers().add(new LabelFunctionalSyntaxStorerFactory());
        OWLOntology ontology = manager.loadOntology(IRI
                .create("http://purl.obolibrary.org/obo/tao.owl"));

        manager.saveOntology(ontology, format,
                IRI.create(new File("out/tao-labels.ofn")));
    }
}
