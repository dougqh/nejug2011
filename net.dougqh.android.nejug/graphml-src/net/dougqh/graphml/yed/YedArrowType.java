package net.dougqh.graphml.yed;


public enum YedArrowType {
    NONE( "none" ),
    STANDARD( "standard" );

    private final String id;
    
    YedArrowType( final String id ) {
        this.id = id;
    }
    
    public final String getId() {
        return this.id;
    }
}
