package optimizations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

public class LoopTypeBenchmark
	extends SimpleBenchmark
{
	@Param( "1000" )
	private int size;
	
	private int[] primitives;
	private Integer[] boxed;
	private List< Integer > intList;
	
	@Override
	protected void setUp() throws Exception {
		Random random = new Random();
		
		primitives = new int[size];
		boxed = new Integer[size];
		intList = new ArrayList<Integer>(size);
		for ( int i = 0; i < this.size; ++i ) {
			int value = random.nextInt();
			primitives[i] = value;
			boxed[i] = value;
			intList.add(value);
		}	
	}
	
	public long timeBackwards(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( int i = primitives.length - 1; i >= 0; --i ) {
				sum += primitives[i];
			}
			totalSum += sum;
		}
		return totalSum;
		
	}
	
	public long timeNewFor(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( int x : primitives ) {
				sum += x;
			}
			totalSum += sum;
		}
		return totalSum;
	}
	
	public long timeOldFor(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( int i = 0; i < primitives.length; ++i ) {
				sum += primitives[i];
			}
			totalSum += sum;
		}
		return totalSum;
	}
	
	public long timeBoxedNewFor(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( int x : boxed ) {
				sum += x;
			}
			totalSum += sum;
		}
		return totalSum;
	}
	
	public long timeBoxedOldFor(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( int i = 0; i < primitives.length; ++i ) {
				sum += primitives[i];
			}
			totalSum += sum;
		}
		return totalSum;
	}
	
	public long timeListThreadUnsafe(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( int i = 0; i < intList.size(); ++i ) {
				sum += intList.get(i);
			}
			totalSum += sum;
		}
		return totalSum;
	}
	
	public long timeListNewFor(int reps) {
		long totalSum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			long sum = 0;
			for ( Integer value : intList ) {
				sum += value;
			}
			totalSum += sum;
		}
		return totalSum;
	}
}
