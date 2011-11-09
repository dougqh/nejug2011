package com.android.dx.command.dexer;

public enum DumpType {
	DEBUG( "--debug", "dbg" ),
	STRICT( "--strict", "strict" ),
	BYTES( "--bytes", "bytes" ),
	OPTIMIZE( "--optimize", "opt" ),
	BASIC_BLOCKS( "--basic-blocks", "blocks" ),
	ROP_BLOCKS( "--rop-blocks", "ropblocks" ),
	SSA_BLOCKS( "--ssa-blocks", "ssablocks" ),
	DOT( "--dot", "dot" ),
	SSA_STEP( "--ssa-step", "ssasteps" );
	
	final String dexId;
	final String ext;
	
	DumpType( final String dexId, final String ext ) {
		this.dexId = dexId;
		this.ext = ext;
	}
}