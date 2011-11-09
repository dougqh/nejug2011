package net.dougqh.blocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import net.dougqh.compile.CompilationConstants;


public final class BlocksParser {
	private BlocksParser() {}
	
	public static final BlocksGraph parseBlocks( final File file )
		throws IOException
	{
		return parseBlocks( file, "method main ([Ljava/lang/String;)V" );
	}
	
	public static final BlocksGraph parseBlocks(
		final File file,
		final String methodSig )
		throws IOException
	{
		LinkedHashMap< String, Block > blocks = new LinkedHashMap< String, Block >();
		
		boolean insideMethod = false;
		Block currentBlock = null;
		for ( DumpScanner scanner = new DumpScanner( file );
			scanner.hasNextLine(); )
		{
			Line line = scanner.nextLine();
			if ( ! insideMethod ) {
				if ( line.isMethodStart( methodSig ) ) {
					insideMethod = true;
				}
			} else {
				if ( line.isMethodStart() ) {
					addBlock( blocks, currentBlock );
					insideMethod = false;
				}
				
				if ( line.isBlockStart() ) {
					addBlock( blocks, currentBlock );
					currentBlock = new Block( line.getBlockId() );
				} else if ( line.isNext() ) {
					currentBlock.addNext( line.getNextBlockId() );
				} else {
					currentBlock.addCode( line );
				}
			}
		}
		if ( insideMethod ) {
			addBlock( blocks, currentBlock );
		}
		
		return new BlocksGraph( blocks );
	}
	
	private static final void addBlock(
		final Map< ? super String, ? super Block > blocks,
		final Block block )
	{
		if ( block != null ) {
			blocks.put( block.getId(), block );
		}
	}
	
	private static final class DumpScanner {
		private final Scanner scanner;
		
		public DumpScanner( final File file )
			throws FileNotFoundException
		{
			this.scanner = new Scanner( file );
		}
		
		public final boolean hasNextLine() {
			return this.scanner.hasNextLine();
		}
		
		public final Line nextLine() {
			return new Line( this.scanner.nextLine() );
		}
	}
	
	static final class Line {
		private final String line;
		private final String trimmedLine;
		
		public Line( final String line ) {
			this.line = line;
			this.trimmedLine = line.trim();
		}
		
		public final boolean isMethodStart() {
			return this.line.startsWith( "method " );
		}
		
		public final boolean isMethodStart( final String sig ) {
			return this.line.equals( sig );
		}
		
		public final boolean isBlockStart() {
			return this.line.startsWith( "block " );
		}
		
		public final String getBlockId() {
			int colonPos = this.line.indexOf( ':' );
			String blockStart = this.line.substring( 0, colonPos );
			return blockStart.substring( 6 );
		}
		
		public final boolean isNext() {
			return this.trimmedLine.startsWith( "next " );
		}
		
		public final String getNextBlockId() {
			return this.trimmedLine.substring( 5 );
		}
		
		public final boolean isReturn() {
			return this.line.contains( "return" );
		}
		
		public final boolean isJump() {
			return this.line.contains( "if" ) || this.line.contains( "goto" );
		}
		
		public final String getCode() {
			String code = this.trimmedLine;
			
			int colonPos = code.indexOf( ':' );
			if ( colonPos != -1 ) {
				code = code.substring( colonPos + 2 );
			}
			
			code = code.replace( "type{", "" );
			code = code.replace( "field{", "" );
			code = code.replace( "string{", "" );
			code = code.replace( "method{", "" );
			code = code.replace( "ifaceMethod{", "" );
			code = code.replace( "}", "" );
			code = code.replace( "Foo$", "" );
			
			for ( String importedPackage: CompilationConstants.IMPORTS ) {
				code = code.replace( importedPackage + ".", "" );
				code = code.replace( importedPackage.replace( '.', '/' ) + '/', "" );
			}
			code = code.replace( "#+", "0x" );
			
//			if ( UNNEEDED_COMMENT_PATTERN.matcher( code ).matches() ) {
//				System.out.println( code );
//				code = code.substring( 0, code.indexOf( "//" ) );
//			}
			
			return code;
		}
	}
}
