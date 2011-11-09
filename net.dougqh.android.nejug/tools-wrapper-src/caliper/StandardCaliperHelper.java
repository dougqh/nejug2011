package caliper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.caliper.Runner;

import net.dougqh.exec.ProcessMonitor;
import net.dougqh.exec.ProcessResult;

/**
 * Helper class to run a standard JVM caliper test.
 */
public final class StandardCaliperHelper implements CaliperHelper {
	private final File caliperJar;
	
	public StandardCaliperHelper( final File caliperJar ) {
		this.caliperJar = caliperJar;
	}
	
	@Override
	public final Future< ProcessResult > caliper(
		final File srcDir,
		final Class< ? > benchmarkClass,
		final String mode )
		throws ExecutionException
	{
		File classpathDir = getClasspathDir( benchmarkClass );
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(
			"java",
			"-cp", this.caliperJar.getAbsolutePath() + ":" + classpathDir.getAbsolutePath() + "/",
			Runner.class.getName(),
			benchmarkClass.getName() );

		//Deliberately returned as an opaque object
		return new ProcessMonitor( processBuilder ).start();
	}
	
	private static final File getClasspathDir( final Class< ? > aClass ) {
		File dir = getClassFile( aClass ).getParentFile();
		
		int packageCount = getPackageCount( aClass );
		for ( int i = 0; i < packageCount; ++i ) {
			dir = dir.getParentFile();
		}
		return dir;
	}
	
	private static final int getPackageCount( final Class< ? > aClass ) {
		int packageCount = 0;
		
		String name = aClass.getName();
		for ( int pos = name.indexOf( '.' ); pos != -1; pos = name.indexOf( '.', pos + 1 ) ) {
			++packageCount;
		}
		
		return packageCount;
	}
	
	private static final File getClassFile( final Class< ? > aClass ) {
		String resourceName = aClass.getName().replace( '.', '/' ) + ".class";
		URL classUrl = aClass.getClassLoader().getResource( resourceName );
		try {
			return new File( classUrl.toURI() );
		} catch ( URISyntaxException e ) {
			throw new IllegalStateException( e );
		}
	}
}
