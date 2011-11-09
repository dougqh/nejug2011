package net.dougqh.benchmark;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public final class BenchmarkParser {
	private BenchmarkParser() {}
	
	public static final BenchmarkResults parse( final String data ) {
		List< BenchmarkResult > results = new ArrayList< BenchmarkResult >();
		
		boolean inResults = false;
		LineFormat format = null;
		for ( BenchmarkScanner scanner = new BenchmarkScanner( data );
			scanner.hasNextLine(); )
		{
			Line line = scanner.nextLine();
			if ( ! inResults ) {
				if ( line.isResultsStart() ) {
					inResults = true;
					format = line.getLineFormat();
				}
			} else {
				if ( line.isResultsEnd() ) {
					inResults = false;
				} else if ( ! line.isEmpty() ) {
					results.add( new BenchmarkResult( line.getData( format ) ) );
				}
			}
		}
		return new BenchmarkResults( results );
	}
	
	public static final class BenchmarkScanner {
		private final Scanner scanner;
		
		public BenchmarkScanner( final String data ) {
			this.scanner = new Scanner( data );
		}
		
		public final boolean hasNextLine() {
			return this.scanner.hasNextLine();
		}
		
		public final Line nextLine() {
			return new Line( this.scanner.nextLine() );
		}
	}
	
	public static final class Line {
		private final String line;
		
		Line( final String line ) {
			this.line = line;
		}
		
		public final boolean isEmpty() {
			return this.line.trim().isEmpty();
		}
		
		public final boolean isResultsStart() {
			//Don't use ns here because other time units are possible, too.
			return this.line.contains( "benchmark" ) && this.line.contains( " linear runtime" );
		}
		
		public final LineFormat getLineFormat() {
			int cutoffPos = this.line.indexOf( " linear runtime" );
			String line = this.line.substring( 0, cutoffPos );
			String[] parts = line.trim().split( "\\s+" );
			return new LineFormat( parts, cutoffPos );
		}
		
		public final Map< String, String > getData( final LineFormat format ) {
			String line = this.line.substring( 0, format.cutoffPos );
			String[] values = line.trim().split( "\\s+" );
			
			HashMap< String, String > mappedValues = 
				new HashMap< String, String >( format.headers.length );
			for ( int i = 0; i < format.headers.length; ++i ) {
				mappedValues.put( format.headers[ i ], values[ i ] );
			}
			return Collections.unmodifiableMap( mappedValues );
		}
		
		public final boolean isResultsEnd() {
		    return this.line.contains( "vm:" );			
		}
		
		public final String toString() {
			return this.line;
		}
	}
	
	public static final class LineFormat {
		protected final String[] headers;
		protected final int cutoffPos;
		
		LineFormat( final String[] parts, final int cutoffPos ) {
			this.headers = parts;
			this.cutoffPos = cutoffPos;
		}
		
		@Override
		public final String toString() {
			return Arrays.toString( this.headers ) + " " + this.cutoffPos;
		}
	}
}
