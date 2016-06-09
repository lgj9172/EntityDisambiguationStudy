import java.io.File;

public class OpenIE_test {
	
	public static void main(String[] args) {
		//String path = "AFP_ENG_20070101.0001.LDC2009T13";
		String path = "/Users/sojungpark/Documents/tac/12";
		
		//get candidate triples
		OpenIE ie = new OpenIE();
		ie.extract(path);
		
		
		
	}
	
}
