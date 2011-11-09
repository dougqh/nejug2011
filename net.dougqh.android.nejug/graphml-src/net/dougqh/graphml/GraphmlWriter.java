package net.dougqh.graphml;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.dougqh.graphml.yed.YedGraphmlExtension;



public final class GraphmlWriter 
    implements Closeable, Flushable
{
    public static final String EXTENSION = ".graphml";
    
    private static final String VERSION = "1.0";
    private static final String ENCODING = "UTF-8";
    
    private static final XMLOutputFactory FACTORY = XMLOutputFactory.newInstance();
    
    private final OutputStream out;
    private final boolean close;
    private final XMLStreamWriter xmlWriter;
    
    private final Map< Object, String > graphIds = new HashMap< Object, String >( 8 );
    private final Map< Object, String > nodeIds = new HashMap< Object, String >( 32 );
    
    private final List< Edge > edges = new ArrayList< Edge >( 32 );
    
    private int curGraphId = 0;
    private int curNodeId = 0;
    
    private GraphmlExtension< ? > yed = null;

    public GraphmlWriter( final File file ) 
        throws GraphmlIoException, FileNotFoundException
    {
        this( new FileOutputStream( file ), true );
    }
    
    public GraphmlWriter( final OutputStream out )
        throws GraphmlIoException
    {
        this( out, false );
    }
    
    public GraphmlWriter( final OutputStream out, final boolean close )
        throws GraphmlIoException
    {
        this.out = out;
        this.close = close;
        try {
            this.xmlWriter = FACTORY.createXMLStreamWriter( out, "utf-8" );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
    }
    
    public final GraphmlWriter forYed() {
        this.yed = new YedGraphmlExtension( this );
        return this;
    }
    
    public final YedGraphmlExtension yed() {
        if ( this.yed != null ) {
            return (YedGraphmlExtension)this.yed;
        } else {
            throw new IllegalStateException( "yEd support was not enabled" );
        }
    }
    
    public final void startGraphml()
        throws GraphmlIoException
    {
        this.startDocument( ENCODING, VERSION );
        
        this.start( "graphml" ).
            attrib( "xmlns", "http://graphml.graphdrawing.org/xmlns" ).
            attrib( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" ).
            attrib( "xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd" );
        
        if ( this.yed != null ) {
            this.yed.addNamespaces();
        }
        
        if ( this.yed != null ) {
            this.yed.addMetaInfo();
        }
    }
    
    public final void startKey() throws GraphmlIoException {
        this.start( "key" );
    }
    
    public final void endKey() throws GraphmlIoException {
        this.end();
    }
    
    public final String startGraph( final Object object )
        throws GraphmlIoException
    {
        String id = this.startGraph();
        this.graphIds.put( object, id );
        return id;
    }    
    
    public final String startGraph()
        throws GraphmlIoException
    {
        String graphId = this.nextGraphId();
        this.start( "graph" ).
            attrib( "id", graphId ).
            attrib( "edgedefault", "undirected" );
        return graphId;
    }
    
    public final String startDirectedGraph( final Object object )
        throws GraphmlIoException
    {
        String id = this.startDirectedGraph();
        this.graphIds.put( object, id );
        return id;
    }    
    
    public final String startDirectedGraph()
        throws GraphmlIoException
    {
        String graphId = this.nextGraphId();
        this.start( "graph" ).
            attrib( "id", graphId ).
            attrib( "edgedefault", "directed" );
        return graphId;
    }

    public final String startNode( final String nodeId ) throws GraphmlIoException {
    	return this.startNodeImpl( nodeId, nodeId ); 	
    }
    
    public final String startNode( final Object node ) throws GraphmlIoException {
    	return this.startNodeImpl( node, this.nextNodeId() );
    }
    
    public final String startNode() throws GraphmlIoException {
        return this.startNodeImpl( null, this.nextNodeId() );
    }
    
    private final String startNodeImpl( final Object node, final String nodeId )
    	throws GraphmlIoException
    {
    	if ( node != null ) {
    		this.nodeIds.put( node, nodeId );
    	}
        this.start( "node" ).attrib( "id", nodeId );
        return nodeId;  
    }
    
    public final void startData( final String key ) throws GraphmlIoException {
        this.start( "data" ).attrib( "key", key );
    }
    
    public final void endData() throws GraphmlIoException {
        this.end();
    }
    
    public final void endNode() throws GraphmlIoException {
        this.end();
    }
    
    public final void edge(
        final Object sourceNode,
        final Object targetNode )
        throws GraphmlIoException
    {
        this.edges.add( new UnresolvedEdge( sourceNode, targetNode, null ) );
    }
    
    public final void undirectedEdge(
        final Object sourceNode,
        final Object targetNode )
        throws GraphmlIoException
    {
        this.edges.add( new UnresolvedEdge( sourceNode, targetNode, false ) );
    }
    
    public final void directedEdge(
        final Object sourceNode,
        final Object targetNode )
        throws GraphmlIoException
    {
        this.edges.add( new UnresolvedEdge( sourceNode, targetNode, true ) );
    }
    
    public final void edge( final String sourceId, final String targetId )
        throws GraphmlIoException
    {
        this.edges.add( new ResolvedEdge( sourceId, targetId, null ) );
    }

    public final void undirectedEdge( final String sourceId, final String targetId )
        throws GraphmlIoException
    {
        this.edges.add( new ResolvedEdge( sourceId, targetId, false ) );
    }
    
    public final void directedEdge( final String sourceId, final String targetId )
        throws GraphmlIoException
    {
        this.edges.add( new ResolvedEdge( sourceId, targetId, true ) );
    }
    
    private final void edge( final Edge edge ) throws GraphmlIoException {
        String sourceId = edge.sourceId();
        String targetId = edge.targetId();
        if ( sourceId == null ) {
            System.err.println( "Unresolved edge sourceId" );
        } else if ( targetId == null ) {
            System.err.println( "Unresolved edge targetId" );
        } else {
        	this.start( "edge" ).
                attrib( "source", edge.sourceId() ).
                attrib( "target", edge.targetId() ).
                attrib( "directed", edge.directed() );
            
            if ( this.yed != null ) {
                this.yed.edge( edge );
            }
            
            this.end();
        }
    }
    
    public final void endDirectedGraph() throws GraphmlIoException {
        this.endGraph();
    }
    
    public final void endGraph() throws GraphmlIoException {
        this.end();
    }    
    
    public final void endGraphml() throws GraphmlIoException {
        for ( Edge edge : this.edges ) {
            this.edge( edge );
        }
        this.edges.clear();
        
        this.end();
    }
    
    public final String getId( final Object object ) {
    	return this.nodeIds.get( object );
    }
    
    private final GraphmlWriter startDocument(
        final String encoding,
        final String version )
        throws GraphmlIoException    
    {
        try {
            this.xmlWriter.writeStartDocument( encoding, version );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter start( final String element )
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeStartElement( element );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter start(
        final String prefix,
        final String element,
        final String namespaceUri )
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeStartElement( prefix, element, namespaceUri );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter characters( final String characters )
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeCharacters( characters );
            return this;
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
    }
    
    final GraphmlWriter attrib( final String name, final Object value )
        throws GraphmlIoException
    {
        try {
            if ( value instanceof Boolean ) {
                if ( (Boolean)value ) {
                    this.xmlWriter.writeAttribute( name, "true" );
                } else {
                    this.xmlWriter.writeAttribute( name, "false" );
                }
            } else if ( value != null ) {
                this.xmlWriter.writeAttribute( name, value.toString() );
            }
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter end()
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeEndElement();
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    private final String nextGraphId() {
        return "G" + ( this.curGraphId++ );
    }
    
    private final String nextNodeId() {
        return "N" + ( this.curNodeId++ );
    }
    
    @Override
    public final void flush() throws IOException {
        try {
            this.xmlWriter.flush();
        } catch ( XMLStreamException e ) {
            throw new IOException( e );
        }
    }
    
    @Override
    public void close() throws IOException {
        if ( this.close ) {
            try {
                this.xmlWriter.close();
            } catch ( XMLStreamException e ) {
                throw new IOException( e );
            } finally {
                this.out.close();
            }
        }
    }
    
    //TODO: Figure out how to handle visibility better
    public static abstract class Edge {
        public abstract String sourceId();
        
        public abstract String targetId();
        
        public abstract Boolean directed();
    }
    
    static final class ResolvedEdge extends Edge {
        private final String sourceId;
        private final String targetId;
        private final Boolean directed;
        
        ResolvedEdge(
            final String sourceId,
            final String targetId,
            final Boolean directed )
        {
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.directed = directed;
        }
        
        @Override
        public final String sourceId() {
            return this.sourceId;
        }
        
        @Override
        public final String targetId() {
            return this.targetId;
        }
        
        @Override
        public final Boolean directed() {
            return this.directed;
        }
    }
    
    final class UnresolvedEdge extends Edge {
        private final Object source;
        private final Object target;
        private final Boolean directed;
        
        UnresolvedEdge(
            final Object source,
            final Object target,
            final Boolean directed )
        {
            this.source = source;
            this.target = target;
            this.directed = directed;
        }
        
        @Override
        public final String sourceId() {
            return GraphmlWriter.this.nodeIds.get( this.source );
        }
        
        @Override
        public final String targetId() {
            return GraphmlWriter.this.nodeIds.get( this.target );
        }
        
        @Override
        public final Boolean directed() {
            return this.directed;
        }
    }
}
