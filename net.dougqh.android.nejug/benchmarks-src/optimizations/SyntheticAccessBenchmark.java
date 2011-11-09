package optimizations;

import com.google.caliper.SimpleBenchmark;

public final class SyntheticAccessBenchmark
	extends SimpleBenchmark
{
	private static final int NUM_POINTS = 5;
	
	private PackagePoint[] packagePoints;
	private PrivatePoint[] privatePoints;
	
	@Override
	protected void setUp() throws Exception {
		packagePoints =
			new PackagePoint[ NUM_POINTS ];
		privatePoints =
			new PrivatePoint[ NUM_POINTS ];
		
		for ( int i = 0; i < NUM_POINTS; ++i ) {
			packagePoints[ i ] =
				new PackagePoint( 2 * i, i );
			privatePoints[ i ] =
				new PrivatePoint( 2 * i, i );
		}
	}
	
	public long[] timeFieldAccess( int reps ) {
		long sumX = 0;
		long sumY = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			PackagePoint point =
				packagePoints[rep % NUM_POINTS];
			sumX += point.x;
			sumY += point.y;
		}
		return new long[] { sumX, sumY };
	}
	
	public long[] timeGetterAccess( int reps ) {
		long sumX = 0;
		long sumY = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			PackagePoint point =
				packagePoints[rep % NUM_POINTS];
			sumX += point.getX();
			sumY += point.getY();
		}
		return new long[] { sumX, sumY };
	}
	
	public long[] timeSyntheticFieldAccess( int reps ) {
		long sumX = 0;
		long sumY = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			PrivatePoint point =
				privatePoints[rep % NUM_POINTS];
			sumX += point.x;
			sumY += point.y;
		}
		return new long[] { sumX, sumY };
	}
	
	public long[] timeSyntheticGetterAccess( int reps ) {
		long sumX = 0;
		long sumY = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			PrivatePoint point =
				privatePoints[rep % NUM_POINTS];
			sumX += point.getX();
			sumY += point.getY();
		}
		return new long[] { sumX, sumY };
	}
	
	static final class PackagePoint {
		final int x;
		final int y;
		
		PackagePoint( final int x, final int y ) {
			this.x = x;
			this.y = y;
		}
		
		final int getX() {
			return this.x;
		}
		
		final int getY() {
			return this.y;
		}
	}
	
	private static final class PrivatePoint {
		private final int x;
		private final int y;
		
		PrivatePoint( final int x, final int y ) {
			this.x = x;
			this.y = y;
		}
		
		private final int getX() {
			return this.x;
		}
		
		private final int getY() {
			return this.y;
		}
	}
}
