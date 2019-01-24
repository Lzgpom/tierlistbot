package pt.lzgpom.bot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import pt.lzgpom.bot.lib.Config;

public class Utils 
{
	private static final String COLORS_FILE = "./save/colors.txt";
	private static final String VIDEOS_FILE = "./save/video.txt";
	
	public static String getReactionInPos(int pos)
	{
		return  "" + Config.REACTIONS.charAt((2 * pos) - 2) + Config.REACTIONS.charAt((2 * pos) - 1);
	}
	
	public static int getReactionValue(String reaction)
	{
		return (Config.REACTIONS.indexOf(reaction) / 2) + 1;
	}
	
	public static boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	public static HashMap<String, String> readColors()
	{
		HashMap<String, String> out = new HashMap<>();
		
		try 
		{
			Scanner in = new Scanner(new File(COLORS_FILE));
			
			while(in.hasNext())
			{
				String[] personColor = in.nextLine().split(":");
				out.put(personColor[0], personColor[1]);
			}
			
			in.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return out;
	}
	
	public static String getRandomVideo() throws FileNotFoundException
	{
		String result = null;
		Random rand = new Random();
		int n = 0;
		for(Scanner sc = new Scanner(new File(VIDEOS_FILE)); sc.hasNext(); )
		{
			++n;
			String line = sc.nextLine();
			if(rand.nextInt(n) == 0)
		       result = line;         
		 }

		return result;
	}
}
