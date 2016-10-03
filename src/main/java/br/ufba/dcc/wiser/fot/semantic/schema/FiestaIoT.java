package br.ufba.dcc.wiser.fot.semantic.schema;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class FiestaIoT {
	
	private static OntModel m_model = ModelFactory.createOntologyModel();
	
	public static final Resource classTimeInterval = m_model.createResource( "http://www.loa-cnr.it/ontologies/DUL.owl#TimeInterval" );
	
	public static final DatatypeProperty hasIntervalDate = m_model.createDatatypeProperty("http://www.loa-cnr.it/ontologies/DUL.owl#hasIntervalDate");
	
	public static final DatatypeProperty hasDataValue = m_model.createDatatypeProperty("http://www.loa-cnr.it/ontologies/DUL.owl#hasDataValue");

}
