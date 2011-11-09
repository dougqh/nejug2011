package net.dougqh.graphml;

import java.io.IOException;

public final class GraphmlIoException extends IOException {
    private static final long serialVersionUID = 8354649510236931729L;

    GraphmlIoException( final Throwable cause ) {
        super( cause );
    }
}
