package net.dougqh.blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class GraphSimplifier {
	//Typed as a LinkedHashMap because the algorithm only works properly
	//with a Map constructed and traversed in the proper order.
	private final LinkedHashMap< ? extends String, ? extends Block > originalBlocks;
	private final Map< ? extends String, ? extends Integer > incomingCounts;
	
	GraphSimplifier( final LinkedHashMap< ? extends String, ? extends Block > blocks ) {
		this.originalBlocks = blocks;
		
		HashMap< String, Integer > incomingCounts = new HashMap< String, Integer >();
		for ( Block block : blocks.values() ) {
			for ( String nextId : block.getNextIds() ) {
				Integer incomingCount = incomingCounts.get( nextId );
				incomingCounts.put( nextId, incomingCount == null ? 1 : incomingCount + 1 );
			}
		}
		this.incomingCounts = incomingCounts;
	}
	
	public final LinkedHashMap< String, Block > consolidate() {
		HashSet< String > processedIds = new HashSet< String >( this.originalBlocks.size() );
		
		LinkedHashMap< String, Block > consolidatedBlocks =
			new LinkedHashMap< String, Block >( this.originalBlocks.size() );
		
		for ( Map.Entry< ? extends String, ? extends Block > entry :
			this.originalBlocks.entrySet() )
		{
			String id = entry.getKey();
			Block block = entry.getValue();
			
			if ( processedIds.contains( id ) ) continue;
			
			Block mergedBlock = this.tryMerge( processedIds, block );
			consolidatedBlocks.put( mergedBlock.getId(), mergedBlock );
		}
		
		return consolidatedBlocks;
	}
	
	private final Block tryMerge(
		final Set< String > processedIds,
		final Block block )
	{
		processedIds.add( block.getId() );
		
		//don't consolidate jumps at either end, so they stand out
		if ( block.isJump() ) {
			return block;
		}
	
		//Cannot merge -- if it would merge into several trailing blocks
		List< String > nextIds = block.getNextIds();
		if ( nextIds.size() != 1 ) {
			return block;
		}
		
		String nextId = nextIds.get( 0 );
		//Cannot merge the next block if more than one block connects to it
		Integer incomingCount = this.incomingCounts.get( nextId );
		if ( incomingCount == null || incomingCount > 1 ) {
			return block;
		}
		
		Block nextBlock = this.originalBlocks.get( nextId );
		
		//don't consolidate jumps at either end, so they stand out
		if ( nextBlock.isJump() ) {
			return block;
		}
		
		processedIds.add( nextId );
		return tryMerge(
			processedIds,
			new Block( block, nextBlock ) );
	}
}