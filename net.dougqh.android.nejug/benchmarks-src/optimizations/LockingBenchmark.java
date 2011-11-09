package optimizations;


import com.google.caliper.SimpleBenchmark;


public class LockingBenchmark
	extends SimpleBenchmark
{
	public void timeStringBuilder(int reps) {
		for ( int rep = 0; rep < reps; ++rep ) {
			StringBuilder builder = new StringBuilder();
			builder.append( "foo" );
			builder.append( "bar" );
			builder.append( "baz" );
			builder.append( "quux" );
			builder.append( "blah blah" );
		}
	}
	
	public void timeIndividualLocks(int reps) {
		for ( int rep = 0; rep < reps; ++rep ) {
			StringBuffer buffer = new StringBuffer();
			buffer.append( "foo" );
			buffer.append( "bar" );
			buffer.append( "baz" );
			buffer.append( "quux" );
			buffer.append( "blah blah" );
		}
	}
	
	public void timeLargerLock(int reps) {
		for ( int rep = 0; rep < reps; ++rep ) {
			StringBuffer buffer = new StringBuffer();
			synchronized( buffer ) {
				buffer.append( "foo" );
				buffer.append( "bar" );
				buffer.append( "baz" );
				buffer.append( "quux" );
				buffer.append( "blah blah" );
			}
		}
	}
}
