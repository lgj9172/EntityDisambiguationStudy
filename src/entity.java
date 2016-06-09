import java.util.ArrayList;

public class entity {
	//int start; //offset in doc
	//int end; //offset in doc
	
	String name; //entity name
	ArrayList<String> information; //relation & pair entity relation
	
	String type; //result of ner
	ArrayList<String> docs; //document name
	
	public entity(String name, String type) {
		this.name = name;
		this.type = type;
		
		this.information = new ArrayList<String>();
		this.docs = new ArrayList<String>();
	}
	
	//getter
	public String getName() {
		//for disambiguation
		return name;
	}
	
	public ArrayList<String> getDocs() {
		return docs;
	}
	
	public ArrayList<String> getInform() {
		return information;
	}
	
	public void addInformation(String info) {
		information.add(info);
	}
	
	public void addDocs(String doc) { 
		//It will be called when 2 entities are same 
		//in disambiguation step between doc1 and doc2
		docs.add(doc);
	}
	
	public void mergeEntities(entity e) {
		docs.addAll(e.getDocs());
		information.addAll(e.getInform());
	}

}
