package net.dougqh.exec;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;


public final class ProcessOutputReader implements Runnable {
    private final Reader stdoutReader;
    private final Reader stderrReader;
    
    private final Writer stdoutWriter;
    private final Writer stderrWriter;
    
    private final AtomicBoolean finish = new AtomicBoolean( false );
    
    public ProcessOutputReader( final Process process ) {
        this.stdoutReader = new InputStreamReader( new BufferedInputStream( process.getInputStream() ) );
        this.stderrReader = new InputStreamReader( new BufferedInputStream( process.getErrorStream() ) );
        
        this.stdoutWriter = new StringWriter();
        this.stderrWriter = new StringWriter();
    }
    
    public final void finish() {
    	this.finish.set( true );
    }
    
    @Override
    public final synchronized void run() {
        try {
            char[] buf = new char[ 256 ];
            
            int stdoutNumRead = 0;
            int stderrNumRead = 0;
            while ( ! Thread.currentThread().isInterrupted() ) {
            	stdoutNumRead = this.stdoutReader.read( buf );
            	if ( stdoutNumRead > 0 ) {
	            	this.stdoutWriter.write( buf, 0, stdoutNumRead );
	            }
	            
	            stderrNumRead = this.stderrReader.read( buf );
	            if ( stderrNumRead > 0 ) {
	            	this.stderrWriter.write( buf, 0, stderrNumRead );
	            }
	            
	            //process is done and the buffers are empty, so bail
	            if ( this.finish.get() && stdoutNumRead <= 0 && stderrNumRead <= 0 ) {
	            	break;
	            }
	            
	            try {
	            	Thread.sleep( 500 );
	            } catch ( InterruptedException e ) {
	            	Thread.currentThread().interrupt();
	            	break;
	            }
            }
        } catch ( IOException e ) {
            throw new IllegalStateException( e );
        }
    }
    
    public final synchronized String getOutput() {
    	return this.stdoutWriter.toString();
    }
    
    public final synchronized String getErrorOutput() {
    	return this.stderrWriter.toString();
    }
}
