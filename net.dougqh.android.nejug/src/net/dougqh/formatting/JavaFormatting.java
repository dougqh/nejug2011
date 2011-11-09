package net.dougqh.formatting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;

public final class JavaFormatting {
	private JavaFormatting() {}
	
	public static final String format( final String code ) {
		StringBuilder builder = new StringBuilder();
		
		for ( Scanner scanner = new Scanner( code );
			scanner.hasNextLine(); )
		{
			String line = scanner.nextLine();
			if ( line.contains( "import " ) ) {
				continue;
			} else if ( line.contains( "package" ) ) {
				continue;
			}
			line = line.replace( "\t", "  " );
			line = line.replace( "this.", "" );
			
			builder.append( line + "\n" );
		}
		return builder.toString().trim();
	}
	
	public static final String getSoure(
		final File srcDir,
		final Class< ? > aClass )
		throws IOException
	{
		File javaFile = new File(
			srcDir,
			aClass.getName().replace( '.', '/' ) + ".java" );
			
		FileReader reader = new FileReader( javaFile );
		try {
			StringWriter writer = new StringWriter();
			try {
				char[] buff = new char[ 256 ];
				for ( int numRead = reader.read( buff );
					numRead > 0;
					numRead = reader.read( buff ) )
				{
					writer.write( buff, 0, numRead );
				}
			} finally {
				writer.close();
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}
}
