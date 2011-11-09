package net.dougqh.benchmark;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import net.dougqh.benchmark.Benchmarker.Platform;

import com.google.caliper.Benchmark;

final class FileBenchmarkCache implements BenchmarkCache {
	private final File cacheDir;
	
	FileBenchmarkCache( final File cacheDir ) {
		this.cacheDir = cacheDir;
	}
	
	@Override
	public final String getOutput(
		final Class< ? extends Benchmark > benchmarkClass,
		final Platform platform )
		throws IOException
	{
		FileReader reader = new FileReader( this.getFileFor( benchmarkClass, platform ) );
		try {
			StringWriter writer = new StringWriter();
			try {
				char[] buf = new char[ 256 ];
				for ( int numRead = reader.read( buf );
					numRead > 0;
					numRead = reader.read( buf ) )
				{
					writer.write( buf, 0, numRead );
				}
			} finally {
				writer.close();
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}
	
	public void putOutput(
		final Class< ? extends Benchmark > benchmarkClass,
		final Platform platform,
		final String output )
		throws IOException
	{
		FileWriter writer = new FileWriter( this.getFileFor( benchmarkClass, platform ) );
		try {
			writer.write( output );
		} finally {
			writer.close();
		}
	}
	
	private final File getFileFor(
		final Class< ? extends Benchmark > benchmarkClass,
		final Platform platform )
	{
		return new File(
			this.cacheDir,
			benchmarkClass.getName() + "-" + platform.name().toLowerCase() );
	}
}
