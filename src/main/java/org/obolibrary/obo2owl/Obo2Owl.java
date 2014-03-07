package org.obolibrary.obo2owl;

import java.io.IOException;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.parser.OBOFormatParserException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class Obo2Owl extends OWLAPIObo2Owl {

    /**
     * Use conversion methods or Obo2OWLConstants.DEFAULT_IRI_PREFIX
     */
    @Deprecated
    public static final String DEFAULT_IRI_PREFIX = Obo2OWLConstants.DEFAULT_IRI_PREFIX;

    // public static final String IRI_CLASS_SYNONYMTYPEDEF =
    // Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_synonymtypedef";
    // public static final String IRI_CLASS_SUBSETDEF =
    // Obo2OWLConstants.DEFAULT_IRI_PREFIX + "IAO_subsetdef";
    public Obo2Owl() {
        // as default create an empty manager
        this(OWLManager.createOWLOntologyManager());
    }

    public Obo2Owl(OWLOntologyManager manager) {
        super(manager);
    }

    /**
     * Static convenience method which: (1) creates an Obo2Owl bridge object (2)
     * parses an obo file from a URL (3) converts that to an OWL ontology (4)
     * saves the OWL ontology as RDF/XML
     * 
     * @param iri
     * @param outFile
     * @throws IOException
     * @throws OWLOntologyCreationException
     * @throws OWLOntologyStorageException
     * @throws OBOFormatParserException
     */
    public static void convertURL(String iri, String outFile)
            throws IOException, OWLOntologyCreationException,
            OWLOntologyStorageException, OBOFormatParserException {
        convertURL(iri, outFile, OWLManager.createOWLOntologyManager());
    }

    /**
     * @param obodoc
     * @param useFreshManager
     * @return ontology
     * @throws OWLOntologyCreationException
     */
    public OWLOntology convert(OBODoc obodoc, boolean useFreshManager)
            throws OWLOntologyCreationException {
        this.obodoc = obodoc;
        if (useFreshManager) {
            init(OWLManager.createOWLOntologyManager());
        } else {
            init(manager);
        }
        return tr();
    }

    private OWLOntology tr() throws OWLOntologyCreationException {
        Frame hf = obodoc.getHeaderFrame();
        Clause ontClause = hf.getClause(OboFormatTag.TAG_ONTOLOGY);
        if (ontClause != null) {
            String ontOboId = (String) ontClause.getValue();
            defaultIDSpace = ontOboId;
            IRI ontIRI;
            if (ontOboId.contains(":")) {
                ontIRI = IRI.create(ontOboId);
            } else {
                ontIRI = IRI.create(Obo2OWLConstants.DEFAULT_IRI_PREFIX
                        + ontOboId + ".owl");
            }
            Clause dvclause = hf.getClause(OboFormatTag.TAG_DATA_VERSION);
            if (dvclause != null) {
                String dv = dvclause.getValue().toString();
                IRI vIRI = IRI.create(Obo2OWLConstants.DEFAULT_IRI_PREFIX
                        + ontOboId + "/" + dv + "/" + ontOboId + ".owl");
                OWLOntologyID oid = new OWLOntologyID(ontIRI, vIRI);
                owlOntology = manager.createOntology(oid);
            } else {
                owlOntology = manager.createOntology(ontIRI);
            }
        } else {
            defaultIDSpace = "TEMP";
            IRI ontIRI = IRI.create(Obo2OWLConstants.DEFAULT_IRI_PREFIX
                    + defaultIDSpace);
            // TODO - warn
            owlOntology = manager.createOntology(ontIRI);
        }
        trHeaderFrame(hf);
        for (Frame f : obodoc.getTypedefFrames()) {
            trTypedefToAnnotationProperty(f);
        }
        for (Frame f : obodoc.getTypedefFrames()) {
            trTypedefFrame(f);
        }
        for (Frame f : obodoc.getTermFrames()) {
            trTermFrame(f);
        }
        // TODO - individuals
        for (Clause cl : hf.getClauses(OboFormatTag.TAG_IMPORT)) {
            String path = getURI(cl.getValue().toString());
            IRI importIRI = IRI.create(path);
            manager.loadOntology(importIRI);
            AddImport ai = new AddImport(owlOntology,
                    fac.getOWLImportsDeclaration(importIRI));
            manager.applyChange(ai);
        }
        postProcess(owlOntology);
        return owlOntology;
    }
}
