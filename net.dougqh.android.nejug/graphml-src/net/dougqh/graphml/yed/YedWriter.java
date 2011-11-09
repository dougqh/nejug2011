package net.dougqh.graphml.yed;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.dougqh.graphml.GraphmlIoException;
import net.dougqh.graphml.GraphmlWriter;



public final class YedWriter implements Closeable {
    private final GraphmlWriter graphmlWriter;
    
    public YedWriter( final File file )
        throws FileNotFoundException, GraphmlIoException
    {
        this.graphmlWriter = new GraphmlWriter( file ).forYed();
        
        this.graphmlWriter.startGraphml();
        this.graphmlWriter.startDirectedGraph();
    }
    
    public final YedWriter add( final YedNode< ? >... nodes )
        throws GraphmlIoException
    {
    	for ( YedNode< ? > node : nodes ) {
    		node.write( this.graphmlWriter );
    	}
        return this;
    }
    
    public final YedWriter connect(
        final YedNode< ? > fromNode,
        final YedNode< ? > toNode )
        throws GraphmlIoException
    {
        this.graphmlWriter.undirectedEdge( fromNode, toNode );
        return this;
    }
    
    public final YedWriter connectWithArrow(
        final YedNode< ? > fromNode,
        final YedNode< ? > toNode )
        throws GraphmlIoException
    {
        this.graphmlWriter.directedEdge( fromNode, toNode );
        return this;
    }
    
    public final YedWriter connectWithArrows(
    	final YedNode< ? > startNode,
    	final YedNode< ? >... nodes )
    	throws GraphmlIoException
    {
    	YedNode< ? > prevNode = startNode;
    	
    	for ( YedNode< ? > curNode : nodes ) {
    		this.connectWithArrow( prevNode, curNode );
    		
    		prevNode = curNode;
    	}
    	return this;
    }
    
    @Override
    public final void close() throws IOException {
        try {
            this.graphmlWriter.endDirectedGraph();
            this.graphmlWriter.endGraphml();
        } finally {        
            this.graphmlWriter.close();
        }
    }
}
