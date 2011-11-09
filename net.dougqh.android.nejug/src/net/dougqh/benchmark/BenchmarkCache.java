package net.dougqh.benchmark;

import java.io.IOException;

import net.dougqh.benchmark.Benchmarker.Platform;

import com.google.caliper.Benchmark;

public interface BenchmarkCache {
	public abstract void putOutput(
		final Class< ? extends Benchmark > benchmarkClass,
		final Platform platform,
		final String output )
		throws IOException;
	
	public abstract String getOutput(
		final Class< ? extends Benchmark > benchmarkClass,
		final Platform platform )
		throws IOException;
}
