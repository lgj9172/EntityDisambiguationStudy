import java.io.*;
import java.util.*;

import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations.DependencyAnnotation;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.DependentsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.*;



public class GetTripleFromDependency
{
	public static void main(String[] args) throws Exception
	{
		
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = "The director of the company is Marge. Marge's son is Bart."; // Add your text here!

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

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
			System.out.println(dependencies.toList());	
			
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
						System.out.println(targetTerm+ "	" + "have" + "	" + sourceTerm );
					}
					
					else if(edge.getRelation().toString().contains("of")){	// of로 이루어진 경우
						System.out.println(targetTerm+ "	" + "have" + "	" + sourceTerm );
					}
					else
					{}
					/*
					else if(edge.getRelation().toString().contains("in")){	// in로 이루어진 경우
						System.out.println(targetTerm+ " - " + "in" + " - " + sourceTerm );
					}
					else if(edge.getRelation().toString().contains("at")){	// at로 이루어진 경우
						System.out.println(targetTerm+ " - " + "at" + " - " + sourceTerm );
					}*/
				}
			}
			//System.out.println(dependencies);
		}    
	    
	}

}
	