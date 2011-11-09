package com.android.dx.command.dexer;

import java.io.File;

public final class AndroidDexer {
	private AndroidDexer() {}
	
	public static final File dex(
		final File classFile,
		final boolean optimize,
		final File outputDir )
	{
		String fileName = classFile.getName();
		//remove .class suffix
		String bareName = fileName.substring( 0, fileName.length() - 6 );
		
		File outputFile = new File( outputDir, bareName + ".dex" );
		
		Main.Arguments args = new Main.Arguments();
		args.parse( new String[] { 
			"--output=" + outputFile.getAbsolutePath(),
			classFile.getAbsolutePath()
		} );
		args.optimize = optimize;
		args.strictNameCheck = false;
		
		Main.run( args );
		
		return outputFile;
	}
	
	public static final File dexDump(
		final File classFile,
		final DumpType dumpType,
		final File outputDir )
	{
		//DQH - Not functioning properly
		
		String fileName = classFile.getName();
		//remove .class suffix
		String bareName = fileName.substring( 0, fileName.length() - 6 );
		
		File outputFile = new File( outputDir, bareName + "." + dumpType.ext );
		
		Main.Arguments args = new Main.Arguments();
		args.parse( new String[] {
			//"--dump", 
			dumpType.dexId,
			"--dump-to=" + outputFile.getAbsolutePath(),
			classFile.getAbsolutePath()
		} );
		
		Main.run( args );
		
		return outputFile;		
	}
}
