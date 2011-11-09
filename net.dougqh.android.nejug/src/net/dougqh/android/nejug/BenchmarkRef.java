package net.dougqh.android.nejug;

import java.io.IOException;

import net.dougqh.formatting.JavaFormatting;

import com.google.caliper.Benchmark;

public final class BenchmarkRef {
	private final String name;
	private final Class< ? extends Benchmark > benchmarkClass;

	BenchmarkRef(
		final String name,
		final Class< ? extends Benchmark > benchmarkClass )
	{
		this.name = name;
		this.benchmarkClass = benchmarkClass;
	}
		
	public final String getName() {
		return this.name;
	}
	
	public final Class< ? extends Benchmark > getBenchmarkClass() {
		return this.benchmarkClass;
	}
	
	public final String getSource() throws IOException {
		return JavaFormatting.format(
			JavaFormatting.getSoure( BenchmarksHandler.BENCHMARKS_SRC_DIR, this.benchmarkClass ) );
	}
}