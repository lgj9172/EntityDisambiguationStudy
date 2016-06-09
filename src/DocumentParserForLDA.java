import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class DocumentParserForLDA
{
	// 파일에서 로드한 내용들이 이곳에 저장됨 (파일이름, ArrayList 형태)
	public HashMap<String, ArrayList<String>> loadedDocument = new HashMap<String, ArrayList<String>>(); 
	public Properties props = null;
	public StanfordCoreNLP pipeline = null;
	
	public void DocumentParserForLDA()
	{

	}
	
	// 문장이 입력되면 lemma을 적용해서 리스트 형태로 반환함
	public ArrayList<String> applyLemma(String input)
	{
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
		
		// create an empty Annotation just with the given text
	    Annotation document = new Annotation(input);
	    
	    // 이곳에 lemmed 된 텍스트가 저장됨
	    ArrayList<String> result = new ArrayList<String>();
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences)
		{
			for (CoreLabel token : sentence.get(TokensAnnotation.class))
			{
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// 스탑워드를 제거함
				switch (word)
				{
					case ".":
						continue;
					case ",":
						continue;
					default:
						break;
				}
				result.add(word);
			}
		}
		return result;
	}
	
	// 입력된 소스 HTML파일에 접근하여 텍스트들만 뽑아내고, 해쉬맵에 등록함
	public void loadHTMLFile(File sourceFile)
	{
		String fileName = null;
		
		// 입력된 파일이 정상적인 파일인지 확인함
		if(!sourceFile.exists()){
			System.out.println("존재하지 않는 파일입니다.");
		}
		else if(sourceFile.isDirectory()){
			System.out.println("잘못된 경로입니다.");
		}
		else
		{
			fileName = sourceFile.getName();
			System.out.println(fileName + " 파일을 로드중입니다.");
		}

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
				// 문장 하나를 뽑아옴
				String str = cols.item(i).getTextContent();
				
				// 뽑아온 문장에 lemma를 적용함
				ArrayList<String> lemmedSentence = new ArrayList<String>();
				lemmedSentence = applyLemma(str);
				
				// lemma를 적용한 단어들을 어레이 리스트에 합병함
				sentences.addAll( lemmedSentence );
			}
			// 테스트
			//System.out.println(sentences);
			System.out.println(fileName + " 파일을 로드했습니다.");
			
			// lemma가 적용되고, 단어로 나열되어 있는 문서를 파일로써 등록함
			loadedDocument.put(fileName, sentences);			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveDocumentFile(File targetFile)
	{
		if(loadedDocument.size() == 0)
		{
			System.out.println("파일을 아직 로드하지 않았습니다.");
			return;
		}
		
		// 모든 문서들을 규합함
		ArrayList<String> documentSet = new ArrayList<String>();
		for(String eachDocumentName:loadedDocument.keySet())
		{
			ArrayList<String> eachDocument = loadedDocument.get(eachDocumentName);
			String eachDocumentLine = eachDocument.toString().replace(",", "").replace("[", "").replace("]", "").trim();
			documentSet.add(eachDocumentLine);
		}
		
		// 규합한 문서들을 파일로 저장함
		try
		{
			// 파일의 가장 위에는 문서의 총 갯수가 저장되어야 함
			BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
			out.write(documentSet.size()+"\n");
			System.out.println(documentSet.size()+"개 문서를 저장하는 중입니다.");
			
			// 모든 파일을 순회하면서 저장함
			for(String eachDocument:documentSet)
			{
				out.write(eachDocument+"\n");
			}
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
		System.out.println("문서 저장을 완료하였습니다.");		
	}
	
	public static void main(String[] args)
	{
		String sourcePath = "D:/Downloads/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/data/AFP_ENG_200701/AFP_ENG_20070101.0001.LDC2009T13";
		String targetPath = "./data/lda/documentset.dat";
		File sourceFile = new File(sourcePath);
		File targetFile = new File(targetPath);
		
		DocumentParserForLDA dp = new DocumentParserForLDA();
		dp.loadHTMLFile(sourceFile);
		dp.saveDocumentFile(targetFile);
		//System.out.println( dp.applyLemma("The director of the company is Marge. Marge's son is Bart.") );
	}
}
