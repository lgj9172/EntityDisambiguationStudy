import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class OpenIE {
	Properties props;
	StanfordCoreNLP pipeline;

	public OpenIE() {
		props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment, natlog, openie");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	public void extract(String path) {
		File dir = new File(path);		
		
		ArrayList<File> files = getFileList(dir);
		
        for(File f : files) {
        	ArrayList<String> sentences = getSentencesFromFile(f);
        	compare(sentences);
        	//compare의 return 값 file save 하도록 변경.
        }
	}
	
	public ArrayList<String> compare(ArrayList<String> sentences) { //doc단위! 다음에 실제 doc 단위로 받아서 처리하는 걸로 바꾸고, dir 받아서 처리하는걸로 확장!
		
		//str -> whole document...
		//String doc = "Japanese share prices face headwinds next week amid jitters about a possible interest rate rise by the Bank of Japan later in the month, analysts said Friday. The Tokyo Stock Exchange's benchmark Nikkei-225 index lost 134.24 points or 0.78 percent to 17,091.59 in the week to January 5, during which it opened for only one and a half days and fell by 1.51 percent on Friday alone.";
		
		/*
		//virtual doc
		ArrayList<String> doc = new ArrayList<String>();
		doc.add("Japanese share prices face headwinds next week amid jitters about a possible interest rate rise by the Bank of Japan later in the month, analysts said Friday.");
		doc.add("The Tokyo Stock Exchange's benchmark Nikkei-225 index lost 134.24 points or 0.78 percent to 17,091.59 in the week to January 5, during which it opened for only one and a half days and fell by 1.51 percent on Friday alone.");
		*/
		
    	String doc =  StringUtils.join(sentences);
    	
		HashMap<String, HashMap<String, String>> coreference = getCoreference(doc);
		
		HashMap<String, ArrayList<String>> triples = new HashMap<String, ArrayList<String>>();
		
		//for each sentence
		for(int i=0; i<sentences.size(); i++) {
			ArrayList<String> tmp = getTriple(sentences.get(i));
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
						tmp[3] = coref.get(tmp[32]);
					}
					
					String newTriple = tmp[0] + "\t" + tmp[1] + "\t" + tmp[2] + "\t" + tmp[3];
					result.add(newTriple);
				}
			}
			else {
				result.addAll(triple);
			}
						
		}
		
		System.out.println("->");
		for(int i=0; i<result.size(); i++) {
			System.out.println(i+1 + ": " + result.get(i));
		}
		
		return result;
	}

	private void getSentence() {
		// TODO Auto-generated method stub
		
	}

	public HashMap<String, HashMap<String, String>> getCoreference(String doc) {
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

	private ArrayList<String> getTriple(String str) {
		Annotation annotation = new Annotation(str);
		pipeline.annotate(annotation);
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		ArrayList<String> result = new ArrayList<String>();
		
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
				String tmp = triple.confidence + "\t" +  triple.subjectLemmaGloss() + "\t" + triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss();
				System.out.println(tmp);
				result.add(tmp);
			}
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
		System.out.println(result);
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
