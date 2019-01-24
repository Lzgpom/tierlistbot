package pt.lzgpom.bot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import pt.lzgpom.bot.lib.Config;

public class ConfigLoader 
{
	private static final String CONFIG_FILE_NAME = "./save/config.properties";
	private static final String PREFIX_PROPERTY = "prefix";
	private static final String ICON_PROPERTY = "icon";
	private static final String TIME_PROPERTY = "time";
	private static final String REACTIONS_PROPERTY = "reactions";
	
	public static void loadCongigurations()
	{
		Properties prop = new Properties();
		InputStream input = null;

		try
		{
			input = new FileInputStream(CONFIG_FILE_NAME);
			prop.load(new InputStreamReader(input, Charset.forName("UTF-8")));
			
			Config.PREFIX = prop.getProperty(PREFIX_PROPERTY);
			Config.ICON = prop.getProperty(ICON_PROPERTY);
			Config.TIME_TO_REACT = Integer.parseInt(prop.getProperty(TIME_PROPERTY));
			Config.REACTIONS = prop.getProperty(REACTIONS_PROPERTY);
		} 
		
		catch (IOException ex)
		{
			ex.printStackTrace();
		} 
		
		finally
		{
			if (input != null) 
			{
				try 
				{
					input.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}

	}
}
