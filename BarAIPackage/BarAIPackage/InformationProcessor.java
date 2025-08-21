package BarAIPackage;

import java.util.List;

public class InformationProcessor {

	public static String infoSummaryGenerator(List<FileData> files)
	{
		String retval = "";
		for(FileData f : files)
		{
			retval+= f.toString() + "\n";
		}
		
		return retval;
	}
	
	
}
