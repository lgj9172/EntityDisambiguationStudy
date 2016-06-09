import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Disambiguation {
	HashMap<String, ArrayList<entity>> entities = new HashMap<String, ArrayList<entity>>();
	
	public void loadCandidateTriples(String path) {
		File dir = new File(path);		
		
		ArrayList<File> files = getFileList(dir);
		
        for(File f : files) {
        	String filename = getFileName(f.toString());
        	ArrayList<String> candidateTriples = getTriplesFromFile(f);
        	addTriples(candidateTriples, filename);
        }
        
       //다음에 doc간 disambiguation
	}
	
	public void docDisambiguation() {
		Set<String> keys = entities.keySet();
		Iterator<String> itr = keys.iterator();

		while(itr.hasNext()) {
			String eName = itr.next();
			ArrayList<entity> eArr = entities.get(eName);
			if(eArr.size() > 1) {
				//need disambiguation process
			}
		}
	}
	
	public boolean compDocSimilarity(String doc1, String doc2) {
		boolean result = true;
		//to be implemented;
		return result;
	}
	
	private String getFileName(String path) {
		// TODO Auto-generated method stub
		int lastSlash = path.lastIndexOf("/"); //윈도우에서는 //로 바꿔야함!
    	String filename = path.substring(lastSlash+1);
    	
		return filename;
	}

	private void addTriples(ArrayList<String> candidateTriples, String filename) {
		// TODO Auto-generated method stub
		for(int i=0; i<candidateTriples.size(); i++) {
			String tmp = candidateTriples.get(i);
			
			String[] elem = tmp.split("\t");
			
			//name ->1, 3
			//relation ->2
			//e1's type ->4 , e2's type ->5
			//id -> filename+1, filename+3
			
			//생성자: entity(String id, String name, String type, String docName)
			if(!entities.containsKey(elem[1])) {
				entity e = new entity(elem[1], elem[4]);
				e.addDocs(filename);
				e.addInformation(elem[2]+">"+elem[3]);
				
				ArrayList<entity> entityArr = new ArrayList<entity>();
				entityArr.add(e);
				
				entities.put(elem[1], entityArr);				
			} else {
				entity e = new entity(elem[1], elem[4]);
				e.addDocs(filename);
				e.addInformation(elem[2]+">"+elem[3]);
				
				entities.get(elem[1]).add(e);	
			}
			
			
			if(!entities.containsKey(elem[3])) {
				entity e = new entity(elem[3], elem[5]);
				e.addDocs(filename);
				e.addInformation(elem[2]+"<"+elem[1]);
				
				ArrayList<entity> entityArr = new ArrayList<entity>();
				entityArr.add(e);
				
				entities.put(elem[3], entityArr);				
			} else {
				entity e = new entity(elem[3], elem[5]);
				e.addDocs(filename);
				e.addInformation(elem[2]+"<"+elem[1]);
				
				entities.get(elem[3]).add(e);
			}			
		}
	}

	private ArrayList<String> getTriplesFromFile(File f) { //open each file, return set of lines
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		
		try {
				FileReader fileReader = new FileReader(f);
				BufferedReader reader = new BufferedReader(fileReader);

				String triple = null;
				
				while ((triple = reader.readLine()) != null) {
					result.add(triple);
				}
				
				reader.close(); 
		} catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
		return result;
	}
	
	public ArrayList<File> getFileList(File dir) {
		ArrayList<File> files = new ArrayList<File>();
	        
		visitAllFiles(files, dir);
		
		return files;
	        
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
