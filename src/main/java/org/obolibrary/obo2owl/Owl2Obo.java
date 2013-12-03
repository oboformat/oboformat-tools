package org.obolibrary.obo2owl;

import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Owl2Obo extends OWLAPIOwl2Obo {

    private static Logger LOG = Logger.getLogger(Owl2Obo.class.getName());

    public Owl2Obo() {
        super(OWLManager.createOWLOntologyManager());
    }

    public Owl2Obo(OWLOntologyManager m) {
        super(m);
    }
}
