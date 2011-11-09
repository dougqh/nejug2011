package net.dougqh.graphml.yed;

import java.awt.Color;
import java.awt.Dimension;

import net.dougqh.graphml.GraphmlIoException;
import net.dougqh.graphml.GraphmlWriter;



public final class YedBasicNode
    extends YedNode< YedBasicNode >
 
{
    private Color color = null;    
    private YedShape shape = null;
    private YedLineStyle lineStyle = null;
    private Dimension dimension = null;
    
    public YedBasicNode() {}
    
    public YedBasicNode( final String label ) {
        this.setLabel( label );
    }
    
    public YedBasicNode(
    	final Object associatedObject,
    	final String label )
    {
    	this.associate( associatedObject );
    	this.setLabel( label );
    }
        
    public final YedBasicNode setColor(
        final int red,
        final int green,
        final int blue )
    {
        if ( red < 0 || red > 255 ) throw new IllegalArgumentException( "red" );
        if ( green < 0 || green > 255 ) throw new IllegalArgumentException( "red" );
        if ( blue < 0 || blue > 255 ) throw new IllegalArgumentException( "red" );
        
        return this.setColor( red, green, blue );
    }
    
    public final YedBasicNode setColor( final Color color ) {
        if ( color == null ) throw new IllegalArgumentException( "color" );
        
        this.color = color;
        return this;
    }
    
    public final YedBasicNode setShape( final YedShape shape ) {
        if ( shape == null ) throw new IllegalArgumentException( "shape" );
        
        this.shape = shape;
        return this;
    }
    
    public final YedBasicNode setLineStyle( final YedLineStyle lineStyle ) {
        if ( lineStyle == null ) throw new IllegalArgumentException( "lineStyle" );
        
        this.lineStyle = lineStyle;
        return this;
    }
    
    public final YedBasicNode setDimension(
    	final double width,
    	final double height )
    {
    	return this.setDimension( (int)width, (int)height );
    }
    
    public final YedBasicNode setDimension(
        final int width,
        final int height )
    {
        if ( width < 0 ) throw new IllegalArgumentException( "width" );
        if ( height < 0 ) throw new IllegalArgumentException( "height" );
        
        return this.setDimension( new Dimension( width, height ) );
    }
    
    public final YedBasicNode setDimension( final Dimension dimension ) {
        this.dimension = dimension;
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
        if ( this.shape != null ) {
            graphmlWriter.yed().shape( this.shape );
        }
        if ( this.color != null ) {
            graphmlWriter.yed().fill( this.color );
        }
        if ( this.lineStyle != null ) {
            graphmlWriter.yed().borderStyle( this.lineStyle );
        }
        if ( this.dimension != null ) {
            graphmlWriter.yed().geometry(
                this.dimension.getWidth(),
                this.dimension.getHeight() );
        }
        graphmlWriter.yed().endShapeNode();
        graphmlWriter.endNode();
        
        this.setId( id );
    }
    
    @Override
    public final YedBasicNode clone() {
        return super.clone();
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
    	} else if ( ! ( obj instanceof YedBasicNode ) ) {
    		return false;
    	} else {
    		YedBasicNode that = (YedBasicNode)obj;
    		return equals(
    			this.associatedObject,
    			that.associatedObject );
    	}
    }
}