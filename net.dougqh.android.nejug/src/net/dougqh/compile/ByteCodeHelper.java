package net.dougqh.compile;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dougqh.formatting.ByteCodeFormatting;

final class ByteCodeHelper {
	private ByteCodeHelper() {}
	
	public static final String getByteCode( final String byteCode )
		throws DecompilationException
	{
		//Extracts the byte code of the main method
		String mainCode = null;
		CodeScanner scanner = new CodeScanner( byteCode );
		for ( Line line = scanner.nextLine();
			line != null;
			line = scanner.nextLine() )
		{
			if ( line.isMethodStart() ) {
				String code = processMethod( scanner, "main" );
				if ( code != null ) {
					mainCode = code;
				}
			}
		}
		return mainCode;
	}
	
	private static final String processMethod(
		final CodeScanner scanner,
		final String targetMethod )
	{
		String methodName = null;
		String result = null;
		for ( Line line = scanner.nextLine();
			line != null;
			line = scanner.nextLine() )
		{
			if ( line.isMethodEnd() ) {
				break;
			}
			
			if ( line.isName() ) {
				methodName = line.getName();
			} else if ( line.isAttributeStart() ) {
				String code = processAttribute( scanner );
				if ( code != null && methodName.equals( targetMethod ) ) {
					result = code;
				}
			}
		}
		return result;
	}
	
	private static final String processAttribute( final CodeScanner scanner )
	{
		String attributeName = null;
		StringBuilder codeBuilder = new StringBuilder();
		
		for ( Line line = scanner.nextLine();
			line != null;
			line = scanner.nextLine() )
		{
			if ( line.isAttributeEnd() && line.getDepth() == 1 ) {
				break;
			} else if ( line.getDepth() > 2 ) {
				continue;
			}
			
			if ( line.isName() ) {
				attributeName = line.getName();
			} else if ( line.isCode() ) {
				codeBuilder.append( line.getCode() + "\n" );
			}
		}
		
		if ( "Code".equals( attributeName ) ) {
			return codeBuilder.toString();
		} else {
			return null;
		}
	}
	
	private static final class CodeScanner {
		private final Scanner scanner;
		
		CodeScanner( final String dump ) {
			this.scanner = new Scanner( dump );
		}
		
		public final Line nextLine() {
			while ( this.scanner.hasNextLine() ) {
				String line = this.scanner.nextLine();
				if ( ! line.trim().isEmpty() ) {
					return toLineObject( line );
				}
			}
			return null;
		}
		
		private static final Line toLineObject( final String line ) {
			char[] lineChars = line.toCharArray();
			int numSpaces = 0;
			for ( int i = 0; i < lineChars.length; ++i ) {
				if ( lineChars[ i ] == ' ' ) {
					++numSpaces;
				} else {
					break;
				}
			}
			return new Line(
				numSpaces / 2,
				new String( lineChars, numSpaces, lineChars.length - numSpaces ) );
		}
	}
	
	public static final class Line {
		private static final Pattern METHOD_START_RE = Pattern.compile( "^methods\\[\\d+\\]\\:$" );
		private static final Pattern METHOD_END_RE = Pattern.compile( "^end methods\\[\\d+\\]$" );
		private static final Pattern ATTRIBUTE_START_RE = Pattern.compile( "^attributes\\[\\d+\\]\\:$" );
		private static final Pattern ATTRIBUTE_END_RE = Pattern.compile( "^end attributes\\[\\d+\\]$" );
		
//		private static final Pattern UNNEEDED_COMMENT_PATTERN = Pattern.compile( "\\_\\d \\/\\/ 0x" );

		private final int depth;
		private final String line;
		
		public Line( final int depth, final String line ) {
			this.depth = depth;
			this.line = line;
		}
		
		public final int getDepth() {
			return this.depth;
		}
		
		public final String getLine() {
			return this.line;
		}
		
		public final boolean isMethodStart() {
			return METHOD_START_RE.matcher( this.line ).matches();
		}
		
		public final boolean isMethodEnd() {
			return METHOD_END_RE.matcher( this.line ).matches();
		}
		
		public final boolean isAttributeStart() {
			return ATTRIBUTE_START_RE.matcher( this.line ).matches();
		}
		
		public final boolean isAttributeEnd() {
			return ATTRIBUTE_END_RE.matcher( this.line ).matches();
		}
		
		public final boolean isName() {
			return "name".equals( this.getKey() );
		}
		
		public final String getName() {
			return this.getValue();
		}

		public final boolean isCode() {
			try {
				Integer.parseInt( this.getKey(), 10 );
				return true;
			} catch ( NumberFormatException e ) {
				return false;
			}
		}
		
		public final String getCode() {
			String code = this.getValue();
			code = code.replace( "type{", "" );
			code = code.replace( "field{", "" );
			code = code.replace( "string{", "" );
			code = code.replace( "method{", "" );
			code = code.replace( "ifaceMethod{", "" );
			code = code.replace( "}", "" );
			code = code.replace( "Foo$", "" );
			int colonPos = code.indexOf( ':' );
			if ( colonPos != -1 ) {
				code = code.substring( 0, colonPos );
			}
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
		
		public final String getKey() {
			int colonPos = this.line.indexOf( ':' );
			if ( colonPos == -1 ) {
				return null;
			} else {
				return this.line.substring( 0, colonPos );
			}
		}
		
		public final String getValue() { 
			int colonPos = this.line.indexOf( ':' );
			if ( colonPos == -1 || colonPos == this.line.length() - 1 ) {
				return null;
			} else {
				return this.line.substring( colonPos + 1 ).trim();
			}
		}
		
		public final String toString() {
			return ByteCodeFormatting.indent( this.depth ) + this.line + "\n";
		}
	}
}
