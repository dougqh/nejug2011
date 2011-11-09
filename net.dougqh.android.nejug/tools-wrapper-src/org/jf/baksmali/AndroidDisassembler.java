package org.jf.baksmali;

import java.io.File;
import java.io.IOException;

import org.jf.dexlib.DexFile;

public class AndroidDisassembler {
	public static final File dissasemble(
		final File dexFile,
		final File outputDir )
		throws IOException
	{		
		String fileName = dexFile.getName();
		//remove .dex suffix
		String bareName = fileName.substring( 0, fileName.length() - 4 );
	
		baksmali.disassembleDexFile(
			dexFile.getAbsolutePath(), //dexFilePath
			new DexFile( dexFile ), //dexFile
			false, //deodex
			outputDir.getAbsolutePath(), //outputDir
			new String[] {}, //classPathDirs
			null, //bootClassPath
			null, //extraBootClassPath
			false, //noParameterRegisters
			false, //useLocalsDirective
			false, //useSequentialLabels
			true, //outputDebugInfo
			false, //addCodeOffsets
			false, //noAccessorComments
			0, //registerInfo
			false, //verify
			false ); //ignoreErrors

		return new File( outputDir, bareName + ".smali" );
	}
}
