package optimizations;

import java.util.Random;

import com.google.caliper.SimpleBenchmark;

public class InvocationsBenchmark
	extends SimpleBenchmark
{
	private static final Random random = new Random();
	
	private static final int NUM_POINTS = 5;
	private static final Point[] points = new Point[ NUM_POINTS ];
	static {
		for ( int i = 0; i < NUM_POINTS; ++i ) {
			points[ i ] = new Point( i, 2 * i );
		}
	}
	
	private static final Point randomPoint() {
		return points[random.nextInt( NUM_POINTS )];
	}
	
	public final long timeStatic(int reps) {
		Point pointA = randomPoint();
		Point pointB = randomPoint();

		long sumSquaredDistances = 0;
		for ( int rep = 0; rep < reps; ++rep ) {		
			sumSquaredDistances += 
				staticSquaredDistance( pointA, pointB );
		}
		return sumSquaredDistances;
	}
	
	static int staticSquaredDistance( Point pointA, Point pointB ) {
		int deltaX = pointA.x - pointB.x;
		int deltaY = pointA.y - pointB.y;
		return deltaX * deltaX + deltaY * deltaY;		
	}
	
	public final long timePrivate(int reps) {
		Point pointA = randomPoint();
		Point pointB = randomPoint();

		long sumSquaredDistances = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			sumSquaredDistances +=
				privateSquaredDistance( pointA, pointB );
		}
		return sumSquaredDistances;
	}
	
	private int privateSquaredDistance( Point pointA, Point pointB ) {
		int deltaX = pointA.x - pointB.x;
		int deltaY = pointA.y - pointB.y;
		return deltaX * deltaX + deltaY * deltaY;		
	}
	
	public final long timeVirtual(int reps) {
		Point pointA = randomPoint();
		Point pointB = randomPoint();

		SquaredDistanceImpl distanceImpl = new SquaredDistanceImpl();
		
		long sumSquaredDistances = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			sumSquaredDistances += 
				distanceImpl.squaredDistance( pointA, pointB );
		}
		return sumSquaredDistances;
	}
	
	public final long timeInterface(int reps) {
		Point pointA = randomPoint();
		Point pointB = randomPoint();

		SquaredDistance distance = new SquaredDistanceImpl2();
		
		long sumSquaredDistances = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			sumSquaredDistances += 
				distance.squaredDistance( pointA, pointB );
		}
		return sumSquaredDistances;
	}

	public static final class Point {
		public final int x;
		public final int y;
		
		public Point( int x, int y ) {
			this.x = x;
			this.y = y;
		}
	}
	
	public interface SquaredDistance {
		public int squaredDistance( Point pointA, Point pointB );
	}
	
	public class SquaredDistanceImpl implements SquaredDistance {
		public int squaredDistance( Point pointA, Point pointB ) {
			int deltaX = pointA.x - pointB.x;
			int deltaY = pointA.y - pointB.y;
			return deltaX * deltaX + deltaY * deltaY;			
		}
	}

	public class SquaredDistanceImpl2 implements SquaredDistance {
		public int squaredDistance( Point pointA, Point pointB ) {
			int deltaX = pointA.x - pointB.x;
			int deltaY = pointA.y - pointB.y;
			return deltaX * deltaX + deltaY * deltaY;			
		}
	}
}
