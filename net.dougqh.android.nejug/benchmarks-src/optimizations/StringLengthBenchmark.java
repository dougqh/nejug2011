package optimizations;

import com.google.caliper.SimpleBenchmark;

public class StringLengthBenchmark
	extends SimpleBenchmark
{
	private static final String[] STRINGS = {
		"foo",
		"bar",
		"baz",
		"quux",
		"quuuuuuuuuuux",
		"foobar"
	};
	
	private static final StringBuilder[] BUILDERS =
		new StringBuilder[STRINGS.length];
	
	private static final int NUM_STRINGS =
		STRINGS.length;
	
	static {
		for ( int i = 0; i < STRINGS.length; ++i ) {
			BUILDERS[i] =
				new StringBuilder(STRINGS[i]);
		}
	}
	
	public long timeString(int reps) {
		long sumLength = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			sumLength += 
				STRINGS[rep % NUM_STRINGS].length();
		}
		return sumLength;
	}
	
	public long timeStringBuilder(int reps) {
		long sumLength = 0;
		for ( int rep = 0; rep < reps; ++rep ) {
			sumLength +=
				BUILDERS[rep % NUM_STRINGS].length();
		}
		return sumLength;
	}
}
