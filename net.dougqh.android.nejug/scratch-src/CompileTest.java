import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompileTest {
	public static final void main( final String... args )
		throws Exception
	{
//		Compiler compiler = new Compiler( new File( "temp" ) );
//		CompilationResult result = compiler.compile( new Code( "", "int[] x = {20, 30, 40};" ) );
//		System.out.println( result.getByteCode() );
//		System.out.println( result.getDalvikByteCode() );
		
		Pattern pattern = Pattern.compile( "_\\d // 0x" );
		Matcher matcher = pattern.matcher( "iconst_4 // 0x04" );
		System.out.println( matcher.matches() );
	}
}
