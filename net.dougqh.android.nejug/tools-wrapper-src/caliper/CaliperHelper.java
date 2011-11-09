package caliper;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.dougqh.exec.ProcessResult;

public interface CaliperHelper {
	public abstract Future< ProcessResult > caliper(
		final File srcDir,
		final Class< ? > benchmarkClass,
		final String mode )
		throws ExecutionException;
}
