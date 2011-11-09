package net.dougqh.compile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dougqh.formatting.ByteCodeFormatting;

public final class DalvikByteCodeHelper {
	private static final Pattern HEX_RE = Pattern.compile( "0x([abcdef\\d]+)" );
	private static final Pattern HEX_T_RE = Pattern.compile( "0x([abcdef\\d]+)t" );
	
	private DalvikByteCodeHelper() {}
	
	public static final String getByteCode( final File smaliFile )
		throws IOException
	{
		StringBuilder codeBuilder = new StringBuilder();
		
		boolean isInMain = false;
		for ( SmaliScanner scanner = new SmaliScanner( smaliFile );
			scanner.hasNextLine(); )
		{
			Line line = scanner.nextLine();
			if ( ! isInMain ) {
				if ( line.isMainStart() ) {
					isInMain = true;
				}
			} else {
				if ( line.isMethodEnd() ) {
					isInMain = false;
				} else if ( ! line.isMeta() ) {
					if ( ! line.isEmpty() ) {
						codeBuilder.append( line.getCode() + "\n" );
					}
				}
			}
		}
		
		return codeBuilder.toString();
	}
	
	static final class SmaliScanner {
		private final Scanner scanner;
		
		SmaliScanner( final File dumpFile ) throws FileNotFoundException {
			this.scanner = new Scanner( dumpFile );
		}
		
		public final boolean hasNextLine() {
			return this.scanner.hasNextLine();
		}
		
		public final Line nextLine() {
			return toLineObject( this.scanner.nextLine() );
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
				numSpaces / 4,
				new String( lineChars, numSpaces, lineChars.length - numSpaces ) );
		}
	}
	
	public static final class Line {
		private final int depth;
		private final String line;
		private final String trimmedLine;
		
		Line( final int depth, final String line ) {
			this.depth = depth;
			this.line = line;
			this.trimmedLine = line.trim();
		}
		
		public final int getDepth() {
			return this.depth;
		}
		
		public final boolean isEmpty() {
			return this.line.isEmpty();
		}
		
		public final boolean isMainStart() {
			//.method public static final varargs main([Ljava/lang/String;)V
			return this.line.endsWith( "main([Ljava/lang/String;)V" );
		}
		
		public final boolean isMethodEnd() {
			return this.trimmedLine.equals( ".end method" );
		}
		
		public final boolean isMeta() {
			return this.trimmedLine.startsWith( "." );
		}
		
		public final String getCode() {
			String code = this.trimmedLine;
			code = code.replace( ";->", "." );
			code = code.replace( ", L", ", " );
			code = code.replace( "Foo$", "" );
			
			code = hexReplace( HEX_T_RE, code );
			code = hexReplace( HEX_RE, code );
			
			int colonPos = code.indexOf( ':' );
			//if the colon is the first char, it is a label
			if ( colonPos != -1 && colonPos != 1 ) {
				//if the colon follows the first space or ",", it is an argument
				//so it must be a label
				int firstSpacePos = code.indexOf( ' ' );
				boolean isLabel = ( colonPos == 0 );
				boolean isLabelArg = 
					( colonPos == firstSpacePos + 1 ) ||
					( ( colonPos - 2 > 0 ) && code.charAt( colonPos - 2 ) == ',' );
				if ( ! isLabel && ! isLabelArg ) {
					code = code.substring( 0, colonPos );
				}
			}
			
			int parenPos = code.indexOf( '(' );
			if ( parenPos != -1 ) {
				code = code.substring( 0, parenPos );
			}
			
			for ( String importedPackage: CompilationConstants.IMPORTS ) {
				code = code.replace( importedPackage + ".", "" );
				code = code.replace( importedPackage.replace( '.', '/' ) + '/', "" );
			}
			
			int numSpaces = this.depth - 1 * 2;
			numSpaces = numSpaces > 0 ? numSpaces : 0;
			char[] indent = new char[ numSpaces ];
			Arrays.fill( indent, ' ' );
			
			return new String( indent ) + code;
		}
		
		private static final String hexReplace(
			final Pattern pattern,
			final String code )
		{
			String sanitizedCode = code;
			
			//change 0x0et to just 0x0e
			//pad everything to two chars
			Matcher matcher = pattern.matcher( sanitizedCode );
			while ( matcher.find() ) {
				String match = matcher.group( 1 );
				if ( match.length() < 2 ) {
					match = "0" + match;
				}
				sanitizedCode = sanitizedCode.replace(
					matcher.group(0),
					"0x" + match );
			}
			return sanitizedCode;
		}
		
		@Override
		public final String toString() {
			return this.toString( this.depth );
		}
		
		public final String toString( final int depth ) {
			return ByteCodeFormatting.indent( depth ) + this.line + "\n";
		}
	}
}
