package net.dougqh.exec;

import java.util.concurrent.ExecutionException;


public final class ProcessExecutionException extends ExecutionException {
    private static final long serialVersionUID = -1723117343956400285L;
    
    private final int exitCode;
    
    public ProcessExecutionException(
        final int exitCode,
        final String errorOutput )
    {
        super( "Execution Error - exit code: " + exitCode + ( errorOutput.isEmpty() ? "" : "\n" + errorOutput ) );
        this.exitCode = exitCode;
    }
    
    public ProcessExecutionException( final Throwable cause ) {
        super( cause );
        this.exitCode = Integer.MIN_VALUE;
    }
    
    public final int getExitCode() {
        return this.exitCode;
    }
}
