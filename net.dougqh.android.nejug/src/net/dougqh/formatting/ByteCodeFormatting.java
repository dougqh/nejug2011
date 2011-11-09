package net.dougqh.formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public final class ByteCodeFormatting {
	private static final int SPACES_PER_INDENT = 2;
	
	private ByteCodeFormatting() {}
	
	public static final String indent() {
		return indent( 1 );
	}
	
	public static final String indent( final int depth ) {
		if ( depth < 0 ) {
			return "";
		} else {
			char[] indent = new char[ depth * SPACES_PER_INDENT ];
			Arrays.fill( indent, ' ' );
			return new String( indent );
		}
	}
	
	public static final String format(
		final String code,
		final int lineLimit )
	{
		StringBuilder builder = new StringBuilder( code.length() * 2 );
		for ( Scanner scanner = new Scanner( code );
			scanner.hasNextLine(); )
		{
			String line = scanner.nextLine();
			if ( ! isReturn( line ) ) {
				for ( String formattedLine: wrapLine( line, lineLimit ) ) {
					builder.append( formattedLine + "\n" );
				}
			}
		}
		return builder.toString();
	}
	
	private static final boolean isReturn( final String line ) {
		return line.trim().startsWith( "return" );
	}
	
	public static final List< String > wrapLine(
		final String line,
		final int lineLimit )
	{
		if ( line.length() > lineLimit ) {
			String[] parts = line.split( " ", 2 );
			
			ArrayList< String > lines = new ArrayList< String >( 4 );
			lines.add( parts[ 0 ] );
			if ( parts[ 1 ].length() + SPACES_PER_INDENT > lineLimit ) {
				lines.addAll( argSplit( parts[ 1 ] ) );
			} else {
				lines.add( indent() + parts[ 1 ] );
			}
			return lines;
		} else {
			return Collections.singletonList( line );
		}
	}
	
	private static final List< String > argSplit( final String argList ) {
		ArrayList< String > args = new ArrayList< String >( 4 );
		
		int lastPos;
		int pos;
		for ( lastPos = 0, pos = argPos( argList, 0 );
			pos != -1;
			lastPos = pos + 1, pos = argPos( argList, pos + 1 ) )
		{
			String line = argList.substring( lastPos, pos + 1 ).trim();
			args.add( indent() + line );
		}
		String lastLine = argList.substring( lastPos ).trim();
		args.add( indent() + lastLine );
		
		return args;
	}
	
	private static final int argPos(
		final String argList,
		final int pos )
	{
		int complexCommaPos = argList.indexOf( "},", pos );
		if ( complexCommaPos != -1 ) {
			return complexCommaPos + 1;
		} else {
			return argList.indexOf( ',', pos );
		}
	}
}
