package com.android.dx.command.dump;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

/*
 * Cheating visibility by creating helper class in com.android.dx.command.dump
 * package to get access to the Args class.  I could do this with reflection,
 * but this seems slightly cleaner.
 */
public final class AndroidClassDumper {
	private AndroidClassDumper() {}
	
	public static final void dump(
		final File file, 
		final PrintStream out )
		throws IOException
	{
		Args args = new Args();
		args.debug = true;
		
		ClassDumper.dump(
			getBytes( file ),
			out,
			file.getAbsolutePath(),
			args );
	}
		
	public static final byte[] getBytes( final File file )
		throws IOException
	{
		FileInputStream in = new FileInputStream( file );
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				byte[] buf = new byte[ 256 ];
				for ( int numRead = in.read( buf );
					numRead != -1;
					numRead = in.read( buf ) )
				{
					out.write( buf, 0, numRead );
				}
				
				return out.toByteArray();
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}
}
