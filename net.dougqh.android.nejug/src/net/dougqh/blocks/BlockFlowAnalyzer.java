package net.dougqh.blocks;

import java.io.File;
import java.io.IOException;

import net.dougqh.compile.Code;
import net.dougqh.compile.CompilationException;
import net.dougqh.compile.Compiler;

import com.android.dx.command.dexer.AndroidDexer;
import com.android.dx.command.dexer.DumpType;

public final class BlockFlowAnalyzer {
	private final Compiler compiler;
	private final File tempDir;
	
	public BlockFlowAnalyzer( final File tempDir ) {
		this.compiler = new Compiler( tempDir );
		this.tempDir = tempDir;
	}
	
	public final BlocksGraph createFlowGraph( final Code code )
		throws CompilationException
	{
		File dumpFile = AndroidDexer.dexDump(
			this.compiler.compile( code ),
			DumpType.BASIC_BLOCKS,
			this.tempDir );
		
		try {
			return BlocksParser.parseBlocks( dumpFile ).simplify();
		} catch ( IOException e ) {
			throw new CompilationException( e );
		}
	}
}
