import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class OpenIE {
	Properties props;
	StanfordCoreNLP pipeline;

	public OpenIE() {
		props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, natlog, openie");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	public void extract(String path) {
		HashSet<String> triples = new HashSet<String>();
		
		File dir = new File(path);		
		
		ArrayList<File> files = getFileList(dir);
		
        for(File f : files) {
        	ArrayList<String> sentences = getSentencesFromFile(f);
        	
        	HashMap<String, HashMap<String, String>> coreference = getCoreference(sentences);
        	
        	ArrayList<String> openIE_triples = compareOpenIE(sentences, coreference);
        	ArrayList<String> depend_triples = compareDepend(sentences, coreference);
        	
        	//compare의 return 값 file save 하도록 변경.
        	triples.addAll(openIE_triples);
        	triples.addAll(depend_triples);
        }
        
        System.out.println(triples.toString());
	}
	
	public ArrayList<String> compareDepend(ArrayList<String> sentences, HashMap<String, HashMap<String, String>> coreference) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> triples = new HashMap<String, ArrayList<String>>();
		
		//for each sentence
		for(int i=0; i<sentences.size(); i++) {
			ArrayList<String> tmp = getTriplesFromDependency(sentences.get(i));
			Integer key = i+1; //array idx starts from 0. sentence nums start from 1.
			
			triples.put(key.toString(), tmp);
		}
		
		ArrayList<String> result = new ArrayList<String>();
		
		Set<String> key = triples.keySet();
		Iterator<String> itr = key.iterator();
		
		while(itr.hasNext()) {
			String sNum = itr.next();
			
			ArrayList<String> triple = triples.get(sNum);
			//System.out.println("sentence: " + sNum + " , size: " + triple.size());
			HashMap<String, String> coref = new HashMap<String, String>();
			
			if(coreference.containsKey(sNum)) {
				//System.out.println(sNum);
				
				coref = coreference.get(sNum);
			
				for(int i=0; i<triple.size(); i++) {
					String t = triple.get(i);
					String[] tmp = t.split("\t");
					//System.out.println(t);
					if(coref.containsKey(tmp[1])) {
						tmp[1] = coref.get(tmp[1]);
					}
					
					if(coref.containsKey(tmp[3])) {
						tmp[3] = coref.get(tmp[3]);
					}
					
					String newTriple = tmp[0] + "\t" + tmp[1] + "\t" + tmp[2] + "\t" + tmp[3]+ "\t" + tmp[4]+ "\t" + tmp[5];
					result.add(newTriple);
				}
			}
			else {
				result.addAll(triple);
			}
						
		}
		/*
		System.out.println("->");
		for(int i=0; i<result.size(); i++) {
			System.out.println(i+1 + ": " + result.get(i));
		}
		*/
		return result;
	}

	public ArrayList<String> compareOpenIE(ArrayList<String> sentences, HashMap<String, HashMap<String, String>> coreference) { //doc단위! 다음에 실제 doc 단위로 받아서 처리하는 걸로 바꾸고, dir 받아서 처리하는걸로 확장!
		
		//str -> whole document...
		//String doc = "Japanese share prices face headwinds next week amid jitters about a possible interest rate rise by the Bank of Japan later in the month, analysts said Friday. The Tokyo Stock Exchange's benchmark Nikkei-225 index lost 134.24 points or 0.78 percent to 17,091.59 in the week to January 5, during which it opened for only one and a half days and fell by 1.51 percent on Friday alone.";
		
		/*
		//virtual doc
		ArrayList<String> doc = new ArrayList<String>();
		doc.add("Japanese share prices face headwinds next week amid jitters about a possible interest rate rise by the Bank of Japan later in the month, analysts said Friday.");
		doc.add("The Tokyo Stock Exchange's benchmark Nikkei-225 index lost 134.24 points or 0.78 percent to 17,091.59 in the week to January 5, during which it opened for only one and a half days and fell by 1.51 percent on Friday alone.");
		*/
		
    	//String doc =  StringUtils.join(sentences);
    	
		//HashMap<String, HashMap<String, String>> coreference = getCoreference(doc);
		
		HashMap<String, ArrayList<String>> triples = new HashMap<String, ArrayList<String>>();
		
		//for each sentence
		for(int i=0; i<sentences.size(); i++) {
			ArrayList<String> tmp = getTripleFromOpenIE(sentences.get(i));
			Integer key = i+1; //array idx starts from 0. sentence nums start from 1.
			
			triples.put(key.toString(), tmp);
		}
		
		ArrayList<String> result = new ArrayList<String>();
		
		Set<String> key = triples.keySet();
		Iterator<String> itr = key.iterator();
		
		while(itr.hasNext()) {
			String sNum = itr.next();
			
			ArrayList<String> triple = triples.get(sNum);
			//System.out.println("sentence: " + sNum + " , size: " + triple.size());
			HashMap<String, String> coref = new HashMap<String, String>();
			
			if(coreference.containsKey(sNum)) {
				//System.out.println(sNum);
				
				coref = coreference.get(sNum);
			
				for(int i=0; i<triple.size(); i++) {
					String t = triple.get(i);
					String[] tmp = t.split("\t");
					
					if(coref.containsKey(tmp[1])) {
						tmp[1] = coref.get(tmp[1]);
					}
					
					if(coref.containsKey(tmp[3])) {
						tmp[3] = coref.get(tmp[3]);
					}
					
					String newTriple = tmp[0] + "\t" + tmp[1] + "\t" + tmp[2] + "\t" + tmp[3]+ "\t" + tmp[4]+ "\t" + tmp[5];
					result.add(newTriple);
				}
			}
			else {
				result.addAll(triple);
			}
						
		}
		
		/*
		System.out.println("->");
		for(int i=0; i<result.size(); i++) {
			System.out.println(i+1 + ": " + result.get(i));
		}
		*/
		
		return result;
	}

	public HashMap<String, HashMap<String, String>> getCoreference(ArrayList<String> arr) {
		
		String doc =  StringUtils.join(arr);
		
		Annotation annotation = new Annotation(doc);
		pipeline.annotate(annotation);
		
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		if (sentences != null && ! sentences.isEmpty()) {
			Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
			if (corefChains != null) {
				for (CorefChain chain : corefChains.values())
		        {	
					CorefChain.CorefMention representative = chain.getRepresentativeMention();
					for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder())
		            {
						 //System.out.println(mention);
						 if (mention != representative) {
							  // all offsets start at 1!
						 /*
						 System.out.println("\t"
						          + mention.mentionID + ": (Mention from sentence " + mention.sentNum + ", "
						          + "Head word = " + mention.headIndex
						          + ", (" + mention.startIndex + "," + mention.endIndex + ")"
						          + ")"
						          + " -> "
						          + "(Representative from sentence " + representative.sentNum + ", "
						          + "Head word = " + representative.headIndex
						          + ", (" + representative.startIndex + "," + representative.endIndex + ")"
						          + "), that is: \"" +
						          mention.mentionSpan + "\" -> \"" +
						          representative.mentionSpan + "\"");
						*/
							Integer tmp = mention.sentNum;
							if(!result.containsKey(tmp.toString())) {
								HashMap<String, String> pair = new HashMap<String, String>();
								pair.put(mention.mentionSpan, representative.mentionSpan);
								result.put(tmp.toString(), pair);
								//System.out.println("first!" + "#: " + mention.sentNum + ", " + mention.mentionSpan + " -> " + representative.mentionSpan);
								//System.out.println(result.size());
							}
							else {
								/*
								HashMap<String, String> tmp = result.get(mention.sentNum);
								tmp.put(mention.mentionSpan, representative.mentionSpan);
								*/
								result.get(tmp.toString()).put(mention.mentionSpan, representative.mentionSpan);
								//System.out.println("exist!" + "#: " + mention.sentNum + ", " + mention.mentionSpan + " -> " + representative.mentionSpan);
								//System.out.println(result.get(tmp.toString()).size());
							}
						}
		            }
		        }
			}
		}
		
		return result;
	}

	private ArrayList<String> getTripleFromOpenIE(String str) { //get triple by using openIE from sentence
		Annotation annotation = new Annotation(str);
		pipeline.annotate(annotation);
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		ArrayList<String> result = new ArrayList<String>();
		
		HashMap<String, String> ner = new HashMap<String, String>();
		
		if (sentences != null && ! sentences.isEmpty()) {
			CoreMap sentence = sentences.get(0);
			//System.out.println("---------------------------------------\n" + str);
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		        String word = token.get(TextAnnotation.class);
		        String ne = token.get(NamedEntityTagAnnotation.class);
		        
		        if(!ne.equals("O")) {
		        	ner.put(word, ne);
		        	//System.out.println("w: " + word + "/ ne: " + ne);
		        }	        
			}
		
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

			// Print the triples
			for (RelationTriple triple : triples) {
				String ner_e1 = "0";
				String ner_e2 = "0";
				
				/*
				if(ner.containsKey(triple.subjectLemmaGloss()))
					e1 = ner.get(triple.subjectLemmaGloss());
				
				if(ner.containsKey(triple.objectLemmaGloss()))
					e2 = ner.get(triple.objectLemmaGloss());
				*/
				
				String e1 = triple.subjectLemmaGloss();
				String e2 = triple.objectLemmaGloss();
				
		        for( String key : ner.keySet() ){
		        	//System.out.println("current key: " + key);
		        	if(e1.contains(key)) {
		        		ner_e1 = ner.get(key);
		        	}
		        	if(e2.contains(key)) {
		        		ner_e2 = ner.get(key);
		        	}
		        }
		        
				String tmp = triple.confidence + "\t"
							+ e1 + "\t" + triple.relationLemmaGloss() + "\t" + e2 + "\t"
							+ ner_e1 + "\t" + ner_e2;
				//System.out.println(tmp);
				result.add(tmp);
				//System.out.println(tmp);
			}
		}
			
		return result;
	}

	private ArrayList<String> getTriplesFromDependency(String str) {
		ArrayList<String> result = new ArrayList<String>();
		
		Annotation document = new Annotation(str);

		// run all Annotators on this text
		pipeline.annotate(document);
		
		// 입력된 텍스트를 문장별로 잘라서 저장한다.
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		// 모든 문장 별로 분석을 진행한다.
		for(CoreMap sentence: sentences) {
			// 해당 문장의 모든 토큰에 대해서 반복한다.
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				String ne = token.get(NamedEntityTagAnnotation.class);
			}
		  
			// 트리 적용
			Tree tree = sentence.get(TreeAnnotation.class);
		
			// 디펜던시 적용
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			//System.out.println(dependencies.toList());	
			
			// 컴파운드 찾기
			Map<String, String> compoundMap = new HashMap<String, String>();	// 조합된 컴파운드를 저장할 곳
			for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
				if(edge.getRelation().toString().contains("compound")){	// 컴파운드를 위한 조합을 만든다.
					// 다음과 같은 단어에 컴파운드가 붙는다.
					String source = String.valueOf(edge.getSource().index());
					// 다음과 같은 컴파운드가 붙는다.
					String target = edge.getTarget().toString(CoreLabel.OutputFormat.VALUE);
					
					// 컴파운드를 맵에 저장한다.
					// 해당 단어에 컴파운드가 붙어있지 않았다면 새롭게 추가한다.
					if((compoundMap.get(source)) == null){
						compoundMap.put(source, target + " ");
					}
					else{	// 해당 단어에 컴파운드가 붙어있었다면 뒤에 더 추가한다.
						String beforeCompound = compoundMap.get(source);
						compoundMap.put(source, beforeCompound + target + " ");
					}
				}
			}
			
			// 트리플 만들기
			for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
				if(edge.getRelation().toString().contains("nmod")){	// nmod가 들어가 있는 경우라면 무조건 트리플을 만든다.
					// 소스 단어 번호 구하기
					String sourceNumber = String.valueOf(edge.getSource().index());
					// 타겟 단어 번호 구하기
					String targetNumber = String.valueOf(edge.getTarget().index());
					// 소스 컴파운드 만들기
					String sourceCompound = "";
					if(compoundMap.get(sourceNumber) == null)
					{
						sourceCompound = "";
					}
					else
					{
						sourceCompound = compoundMap.get(sourceNumber);
					}
					// 타겟 컴파운드 만들기
					String targetCompound = "";
					if(compoundMap.get(targetNumber) == null)
					{
						targetCompound = "";
					}
					else
					{
						targetCompound = compoundMap.get(targetNumber);
					}
					// 소스 단어 만들기
					String sourceTerm = sourceCompound + edge.getSource().toString(CoreLabel.OutputFormat.VALUE);
					// 타겟 단어 만들기 
					String targetTerm = targetCompound + edge.getTarget().toString(CoreLabel.OutputFormat.VALUE);
					
					if(edge.getRelation().toString().contains("pos")){	// pos로 이루어진 경우
						//System.out.println(targetTerm+ "	" + "have" + "	" + sourceTerm );
						result.add(1.0 + "\t" + targetTerm+ "\t" + "have" + "\t" + sourceTerm);
					}
					
					else if(edge.getRelation().toString().contains("of")){	// of로 이루어진 경우
						//System.out.println(targetTerm+ "	" + "have" + "	" + sourceTerm );
						result.add(1.0 + "\t" + targetTerm+ "\t" + "have" + "\t" + sourceTerm);
					}
					else
					{
						
					}
					/*
					else if(edge.getRelation().toString().contains("in")){	// in로 이루어진 경우
						System.out.println(targetTerm+ " - " + "in" + " - " + sourceTerm );
					}
					else if(edge.getRelation().toString().contains("at")){	// at로 이루어진 경우
						System.out.println(targetTerm+ " - " + "at" + " - " + sourceTerm );
					}*/
				}
			      //buf.append(edge.getRelation().toString()).append("(");
			      //buf.append(edge.getSource().toString(CoreLabel.OutputFormat.VALUE_INDEX)).append(", ");
			      //buf.append(edge.getTarget().toString(CoreLabel.OutputFormat.VALUE_INDEX)).append(")\n");
			}
			//System.out.println(dependencies);
			
			//add triple to result array
		}
		
		return result;
	}
	
	public ArrayList<File> getFileList(File dir) { //dir 탐색해서 doc list 받고, doc 별로 extract triple 
		//dir 탐색해서 파일 받아오기
		
		Date dt = new Date();
		System.out.println(dt.toString() + "---------------");
		
		//File dir = new File("D:/Downloads/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/data/1");
		/*
		if(dir.exists() == false ) {
            System.out.println("경로가 존재 하지 않습니다.");
            return ;
        }
        */
		
        ArrayList<File> files = new ArrayList<File>();
        
        visitAllFiles(files, dir);
        
        /*
        for(File f : files) {
        	//getTriplesFromFile(f);
        }
        */
        
        System.out.println(dt.toString() + "---------------");
        
        return files;
	}
	
	public ArrayList<String> getSentencesFromFile(File f) { 
		//원래 파라미터: File f
		//temporary path: D:\Downloads\LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014\LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014\data\AFP_ENG_200701\AFP_ENG_20070101.0001.LDC2009T13
		//hard coding for debugging. 
		//File f = new File("D:/Downloads/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/LDC2015E45_TAC_KBP_Comprehensive_English_Source_Corpora_2009-2014/data/AFP_ENG_200701/AFP_ENG_20070101.0001.LDC2009T13");
		System.out.println(f.toString());
		
		ArrayList<String> sentences = new ArrayList<String>();
		
		try{			
			XPath  xpath = XPathFactory.newInstance().newXPath();
			
			InputSource is = new InputSource(new FileReader(f));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			
			String expression = "//*/P"; //it can be changed according to corpora
			
			NodeList cols = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
			//System.out.println(cols.getLength());
			for(int i=0; i<cols.getLength(); i++ ){
				String str = cols.item(i).getTextContent(); //이것이 맞능가
				sentences.add(str);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sentences;
	}
	
	public String getTriplesFromSentence(String str) { //sentence 별로 triple 뽑음
		Annotation annotation = new Annotation(str);
		pipeline.annotate(annotation);
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		String result = "";
		
		if (sentences != null && ! sentences.isEmpty()) {
			CoreMap sentence = sentences.get(0);
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

			// Print the triples
			for (RelationTriple triple : triples) {
				/*
				System.out.println(triple.confidence + "\t" +
				triple.subjectLemmaGloss() + "\t" +
				triple.relationLemmaGloss() + "\t" +
				triple.objectLemmaGloss());
				*/
				result += triple.confidence + "\t" + "Gloss: " + triple.subjectGloss() + "/Head: " + triple.subjectHead() + "/" + triple.subjectLemmaGloss() + "\t" + triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss()+"\n";
			}
		}
		//System.out.println(result);
		return result;
	}
	
	private void visitAllFiles(ArrayList files, File dir) {		 
        if(dir.isDirectory()) {
            File[] children = dir.listFiles();
            for(File f : children) {
                // 재귀 호출 사용
                // 하위 폴더 탐색 부분
                visitAllFiles(files,f);
            }
        } else {
            files.add(dir);
        }
    }
}
