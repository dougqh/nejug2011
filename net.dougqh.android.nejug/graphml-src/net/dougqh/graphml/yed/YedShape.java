package net.dougqh.graphml.yed;


public enum YedShape {
    RECTANGLE( "rectangle" ),
    ELLIPSE( "ellipse" ),
    TRIANGLE( "triangle" ),
    DIAMOND( "diamond" ),
    OCTAGON( "octagon" ),
    TRAPEZOID( "trapezoid" ),
    TRAPEZOID2( "trapezoid2" );
    
    private final String id;
    
    private YedShape( final String id ) {
        this.id = id;
    }
    
    public final String getId() {
        return this.id;
    }
}
