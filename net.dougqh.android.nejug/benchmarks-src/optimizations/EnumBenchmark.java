package optimizations;

import java.util.Random;

import com.google.caliper.SimpleBenchmark;

public final class EnumBenchmark
	extends SimpleBenchmark
{
	enum Enum {
		ALPHA,
		BETA,
		GAMMA,
		DELTA,
		EPSILON
	}
	
	private static final int[] nums;
	private static final Enum[] enums;
	
	static {
		int len = Enum.values().length;
		nums = new int[len];
		enums = new Enum[len];
		
		Random random = new Random();
		
		for ( int i = 0; i < len; ++i ) {
			nums[i] = random.nextInt(len);
			enums[i] = Enum.values()[nums[i]];
		}
	}
	
	public int timeInt(int reps) {
		int sum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			for ( int num : nums ) {
				switch ( num ) {
					case 0:
					sum += 10;
					break;
					
					case 1:
					sum += 20;
					break;
					
					case 2:
					sum += 40;
					break;
					
					case 3:
					sum += 80;
					break;
				
					default:
					sum -= 20;
					break;
				}
			}
		}
		return sum;
	}
	
	public int timeEnum(int reps) {
		int sum = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			for ( Enum enumObj : enums ) {
				switch ( enumObj ) {
					case ALPHA:
					sum += 10;
					break;
					
					case BETA:
					sum += 20;
					break;
					
					case GAMMA:
					sum += 40;
					break;
					
					case DELTA:
					sum += 80;
					break;
				
					default:
					sum -= 20;
					break;
				}
			}
		}
		return sum;
	}
}
