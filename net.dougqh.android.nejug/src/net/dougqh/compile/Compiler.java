package net.dougqh.compile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.android.dx.command.dexer.AndroidDexer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public final class Compiler {
	private static final Configuration CONFIG = createConfig();
	
	private static final Configuration createConfig() {
		Configuration config = new Configuration();
		config.setClassForTemplateLoading( Compiler.class, "" );
		return config;
	}
	
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	
	private final File tempDir;
	
	public Compiler( final File tempDir ) {
		this.tempDir = tempDir;
	}
	
	public final File compile( final Code code ) throws CompilationException {
		StandardJavaFileManager fileManager = 
			this.compiler.getStandardFileManager( null, null, null );
		
		try {
			Iterable< ? extends JavaFileObject > sourceObjects = 
				createSourceFile( fileManager, code.getCode() );
				
			CompilationTask task = 
				this.compiler.getTask( null, fileManager, null, null, null, sourceObjects );
			task.call();
			
			return new File( this.tempDir, "Foo.class" );
		} finally {
			try {
				fileManager.close();
			} catch ( IOException e ) {
				throw new CompilationException( e );
			}
		}
	}
	
	public final CompilationResult compile(
		final Code code,
		final boolean optimizeDex )
		throws CompilationException
	{
		File classFile = this.compile( code );
			
		File dexFile = AndroidDexer.dex( classFile, optimizeDex, this.tempDir );
		
		return new CompilationResult( classFile, dexFile );
	}

	private final Iterable< ? extends JavaFileObject > createSourceFile(
		final StandardJavaFileManager fileManager,
		final String code )
		throws CompilationException
	{
		File outputFile = new File( this.tempDir, "Foo.java" );
		
		Map< String, Object > vars = new HashMap< String, Object >( 4 );
		vars.put( "imports", CompilationConstants.IMPORTS );
		vars.put( "code", code );

		try {
			Template template = CONFIG.getTemplate( "Foo.java.ftl" );
			
			FileWriter writer = new FileWriter( outputFile );
			try {
				template.process( vars, writer, null );
			} finally {
				writer.close();
			}
		} catch ( IOException e ) {
			throw new CompilationException( e );
		} catch ( TemplateException e ) {
			throw new CompilationException( e );
		}
		
		return fileManager.getJavaFileObjects( outputFile );
	}
}
