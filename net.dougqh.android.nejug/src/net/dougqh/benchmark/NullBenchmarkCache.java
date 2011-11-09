package net.dougqh.benchmark;

import net.dougqh.benchmark.Benchmarker.Platform;

import com.google.caliper.Benchmark;

final class NullBenchmarkCache implements BenchmarkCache {
	@Override
	public final String getOutput(
		final Class<? extends Benchmark> benchmarkClass,
		final Platform platform )
	{
		return null;
	}
	
	public void putOutput(
		final Class<? extends Benchmark> benchmarkClass,
		final Platform platform,
		final String output )
	{
	}
}
