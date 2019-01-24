package pt.lzgpom.bot;

import com.google.api.services.customsearch.model.Result;
import pt.lzgpom.bot.util.bracket.ImageSearch;
import pt.lzgpom.bot.util.bracket.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Test 
{

	public static void main(String[] args)
	{
		List<Result> results = ImageSearch.search("Sana Twice");

		for(Result result : results)
		{
			BufferedImage img = Utils.getImageFromUrl(result.getLink());
			showImage(img);
			showImage(Utils.getImageSquared(img));
		}
	}

	private static void showImage(Image image)
	{
		ImageIcon icon=new ImageIcon(image);
		JFrame frame=new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(200,300);
		JLabel lbl=new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
