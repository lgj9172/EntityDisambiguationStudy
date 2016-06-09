import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class LDA
{
	public int getDocumentNumber(String docName)
	{
		String mapFilePath = "./data/lda/map.dat";
		File mapFile = new File(mapFilePath);
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(mapFile));
			String s;
			String[] row;
			while((s = in.readLine()) != null)
			{
				row = s.split("\t");
				//System.out.println(row[1]);
				if(row[1].equals(docName))
				{
					System.out.println(row[0]);	// 문서 번호 0 부터 시작함
					System.out.println(row[1]);	// 문서 이름
					return Integer.valueOf(row[0]);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return(-1);
	}
	
	public String getDistribution(int docNum)
	{
		String thetaFilePath = "./data/lda/model-final.THETA";
		File thetaFile = new File(thetaFilePath);
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(thetaFile));
			String distribution;
			String s;
			int num = 0;
			while((s = in.readLine()) != null)
			{
				if(num==docNum)
				{
					return s;
				}
				num = num + 1;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "error";
	}
	
	public String getDistributionByFileName(String docName)
	{
		int docNum = getDocumentNumber(docName);
		String distribution = getDistribution(docNum);
		return distribution;
	}
	
	public void compareTopicSimilarity(String docAName, String docBName)
	{
		int docANum = 0;
		int docBNum = 0;
	}
	
	public static void main(String[] args)
	{
		LDA lda = new LDA();
		String distribution = lda.getDistributionByFileName("AFP_ENG_20070124.0600.LDC2009T13");
		System.out.println(distribution);
	}
}
