package pt.lzgpom.bot;

import pt.lzgpom.bot.model.bracket.BracketSolo;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.impl.ChallengerImpl;
import pt.lzgpom.bot.model.bracket.impl.DuelSolo;
import pt.lzgpom.bot.util.bracket.image.ImageBracket;
import pt.lzgpom.bot.util.bracket.image.ImageDuel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Test 
{
	private static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws IOException
	{
		BracketSolo bracket = new BracketSolo(createTeams(15), true);
		System.out.println(bracket);

		/*
		while(bracket.hasNextDuel())
		{
			System.out.println(bracket);
			DuelSolo duel = bracket.getNextDuel();
			System.out.println(duel);
			System.out.println("Who wins?");
			bracket.setDuelWinner(duel, input.nextInt());
		}


		/*
		BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		DuelSolo duel = new DuelSolo();
		List<Challenger> team = new ArrayList<>();
		team.add(new ChallengerImpl("Sana", "Twice"));
		duel.addTeam(team);

		new ImageDuel(duel, 0, 300).draw(g2);

		ImageIcon icon = new ImageIcon(img);
		JLabel label = new JLabel(icon);

		JOptionPane.showMessageDialog(null, label);
		*/

		/*
		ImageBracket imgBracket = new ImageBracket(bracket);

		File outputfile = new File("image.jpg");
		ImageIO.write(imgBracket.createImage(), "jpg", outputfile);
		*/
	}

	private static List<Challenger> createTeam(String user)
	{
		Challenger c1 = new ChallengerImpl(user, "");
		List<Challenger> out = new ArrayList<>();
		out.add(c1);
		return out;
	}

	private static List<List<Challenger>> createTeams(int n)
	{
		List<List<Challenger>> out = new ArrayList<>();

		for(int i = 0; i < n; i++)
		{
			//out.add(createTeam("Team " + i));
			out.add(createTeam("Team 1:" + i));
		}

		return out;
	}
}
