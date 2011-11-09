package net.dougqh.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Block {
	private final String id;
	private final StringBuilder codeBuilder = new StringBuilder();
	private final List< String > nextIds = new ArrayList< String >( 2 );

	private boolean isEntry;
	private boolean isExit;
	private boolean isJump;
	
	public Block( final String id ) {
		this.id = id;
	}
	
	public Block( final Block blockA, final Block blockB ) {
		this.id = blockA.id;
		
		this.isEntry = blockA.isEntry;
		this.isExit = blockB.isExit;
		this.isJump = blockA.isJump || blockB.isJump;
		
		this.codeBuilder.append( blockA.codeBuilder );
		this.codeBuilder.append( blockB.codeBuilder );
		this.nextIds.addAll( blockB.nextIds );
	}
	
	public final Block addNext( final String nextId ) {
		this.nextIds.add( nextId );
		return this;
	}
	
	public final String getId() {
		return this.id;
	}
	
	public final List< String > getNextIds() {
		return Collections.unmodifiableList( this.nextIds );
	}
	
	public final String getCode() {
		return this.codeBuilder.toString();
	}
	
	public final boolean isJump() {
		return this.isJump;
	}
	
	public final boolean isEntry() {
		return this.isEntry;
	}
	
	public final boolean isExit() {
		return this.isExit;
	}
	
	protected final Block markEntry() {
		this.isEntry = true;
		return this;
	}
	
	protected final Block markExit() {
		this.isExit = true;
		return this; 
	}
	
	protected final Block addCode( final BlocksParser.Line line ) {
		if ( line.isJump() ) {
			this.isJump = true;
		}
		this.codeBuilder.append( line.getCode() + "\n" );
		return this;
	}
	
	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "id: " + this.id ).append( " " );
		builder.append( "next ids: " + this.nextIds );
		builder.append( "\n" );
		
		String code = this.getCode();
		code = code.substring( 0, code.length() - 1 );
		builder.append( "\t" + code.replace( "\n", "\n\t" ) + "\n" );
		return builder.toString();
	}
}