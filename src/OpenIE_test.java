import java.io.File;

public class OpenIE_test {
	
	public static void main(String[] args) {
		String path = "D:/Downloads/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/data/AFP_ENG_200701/AFP_ENG_20070101.0001.LDC2009T13";
		
		OpenIE ie = new OpenIE();
		ie.extract(path);
		
	}
	
}
