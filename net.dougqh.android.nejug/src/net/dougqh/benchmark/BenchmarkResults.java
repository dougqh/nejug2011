package net.dougqh.benchmark;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class BenchmarkResults implements Iterable< BenchmarkResult >{
	private final List< BenchmarkResult > results;
	private boolean cached;
	
	public BenchmarkResults( final List< ? extends BenchmarkResult > results  ) {
		this.results = Collections.unmodifiableList( results );
	}
	
	final BenchmarkResults markCached() {
		this.cached = true;
		return this;
	}
	
	public final boolean isCachedResult() {
		return this.cached;
	}
	
	@Override
	public final Iterator< BenchmarkResult > iterator() {
		return this.results.iterator();
	}
	
	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for ( BenchmarkResult result : this ) {
			if ( first ) {
				first = false;
			} else {
				builder.append( '\n' );
			}
			builder.append( result ).append( '\n' );
		}
		return builder.toString();
	}
}
