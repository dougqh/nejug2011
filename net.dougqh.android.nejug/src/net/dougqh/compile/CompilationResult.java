package net.dougqh.compile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.jf.baksmali.AndroidDisassembler;

import com.android.dx.command.dump.AndroidClassDumper;

public final class CompilationResult {
	private final File classFile;
	private final File dexFile;
	
	public CompilationResult(
		final File classFile,
		final File dexFile )
	{
		this.classFile = classFile;
		this.dexFile = dexFile;
	}
	
	public final String getByteCode() throws DecompilationException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream( out );
		try {
			try {
				AndroidClassDumper.dump( classFile, printStream );
			} finally {
				printStream.close();
			}
		} catch ( IOException e ) {
			throw new DecompilationException( e );
		}
		return ByteCodeHelper.getByteCode( out.toString() );
	}
	
	public final String getDalvikByteCode() throws DecompilationException {
		try {
			File smaliFile = AndroidDisassembler.dissasemble(
				this.dexFile,
				this.dexFile.getParentFile() );
			
			return DalvikByteCodeHelper.getByteCode( smaliFile );
		} catch ( IOException e ) {
			throw new DecompilationException( e );
		}
	}
}
