package net.dougqh.android.nejug;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.dougqh.compile.Code;
import net.dougqh.compile.CompilationException;
import net.dougqh.compile.CompilationResult;
import net.dougqh.compile.Compiler;
import net.dougqh.compile.DecompilationException;
import net.dougqh.formatting.ByteCodeFormatting;
import net.dougqh.freemarker.TemplateRenderer;

import com.google.gson.Gson;

final class CompileHandler extends DemoHandler {
	private static final Code[] CODE_EXAMPLES = {
		new Code( "Hello World",
			"System.out.println(\"Hello World\");" ),
		new Code( "Array",
			"int[] xs = {20, 30, 0, 50};" ),
		new Code( "Invoke Static",
			"Math.max(10, 20);" ),
//		new Code( "Invoke Interface",
//			"AnInterface anObject = null;\n" +
//			"anObject.voidMethod();" ),
//		new Code( "Invoke Virtual",
//			"AClass anObject = null;\n" +
//			"anObject.voidMethod();" ),
		new Code( "New",
			"BigDecimal x = new BigDecimal(\"2.0\");" )
	};
	
	private final Compiler compiler = new Compiler( new File( "temp" ) );
	
	CompileHandler( final String pattern, final TemplateRenderer renderer ) {
		super( pattern, renderer );
	}

	@Override
	protected final void handleGet(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException
	{
		Map< String, Object > vars = new HashMap< String, Object >( 4 );
		vars.put( "codeExamples", CODE_EXAMPLES );
		
		this.render( httpRequest, httpResponse, "compile.ftl", vars );
	}
	
	@Override
	protected final void handlePost(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException
	{
		try {
			String code = httpRequest.getParameter( "code" );
			if ( code == null || code.trim().equals( "" ) ) {
				throw new ServletException( "No 'code' provided" );
			}
			
			boolean optimizeDex = ( httpRequest.getParameter( "optimizeDex" ) != null );
			
			CompilationResult compilationResult = 
				this.compiler.compile( new Code( "", code ), optimizeDex );
			
			httpResponse.setStatus( 200 );
			httpResponse.setContentType( "application/json" );
			
			Gson gson = new Gson();
			httpResponse.getWriter().print(
				gson.toJson( new JsonCompilation( compilationResult ) ) );
		} catch ( CompilationException e ) {
			throw new ServletException( e );
		} catch ( DecompilationException e ) {
			throw new ServletException( e );
		}
	}
	
	public static final class JsonCompilation {
		public final String byteCode;
		public final String dalvikCode;
		
		public JsonCompilation( final CompilationResult result )
			throws DecompilationException
		{
			this.byteCode = ByteCodeFormatting.format( result.getByteCode(), 20 );
			this.dalvikCode = ByteCodeFormatting.format( result.getDalvikByteCode(), 20 );
		}
	}
}
