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
					//System.out.println(row[0]);	// 문서 번호 0 부터 시작함
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
		//System.out.println(distA);
		//System.out.println(distB);
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
		/*
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0022.LDC2009T13", "AFP_ENG_20070101.0037.LDC2009T13"));System.out.println();
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0022.LDC2009T13", "AFP_ENG_20070101.0204.LDC2009T13"));System.out.println();
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0022.LDC2009T13", "AFP_ENG_20070101.0205.LDC2009T13"));System.out.println();
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0022.LDC2009T13", "AFP_ENG_20070101.0206.LDC2009T13"));System.out.println();
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070101.0022.LDC2009T13", "AFP_ENG_20070101.0212.LDC2009T13"));System.out.println();

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070104.0355.LDC2009T13", "AFP_ENG_20070111.0369.LDC2009T13"));System.out.println();
		System.out.println(lda.compareTopicSimilarity("AFP_ENG_20070104.0355.LDC2009T13", "AFP_ENG_20070104.0077.LDC2009T13"));
		*/
		System.out.println(lda.getDistribution(3));
		System.out.println(lda.getDistribution(4));
		System.out.println(lda.getDistribution(5));
		
		System.out.println("");
		System.out.println("  Doc1 : [0.0186567164179104460,  0.2723880597014925500,  0.0111940298507462680, ...,  0.123134328358208950]");
		System.out.println("  Doc2 : [0.0647668393782383400,  0.0077720207253886010,  0.0025906735751295338, ...,  0.007772020725388601]");
		System.out.println("  Doc3 : [0.0057471264367816090,  0.0977011494252873600,  0.0172413793103448270, ...,  0.005747126436781609]");
		System.out.println("  Doc4 : [0.0037878787878787880,  0.0643939393939393900,  0.0037878787878787880, ...,  0.003787878787878788]");
		System.out.println("  Doc5 : [0.0152439024390243900,  0.0030487804878048780,  0.0030487804878048780, ...,  0.003048780487804878]");
		System.out.println("");
		                         
		
	}
}
