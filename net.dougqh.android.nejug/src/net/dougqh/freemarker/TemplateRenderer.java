package net.dougqh.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public final class TemplateRenderer {
	private final Configuration config;
	
	public TemplateRenderer( final Class< ? > aClass )
		throws IOException
	{
		this( createConfiguration( aClass ) );
	}
	
	private static final Configuration createConfiguration(
		final Class< ? > aClass )
		throws IOException
	{
		Configuration config = new Configuration();
		config.setClassForTemplateLoading( aClass, "" );
		return config;
	}
	
	public TemplateRenderer( final Configuration config ) {
		this.config = config;
	}
	
	public final void render(
		final Writer writer,
		final String page, 
		final Map< ? extends String, ? extends Object > vars )
		throws TemplateException, IOException
	{
		this.config.getTemplate( page ).process( vars, writer );
	}
}
