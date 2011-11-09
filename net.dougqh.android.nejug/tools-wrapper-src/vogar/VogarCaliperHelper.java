package vogar;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import caliper.CaliperHelper;

import net.dougqh.exec.ProcessMonitor;
import net.dougqh.exec.ProcessResult;

public final class VogarCaliperHelper implements CaliperHelper {
	private final File vogarJar;
	
	public VogarCaliperHelper( final File vogarJar ) {
		this.vogarJar = vogarJar;
	}
	
	@Override
	public final Future< ProcessResult > caliper(
		final File srcDir,
		final Class< ? > benchmarkClass,
		final String mode )
		throws ExecutionException
	{
		File javaFile = new File( srcDir, benchmarkClass.getName().replace( '.', '/' ) + ".java" );

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(
			"java", "-jar", this.vogarJar.getAbsolutePath(),
			"--mode", mode,
			"--benchmark",
			"--stream",
			"--sourcepath", srcDir.getAbsolutePath(),
			javaFile.getAbsolutePath() );

		//Deliberately returned as an opaque object
		return new ProcessMonitor( processBuilder ).start();	
	}
}
