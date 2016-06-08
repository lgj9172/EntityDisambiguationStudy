import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentParserForLDA
{
	public String sourcePath = null;
	public String targetPath = null;
	
	public void setSourcePath(String sourcePath)
	{
		this.sourcePath = sourcePath;
	}
	
	public void setTargetPath(String targetPath)
	{
		this.targetPath = targetPath;
	}
	
	public void convertFile()
	{
		// 파일 위치가 지정되어있지 않을때에는 그냥 종료함
		if( (this.sourcePath==null) || (this.targetPath==null) )
		{
			System.out.println("변환 할 파일의 위치 또는 변환 된 파일의 저장 위치가 지정되지 않았습니다.");
			System.out.println("파일을 변환할 수 없었습니다.");
			return;
		}
		
		// 파일을 연결함
		File sourceFile = new File(this.sourcePath);
		File targetFile = new File(this.targetPath);
		
		// 소스 파일을 불러옴(html 방식 문서임)
		ArrayList<String> sentences = new ArrayList<String>();
		XPath xpath = XPathFactory.newInstance().newXPath();
		try
		{
			// 소정씨 코드 복붙
			InputSource is = new InputSource(new FileReader(sourceFile));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			String expression = "//*/P"; //it can be changed according to corpora
			NodeList cols = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
			for(int i=0; i<cols.getLength(); i++ )
			{
				String str = cols.item(i).getTextContent(); //이것이 맞능가
				sentences.add(str);
			}
			// 테스트
			System.out.println(sentences);
			
			// 문장들에 자연어 처리기 적용해야 함
			
			// 자연어 처리기 적용한 내용을 한줄로 만들어서 저장해야 함
			
			// 저장하고 카운터 올리고 수정하고 파일 닫음
		}
		catch (Exception e)
		{
			//System.out.println("변환 할 파일의 위치가 잘못되었거나 정상적인 파일이 아닙니다.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		String sourcePath = "D:/Downloads/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/data/AFP_ENG_200701/AFP_ENG_20070101.0001.LDC2009T13";
		String targetPath = "./newdocs.dat";
		DocumentParserForLDA dp = new DocumentParserForLDA();
		
		dp.setSourcePath(sourcePath);
		dp.setTargetPath(targetPath);
		dp.convertFile();
	}
}
