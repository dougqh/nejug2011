package optimizations;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

public class AnonymousObjectsBenchmark
	extends SimpleBenchmark
{
	@Param( "10000" )
	private int size;
	
	private int[] ints;
	private int[] copyOfInts;
	
	private Integer[] integers;
	private Integer[] copyOfIntegers;
	
	@Override
	protected void setUp() throws Exception {
		Random random = new Random();
		
		this.ints = new int[ this.size ];
		this.integers = new Integer[ this.size ];
		for ( int i = 0; i < this.size; ++i ) {
			int randomInt = random.nextInt();
			
			this.ints[ i ] = randomInt;
			this.integers[ i ] = randomInt;
		}
		
		this.copyOfInts = new int[ this.size ];
		this.copyOfIntegers = new Integer[ this.size ];
	}
	
	public void timePrimitive( int reps ) {
		for ( int rep = 0; rep < reps; ++rep ) {
			System.arraycopy(
				this.ints, 0,
				this.copyOfInts, 0, this.ints.length );
			
			Arrays.sort( this.copyOfInts );
		}
	}
	
	public void timeBoxed( int reps ) {
		for ( int rep = 0; rep < reps; ++rep ) {
			System.arraycopy(
				this.integers, 0,
				this.copyOfIntegers, 0, this.integers.length );
			
			Arrays.sort( this.copyOfIntegers );
		}
	}
	
	public void timeBoxedSingletonComparator( int reps ) {
		for ( int rep = 0; rep < reps; ++rep ) {
			System.arraycopy(
				this.integers, 0,
				this.copyOfIntegers, 0, this.integers.length );
			
			Arrays.sort( this.copyOfIntegers, IntComparator.INSTANCE );
		}
	}

	public void timeBoxedAnonymousComparator( int reps ) {
		for ( int rep = 0; rep < reps; ++rep ) {
			System.arraycopy(
				this.integers, 0,
				this.copyOfIntegers, 0, this.integers.length );
			
			Arrays.sort(
				this.copyOfIntegers,
				new Comparator< Integer >() {
					@Override
					public int compare(
						Integer lhs,
						Integer rhs )
					{
						if ( lhs < rhs ) {
							return -1;
						} else if ( lhs == rhs ) {
							return 0;
						} else {
							return 1;
						}
					}
				} );
		}
	}
	
	private static class IntComparator 
		implements Comparator< Integer >
	{
		static final IntComparator INSTANCE = 
			new IntComparator();
		
		private IntComparator() {}
		
		@Override
		public int compare(
			Integer lhs,
			Integer rhs )
		{
			if ( lhs < rhs ) {
				return -1;
			} else if ( lhs == rhs ) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
