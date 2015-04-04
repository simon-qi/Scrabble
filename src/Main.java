import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;

// This class allows the user to start the game, turn on/off sound, and access a help menu
class Main
{
	Drawing draw;
	static JFrame frame;
	ImageIcon intro = new ImageIcon("files/mainpage.png");
	static boolean sound;
	AudioClip music;
	ImageIcon returnbutton = new ImageIcon("files/returnbutton.png");
	static int imageon = 0; // the help image that is on, 0 indicates no help image
	ImageIcon help1 = new ImageIcon("files/Help1.png");
	ImageIcon help2 = new ImageIcon("files/Help2.png");
	ImageIcon help3 = new ImageIcon("files/Help3.png");
	ImageIcon help4 = new ImageIcon("files/Help4.png");


	// constructor
	public Main()
	{
		draw = new Drawing();
		frame = new JFrame("Scrabble");
		frame.setSize(1024 + 5, 768 + 25);
		frame.add(draw);
		draw.addMouseListener(new MouseListen());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// sound
		try
		{ 
			music = Applet.newAudioClip(new URL("file:files/Best Music for Studying - Alex Barry - 05 Awaiting Your Arrival.wav")); 
		} 
		catch (MalformedURLException mfe)
		{ 
		}
	}

	public static void main(String[] args)
	{
		new Main();
	}

	class Drawing extends JComponent
	{
		public void paint(Graphics g)
		{

			g.drawImage(intro.getImage(), 0, 0, this);	
			// draws the help appropriate images
			if (imageon == 1)
				g.drawImage(help1.getImage(), 0, 0, this);	
			else if (imageon == 2)
				g.drawImage(help2.getImage(), 0, 0, this);
			else if (imageon == 3)
				g.drawImage(help3.getImage(), 0, 0, this);
			else if (imageon == 4)
				g.drawImage(help4.getImage(), 0, 0, this);

			if (imageon != 0)
				g.drawImage(returnbutton.getImage(), 647, 0, this);

			repaint();
		}
	}

	class MouseListen extends MouseAdapter
	{
		public void mouseReleased(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();

			if (imageon != 0) // on help 
			{
				if (x >= 19 && x <= 222 && y >= 67 && y <= 95)
					imageon = 1;
				else if (x >= 19 && x <= 220 && y >= 106 && y <= 136)
					imageon = 2;
				else if (x >= 18 && x <= 220 && y >= 148 && y <= 180)
					imageon = 3;
				else if (x >= 19 && x <= 221 && y >= 190 && y <= 221)
					imageon = 4;
				else if (x >= 647 && x <= 674 && y >= 0 && y <= 27)
					imageon = 0;
			}
			else // not on help
			{
				if (x >= 454 && x <= 538 && y >= 262 && y <= 424) // play button
				{
					if (Scrabble.backmenu) // saved game
					{
						Object[] options = {"Yes", "No"};
						int n = JOptionPane.showOptionDialog(frame,"Do you wish to resume the current game?", "Scrabble", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,null, options, options[1]);

						if (n == 0)
						{
							Main.frame.setVisible(false);
							Scrabble.backmenu = false;
							Scrabble.frame.setVisible(true);
						}
						else if (n == 1)
						{
							frame.setVisible(false);
							new Selection();
						}
					}
					else
					{
						frame.setVisible(false);
						new Selection();
					}
				}
				// help
				else if (x >= 405 && x <= 444 && y >= 464 && y <= 542)
				{	
					imageon = 1;
				}
				// options
				else if (x >= 537 && x <= 598 && y >= 462 && y <= 528)
				{	
					boolean temp = sound;

					Object[] options = {"Yes", "No"};
					int n = JOptionPane.showOptionDialog(frame,"Do you want music?", "Options", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,null, options, options[1]);

					if (n == 0)
					{  
						sound = true;
					}
					else if (n == 1)
					{
						sound = false;
					}

					if (temp != sound)
					{
						if (sound)
							music.loop();
						else
							music.stop();
					}

				}
			}
		}
	}
} 