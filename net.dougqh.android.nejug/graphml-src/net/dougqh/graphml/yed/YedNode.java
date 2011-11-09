package net.dougqh.graphml.yed;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import net.dougqh.graphml.GraphmlIoException;
import net.dougqh.graphml.GraphmlWriter;



public abstract class YedNode< ThisType extends YedNode< ThisType > >
    implements Cloneable
{
    protected String label = null;
    protected String description = null;
    protected URL url = null;
    
    protected Object associatedObject = null;
    
    private String id = null;
    
    @SuppressWarnings( "unchecked" )
    public final ThisType associate( final Object object ) {
    	this.associatedObject = object;
    	return (ThisType)this;
    }
    
    @SuppressWarnings( "unchecked" )
    public final ThisType setLabel( final String label ) {
        this.label = label;
        return (ThisType)this;
    }
    
    @SuppressWarnings( "unchecked" )
    public final ThisType setDescription( final String description ) {
        this.description = description;
        return (ThisType)this;
    }
    
    @SuppressWarnings( "unchecked" )
    public final ThisType setUrl( final URL url ) {
        this.url = url;
        return (ThisType)this;
    }
    
    public final ThisType setFile( final File file ) {
        try {
            return this.setUrl( file.toURI().toURL() );
        } catch ( MalformedURLException e ) {
            throw new IllegalStateException( e );
        }       
    }
    
    final void setId( final String id ) {
        this.id = id;
    }
    
    final String getId() {
        return this.id;
    }
    
    protected abstract void write( final GraphmlWriter graphmlWriter )
        throws GraphmlIoException;    
    
    @Override
    @SuppressWarnings( "unchecked" )
    protected ThisType clone() {
        try {
            return (ThisType)super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw new IllegalStateException( e );
        }
    }
    
    public final ThisType clone( final String label ) {
        ThisType clone = this.clone();
        clone.setLabel( label );
        return clone;
    }
    
    public final ThisType clone(
    	final Object associatedObject,
    	final String label )
    {
    	ThisType clone = this.clone();
    	clone.associate( associatedObject ).setLabel( label );
    	return clone;
    }
    
    protected static final boolean equals( final Object lhs, final Object rhs ) {
    	return ( lhs == rhs ) || ( lhs.equals( rhs ) );
    }
}
