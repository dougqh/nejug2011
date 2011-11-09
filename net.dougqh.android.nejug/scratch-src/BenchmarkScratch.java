import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.dougqh.benchmark.BenchmarkResults;
import net.dougqh.benchmark.Benchmarker;
import net.dougqh.benchmark.Benchmarker.Caching;
import net.dougqh.benchmark.Benchmarker.Platform;
import optimizations.InvocationsBenchmark;

public class BenchmarkScratch {
	public static final void main( final String[] args )
		throws InterruptedException, ExecutionException
	{
		Benchmarker benchmarker = new Benchmarker(
			new File( "benchmarks-lib" ),
			new File( "benchmarks-src" ) );
		
		Future< BenchmarkResults > benchmarkFuture = benchmarker.benchmark(
			InvocationsBenchmark.class,
			Platform.JVM,
			Caching.FORCE_UPDATE );
		System.out.println( benchmarkFuture.get() );
	}
}
