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
	
	public double compareTopicSimilarity(String docAName, String docBName)
	{
		String distA = getDistributionByFileName(docAName);
		String distB = getDistributionByFileName(docBName);
		String[] stringDistA = distA.split(" ");
		String[] stringDistB = distB.split(" ");
		double[] doubleDistA = new double[stringDistA.length];
		double[] doubleDistB = new double[stringDistB.length];
		int num = 0;
		
		for(String eachProp:stringDistA)
		{
			doubleDistA[num] = Double.valueOf(eachProp);
			num = num + 1;
		}
		
		num = 0;
		for(String eachProp:stringDistB)
		{
			doubleDistB[num] = Double.valueOf(eachProp);
			num = num + 1;
		}
		
		double result = klDivergence(doubleDistA, doubleDistB);
		return result;
	}
	
	public static final double log2 = Math.log(2);
	public static double klDivergence(double[] p1, double[] p2)
	{

		double klDiv = 0.0;

		for (int i = 0; i < p1.length; ++i)
		{
			if (p1[i] == 0)
			{
				continue;
			}
			if (p2[i] == 0.0)
			{
				continue;
			} // Limin

			klDiv += p1[i] * Math.log(p1[i] / p2[i]);
		}

		return klDiv / log2; // moved this division out of the loop -DM
	}
	
	public static void main(String[] args)
	{
		LDA lda = new LDA();
		
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0002.LDC2009T13", "AFP_ENG_20070101.0074.LDC2009T13"));
		System.out.println();
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0074.LDC2009T13", "AFP_ENG_20070101.0002.LDC2009T13"));
	}
}
