package net.dougqh.graphml;

public enum GraphmlElement {
	GRAPHML( "graphml" ),
	NODE( "node" ),
	EDGE( "edge" );
	
	private final String id;
	
	private GraphmlElement( final String id ) {
		this.id = id;
	}
	
	final String getId() {
		return this.id;
	}
}
