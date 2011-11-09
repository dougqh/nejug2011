package net.dougqh.blocks;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public final class BlocksGraph {
	private final LinkedHashMap< String, Block > blocks;
	
	BlocksGraph( final LinkedHashMap< String, Block > blocks ) {
		this.blocks = blocks;
	}
	
	public final Block get( final String id ) {
		return this.blocks.get( id );
	}

	public final Collection< Block > getBlocks() {
		return Collections.unmodifiableCollection( this.blocks.values() );
	}
	
	public final BlocksGraph simplify() {
		return new BlocksGraph( new GraphSimplifier( this.blocks ).consolidate() );
	}
}
