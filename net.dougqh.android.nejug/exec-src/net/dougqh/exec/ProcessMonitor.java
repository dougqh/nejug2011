package net.dougqh.exec;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;



public final class ProcessMonitor {
    private final ProcessBuilder builder;
    
    public ProcessMonitor( final ProcessBuilder builder ) {
        this.builder = builder;
    }
    
    public final Future< ProcessResult > start() throws ProcessExecutionException {
        try {
            return new ProcessFuture( this.builder.start() );
        } catch ( IOException e ) {
            throw new ProcessExecutionException( e );
        }        
    }
    
    private static final class ProcessFuture implements Future< ProcessResult > {
        private final Process process;
        private final Thread thread;
        private final ProcessOutputReader outputReader;
        
        private AtomicBoolean canceled = new AtomicBoolean( false );        
        
        ProcessFuture( final Process process ) {
            this.process = process;
            this.outputReader = new ProcessOutputReader( process );
            this.thread = new Thread( this.outputReader, "output-reader" );
            this.thread.start();
        }
        
        @Override
        public final boolean isDone() {
            try {
                this.process.exitValue();
                this.outputReader.finish();
                return true;
            } catch ( IllegalThreadStateException e ) {
                return false;
            }
        }
        
        @Override
        public boolean isCancelled() {
            return this.canceled.get();
        }
        
        @Override
        public final boolean cancel( final boolean mayInterruptIfRunning ) {            
            if ( this.canceled.get() ) {
                return false;
            }
            
            if ( this.isDone() ) {                
                return false;
            } else {
                if ( mayInterruptIfRunning ) {
                    this.process.destroy();
                    this.thread.interrupt();
                    return true;
                } else {
                    return false;
                }
            }
        }
        
        @Override
        public final ProcessResult get(
            final long timeout,
            final TimeUnit unit )
            throws InterruptedException, ExecutionException, TimeoutException
        {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final ProcessResult get()
            throws InterruptedException, ExecutionException
        {
            int exitCode = this.process.waitFor();
            this.outputReader.finish();
            if ( exitCode != 0 ) {
                throw new ProcessExecutionException(
                    exitCode,
                    this.outputReader.getErrorOutput() );
            }
            return new ProcessResultImpl( this.outputReader );
        }
    }
    
    private static final class ProcessResultImpl implements ProcessResult {
        private final ProcessOutputReader outputReader;
        
        ProcessResultImpl( final ProcessOutputReader outputReader ) {
            this.outputReader = outputReader;
        }
        
        @Override
        public final String getOutput() {
            return this.outputReader.getOutput();
        }
        
        @Override
        public final String getErrorOutput() {
            return this.outputReader.getErrorOutput();
        }
    }
}
