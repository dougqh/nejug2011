package net.dougqh.graphml.yed;

import java.awt.Color;
import java.net.URL;

import net.dougqh.graphml.GraphmlExtension;
import net.dougqh.graphml.GraphmlIoException;
import net.dougqh.graphml.GraphmlWriter;
import net.dougqh.graphml.GraphmlWriter.Edge;



public final class YedGraphmlExtension
    extends GraphmlExtension< YedGraphmlExtension >
{
    private static final String PREFIX = "y";
    private static final String URI = "http://www.yworks.com/xml/graphml";
    
    private Geometry geometry = null;
    
    public YedGraphmlExtension( final GraphmlWriter graphmlWriter ) {
        super( graphmlWriter );
    }
    
    protected final void addNamespaces() throws GraphmlIoException {
        this.attrib( "xmlns:" + PREFIX, URI );
    }
    
    @Override
    protected final void addMetaInfo() throws GraphmlIoException {
        this.startKey().
            attrib( "for", "graphml" ).
            attrib( "id", "d0" ).
            attrib( "yfiles.type", "resources" ).
            endKey();
        this.startKey().
            attrib( "attr.name", "url" ).
            attrib( "attr.type", "string" ).
            attrib( "for", "node" ).
            attrib( "id", "d1" ).
            endKey();
        this.startKey().
            attrib( "attr.name", "description" ).
            attrib( "attr.type", "string" ).
            attrib( "for", "node" ).
            attrib( "id", "d2" ).
            endKey();
        this.startKey().
            attrib( "for", "node" ).
            attrib( "id", "d3" ).
            attrib( "yfiles.type", "nodegraphics" ).
            endKey();
        this.startKey().
            attrib( "attr.name", "url" ).
            attrib( "attr.type", "string" ).
            attrib( "for", "edge" ).
            attrib( "id", "d4" ).
            endKey();
        this.startKey().
            attrib( "attr.name", "description" ).
            attrib( "attr.type", "string" ).
            attrib( "for", "edge" ).
            attrib( "id", "d5" ).
            endKey();
        this.startKey().
            attrib( "for", "edge" ).
            attrib( "id", "d6" ).
            attrib( "yfiles.type", "edgegraphics" ).
            endKey();
    }
    
    public final void url( final URL url )
        throws GraphmlIoException
    {
        this.startData( "d1" ).characters( url.toExternalForm() ).endData();
    }
    
    public final void description( final String text )
        throws GraphmlIoException
    {
        this.startData( "d2" ).characters( text ).endData();
    }
    
    public final void startShapeNode() throws GraphmlIoException {
        this.startData( "d3" );
        this.startYed( "ShapeNode" );
        
        this.geometry = new NullGeometry();
    }
    
    public final void endShapeNode() throws GraphmlIoException {
        this.geometry( this.geometry );
        
        this.endYed();
        this.endData();
    }
    
    public final void startUmlClassNode() throws GraphmlIoException {
        this.startData( "d3" );        
        this.startYed( "UMLClassNode" );
    }
    
    public final void endUmlClassNode() throws GraphmlIoException {
        this.endYed();
        this.endData();
    }
    
    public final void geometry(
        final double width,
        final double height )
        throws GraphmlIoException
    {
        this.geometry = new FixedGeometry( width, height );
    }
    
    private final void geometry( final Geometry geometry )
        throws GraphmlIoException
    {
        if ( ! ( this.geometry instanceof NullGeometry ) ) {
            this.startYed( "Geometry" ).
                attrib( "height", geometry.getHeight() ).
                attrib( "width", geometry.getWidth() ).
                attrib( "x", 0.0 ).
                attrib( "y", 0.0 ).
                endYed();
        }
    }
    
    public final void fill( final Color color ) throws GraphmlIoException {
        StringBuilder builder = new StringBuilder( 7 );
        builder.append( '#' );
        appendHex( builder, color.getRed() );
        appendHex( builder, color.getGreen() );
        appendHex( builder, color.getBlue() );
        this.fill( builder.toString() );
    }
    
    private static final void appendHex(
        final StringBuilder builder,
        final int colorComponent )
    {
        if ( colorComponent < 16 ) {
            builder.append( '0' );
        }
        builder.append( Integer.toHexString( colorComponent ) );
    }
    
    public final void fill( final String hexString ) throws GraphmlIoException {
        this.startYed( "Fill" ).
            attrib( "color", hexString ).
            attrib( "transparent", false ).
            endYed();
    }
    
    public final void borderStyle( final int lineWidth ) throws GraphmlIoException {
        this.borderStyle( YedLineStyle.LINE, lineWidth );
    }
    
    public final void borderStyle( final YedLineStyle lineStyle ) throws GraphmlIoException {
        this.borderStyle( lineStyle, 1 );
    }
    
    public final void borderStyle(
        final YedLineStyle lineStyle,
        final int lineWidth )
        throws GraphmlIoException
    {
        this.startYed( "BorderStyle" ).
            attrib( "color", "#000000" ).
            attrib( "type", lineStyle.getId() ).
            attrib( "width", lineWidth ).
            endYed();
    }
    
    public final void nodeLabel( final String text ) throws GraphmlIoException {
        this.nodeLabel( text, true );
    }
    
    public final void nodeLabel( final String text, final boolean visible )
        throws GraphmlIoException
    {
        if ( visible ) {
            this.geometry.addLine();
        }
        
        this.startYed( "NodeLabel" ).
            attrib( "alignment", "center" ).
            attrib( "autoSizePolicy", "content" ).
            attrib( "borderDistance", 0.0 ).
            attrib( "fontFamily", "Dialog" ).
            attrib( "fontSize", 12 ).
            attrib( "fontStyle", "plain" ).
            attrib( "hasBackgroundColor", false ).
            attrib( "hasLineColor", false ).
            attrib( "modelName", "internal" ).
            attrib( "modelPosition", "c" ).
            attrib( "textColor", "#000000" ).
            attrib( "visible", visible ).
            attrib( "width", 30.0 ).
            attrib( "x", 0.0 ).
            attrib( "y", 0.0 ).
            characters( text ).
            endYed();
    }
    
    public final void shape( final YedShape shape ) throws GraphmlIoException {
        this.startYed( "Shape" ).
            attrib( "type", shape.getId() ).
            endYed();
    }
    
    public final void startUml() throws GraphmlIoException {
        this.startYed( "UML" ).
            attrib( "clipContent", true ).
            attrib( "constraint", "" ).
            attrib( "omitDetails", false ).
            attrib( "stereotype", "" ).
            attrib( "use3DEffect", true );
    }
    
    public final void attribute( final String attribute ) throws GraphmlIoException {
        this.geometry.addLine();
        
        this.startYed( "AttributeLabel" ).characters( attribute ).endYed();
    }
    
    public final void method( final String method ) throws GraphmlIoException {
        this.geometry.addLine();
        
        this.startYed( "MethodLabel" ).characters( method ).endYed();
    }
    
    public final void endUml() throws GraphmlIoException {
        this.endYed();
    }
    
    @Override
    protected final void edge( final Edge edge ) throws GraphmlIoException {
        if ( Boolean.TRUE.equals( edge.directed() ) ) {
            this.startPolyLineEdge();
            this.arrows( YedArrowType.NONE, YedArrowType.STANDARD );
            this.endPolyLineEdge();
        }
    }
    
    final void startPolyLineEdge() throws GraphmlIoException {
        this.startData( "d6" );
        this.startYed( "PolyLineEdge" );
    }
    
    final void endPolyLineEdge() throws GraphmlIoException {
        this.endYed();
        this.endData();
    }
    
    final void arrows(
        final YedArrowType sourceType,
        final YedArrowType targetType )
        throws GraphmlIoException
    {
        this.startYed( "Arrows" ).
            attrib( "source", sourceType.getId() ).
            attrib( "target", targetType.getId() ).
            endYed();
    }
    
    private final YedGraphmlExtension startYed( final String element ) throws GraphmlIoException {
        return this.start( PREFIX, element, URI );
    }
    
    private final YedGraphmlExtension endYed() throws GraphmlIoException {
        return this.end();
    }
    
    private abstract class Geometry {
        protected int numLines = 0;
        protected int maxNumChars = 0;
        
        abstract double getWidth();
        abstract double getHeight();
        
        final void addLine() {
            ++this.numLines;
        }
        
        final void adjustCharacterWidth( final int numChars ) {
            if ( numChars > this.maxNumChars ) {
                this.maxNumChars = numChars;
            }
        }
    }
    
    private final class NullGeometry extends Geometry {
        @Override
        public final double getWidth() {
            return 0;
        }
        
        @Override
        public final double getHeight() {
            return 0;
        }
    }
    
    private final class EstimatedGeometry extends Geometry {
        @Override
        final double getWidth() {
            return this.maxNumChars * 8.0 + 10.0;
        }
        
        @Override
        final double getHeight() {
            return this.numLines * 8.0 + 10.0;
        }
    }
    
    private final class FixedGeometry extends Geometry {
        private final double width;
        private final double height;
        
        FixedGeometry( final double width, final double height ) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public final double getWidth() {
            return this.width;
        }
        
        @Override
        public final double getHeight() {
            return this.height;
        }
    }    
}
