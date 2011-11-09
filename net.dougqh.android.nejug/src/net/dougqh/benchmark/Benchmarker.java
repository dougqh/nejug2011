package net.dougqh.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import net.dougqh.concurrent.ConvertingFuture;
import net.dougqh.concurrent.TrivialFuture;
import net.dougqh.exec.ProcessResult;
import vogar.VogarCaliperHelper;
import caliper.CaliperHelper;
import caliper.StandardCaliperHelper;

import com.google.caliper.Benchmark;

public final class Benchmarker {
	private static final Logger LOGGER = Logger.getLogger( "benchmark" );
	
	public enum Platform {
		JVM( "jvm" ),
		ANDROID_DEVICE( "device" );
		
		final String vogarMode;
		
		Platform( final String vogarMode ) {
			this.vogarMode = vogarMode;
		}
	}
	
	public enum Caching {
		USE_CACHE,
		FORCE_UPDATE
	}
	
	private final File srcDir;
	private volatile BenchmarkCache cache;
	
	private final CaliperHelper standardHelper;
	private final CaliperHelper vogarHelper;
	
	public Benchmarker(
		final File benchmarksLibDir,
		final File srcDir )
	{
		this.srcDir = srcDir;
		
		this.standardHelper = new StandardCaliperHelper(
			new File( benchmarksLibDir, "caliper.jar" ) );
		
		this.vogarHelper = new VogarCaliperHelper( 
			new File( benchmarksLibDir, "vogar.jar" ) ); 
		
		this.cache = new NullBenchmarkCache();
	}
	
	public final Benchmarker initCacheDir( final File cacheDir ) {
		this.cache = new FileBenchmarkCache( cacheDir );
		return this;
	}
	
	public final Future< BenchmarkResults > benchmark(
		final Class< ? extends Benchmark > benchmarkClass,
		final Platform platform,
		final Caching caching )
		throws ExecutionException
	{
		if ( ! this.srcDir.isDirectory() ) {
			throw new IllegalStateException( "srcDir " + this.srcDir.getAbsolutePath() + " is not a directory." );
		}
		
		if ( caching != Caching.FORCE_UPDATE ) {
			try {
				String output = this.cache.getOutput( benchmarkClass, platform );
				if ( output != null ) {
					BenchmarkResults results = BenchmarkParser.parse( output ).markCached();
					return new TrivialFuture< BenchmarkResults >( results );
				}
			} catch ( IOException e ) {
				//ignore fallback on running the test
			}
		}
		
		LOGGER.info( "Running " + benchmarkClass.getSimpleName() + " on " + platform.name().toLowerCase() + "..." );
		
		Future< ProcessResult > vogarFuture = this.getCaliperHelperFor( platform ).caliper(
			this.srcDir,
			benchmarkClass,
			platform.vogarMode );
				
		return new ConvertingFuture< ProcessResult, BenchmarkResults >( vogarFuture ) {
			@Override
			protected final BenchmarkResults convert( final ProcessResult in ) {
				String output = in.getOutput();
				LOGGER.info( output );
				try {
					Benchmarker.this.cache.putOutput( benchmarkClass, platform, output );
				} catch ( IOException e ) {
					//ignore just return the result
				}
				
				return BenchmarkParser.parse( output );
			}
		};
	}
	
	private final CaliperHelper getCaliperHelperFor( final Platform platform ) {
		switch ( platform ) {
			case JVM:
			return this.standardHelper;
			
			default:
			return this.vogarHelper;
		}	
	}
}
