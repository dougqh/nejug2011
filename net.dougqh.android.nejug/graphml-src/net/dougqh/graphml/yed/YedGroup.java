package net.dougqh.graphml.yed;

import java.util.ArrayList;
import java.util.Arrays;

import net.dougqh.graphml.GraphmlIoException;
import net.dougqh.graphml.GraphmlWriter;



public final class YedGroup extends YedNode< YedGroup > {
    private final ArrayList< YedNode< ? > > nodes = new ArrayList< YedNode< ? > >();
    
    public YedGroup() {}
    
    public YedGroup( final String label ) {
    	this.setLabel( label );
    }
    
    public final YedGroup add( final YedNode< ? > node ) {
        this.nodes.add( node );
        return this;
    }
    
    public final YedGroup add( final YedNode< ? >... nodes ) {
    	this.nodes.addAll( Arrays.asList( nodes ) );
    	return this;
    }
        
    @Override
    protected final void write( final GraphmlWriter graphmlWriter )
        throws GraphmlIoException
    {
    	if ( graphmlWriter.getId( this ) != null ) {
    		return;
    	}
    	
        String id = graphmlWriter.startNode( this );
        if ( this.description != null ) {
            graphmlWriter.yed().description( this.description );
        }
        if ( this.url != null ) {
            graphmlWriter.yed().url( this.url );
        }
        graphmlWriter.yed().startShapeNode();
        if ( this.label != null ) {
            graphmlWriter.yed().nodeLabel( this.label );
        }
        graphmlWriter.yed().endShapeNode();
        
        graphmlWriter.startGraph();
        for ( YedNode< ? > node : this.nodes ) {
            node.write( graphmlWriter );
        }
        graphmlWriter.endGraph();
        
        graphmlWriter.endNode();
        
        this.setId( id );
    }
    
    @Override
    public final YedGroup clone() {
        YedGroup group =  super.clone();
        group.nodes.clear();
        return group;
    }

    @Override
    public final int hashCode() {
    	if ( this.associatedObject == null ) {
    		return super.hashCode();
    	} else {
    		return this.associatedObject.hashCode();
    	}
    }
    
    @Override
    public final boolean equals( final Object obj ) {
    	if ( obj == this ) {
    		return true;
    	} else if ( ! ( obj instanceof YedGroup ) ) {
    		return false;
    	} else {
    		YedGroup that = (YedGroup)obj;
    		return equals(
    			this.associatedObject,
    			that.associatedObject );
    	}
    }
}
