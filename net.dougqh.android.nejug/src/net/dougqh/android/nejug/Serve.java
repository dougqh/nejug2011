package net.dougqh.android.nejug;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import net.dougqh.freemarker.TemplateRenderer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public final class Serve {
	public static final void main( final String... args ) throws Exception {
		TemplateRenderer renderer = new TemplateRenderer( CompileHandler.class );
		
		HandlerCollection handlers = new HandlerCollection();
		handlers.addHandler( new CompileHandler( "/compile", renderer ) );
		handlers.addHandler( new BenchmarksHandler( "/benchmarks", renderer ) );
		handlers.addHandler( new BlocksHandler( "/blocks", renderer) );
		handlers.addHandler( resourceHandler( new File( "static" ) ) );
		
		Server server = new Server( 8080 );
		server.setHandler( handlers );
		server.start();
	}
	
	private static final Handler resourceHandler( final File dir )
		throws IOException
	{
		if ( ! dir.isDirectory() ) {
			throw new IllegalStateException( "Invalid directory " + dir.getAbsolutePath() );
		}
		try {
			ResourceHandler handler = new ResourceHandler();			
			handler.setBaseResource( Resource.newResource( dir ) );
			return handler;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e );
		}
	}
}
