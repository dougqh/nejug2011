package net.dougqh.graphml;

public enum EdgeStyle {
	DIRECTED( "directed", true ),
	UNDIRECTED( "undirected", false );
	
	private final String id;
	private final boolean directed;
	
	private EdgeStyle(
		final String id,
		final boolean directed )
	{
		this.id = id;
		this.directed = directed;
	}
	
	final String getId() {
		return this.id;
	}
	
	final boolean isDirected() {
		return this.directed;
	}
}
