package net.dougqh.compile;

public final class Code {
	private final String name;
	private final String code;
	
	public Code( final String name, final String code ) {
		this.name = name;
		this.code = code;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final String getCode() {
		return this.code;
	}
}
