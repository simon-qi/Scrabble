import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Scrabble
{
	static boolean gameEnd; // whether the game has ended
	Drawing draw; 
	static boolean allwordsvalid;
	ImageIcon pic2p = new ImageIcon("files/2Pboard.png");  
	ImageIcon pic3p = new ImageIcon("files/3Pboard.png"); 
	ImageIcon pic4p = new ImageIcon("files/4Pboard.png");  
	ImageIcon rackpic = new ImageIcon("files/rack.png");
	ImageIcon recall = new ImageIcon("files/recall.png");
	ImageIcon scorebox = new ImageIcon("files/scorebox.png");
	ImageIcon returnbutton = new ImageIcon("files/returnbutton.png");
	ImageIcon play = new ImageIcon("files/play.png");
	ImageIcon pass = new ImageIcon("files/pass.png");
	ImageIcon shuffle = new ImageIcon("files/shuffle.png");
	static JFrame frame;
	ImageIcon tile = new ImageIcon("files/Tilebag2.png");
	static Player[] p = new Player[4]; // the players - stored as an array of objects
	static int numplayers; // number of players
	static boolean passed;


	static int currentp; // player that is current playing
	static int passes; // number of successive passes

	static char[][] board; // the board  ' ' represents blank  '0' represents nothing  appropriate letter represent letters
	static ImageIcon[] letters = new ImageIcon[27]; // an array storing the images
	static ImageIcon[] lettersrack = new ImageIcon[27]; // an array storing the images on rack
	static ImageIcon[] redletters = new ImageIcon[26]; 
	static boolean[][] blank; // if letter on board is made from blank tile

	static boolean isDragged; // is true when the mouse is dragging a letter
	static char letterDragged; // the tile that is dragged
	static int xdrag, ydrag; // keeps track of coordinates while dragging using mousemotionlistener

	// these variables are for in case person drags tile to invalid location (the tile would be moved back to original place)
	static int racklocfrom; // location on the rack the tile is from 
	static int xfrom; // x coordinate on board the tile is from
	static int yfrom; // y coordinate on board the tile is from
	static boolean fromboard; // true if dragged from board, false if dragged from rack

	// variables used to generate random letters for the rack
	static char[] bag; // the bag of tiles 
	static int size; // number of tiles in back
	static boolean bagshown; // if the bag is shown

	static HashSet<String> words; // array of words in dictionary

	// variables to display the points that would be gained
	static int pointsround; // point that would be gained for current round
	static int xdisplay;
	static int ydisplay; // place to displace the points that would be gained
	static boolean verticalplace;
	static boolean backmenu; // if person clicks the menu to return

	static boolean firstturn;

	static boolean[][] permanent; // whether the character on board is played in current turn or previous turns

	// arrays to keep track of triple word, double word, triple letter, and double letter locations
	static boolean[][] dl;
	static boolean[][] tl;
	static boolean[][] dw;
	static boolean[][] tw;

	// arrays to keep track of the history
	static String[] playerhis;
	static String[] wordhis;
	static int[] pointhis;
	static int[] totalhis;
	static int indexhis;




	public Scrabble()
	{
		draw = new Drawing();
		frame = new JFrame("Scrabble");
		initialize();

		// creating images and setting up 
		for (int i = 0; i < 26; i ++)
			letters[i] = new ImageIcon("files/" + (char)('a' + i) + ".png");
		letters[26] = new ImageIcon("files/blank.png");

		for (int i = 0; i < 26; i ++)
			lettersrack[i] = new ImageIcon("files/" + (char)('a' + i) + "60.png");
		lettersrack[26] = new ImageIcon("files/blank60.png");

		for(int i = 0; i < 26; i ++)
			redletters[i] = new ImageIcon("files/" + (char)('a' + i) + "red.png");

		words = new HashSet<String>();
		// reading the dictionary and storing in array	
		try
		{
			// read from file and store in the array
			FileReader fr = new FileReader("files/TWL06.txt");
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();

			while (s != null)
			{
				words.add(s);
				s = br.readLine();
			}
			
			
			br.close();
		}
		catch(IOException e)
		{
		}	


		// setting up graphics
		frame.setSize(1030,795 - 36);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(draw);
		frame.setResizable(false);
		frame.setVisible(true);
		draw.addMouseListener(new MouseListen());
		draw.addMouseMotionListener(new MouseMotionListen());
	}

	// method to create original conditiosn
	public static void initialize()
	{
		bagshown = false;
		xdisplay = -100;
		ydisplay = -100;
		backmenu = false;
		gameEnd = false;
		passes = 0;
		board = new char[15][15];
		blank = new boolean[15][15];
		bag = new char[100];
		firstturn = true;
		permanent = new boolean[15][15]; 
		dl = new boolean[15][15];
		tl = new boolean[15][15];
		dw = new boolean[15][15];
		tw = new boolean[15][15];
		playerhis = new String[5];
		wordhis = new String[5];
		pointhis = new int[5];
		totalhis = new int[5];
		indexhis = 0;

		// initialize board to blank
		for (int i = 0; i < 15; i ++)
			for (int j = 0; j < 15; j++)
				board[i][j] = '0';					

		// initialize tile bag
		size = 100;
		for (int num = 0; num <= 8; num ++)
			bag[num] = 'a';
		bag[9] = 'b';
		bag[10] = 'b';		  
		bag[11] = 'c';
		bag[12] = 'c';
		for (int num = 13; num <= 16; num++)
			bag[num] = 'd';
		for (int num = 17; num <= 28; num++)
			bag[num] = 'e';
		bag[29] = 'f';
		bag[30] = 'f';
		for (int num = 31; num <= 33; num++)
			bag[num] = 'g';
		bag[34] = 'h';
		bag[35] = 'h';
		for (int num = 36; num <= 44; num++)
			bag[num] = 'i';
		bag[45] = 'j';
		bag[46] = 'k';
		for (int num = 47; num <= 50; num++)
			bag[num] = 'l';
		bag[51] = 'm';
		bag[52] = 'm';
		for (int num = 53; num <= 58; num++)
			bag[num] = 'n';
		for (int num = 59; num <= 66; num++)
			bag[num] = 'o';
		bag[67] = 'p';
		bag[68] = 'p';
		bag[69] = 'q';
		for (int num = 70; num <= 75; num++)
			bag[num] = 'r';
		for (int num = 76; num <= 79; num++)
			bag[num] = 's';
		for (int num = 80; num <= 85; num++)
			bag[num] = 't';
		for (int num = 86; num <= 89; num++)
			bag[num] = 'u';
		bag[90] = 'v';
		bag[91] = 'v';
		bag[92] = 'w';
		bag[93] = 'w';
		bag[94] = 'x';
		bag[95] = 'y';
		bag[96] = 'y';
		bag[97] = 'z';
		bag[98] = ' ';
		bag[99] = ' ';


		for (int i = 0; i < numplayers; i ++)
		{
			p[i] = new Player(Selection.names[i], Selection.ai[i]);
		}

		currentp = 0;


		// set appropriate boxes for special locations to true
		tw[0][0] = true;
		tw[0][14] = true;
		tw[7][0] = true;
		tw[14][0] = true;
		tw[0][7] = true;
		tw[7][14] = true;
		tw[14][7] = true;
		tw[14][14] = true;

		dl[3][0] = true;
		dl[6][2] = true;
		dl[8][2] = true;
		dl[0][3] = true;
		dl[7][3] = true;
		dl[14][3] = true;
		dl[2][6] = true;
		dl[6][6] = true;
		dl[8][6] = true;
		dl[12][6] = true;
		dl[3][7] = true;
		dl[11][7] = true;
		dl[2][8] = true;
		dl[6][8] = true;
		dl[8][8] = true;
		dl[12][8] = true;
		dl[0][11] = true;
		dl[7][11] = true;
		dl[14][11] = true;
		dl[6][12] = true;
		dl[8][12] = true;
		dl[3][14] = true;
		dl[11][14] = true;
		dl[11][0] = true;

		tl[5][1] = true;
		tl[9][1] = true;
		tl[1][5] = true;
		tl[5][5] = true;
		tl[9][5] = true;
		tl[13][5] = true;
		tl[1][9] = true;
		tl[5][9] = true;
		tl[9][9] = true;
		tl[13][9] = true;
		tl[5][13] = true;
		tl[9][13] = true;

		dw[1][1] = true;
		dw[13][1] = true;
		dw[2][2] = true;
		dw[12][2] = true;
		dw[3][3] = true;
		dw[11][3] = true;
		dw[4][4] = true;
		dw[10][4] = true;
		dw[4][10] = true;
		dw[10][10] = true;
		dw[3][11] = true;
		dw[11][11] = true;
		dw[2][12] = true;
		dw[12][12] = true;
		dw[1][13] = true;
		dw[13][13] = true;	

		dw[7][7] = true; // center
	}


	public static void main(String[] args)
	{
		new Scrabble();
	}

	class Drawing extends JComponent
	{
		public void paint(Graphics g)
		{
			if (p[currentp].ai)
			{
				p[currentp].move();
				calculate(false);
				if (!allwordsvalid)
					p[currentp].recall();
			}
			
			// draw board using appropriate images 
			if (numplayers == 2) 
				g.drawImage(pic2p.getImage(), 0, -1, this);
			else if (numplayers == 3)
				g.drawImage(pic3p.getImage(), 0, 0, this);
			else if (numplayers == 4)
				g.drawImage(pic4p.getImage(), 0, 0, this);

			for (int i = 0; i < 15; i ++)
				for (int j = 0; j < 15; j ++)
					if (board[i][j] != '0')
						if (!blank[i][j]) 
						{
							g.drawImage(letters[board[i][j] - 'a'].getImage(), 377 + i * 42, 41 + j * 42 - 36, this);
						}
						else
							g.drawImage(redletters[board[i][j] - 'a'].getImage(), 377 + i * 42, 41 + j * 42 - 36, 42, 42, this);

			if (p[currentp].ai)
			{
				for (int i = 0; i < permanent.length; i++)
				{
					for (int j = 0; j < permanent[i].length; j++)
					{
						if (!permanent[i][j] && board[i][j] != '0')
						{
							g.setColor(Color.yellow);
							g.drawRect(377 + i * 42, 41 + j * 42 - 36, 42, 42);
						    g.setColor(Color.black);
						}
					}
				}
			}
			
			// draw rack using appropriate images   

			for (int i = 0; i < 7; i ++)
				if (p[currentp].rack[i] == ' ')
					g.drawImage(lettersrack[26].getImage(), 535 + i * 59, 698 - 36, 59, 60, this);
				else if (p[currentp].rack[i] == '0')
					;
				else
					g.drawImage(lettersrack[p[currentp].rack[i] - 'a'].getImage(), 535 + i * 59, 698 - 36, 59, 60, this);


			// the past 5 turns
			for (int i = 0; i < indexhis; i ++)
			{
				g.drawString(playerhis[i], 12, 525 + i * 20 - 36);
				g.drawString(wordhis[i], 125, 525 + i * 20 - 36);
				if (pointhis[i] == -1)
					g.drawString("-", 236, 525 + i * 20 - 36);
				else
					g.drawString("" + pointhis[i], 236, 525 + i * 20 - 36);
				g.drawString("" + totalhis[i], 310, 525 + i * 20 - 36);
			}

			g.setColor(Color.white);
			Font f = new Font("Calibri", Font.BOLD, 25);
			Font flarge = new Font("Calibri", Font.BOLD, 40);
			// score
			for (int i = 0; i < numplayers; i ++)
			{
				g.setFont(f);
				g.setColor(Color.black);
				g.drawString(p[i].name + ":", 75, 200 + 73 * i - 36);
				g.setColor(Color.white);
				g.setFont(flarge);
				if (p[i].points >= 0)
					g.drawString(p[i].points +"", 75, 235 + 73 * i - 36);
				else
					g.drawString("0", 75, 235 + 73 * i - 36);
			}
			// tile left
			g.drawString(size +"", 35, 725 - 36);

			// drawing play button and recall button, if letters are played
			boolean plays = false;

			for (int i = 0; i < 15; i ++)
				for (int j = 0; j < 15; j++)
					if (board[i][j] != '0' && !permanent[i][j])
						plays = true;

			if (plays)
			{
				g.drawImage(play.getImage(), 948, 685 - 36, this);
				g.drawImage(recall.getImage(), 460, 685 - 36, this);
			}
			else
			{
				g.drawImage(pass.getImage(), 948, 685 - 36, this);
				g.drawImage(shuffle.getImage(), 460, 685 - 36, this);
			}

			g.setColor(Color.blue);

			g.fillRect(12, 136 + currentp * 73, 361, 5);
			g.fillRect(12, 136 + currentp * 73, 5, 73);
			g.fillRect(12 + 361 - 5, 136 + currentp * 73, 5, 73);
			g.fillRect(12,  136 + (currentp + 1) * 73 - 5, 361, 5);


			// draw the tile that is dragged
			if (isDragged)
			{
				if (fromboard && blank[xfrom][yfrom])
				{
					if (letterDragged != ' ')
						g.drawImage(redletters[letterDragged - 'a'].getImage(), xdrag - 21, ydrag - 21, 42, 42, this);
					else
						g.drawImage(redletters[26].getImage(), xdrag - 21, ydrag - 21, 42, 42, this);
				}
				else
				{
					if (letterDragged != ' ')
						g.drawImage(letters[letterDragged - 'a'].getImage(), xdrag - 21, ydrag - 21, this);
					else
						g.drawImage(letters[26].getImage(), xdrag - 21, ydrag - 21, this);
				}
			}

			g.setColor(Color.black);
			Font sf = new Font("Calibri", Font.BOLD, 20);
			g.setFont(sf);

			// number of points that would be gained in round
			if (pointsround != -1)
				if (!verticalplace)
				{
					g.drawImage(scorebox.getImage(), 378 + (xdisplay + 1) * 42, 41 + (ydisplay + 1) * 42 - 36 - 25, null);
					g.setColor(Color.white);
					g.drawString("" + pointsround, 378 + (xdisplay + 1) * 42 + 3, 41 + (ydisplay + 1) * 42 - 36 - 5);
				}
				else
				{
					g.drawImage(scorebox.getImage(), 378 + xdisplay  * 42 - 3, 41 + (ydisplay + 2) * 42 - 36 - 37, null);
					g.setColor(Color.white);
					g.drawString("" + pointsround, 378 + xdisplay  * 42 + 3, 41 + (ydisplay + 2) * 42 - 36 - 17);
				}

			if (bagshown)
			{
				g.drawImage(tile.getImage(), 0, 0, this);

				Font bf = new Font("Calibri", Font.BOLD, 20);
				g.setColor(Color.black);
				g.setFont(bf);

				g.drawString("" + Scrabble.size, 265, 80);

				g.drawImage(returnbutton.getImage(), 735, 0, this);
			}   
			
			repaint();			
		}
	}

	class MouseListen extends MouseAdapter
	{
		// mousePressed is used to initiate dragging
		public void mousePressed(MouseEvent e)
		{
			if (!p[currentp].ai && !bagshown)
			{
				if (!isDragged)
				{
					int x = e.getX();
					int y = e.getY();

					// pressed on valid location the board (the location on board has a letter that is to be removed and is not played in previous round)
					if (x >= 377 && x < 1007 && y >= 41 - 36 && y < 671 - 36 && board[(x - 377) / 42][(y - 41 + 36) / 42] != '0' &&  !permanent[(x - 377) / 42][(y - 41 + 36) / 42])
					{
						int boxx = (x - 377) / 42;
						int boxy = (y - 41 + 36) / 42;	
						// removing letter from board and start dragging
						letterDragged = board[boxx][boxy];
						board[boxx][boxy] = '0';
						xfrom = boxx;
						yfrom = boxy;
						fromboard = true;
						xdrag = x;
						ydrag = y;
						draw.repaint();
						isDragged = true;
					}
					// pressed on valid location the rack (the location on rack has a letter that is to be removed)
					if (y >= 698 - 36 && y <= 758 - 36 && x >= 535 && x < 948 && p[currentp].rack[(x - 535) / 59] != '0')
					{
						int indexwanted = (x - 535) / 59;
						// removing letter from rack and start dragging
						letterDragged = p[currentp].rack[indexwanted];
						p[currentp].rack[indexwanted] = '0';
						racklocfrom = indexwanted;
						fromboard = false;
						xdrag = x;
						ydrag = y;
						draw.repaint();
						isDragged = true;
					}
				}

				calculate(false);
			}
		}

		// mouseReleased is used to stop dragging, and put tile on the appropriate place and also just as a buttons to play words and do other stuff
		public void mouseReleased(MouseEvent e)
		{		
			if (!bagshown && !isDragged)
			{
				// play word/pass
				if (e.getX() >= 958 && e.getX() <= 1009 && e.getY() >= 654 && e.getY() <= 720)
				{							
					// if any new tiles played 
					boolean newtiles = false;
					for (int i = 0; i < 15; i ++)
						for (int j = 0; j < 15; j++)
							if (board[i][j] != '0' && !permanent[i][j])
								newtiles = true;
					
					if (newtiles)
					{
						// calls the long method for checking points and updating scores
						calculate(true);
					}
					// played no letter - pass turn
					else
					{	

						Object[] options = {"Yes", "No"};
						int n = JOptionPane.showOptionDialog(frame,"Are you sure you want to pass your turn and score 0 points?", "Pass", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,null, options, options[1]);


						// if the person clicks okay
						if (n == 0)
						{	

							passes ++;
							if (passes == 6)
								gameEnd = true;	

							// update history
							if (indexhis == 5)
							{
								for (int i = 0; i < 4 ; i ++)
								{
									playerhis[i] = playerhis[i + 1];
									pointhis[i] = pointhis[i + 1];
									totalhis[i] = totalhis[i + 1];
									wordhis[i] = wordhis[i + 1];
								}
							}

							int actualindex = -1;

							if (indexhis == 5)
								actualindex = 4;
							else
								actualindex = indexhis;

							playerhis[actualindex] = p[currentp].name;
							pointhis[actualindex] = - 1;
							totalhis[actualindex] = p[currentp].points;
							wordhis[actualindex] = "Skip Turn";

							if (indexhis < 5)
								indexhis ++;


							if (!gameEnd)
							{
								// switch players
								if (currentp == numplayers - 1)
									currentp = 0;
								else
									currentp ++;
							}
							else
							{
								GameEnd.play = false;
								new GameEnd();
							}

						}

						draw.repaint();
					}
				}
			}
			
			if (!p[currentp].ai && !bagshown)
			{
				// if any new tiles played 
				boolean newtiles = false;
				for (int i = 0; i < 15; i ++)
					for (int j = 0; j < 15; j++)
						if (board[i][j] != '0' && !permanent[i][j])
							newtiles = true;

				if (isDragged)
				{
					int x = e.getX();
					int y = e.getY();

					// release mouse on valid location on board (not taken by a letter)  - adding letter to board  
					if (x >= 377 && x < 1007 && y >= 41 - 36 && y < 671 - 36 && board[(x - 377) / 42][(y - 41 + 36) / 42] == '0')
					{
						int boxx = (x - 377) / 42;
						int boxy = (y - 41 + 36) / 42;
						// special case for blank characters
						if (letterDragged == ' ')
						{
							xdrag = 377 + boxx * 42 + 21;
							ydrag = 41 + boxy * 42 + 21 - 36;

							String input = JOptionPane.showInputDialog("What character do you want?");
							if (input != null && input.length() == 1 && input.toLowerCase().charAt(0) >= 'a' && input.toLowerCase().charAt(0) <= 'z')
							{								
								board[boxx][boxy] = input.toLowerCase().charAt(0);
								blank[boxx][boxy] = true;
							}
							// entering invalid input would return letter to where it came from
							else
							{
								p[currentp].rack[racklocfrom] = letterDragged;
							}
						}
						else if (fromboard && blank[xfrom][yfrom] == true)
						{
							blank[xfrom][yfrom] = false;
							board[boxx][boxy] = letterDragged;
							blank[boxx][boxy] = true;
						}
						else
							board[boxx][boxy] = letterDragged;
						isDragged = false;
						draw.repaint();
					}		
					// release mouse on valid location on rack (not taken by a letter)  - adding letter to back to rack  		
					else if (y >= 698 - 36 && y <= 758 - 36 && x >= 535 && x < 948 && p[currentp].rack[(x - 535) / 59] == '0')
					{
						int indexwanted = (x - 535) / 59;
						if (fromboard && blank[xfrom][yfrom])
						{
							p[currentp].rack[indexwanted] = ' ';
							blank[xfrom][yfrom] = false;
						}
						else
							p[currentp].rack[indexwanted] = letterDragged;
						isDragged = false;
						draw.repaint();    
					}
					// switching letter locations on rack
					else if (y >= 698 - 36 && y <= 758 - 36 && x >= 535 && x < 948 && p[currentp].rack[(x - 535) / 59] != '0' && !fromboard)
					{
						int indexwanted = (x - 535) / 59;
						p[currentp].rack[racklocfrom] = p[currentp].rack[indexwanted];
						p[currentp].rack[indexwanted] = letterDragged;
						isDragged = false;
						draw.repaint(); 
					}
					// placing tile on board to non-empty rack location, need to move tile
					else if (y >= 698 - 36 && y <= 758 - 36 && x >= 535 && x < 948 && p[currentp].rack[(x - 535) / 59] != '0' && fromboard)
					{
						int firstblank = 0;
						for (int i = 6; i >= 0 ; i --)
							if (p[currentp].rack[i] == '0')
								firstblank = i;

						int indexwanted = (x - 535) / 59;
						p[currentp].rack[firstblank] = p[currentp].rack[indexwanted];

						if (fromboard && blank[xfrom][yfrom])
						{
							p[currentp].rack[indexwanted] = ' ';
							blank[xfrom][yfrom] = false;
						}
						else
							p[currentp].rack[indexwanted] = letterDragged;
						isDragged = false;
						draw.repaint();    
					}
					// release mouse on invalid place - return letter to where it came from
					else
					{
						if (fromboard)
							board[xfrom][yfrom] = letterDragged;
						else
							p[currentp].rack[racklocfrom] = letterDragged;
						isDragged = false;
						draw.repaint();
					}
				}
				else
				{

					// shuffle
					if (e.getX() >= 460 && e.getX() <= 531 && e.getY() >= 685 - 36 && e.getY() <= 763 - 36)
					{

						if (!newtiles)
							p[currentp].shuffle();
						else
							p[currentp].recall();

						draw.repaint();
					}

					// dictionary
					if (e.getX() >= 115 && e.getX() <= 186 && e.getY() >= 685 - 36 && e.getY() <= 763 - 36)
					{
						new Dictionary();

					}

					// swap
					if (e.getX() >= 379 && e.getX() <= 450 && e.getY() >= 685 - 36 && e.getY() <= 763 - 36)
					{
						if (size != 0 && !newtiles)
						{
							new Swap();
						}
						else if (size == 0)
							JOptionPane.showMessageDialog(frame, "The are no more tiles in the bag.");
						else if (newtiles)
							JOptionPane.showMessageDialog(frame, "You can not swap after tiles have been placed.");
					}

					// tile bag
					if (e.getX() >= 21 && e.getX() <= 93 && e.getY() >= 665 - 36 && e.getY() <= 757 - 36)
					{
						bagshown = true;
					}

					// return to menu
					if (e.getX() >= 24 && e.getX() <= 83 && e.getY() >= 75 && e.getY() <= 100)
					{
						backmenu = true;
						Scrabble.frame.setVisible(false);
						Main.frame.setVisible(true);
					}
				}

				calculate(false);
			}
			else
			{
				int x = e.getX();
				int y = e.getY();

				if (x >= 735 && x <= 762 && y >= 0 && y <= 27)
					bagshown = false;
			}
		}
	}

	// method used to keep track of coordinate while dragging
	class MouseMotionListen extends MouseMotionAdapter
	{
		public void mouseDragged(MouseEvent e)
		{
			xdrag = e.getX();
			ydrag = e.getY();
			draw.repaint();
		}
	}

	// method that calculates the points that would be gained in the round
	// and takes appropriate actions if the play button was pressed as well (indicated by parameter play)
	public static void calculate(boolean play)
	{

		int[] turnx = new int[7]; // the x coordinates of the characters played
		int[] turny = new int[7]; // the y coordinates of the characters played
		int index = 0; // the number of newly played letters

		String mainword = "";

		pointsround = -1; // point for the current round

		// find all the new coordinates (not permanent and played in previous round)
		for (int i = 0; i < 15; i ++)
			for (int j = 0; j < 15; j++)
				if (!permanent[i][j] && board[i][j] != '0')
				{
					turnx[index] = i;
					turny[index] = j;
					index ++;
				}

		// if the person played any new letters
		if (index != 0)
		{
			boolean horizontal = true; // if word is placed horizontally and is valid in terms of postion
			boolean vertical = true; // if word is placed vertically and is valid in terms of postion

			// must go through star if first turn
			boolean star = false;
			for (int i = 0; i < index; i ++)
				if (turnx[i] == 7 && turny[i] == 7)
					star = true;	  

			if (firstturn && !star)
			{
				horizontal = false;
				vertical = false; 
			}

			// if first turn, can't play only 1 letter
			if (firstturn && index == 1)
			{
				horizontal = false;
				vertical = false;
			}

			boolean samex = true;
			boolean samey = true;
			// checking for same x/y coordinates
			for (int i = 0; i < index - 1; i++)
			{
				if (turny[i] != turny[i + 1])
				{
					horizontal = false;
					samex = false;
				}

				if(turnx[i] != turnx[i + 1])
				{
					vertical = false;
					samey = false;
				}
			}

			boolean gap = false;

			// checking for blanks in between that would make move invalid
			if (horizontal)
			{
				for (int i = turnx[0]; i <= turnx[index - 1]; i ++)
					if (board[i][turny[0]] == '0')
					{
						horizontal = false;
						gap = true;
					}
			}

			if (vertical)
			{
				for (int i = turny[0]; i <= turny[index - 1]; i ++)
					if (board[turnx[0]][i] == '0')
					{
						vertical = false;
						gap = true;
					}
			}			

			boolean connected = false;
			// checking if touching permanent coordinates if not first turn	
			if (!firstturn)
			{
				for (int i = 0; i < index; i ++)
				{
					if (turny[i] + 1 <= 14 && board[turnx[i]][turny[i] + 1] != '0' && permanent[turnx[i]][turny[i] + 1])
						connected = true;
					if (turny[i] - 1 >= 0 && board[turnx[i]][turny[i] - 1] != '0' && permanent[turnx[i]][turny[i] - 1])
						connected = true;
					if (turnx[i] + 1 <= 14 && board[turnx[i] + 1][turny[i]] != '0' && permanent[turnx[i] + 1][turny[i]])
						connected = true;
					if (turnx[i] - 1 >= 0 && board[turnx[i] - 1][turny[i]] != '0' && permanent[turnx[i] - 1][turny[i]])
						connected = true;
				}

				if (!connected)
				{
					horizontal = false;
					vertical = false;
				}		
			}

			if (play)
			{
				// display appropriate error messages if necessary

				if (firstturn && !star)
				{
					JOptionPane.showMessageDialog(frame,"The first word must go through the centre.");
				}
				else if (firstturn && index == 1)
				{
					JOptionPane.showMessageDialog(frame, "All words must be at least 2 letters long.");
				}
				else if (!samex && !samey)
				{
					JOptionPane.showMessageDialog(frame, "Your tiles must be placed in the same row or column.");
				}
				else if (gap)
				{
					JOptionPane.showMessageDialog(frame, "Your words must not contain a gap.");
				}
				else if (!connected && !firstturn)
				{
					JOptionPane.showMessageDialog(frame, "Your words must connect to existing tiles.");
				}
			}

			String invalidWords = ""; // message displaying invalid words
			boolean invalidfound = false;

			if (vertical || horizontal)
			{
				pointsround = 0;
				allwordsvalid = true;

				if (vertical)
				{

					// determining top and bottom coordinates of the vertical word
					int top = turny[0];

					while (top > 0 && board[turnx[0]][top - 1] != '0')
						top --;

					int bottom = turny[index - 1];

					while (bottom < 14 && board[turnx[0]][bottom + 1] != '0')
						bottom ++;

					int points;
					String wordattempt;

					xdisplay = turnx[0];
					ydisplay = bottom;
					verticalplace = true;

					int temp = 0;

					if (top != bottom)
					{
						// creating the vertical word and calculating points 
						points = verticalPoint(top, bottom, turnx[0]);
						pointsround += points;
						temp = points;


						wordattempt = verticalWord(top, bottom, turnx[0]);
						mainword = wordattempt;

						if (!isWord(wordattempt))
						{
							if (!invalidfound)
							{
								invalidWords = wordattempt;
								invalidfound = true;
							}
							allwordsvalid = false;
						}
						else
						{
						}
					}

					// creating any horizontal words also formed in the process
					for (int i = 0; i < index; i ++)
					{
						// determining left most and right most coordinates
						int left = turnx[0];

						while (left > 0 && board[left - 1][turny[i]] != '0')
							left --;

						int right = turnx[0];

						while (right < 14 && board[right + 1][turny[i]] != '0')
							right ++;

						// creating the horizontal word and calculating points 
						if (left != right)
						{
							points = horizontalPoint(left, right, turny[i]);
							pointsround += points;

							wordattempt = horizontalWord(left, right, turny[i]);


							if (!isWord(wordattempt))
							{
								if (!invalidfound)
								{
									invalidWords = wordattempt;
									invalidfound = true;
								}
								allwordsvalid = false;
							}
							else
							{
								;
							}

							if (index == 1 && points > temp)
							{
								mainword = wordattempt;
								xdisplay = right;
								ydisplay = turny[0];
								verticalplace = false;
								if (right == 14)
									verticalplace = true;
							}

						}
					}     
				}
				else if (horizontal)
				{												
					// determining left most and right most coordinates
					int left = turnx[0];

					while (left > 0 && board[left - 1][turny[0]] != '0')
						left --;

					int right = turnx[index - 1];

					while (right < 14 && board[right + 1][turny[0]] != '0')
						right ++;

					xdisplay = right;
					ydisplay = turny[0];


					verticalplace = false;

					if (xdisplay == 14)
					{
						verticalplace = true;
					}

					int points;
					String wordattempt;

					if (left != right)
					{
						// creating the horizontal word and calculating points 
						points = horizontalPoint(left, right, turny[0]);
						pointsround += points;


						wordattempt = horizontalWord(left, right, turny[0]);

						if (!isWord(wordattempt))
						{
							if (!invalidfound)
							{
								invalidWords = wordattempt;
								invalidfound = true;
							}
							allwordsvalid = false;
						}
						else
						{
							mainword = wordattempt;
						}
					}

					// creating any vertical words also formed in the process
					for (int i = 0; i < index; i ++)
					{
						// determining top and bottom coordinates of the vertical word
						int top = turny[0];

						while (top > 0 && board[turnx[i]][top - 1] != '0')
							top --;

						int bottom = turny[0];

						while (bottom < 14 && board[turnx[i]][bottom + 1] != '0')
							bottom ++;

						if (top != bottom)
						{
							// creating the vertical word and calculating points 
							points = verticalPoint(top, bottom, turnx[i]);
							pointsround += points;


							wordattempt = verticalWord(top, bottom, turnx[i]);

							if (!isWord(wordattempt))
							{
								if (!invalidfound)
								{
									invalidWords = wordattempt;
									invalidfound = true;
								}
								allwordsvalid = false;
							}

						}
					}								    
				}


				// if all tiles are used - add 50
				if (index == 7)
					pointsround += 50;	

				if (!allwordsvalid && play)
				{
					JOptionPane.showMessageDialog(frame, invalidWords.toUpperCase().charAt(0) + invalidWords.substring(1) + " is not a valid word.");
				}


				// new round if ALL newly formed words are valid
				if (play && allwordsvalid)
				{

					passes = 0;

					// end game if tile bag is empty and one player has empty rack
					if (size == 0 && (p[currentp].rackEmpty()))
						gameEnd = true;

					// setting special boxes to false
					for (int i = 0; i < index; i ++)
					{
						permanent[turnx[i]][turny[i]] = true;
						dw[turnx[i]][turny[i]] = false;
						dl[turnx[i]][turny[i]] = false;
						tw[turnx[i]][turny[i]] = false;
						tl[turnx[i]][turny[i]] = false;
					}


					p[currentp].points += pointsround;

					// regenerate rack
					if (size != 0)
					{
						p[currentp].fillRack();
						for (int i = 0; i < p[currentp].rack.length; i++) 
						{
							p[currentp].origRack[i] = p[currentp].rack[i];
						}
						if (p[currentp].ai)
						{
							p[currentp].bestScore = 0;
							p[currentp].moveLetters = new char[7];
							p[currentp].movesX = new int[7];
							p[currentp].movesY = new int[7];
							p[currentp].moveSize = 0;
						}
					}


					if (firstturn)
						firstturn = false; 

					// update history
					if (indexhis == 5)
					{
						for (int i = 0; i < 4 ; i ++)
						{
							playerhis[i] = playerhis[i + 1];
							pointhis[i] = pointhis[i + 1];
							totalhis[i] = totalhis[i + 1];
							wordhis[i] = wordhis[i + 1];
						}
					}

					int actualindex = -1;

					if (indexhis == 5)
						actualindex = 4;
					else
						actualindex = indexhis;

					playerhis[actualindex] = p[currentp].name;
					pointhis[actualindex] = pointsround;
					totalhis[actualindex] = p[currentp].points;
					wordhis[actualindex] = mainword;

					if (indexhis < 5)
						indexhis ++;

					if (!gameEnd)
					{		
						// switch players
						if (currentp == numplayers - 1)
							currentp = 0;
						else
							currentp ++; 
						
						
						
					}
					else
					{
						GameEnd.play = true;
						new GameEnd();											
					}

				}
			}
			// invalid position
			else
			{
			}
		}	


	}


	// method to checks if it's a word in the dictionary using binary search
	public static boolean isWord(String test)
	{
		test = test.toUpperCase();
		return words.contains(test);
	}

	// point of a word placed horizontally - given left most, right most, and y coordiante
	public static int horizontalPoint (int left, int right, int y)
	{							
		int points = 0;
		int doubleword = 0;
		int tripleword = 0;

		// add point of each character - double letter and triple letter if necessary
		for (int i = left; i <= right; i ++)
		{
			if (!blank[i][y])
			{
				if (dl[i][y])
					points += 2 * value(board[i][y]);
				else if (tl[i][y])
					points += 3 * value(board[i][y]);
				else
					points += value(board[i][y]);
			}


			if (dw[i][y])
				doubleword ++;

			if (tw[i][y])
			{
				tripleword ++;
			}

		}

		// double and triple words		
		for (int i = 0; i < doubleword; i ++)
			points *= 2;

		for (int i = 0; i < tripleword; i ++)
			points *= 3;

		return points;
	}

	// a word that is placed horizontally -  given left most, right most, and y coordiante
	public static String horizontalWord (int left, int right, int y)
	{
		String wordattempt = "";

		for (int i = left; i <= right; i ++)
			wordattempt += board[i][y];

		return wordattempt;
	}

	// point of a word placed vertically - given top most, bottom most, and x coordiante
	public static int verticalPoint (int top, int bottom, int x)
	{
		int points = 0;
		int doubleword = 0;
		int tripleword = 0;


		// add point of each character - double letter and triple letter if necessary
		for (int i = top; i <= bottom; i ++)
		{
			if (!blank[x][i])
			{
				if (dl[x][i])
					points += 2 * value(board[x][i]);
				else if (tl[x][i])
					points += 3 * value(board[x][i]);
				else
					points += value(board[x][i]);
			}

			if (dw[x][i])
				doubleword ++;
			if (tw[x][i])
				tripleword ++;
		}

		// double and triple words			
		for (int i = 0; i < doubleword; i ++)
			points *= 2;

		for (int i = 0; i < tripleword; i ++)
			points *= 3;

		return points;
	}

	// a word placed vertically - given top most, bottom most, and x coordiante
	public static String verticalWord(int top, int bottom, int x)
	{
		String wordattempt = "";
		for (int i = top; i <= bottom; i ++)
			wordattempt += board[x][i];
		return wordattempt;
	}

	// method that returns value of character when it is played
	public static int value(char c)
	{
		if (c == 'a')
			return 1;
		else if (c == 'b')
			return 3;
		else if (c == 'c')
			return 3;
		else if (c == 'd')
			return 2;
		else if (c == 'e')
			return 1;
		else if (c == 'f')
			return 4;
		else if (c == 'g')
			return 2;
		else if (c == 'h')
			return 4;
		else if (c == 'i')
			return 1;
		else if (c == 'j')
			return 8;
		else if (c == 'k')
			return 5;
		else if (c == 'l')
			return 1;
		else if (c == 'm')
			return 3;
		else if (c == 'n')
			return 1;
		else if (c == 'o')
			return 1;
		else if (c == 'p')
			return 3;
		else if (c == 'q')
			return 10;
		else if (c == 'r')
			return 1;
		else if (c == 's')
			return 1;
		else if (c == 't')
			return 1;
		else if (c == 'u')
			return 1;
		else if (c == 'v')
			return 4;
		else if (c == 'w')
			return 4;
		else if (c == 'x')
			return 8;
		else if (c == 'y')
			return 4;
		else if (c == 'z')
			return 10;
		else
			return 0;
	}
} 