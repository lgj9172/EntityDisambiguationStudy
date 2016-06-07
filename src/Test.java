import java.io.*;
import java.util.*;

import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

public class Test {
	  public static void main(String[] args) throws IOException {
		    // set up optional output files
		    PrintWriter out;
		    if (args.length > 1) {
		      out = new PrintWriter(args[1]);
		    } else {
		      out = new PrintWriter(System.out);
		    }
		    PrintWriter xmlOut = null;
		    if (args.length > 2) {
		      xmlOut = new PrintWriter(args[2]);
		    }

		    // Create a CoreNLP pipeline. To build the default pipeline, you can just use:
		    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		    // Here's a more complex setup example:
		    //   Properties props = new Properties();
		    //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
		    //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
		    //   props.put("ner.applyNumericClassifiers", "false");
		    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		    // Add in sentiment
		    Properties props = new Properties();
		    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

		    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
		    Annotation annotation;
		    if (args.length > 0) {
		      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
		    } else {
		      annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply.");
		    }

		    // run all the selected Annotators on this text
		    pipeline.annotate(annotation);

		    // this prints out the results of sentence analysis to file(s) in good formats
		    pipeline.prettyPrint(annotation, out);
		    if (xmlOut != null) {
		      pipeline.xmlPrint(annotation, xmlOut);
		    }
		    
		    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		    if (sentences != null && ! sentences.isEmpty()) {
		    	CoreMap sentence = sentences.get(0);
		        out.println("The first sentence tokens are:");
				for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				       out.println(token.toShorterString());
				       out.println("type: " + token.get(NamedEntityTagAnnotation.class));
				}
				
				Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
				out.println();
				out.println("The first sentence parse tree is:");
				tree.pennPrint(out);
				out.println();
				
			    out.println("The first sentence basic dependencies are:");
			    out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
			    out.println("The first sentence collapsed, CC-processed dependencies are:");
			    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
			    out.println(graph.toString(SemanticGraph.OutputFormat.LIST));   	      
		    	}
	  }
}
