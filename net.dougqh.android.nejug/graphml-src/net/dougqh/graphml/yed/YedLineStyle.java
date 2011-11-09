package net.dougqh.graphml.yed;


public enum YedLineStyle {
    LINE( "line" ),
    DOTTED( "dotted" );

    private final String id;
    
    YedLineStyle( final String id ) {
        this.id = id;
    }
    
    public final String getId() {
        return this.id;
    }
}
