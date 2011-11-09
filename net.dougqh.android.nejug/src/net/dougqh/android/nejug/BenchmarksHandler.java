package net.dougqh.android.nejug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.dougqh.benchmark.BenchmarkResult;
import net.dougqh.benchmark.BenchmarkResults;
import net.dougqh.benchmark.Benchmarker;
import net.dougqh.benchmark.Benchmarker.Caching;
import net.dougqh.benchmark.Benchmarker.Platform;
import net.dougqh.freemarker.TemplateRenderer;
import optimizations.EnumBenchmark;
import optimizations.InvocationsBenchmark;
import optimizations.LoopTypeBenchmark;
import optimizations.StringLengthBenchmark;
import optimizations.SyntheticAccessBenchmark;

import com.google.caliper.Benchmark;

public final class BenchmarksHandler extends DemoHandler {	
	static final File BENCHMARKS_SRC_DIR = new File( "benchmarks-src" );
	private static final File BENCHMARKS_LIB_DIR = new File( "benchmarks-lib" );

	private static final BenchmarkRef[] BENCHMARKS = {
		new BenchmarkRef( "String Length", StringLengthBenchmark.class ),
		new BenchmarkRef( "Invocations", InvocationsBenchmark.class ),
		new BenchmarkRef( "Synthetic Access", SyntheticAccessBenchmark.class ),
		new BenchmarkRef( "Loops", LoopTypeBenchmark.class ),
		new BenchmarkRef( "Enums", EnumBenchmark.class )
	};
	
	private final Benchmarker benchmarker = new Benchmarker( BENCHMARKS_LIB_DIR, BENCHMARKS_SRC_DIR ).
		initCacheDir( new File( "cache" ) );
	
	BenchmarksHandler( final String path, final TemplateRenderer renderer ) {
		super( path, renderer );
	}
	
	@Override
	protected final void handleGet(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException
	{
		Map< String, Object > vars = new HashMap< String, Object >( 4 );
		vars.put( "benchmarks", BENCHMARKS );
		
		this.render( httpRequest, httpResponse, "benchmarks.ftl", vars );
	}
	
	@Override
	protected final void handlePost(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException
	{
		Caching caching;
		if ( httpRequest.getParameter( "force" ) != null ) {
			caching = Caching.FORCE_UPDATE;
		} else {
			caching = Caching.USE_CACHE;
		}
		
		int index = Integer.parseInt( httpRequest.getParameter( "benchmarkIndex" ) );
		Class< ? extends Benchmark > benchmarkClass = BENCHMARKS[ index ].getBenchmarkClass();
		
		try {

			Future< BenchmarkResults > androidFuture = 
				this.benchmarker.benchmark( benchmarkClass, Platform.ANDROID_DEVICE, caching );
			
			//Need a long delay here
			//Vogar builds, dexes, and deploys the code to the Android device which 
			//is an expensive process.  Running the HotSpot benchmark while this processes
			//is occurring will skew the results.
			//None-the-less, some overlap in the benchmarks is desirable to keep the 
			//benchmark from taking forever to run.
			if ( ! androidFuture.isDone() || ! androidFuture.get().isCachedResult() ) {
				try {
					Thread.sleep( 30 * 1000 );
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					throw new ServletException( e );
				}
			}
			
			Future< BenchmarkResults > hotspotFuture = 
				this.benchmarker.benchmark( benchmarkClass, Platform.JVM, caching );

			this.renderJson(
				httpRequest,
				httpResponse,
				new JsonBenchmarks( hotspotFuture.get(), androidFuture.get() ) );
		} catch ( ExecutionException e ) {
			throw new ServletException( e );
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new ServletException( e );
		}
	}
	
	public static final class JsonBenchmarks {
		public final List< Bar > hotspot = new ArrayList< Bar >();
		public final List< Bar > dalvik = new ArrayList< Bar >();
		
		public JsonBenchmarks(
			final BenchmarkResults hotspotResults,
			final BenchmarkResults dalvikResults )
		{
			//DQH - Cannot simply loop over the results because the two different
			//VMs may have run the benchmarks in different orders.
			//To compensate, use HotSpot as the order of record and make the 
			//Dalvik results match that order.
			for ( BenchmarkResult hotspotResult : hotspotResults ) {
				this.hotspot.add( new Bar( hotspotResult ) );
			
				for ( BenchmarkResult dalvikResult: dalvikResults ) {
					if ( dalvikResult.getBenchmarkName().equals( hotspotResult.getBenchmarkName() ) ) {
						this.dalvik.add( new Bar( dalvikResult ) );
					}
				}
			}
		}
	}
	
	public static final class Bar {
		public final String label;
		public final double nanoTime;
		
		public Bar( final BenchmarkResult result ) {
			this.label = result.getBenchmarkName();
			this.nanoTime = result.getNanoTime();
		}
	}
}
