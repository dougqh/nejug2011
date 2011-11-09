package net.dougqh.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ConvertingFuture< I, O > implements Future< O > {
	private final Future< ? extends I > wrappedFuture;
	
	public ConvertingFuture( final Future< ? extends I > wrappedFuture ) {
		this.wrappedFuture = wrappedFuture;
	}
	
	@Override
	public final boolean cancel( final boolean mayInterruptIfRunning ) {
		return this.wrappedFuture.cancel( mayInterruptIfRunning );
	}
	
	@Override
	public final O get() throws InterruptedException, ExecutionException {
		return this.convert( this.wrappedFuture.get() );
	}
	
	@Override
	public final O get( final long timeout, final TimeUnit unit )
		throws InterruptedException, ExecutionException, TimeoutException
	{
		return this.convert( this.wrappedFuture.get( timeout, unit ) );
	}
	
	@Override
	public final boolean isCancelled() {
		return this.wrappedFuture.isCancelled();
	}
	
	@Override
	public final boolean isDone() {
		return this.wrappedFuture.isDone();
	}
	
	protected abstract O convert( final I in );
}
