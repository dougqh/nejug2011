package net.dougqh.android.nejug;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.dougqh.freemarker.TemplateRenderer;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.DefaultHandler;

import com.google.gson.Gson;

import freemarker.template.TemplateException;

public abstract class DemoHandler extends DefaultHandler {
	private final String path;
	private final TemplateRenderer renderer;

	DemoHandler( final String path, final TemplateRenderer renderer ) {
		this.path = path;
		this.renderer = renderer;
	}
	
	@Override
	public final void handle(
		final String path,
		final Request request,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException
	{
		if ( ! path.equals( this.path ) ) {
			return;
		}
		
		if ( request.getMethod().equals( "GET" ) ) {
			this.handleGet(
				path,
				httpRequest,
				httpResponse );
		} else if ( request.getMethod().equals( "POST" ) ) {
			this.handlePost(
				path,
				httpRequest,
				httpResponse );			
		}
		request.setHandled( true );
	}
	
	protected abstract void handleGet(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException;
	
	protected abstract void handlePost(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException;

	protected final void render(
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse,
		final String template )
		throws IOException, ServletException
	{
		this.render(
			httpRequest,
			httpResponse,
			template,
			Collections.< String, Object >emptyMap() );
	}

	protected final void render(
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse,
		final String template,
		final Map< ? extends String, ? extends Object > vars )
		throws IOException, ServletException
	{
		HashMap< String, Object > allVars = new HashMap< String, Object >( vars );
		allVars.put( "path", httpRequest.getPathInfo() );
		
		httpResponse.setStatus( 200 );
		httpResponse.setContentType( "text/html" );
		try {
			this.renderer.render(
				httpResponse.getWriter(),
				template,
				allVars );
		} catch ( TemplateException e ) {
			throw new ServletException( e );
		}
	}
	
	protected final void renderJson(
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpRepsonse,
		final Object object )
		throws IOException
	{
		httpRepsonse.setStatus( 200 );
		httpRepsonse.setContentType( "application/json" );
		
		Gson gson = new Gson();
		httpRepsonse.getWriter().print( gson.toJson( object ) );
	}
}
