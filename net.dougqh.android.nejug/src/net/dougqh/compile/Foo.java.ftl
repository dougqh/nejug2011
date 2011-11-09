[#ftl]
[#list imports as import]
import ${import}.*;
[/#list]

public final class Foo {
	public static final void main( final String... args ) {
		${code}
	}
	
	public interface AnInterface {
		public abstract void voidMethod();
		
		public abstract int intMethod();
	}
	
	public abstract class AClass implements AnInterface {
		public void voidMethod() {
		}
		
		public int intMethod() {
			return 2;
		}
	}
}