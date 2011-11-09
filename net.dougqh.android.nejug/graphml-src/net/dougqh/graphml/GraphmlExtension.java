package net.dougqh.graphml;

import net.dougqh.graphml.GraphmlWriter.Edge;

@SuppressWarnings( "unchecked" )
public abstract class GraphmlExtension< T extends GraphmlExtension< T > > {
    protected final GraphmlWriter graphmlWriter;
    
    protected GraphmlExtension( final GraphmlWriter graphmlWriter ) {
        this.graphmlWriter = graphmlWriter;
    }
    
    protected abstract void addNamespaces() throws GraphmlIoException;
    
    protected abstract void addMetaInfo() throws GraphmlIoException;    
    
    protected void edge( final Edge edge ) throws GraphmlIoException {
    }
    
    protected final T start( final String element ) throws GraphmlIoException {
        this.graphmlWriter.start( element );
        return (T)this;
    }
    
    protected final T start(
        final String prefix,
        final String element,
        final String namespaceUri )
        throws GraphmlIoException
    {
        this.graphmlWriter.start( prefix, element, namespaceUri );
        return (T)this;
    }
    
    protected final T attrib(
        final String name,
        final Object value )
        throws GraphmlIoException
    {
        this.graphmlWriter.attrib( name, value );
        return (T)this;
    }
    
    protected final T characters( final String text ) throws GraphmlIoException {
        this.graphmlWriter.characters( text );
        return (T)this;
    }
    
    protected final T end() throws GraphmlIoException {
        this.graphmlWriter.end();
        return (T)this;
    }
    
    protected final T startKey() throws GraphmlIoException {
        this.graphmlWriter.startKey();
        return (T)this;
    }
    
    protected final T endKey() throws GraphmlIoException {
        this.graphmlWriter.endKey();
        return (T)this;
    }
    
    protected final T startData( final String key ) throws GraphmlIoException {
        this.graphmlWriter.startData( key );
        return (T)this;
    }
    
    protected final T endData() throws GraphmlIoException {
        this.graphmlWriter.endData();
        return (T)this;
    }
}
