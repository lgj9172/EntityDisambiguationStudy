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
	
	
	public DocumentParserForLDA()
	{
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	// 문장이 입력되면 lemma을 적용해서 리스트 형태로 반환함
	public ArrayList<String> applyLemma(String input)
	{
		// create an empty Annotation just with the given text
	    Annotation document = new Annotation(input);
	    
	    // 이곳에 lemmed 된 텍스트가 저장됨
	    ArrayList<String> result = new ArrayList<String>();
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    //pipeline.annotate(document);
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
					case "/":
						continue;
					case "``":
						continue;
					case "''":
						continue;
					case ":":
						continue;
					case "-LRB-":
						continue;
					case "-RRB-":
						continue;
					case "--":
						continue;
					case "~":
						continue;
					case "on":
						continue;
					case "of":
						continue;
					case "to":
						continue;
					case "in":
						continue;
					case "the":
						continue;
					case "by":
						continue;
					case "he":
						continue;
					case "she":
						continue;
					case "a":
						continue;
					case "and":
						continue;
					case "or":
						continue;
					case "his":
						continue;
					case "him":
						continue;
					case "her":
						continue;
					case "for":
						continue;
					case "over":
						continue;
					case "men":
						continue;
					case "woman":
						continue;
					case "?":
						continue;
					case "!":
						continue;
					case "'":
						continue;
					case "at":
						continue;
					case "as":
						continue;
					case "it":
						continue;
					case "its":
						continue;
					case "other":
						continue;
					case "another":
						continue;
					case "'s":
						continue;
					case "-":
						continue;
					case "an":
						continue;
					case "but":
						continue;
					case "just":
						continue;
					case "we":
						continue;
					case "all":
						continue;
					case "some":
						continue;
					case "were":
						continue;
					case "had":
						continue;
					case "off":
						continue;
					case "from":
						continue;
					case "was":
						continue;
					case "more":
						continue;
					case "that":
						continue;
					case "such":
						continue;
					case "She":
						continue;
					case "He":
						continue;
					case "The":
						continue;
					case "if":
						continue;
					case "before":
						continue;
					case "after":
						continue;
					case "until":
						continue;
					case "also":
						continue;
					case "has":
						continue;
					case "But":
						continue;
					case "Be":
						continue;
					case "be":
						continue;
					case "who":
						continue;
					case "And":
						continue;
					case "At":
						continue;
					case "I":
						continue;
					case "next":
						continue;
					case "last":
						continue;
					case "than":
						continue;
					case "said":
						continue;
					case "My":
						continue;
					case "my":
						continue;
					case "with":
						continue;
					case "is":
						continue;
					case "are":
						continue;
					case "not":
						continue;
					case "will":
						continue;
					case "been":
						continue;
					case "they":
						continue;
					case "their":
						continue;
					case "would":
						continue;
					case "there":
						continue;
					case "0":
						continue;
					case "1":
						continue;
					case "2":
						continue;
					case "3":
						continue;
					case "4":
						continue;
					case "5":
						continue;
					case "6":
						continue;
					case "7":
						continue;
					case "8":
						continue;
					case "9":
						continue;
					case "10":
						continue;
					case "which":
						continue;
					case "have":
						continue;
					case "this":
						continue;
					case "do":
						continue;
					case "It":
						continue;
					case "We":
						continue;
					case "you":
						continue;
					case "can":
						continue;
					case "very":
						continue;
					case "about":
						continue;
					case "no":
						continue;
					case "could":
						continue;
					case "what":
						continue;
					case "so":
						continue;
					case "now":
						continue;
					case "out":
						continue;
					case "up":
						continue;
					case "A":
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
			//System.out.println(fileName + " 파일을 로드중입니다.");
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
			//System.out.println(fileName + " 파일을 로드했습니다.");
			
			// lemma가 적용되고, 단어로 나열되어 있는 문서를 파일로써 등록함
			loadedDocument.put(fileName, sentences);			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<ArrayList<String>> documentMap = new ArrayList<ArrayList<String>>();
	public void saveDocumentFile(File targetFile)
	{
		if(loadedDocument.size() == 0)
		{
			System.out.println("파일을 아직 로드하지 않았습니다.");
			return;
		}
		
		// 모든 문서들을 규합함
		ArrayList<String> documentSet = new ArrayList<String>();
		int num = 0;
		for(String eachDocumentName:loadedDocument.keySet())
		{
			ArrayList<String> eachDocument = loadedDocument.get(eachDocumentName);
			String eachDocumentLine = eachDocument.toString().replace(",", "").replace("[", "").replace("]", "").trim();
			documentSet.add(eachDocumentLine);
			
			ArrayList<String> eachMap = new ArrayList<String>();
			eachMap.add(String.valueOf(num));
			eachMap.add(eachDocumentName);
			documentMap.add(eachMap);
			num = num + 1;
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
	
	public void saveMapFile(ArrayList<ArrayList<String>> documentMap)
	{
		// LDA의 학습 파일은 각 문서에 대한 id정보를 갖지 않으므로 따로 작성해 주어야 함
		try
		{
			File mapFile = new File("./data/lda/map.dat");
			BufferedWriter mapOut = new BufferedWriter(new FileWriter(mapFile));
			int num = 0;
			for(ArrayList<String> eachMap:documentMap)
			{
				mapOut.write(eachMap.get(0) + "\t" + eachMap.get(1) + "\n");
				System.out.println(eachMap.get(0));
				num = num + 1;
			}
			mapOut.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)
	{
		// 저장될 경로를 지정함
		String targetPath = "./data/lda/documentset.dat";
		File targetFile = new File(targetPath);
		
		// 다음 문자열이 들어간 폴더를 모두 순회하여 파일을 읽도록 합니다.
		String sourceClue = "AFP_ENG_200701";
		
		// 폴더들을 찾을 범위를 지정합니다.
		String sourceArea = "D:/Downloads/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/data/";
		
		// 폴더들을 찾을 범위에서 폴더명들을 모두 얻어냅니다.
		File sourceAreaFolder = new File(sourceArea);
		
		// AFP_ENG_로 시작하는 모든 폴더패스들을 얻어냅니다.
		ArrayList<String> folderFaths = new ArrayList<String>();
		for(File eachFolder:sourceAreaFolder.listFiles())
		{
			String eachFolderName = eachFolder.getName();
			if(eachFolderName.startsWith(sourceClue)&&eachFolder.isDirectory()&&!eachFolderName.endsWith("BAK"))
			{
				//System.out.println(eachFolderName);
				folderFaths.add(sourceArea + eachFolderName);
			}
		}
		System.out.println(sourceClue + " 로 시작하는 " + folderFaths.size() + "개의 폴더를 찾았습니다.");
		
		// 모든 폴더를 순회하면서 모든 파일의 패스를 얻어냄
		ArrayList<String> fileFaths = new ArrayList<String>();
		System.out.println(folderFaths.size() +"개의 폴더에 있는 모든 파일 목록을 얻어내는 중입니다.");
		int num = 0;
		for(String folderFath:folderFaths)
		{
			File eachFolder = new File(folderFath);
			//System.out.println(eachFolder.exists());
			for(File eachFile:eachFolder.listFiles())
			{
				String eachFileName = eachFolder.getName();
				if(eachFile.isFile())
				{
					//System.out.println(eachFolderName);
					fileFaths.add(eachFile.getAbsolutePath());
				}
				num = num + 1;
			}
		}
		System.out.println(fileFaths.size() +"개의 파일을 찾았습니다.");		
		
		// 모든 파일을 순회하면서 LDA를 위한 파일을 작성함
		// 문서 파서를 만듦
		DocumentParserForLDA dp = new DocumentParserForLDA();
		num = 1;
		for(String eachDocument:fileFaths)
		{
			// 로드할 파일 경로를 지정함
			File sourceFile = new File(eachDocument);
			// 문서 파서로 로드할 파일을 읽어들임
			dp.loadHTMLFile(sourceFile);
			System.out.println(num + "/" + fileFaths.size() +"개 진행");
			num = num + 1;
		}
		
		
		// 로드한 파일을 LDA에 적용 될 수 있는 형태로 저장함
		dp.saveDocumentFile(targetFile);
		dp.saveMapFile(dp.documentMap);
		//System.out.println( dp.applyLemma("The director of the company is Marge. Marge's son is Bart.") );
	}
}
