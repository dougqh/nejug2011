package net.dougqh.android.nejug;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.dougqh.blocks.Block;
import net.dougqh.freemarker.TemplateRenderer;


public final class BlocksHandler extends DemoHandler {
	//private static final Block
	
	public BlocksHandler(
		final String path,
		final TemplateRenderer renderer )
	{
		super( path, renderer );
	}
	
	@Override
	protected final void handleGet(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException, ServletException
	{
		HashMap< String, Object > vars = new HashMap< String, Object >();
		vars.put( "blockExamples", Collections.emptyList() );
				
		this.render( httpRequest, httpResponse, "blocks.ftl", vars );
	}
	
	protected final void handlePost(
		final String path,
		final HttpServletRequest httpRequest,
		final HttpServletResponse httpResponse )
		throws IOException ,ServletException
	{
		HashMap< String, Block > blocks = new HashMap< String, Block >();
		
	}
	
	private static final class JsonBlock {
		public final String id;
		public final String code;
		public final List< String > nextIds;
		public final boolean entry;
		public final boolean exit;
		
		public JsonBlock( final Block block ) {
			this.id = block.getId();
			this.code = block.getCode();
			this.nextIds = block.getNextIds();
			this.entry = block.isEntry();
			this.exit = block.isExit();
		}
	}
}
