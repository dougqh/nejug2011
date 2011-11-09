package net.dougqh.benchmark;

import java.util.Map;

public final class BenchmarkResult {
	private static final String BENCHMARK = "benchmark";
	private static final String NANO_TIME = "ns";
	private static final String MICRO_TIME= "us";
	private static final String MILLI_TIME = "ms";
	
	private final Map< ? extends String, ? extends String > rowData;
	
	BenchmarkResult( final Map< ? extends String, ? extends String > rowData ) {
		this.rowData = rowData;
	}
	
	public final String getName() {
		StringBuilder builder = new StringBuilder();
		builder.append( this.getBenchmarkName() );
		for ( Map.Entry< ? extends String, ? extends String > entry: this.rowData.entrySet() ) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			if ( ! isCoreAttribute( key ) ) {
				builder.append( String.format( " %s %s", key, value ) );
			}
		}
		return builder.toString();
	}
	
	public final String getBenchmarkName() {
		return this.rowData.get( BENCHMARK );
	}
	
	public final double getNanoTime() {
		String nanoTime = this.rowData.get( NANO_TIME );
		if ( nanoTime != null ) {
			return Double.parseDouble( nanoTime );
		}
		
		String microTime = this.rowData.get( MICRO_TIME );
		if ( microTime != null ) {
			return Double.parseDouble( microTime ) * 1000;
		}
		
		String milliTime = this.rowData.get( MILLI_TIME );
		if ( milliTime != null ) {
			return Double.parseDouble( milliTime ) * 1000 * 1000;
		}
		
		throw new IllegalStateException(
			"Could not find time key: " + this.rowData.keySet().toString() );
	}
	
	private static final boolean isCoreAttribute( final String key ) {
		return key.equals( BENCHMARK ) ||
			key.equals( NANO_TIME ) ||
			key.equals( MICRO_TIME ) ||
			key.equals( MILLI_TIME );
	}
	
	@Override
	public String toString() {
		return this.getName() + " " + this.getNanoTime();
	}
}
