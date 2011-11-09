package net.dougqh.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class TrivialFuture< T > implements Future< T > {
	private final T result;
	
	public TrivialFuture( final T result ) {
		this.result = result;
	}
	
	@Override
	public final T get() {
		return this.result;
	}
	
	@Override
	public final T get( final long timeout, final TimeUnit unit ) {
		return this.result;
	}
	
	@Override
	public final boolean cancel( final boolean mayInterruptIfRunning ) {
		return false;
	}
	
	@Override
	public final boolean isCancelled() {
		return false;
	}
	
	@Override
	public final boolean isDone() {
		return true;
	}
}
