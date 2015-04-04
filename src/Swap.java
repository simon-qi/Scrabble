import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// class that allows the user swap tiles
class Swap
{
	Drawing draw;
	static boolean[] highlight;
	static JDialog frame;
	ImageIcon swap = new ImageIcon("files/swap2.png");
	static int numhighlight;

	// constructor
	public Swap()
	{
		draw = new Drawing();
		frame = new JDialog(Scrabble.frame, "Swap", true);
		frame.setSize(477 + 5,304 + 25);
		frame.add(draw);
		draw.addMouseListener(new MouseListen());
		highlight = new boolean[7];
		frame.setResizable(false);
		frame.setVisible(true);
		numhighlight = 0;
	}

	public static void main(String[] args)
	{
		new Swap();
	}

	class Drawing extends JComponent
	{
		public void paint(Graphics g)
		{
			g.drawImage(swap.getImage(), 0, 0, this);

			// draw the rack
			for (int i = 0; i < 7; i ++)
				if (Scrabble.p[Scrabble.currentp].rack[i] == ' ')
					g.drawImage(Scrabble.lettersrack[26].getImage(), 10 + i * 65, 80, 59, 60, this);
				else if (Scrabble.p[Scrabble.currentp].rack[i] == '0')
					;
				else
					g.drawImage(Scrabble.lettersrack[Scrabble.p[Scrabble.currentp].rack[i] - 'a'].getImage(), 10 + i * 65, 80, 59, 60, this);

			g.setColor(Color.yellow);
			// if highlighted - draw a yellow box around tile
			for (int i = 0; i < 7; i ++)
				if (highlight[i])
				{
					g.fillRect(10 + i * 65, 80, 5, 60);
					g.fillRect(10 + i * 65, 80, 60, 5);
					g.fillRect(10 + i * 65 + 59 - 5, 80, 5, 60);
					g.fillRect(10 + i * 65, 80 + 60 - 5, 60, 5);
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


			// click on rack
			if (x >= 10 && x < 465 && y >= 80 && y <= 140 && (x - 10) % 65 < 60)
			{
				int index = (x - 10) / 65;

				if (!highlight[index] && numhighlight < Scrabble.size)
				{
					highlight[index] = true;
					numhighlight ++;
				}
				else if (!highlight[index] && numhighlight >= Scrabble.size)
				{
					JOptionPane.showMessageDialog(frame, "There are not enough tiles to swap more letters.");
				}
				else
				{
					highlight[index] = false;
					numhighlight --;
				}
			} 

			//return
			if (x >= 253 && x <= 385 && y >= 225 && y <= 270)
			{
				frame.setVisible(false);
			}

			boolean swap = false;
			for (int i = 0; i < 7; i ++)
				if (highlight[i])
					swap = true;

			// swap
			if (x >= 97 && x <= 232 && y >= 225 && y <= 270 && swap)
			{
				Scrabble.passes ++;

				if (Scrabble.passes == 6)
					Scrabble.gameEnd = true;	

				int sizelocs = Scrabble.size;
				int[] possiblelocs = new int[Scrabble.size];
				for (int i = 0; i < Scrabble.size; i++)
					possiblelocs[i] = i;

				for (int i = 0; i < 7; i ++)
					if (highlight[i])
					{ 	
						int loc = (int)(Math.random() * sizelocs);

						char temp = Scrabble.p[Scrabble.currentp].rack[i];
						Scrabble.p[Scrabble.currentp].rack[i] = Scrabble.bag[possiblelocs[loc]];
						Scrabble.bag[possiblelocs[loc]] = temp;

						// remove from possible locations generated
						for (int j = loc; j < Scrabble.size - 1; j++)
						{
							possiblelocs[j] = possiblelocs[j + 1];
						}

						sizelocs --;
					}

				// update the history
				if (Scrabble.indexhis == 5)
				{
					for (int i = 0; i < 4 ; i ++)
					{
						Scrabble.playerhis[i] = Scrabble.playerhis[i + 1];
						Scrabble.pointhis[i] = Scrabble.pointhis[i + 1];
						Scrabble.totalhis[i] = Scrabble.totalhis[i + 1];
						Scrabble.wordhis[i] = Scrabble.wordhis[i + 1];
					}
				}

				int actualindex = -1;

				if (Scrabble.indexhis == 5)
					actualindex = 4;
				else
					actualindex = Scrabble.indexhis;		

				Scrabble.playerhis[actualindex] = Scrabble.p[Scrabble.currentp].name;
				Scrabble.pointhis[actualindex] = - 1;
				Scrabble.totalhis[actualindex] = Scrabble.p[Scrabble.currentp].points;
				Scrabble.wordhis[actualindex] = "Swap Letters";

				if (Scrabble.indexhis < 5)
					Scrabble.indexhis ++;


				if (!Scrabble.gameEnd)
				{
					// switch players
					if (Scrabble.currentp == Scrabble.numplayers - 1)
						Scrabble.currentp = 0;
					else
						Scrabble.currentp ++;
				}
				else
				{
					// appropriate actions if 6 passes/swap occured - game end
					// swap counts as a pass
					frame.setVisible(false);
					GameEnd.play = false;
					new GameEnd();
				}
				frame.setVisible(false);
			}
		}
	}
} 