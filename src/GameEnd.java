import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// popup that occurs when a game ends
class GameEnd implements WindowListener
{
	Drawing draw;
	static JDialog frame;
	ImageIcon endgame2 = new ImageIcon("files/2PEndofGame.png");
	ImageIcon endgame3 = new ImageIcon("files/3PEndofGame.png");
	ImageIcon endgame4 = new ImageIcon("files/4PEndofGame.png");

	ImageIcon rank1 = new ImageIcon("files/1.png");
	ImageIcon rank2 = new ImageIcon("files/2.png");
	ImageIcon rank3 = new ImageIcon("files/3.png");
	ImageIcon rank4 = new ImageIcon("files/4.png");

	ImageIcon profile1 = new ImageIcon("files/Profile1.png");
	ImageIcon profile2 = new ImageIcon("files/Profile2.png");
	ImageIcon profile3 = new ImageIcon("files/Profile3.png");
	ImageIcon profile4 = new ImageIcon("files/Profile4.png");


	static int[] grossscore;
	static int[] penalties;
	static boolean play; // true - play   false - 6 passes
	static int bonus; 
	static int[] points;
	static int[] rank;
	static String message;
	static int numtie;

	// constructor
	public GameEnd()
	{
		frame = new JDialog(Scrabble.frame, "End of Game", true);
		draw = new Drawing();
		frame.setSize(660 + 5, 469 + 25);
		frame.add(draw);
		draw.addMouseListener(new MouseListen());
		frame.addWindowListener(this);
		frame.setResizable(false);
		initialize();
		frame.setVisible(true);


	}

	public static void initialize()
	{
		bonus = 0;

		grossscore = new int[Scrabble.numplayers];
		penalties = new int[Scrabble.numplayers];

		// adjust points based on player's rack
		for (int i = 0; i < Scrabble.numplayers; i ++)
		{	
			grossscore[i] = Scrabble.p[i].points;

			for (int j = 0; j < 7; j ++)
			{
				if (Scrabble.p[i].rack[j] != '0')
				{
					penalties[i] -= Scrabble.value(Scrabble.p[i].rack[j]);
					Scrabble.p[i].points -= Scrabble.value(Scrabble.p[i].rack[j]);
					if (play)
						bonus += Scrabble.value(Scrabble.p[i].rack[j]);
				}
			}
		}

		// add bonus to player with empty rack
		if (play)
		{					
			Scrabble.p[Scrabble.currentp].points += bonus;
			penalties[Scrabble.currentp] = bonus;
		}   


		// determine the rank of players
		points = new int[Scrabble.numplayers];

		for (int i = 0; i < Scrabble.numplayers; i ++)
			points[i] = Scrabble.p[i].points;


		Arrays.sort(points);

		rank = new int[Scrabble.numplayers];

		for (int i = 0; i < Scrabble.numplayers; i ++)
		{
			for (int j = 0; j < Scrabble.numplayers; j ++)
			{
				if (Scrabble.p[j].points == points[i])
				{
					rank[j] = i;
				}
			}
		}

		for (int i = 0; i < Scrabble.numplayers; i ++)
			rank[i] = Scrabble.numplayers - 1 - rank[i];


		// display winning message 
		message = "";
		numtie = 0;
		for (int d = 0; d < Scrabble.numplayers; d ++)
		{
			if (rank[d] == 0)
			{
				message += Scrabble.p[d].name + ", ";
				numtie ++;
			}
		}

		message = message.substring(0, message.length() - 2);
		int commaloc = -1;
		for (int w = 0; w < message.length(); w ++)
			if (message.charAt(w) == ',')
				commaloc = w;

		if (commaloc != -1)
			message = message.substring(0, commaloc) + " and" + message.substring(commaloc + 1);

		if (numtie > 1)
			message = message + " have received the highest score.";
		else
			message = message + " wins!";

		message = message.toUpperCase().charAt(0) + message.substring(1);		   

	}

	public static void main(String[] args)
	{
		new GameEnd();
	}

	class Drawing extends JComponent
	{
		public void paint(Graphics g)
		{

			g.setColor(Color.white);

			// draw pictures
			if (Scrabble.numplayers == 2)
				g.drawImage(endgame2.getImage(), 0, 0, this);		
			else if (Scrabble.numplayers == 3)
				g.drawImage(endgame3.getImage(), 0, 0, this);		
			else if (Scrabble.numplayers == 4)
				g.drawImage(endgame4.getImage(), 0, 0, this);		



			Font f = new Font("Calibri", Font.BOLD, 25);
			Font smallerf = new Font("Calibri", Font.BOLD, 20);
			g.setColor(Color.white);

			// draw the rankings and informations
			for (int i = 0; i < Scrabble.numplayers; i ++)
			{
				g.setFont(f);
				g.setColor(Color.white);
				int samescorebefore = 0;
				for (int j = 0; j < i; j ++)
					if (rank[j] == rank[i])
						samescorebefore ++;

				if (i == 0)
					g.drawImage(profile1.getImage(), 77, 95 + (rank[i] + samescorebefore) * 83, this);
				else if (i == 1)
					g.drawImage(profile2.getImage(), 77, 95 + (rank[i] + samescorebefore) * 83, this);
				else if (i == 2)
					g.drawImage(profile3.getImage(), 77, 95 + (rank[i] + samescorebefore) * 83, this);
				else if (i == 3)
					g.drawImage(profile4.getImage(), 77, 95 + (rank[i] + samescorebefore) * 83, this);

				if (rank[i] == 0)
					g.drawImage(rank1.getImage(), 35, 106 + (rank[i] + samescorebefore) * 83, this);
				else if (rank[i] == 1)
					g.drawImage(rank2.getImage(), 35, 106 + (rank[i] + samescorebefore) * 83, this);
				else if (rank[i] == 2)
					g.drawImage(rank3.getImage(), 35, 106 + (rank[i] + samescorebefore) * 83, this);
				else if (rank[i] == 3)
					g.drawImage(rank4.getImage(), 33, 106 + (rank[i] + samescorebefore) * 83, this);


				Font display = new Font("Calibri", Font.BOLD, 16);

				g.drawString("" + grossscore[i], 275, 150 + (rank[i] + samescorebefore) * 83);
				if (penalties[i] > 0)
				{
					g.setFont(display);
					g.setColor(Color.black);
					g.drawString("Bonus", 397, 108 + (rank[i] + samescorebefore) * 83);
					g.setColor(Color.white);
					g.setFont(f);
					g.drawString("+" + penalties[i], 397, 150 + (rank[i] + samescorebefore) * 83);
				}
				else
				{
					g.setFont(display);
					g.setColor(Color.black);
					g.drawString("Penalties", 397, 108 + (rank[i] + samescorebefore) * 83);
					g.setColor(Color.white);
					g.setFont(f);
					g.drawString("" + penalties[i], 397, 150 + (rank[i] + samescorebefore) * 83);
				}

				g.setColor(Color.black);
				g.setFont(smallerf);
				g.drawString(Scrabble.p[i].name, 140, 115 + (rank[i] + samescorebefore) * 83);
				g.setColor(Color.white);
				g.setFont(f);
				if (Scrabble.p[i].points >= 0)
					g.drawString("" + Scrabble.p[i].points, 500, 150 + (rank[i] + samescorebefore) * 83);
				else
					g.drawString("0", 500, 150 + (rank[i] + samescorebefore) * 83);

				String rack = "";

				for (int j = 0; j < 7; j ++)
					if (Scrabble.p[i].rack[j] == ' ')
						rack += "*";
					else if (Scrabble.p[i].rack[j] == '0')
						;
					else 
						rack += Scrabble.p[i].rack[j];

				g.drawString(rack, 168, 150 + (rank[i] + samescorebefore) * 83);

				// display winning message 

				Font mf = new Font("Calibri", Font.BOLD, 20);
				g.setFont(mf);
				g.setColor(Color.black);
				if (numtie == 1)
					g.drawString(message, 20, 60);
				else 
				{
					g.drawString("There has been a tie.", 20, 60);
					g.drawString(message, 20, 80);
				}

			}

			repaint();

		}
	}

	class MouseListen extends MouseAdapter
	{
		public void mouseReleased(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();


			// rematch
			if (x >= 438 && x <= 524 && y >= 419 && y <= 449)
			{
				frame.setVisible(false);
				Scrabble.initialize();
			}

			// menu
			if (x >= 548 && x <= 634 && y >= 419 && y <= 449)
			{	
				frame.setVisible(false);
				Scrabble.frame.setVisible(false);
				Main.frame.setVisible(true);
			}

		}
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
		frame.setVisible(false);
		Scrabble.frame.setVisible(false);
		Main.frame.setVisible(true);
	}

	public void windowClosing(WindowEvent e)
	{
		frame.setVisible(false);
		Scrabble.frame.setVisible(false);
		Main.frame.setVisible(true);
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e) 
	{
	}

} 