import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

// this class allows user to select players for one scrabble game
public class Selection implements MouseListener
{
	ImageIcon players2 = new ImageIcon("files/2player.png");
	ImageIcon players3 = new ImageIcon("files/3player.png");
	ImageIcon players4 = new ImageIcon("files/4player.png");
	ImageIcon yesai = new ImageIcon("files/yesai.png");
	ImageIcon noai = new ImageIcon("files/noai.png");
	static JTextField p1 = new JTextField(20);  // textfields for the players
	static JTextField p2 = new JTextField(20);
	static JTextField p3 = new JTextField(20);
	static JTextField p4 = new JTextField(20); 
	static JFrame frame;
	Font font = new Font("SansSerif", Font.BOLD, 20);
	ImageIcon menu = new ImageIcon("files/menubutton.png");
	ImageIcon plusbutton = new ImageIcon("files/plusbutton.png");
	static int players = 2;
	static String[] names = new String[4];
	static boolean[] ai = new boolean[4];

	// the constructor for the application
	public Selection()
	{	
		players = 2;
		frame = new JFrame("Scrabble");
		for (int i = 0; i < 4; i++) 
		{
			names[i] = "Player " + (i + 1);
		}
		for (int i = 0; i < 4; i++) 
		{
			ai[i] = false;
		}
		p1.setDocument(new JTextFieldLimit(10));
		p2.setDocument(new JTextFieldLimit(10));
		p3.setDocument(new JTextFieldLimit(10));
		p4.setDocument(new JTextFieldLimit(10));

		JPanel panel = new JPanel()
		{  
			// draws the backround images and custimizations
			protected void paintComponent(Graphics g)
			{

				if (players == 2) 
				{
					g.drawImage(players2.getImage(), 0, 0, null);
				}
				else if (players == 3)
				{
					g.drawImage(players3.getImage(), 0, 0, null);
					
					Font f = new Font("Calibri", Font.BOLD, 20);
					g.setFont(f);
					g.drawString("AI:", 718, 418);
					if (ai[2])
						g.drawImage(yesai.getImage(), 740, 400, null);
					else
						g.drawImage(noai.getImage(), 740, 400, null);
				}
				else if (players == 4)
				{
					g.drawImage(players4.getImage(), 0, 0, null);
					Font f = new Font("Calibri", Font.BOLD, 20);
					g.setFont(f);
					g.drawString("AI:", 718, 418);
					if (ai[2])
						g.drawImage(yesai.getImage(), 740, 400, null);
					else
						g.drawImage(noai.getImage(), 740, 400, null);
					
					g.drawString("AI:", 718, 456);
					if (ai[3])
						g.drawImage(yesai.getImage(), 740, 438, null);
					else
						g.drawImage(noai.getImage(), 740, 438, null);
				}

				g.drawImage(menu.getImage(), 200, 660, null);

				// plus button
				Font f = new Font("Calibri", Font.BOLD, 20);
				g.setFont(f);
				g.drawImage(plusbutton.getImage(), 219, 596, null);
				g.drawString("Add player", 255, 615);
				
				g.drawString("AI:", 718, 342);
				if (ai[0])
					g.drawImage(yesai.getImage(), 740, 324, null);
				else 
					g.drawImage(noai.getImage(), 740, 324, null);
				
				g.drawString("AI:", 718, 380);
				if (ai[1])
					g.drawImage(yesai.getImage(), 740, 362, null);
				else
					g.drawImage(noai.getImage(), 740, 362, null);

				repaint();
				super.paintComponent(g);
			}
		};    
		panel.setOpaque(false);
		panel.setLayout(null);
		// adding the textboxes with appropriate settings
		p1.setOpaque(false);
		p1.setText(names[0]);
		p1.setFont(font);
		p2.setOpaque(false);
		p2.setText(names[1]);
		p2.setFont(font);
		p3.setOpaque(false);
		p3.setText(names[2]);
		p3.setFont(font);
		p4.setOpaque(false);
		p4.setText(names[3]);
		p4.setFont(font);
		p1.setBounds(374, 323, 325, 27);
		p2.setBounds(374, 362, 325, 27);
		p3.setBounds(374, 401, 325, 27);
		p4.setBounds(374, 440, 325, 27); 
		p3.setVisible(false);
		p4.setVisible(false);
		panel.addMouseListener(this);
		frame.setResizable(false);
		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(p4);
		frame.add(panel);   
		frame.setSize(1024 + 5, 768 + 25);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);	
	}

	// main method runs the application
	public static void main(String []args) 
	{
		new Selection();
	}   

	// changes settings and updates graphics when user clicked appropriate coordinates
	public void mouseClicked (MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();	
		
		// changing ai status
		if (x >= 740 && x <= 770 && y >= 324 && y <= 354)
		    ai[0] = !ai[0];
		
		if (x >= 740 && x <= 770 && y >= 362 && y <= 392)
			ai[1] = !ai[1];
		
		if ((players == 3 || players == 4) && x >= 740 && x <= 770 && y >= 400 && y <= 430)
			ai[2] = !ai[2];
	    
		if (players == 4 && x >= 740 && x <= 770 && y >= 438 && y <= 468)
			ai[3] = !ai[3];

		// changing the number of players
		if (x >= 398 && x <= 416 && y >= 285 && y <= 306)
		{
			players = 2;
			p3.setVisible(false);
			p4.setVisible(false);

		}

		if (x >= 561 && x <= 579 && y >= 285 && y <= 306)
		{
			players = 3;
			p3.setVisible(true);
			p4.setVisible(false);

		}		
		if (x >= 722 && x <= 740 && y >= 285 && y <= 306)
		{
			players = 4;
			p3.setVisible(true);
			p4.setVisible(true);
		}		

		// plus button
		if (x >= 222 && x <= 245 && y >= 599  && y <= 623)
		{
			if (players == 2)
			{
				players = 3;
				p3.setVisible(true);
			}
			else if (players == 3)
			{
				players = 4;
				p4.setVisible(true);
			}


		}

		// back button
		if (x >= 205 && x <= 291 && y >= 664 && y <= 693)
		{
			frame.setVisible(false);
			Main.frame.setVisible(true);
		}

		// start button 		
		if (x >= 694 && x <= 811 && y >= 665 && y <= 696)
		{
			names[0] = p1.getText();
			names[1] = p2.getText();
			names[2] = p3.getText();
			names[3] = p4.getText();
			frame.setVisible(false);
			Scrabble.numplayers = players;
			new Scrabble();
		}		
	}	

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased (MouseEvent e)
	{
	}

	public void mouseEntered (MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}
}