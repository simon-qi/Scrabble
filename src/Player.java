// a class that creates a player

class Player
{
	char[] rack = new char[7]; // rack of the player
	char[] origRack = new char[7]; // original rack
	String name; // name of the player
	int points; // the player's point
	boolean ai; // whether the player is an ai
	int bestScore; // best score so far as played by ai
	char[] moveLetters; // the letters used by the best ai move
	int[] movesX; // the x coordinates of the best ai move
	int[] movesY; // the y coordinates of the best ai move
	int moveSize;

	// constructor
	public Player (String n, boolean ai)
	{
		bestScore = 0;
		moveLetters = new char[7];
		movesX = new int[7];
		movesY = new int[7];
		moveSize = 0;
		points = 0;
		name = n;
		this.ai = ai;

		for (int i = 0; i < 7; i ++)
			rack[i] = '0';


		this.fillRack();
		for (int i = 0; i < rack.length; i++) 
		{
			origRack[i] = rack[i];
		}
	}

	// a permute r
	public void choose(char[] a, int R) 
	{ 
		enumerate(a, a.length, R); 
	}

	private void enumerate(char[] a, int n, int r)
	{
		if (r == 0) 
		{
			char[] newArray = new char[a.length - n];
			for (int i = n; i < a.length; i++)
			{
				newArray[i - n] = a[i];
			}
			moveHelper(newArray);
			return;
		}
		for (int i = 0; i < n; i++) 
		{
			swap(a, i, n-1);
			enumerate(a, n-1, r-1);
			swap(a, i, n-1);
		}
	}  

	// helper function that swaps a[i] and a[j]
	public static void swap(char[] a, int i, int j) 
	{
		char temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	// the ai making play
	public void move()
	{
		for (int i = 2; i <= 7; i++)
			choose(origRack, i);

		for (int i = 0; i < Scrabble.board.length; i++)
			for (int j = 0; j < Scrabble.board[i].length; j++)
				if (!Scrabble.permanent[i][j])
					Scrabble.board[i][j] = '0';

		for (int h = 0; h < 7; h++)
			rack[h] = origRack[h];

		for (int i = 0; i < moveSize; i++)
		{
			Scrabble.board[movesX[i]][movesY[i]] = moveLetters[i];

			int k;

			for (k = 0; k < rack.length && moveLetters[i] != rack[k]; k++)
				;
			rack[k] = '0';
		}
	}

	private void moveHelper(char[] permRack) 
	{
		if (Scrabble.firstturn) // first play of game
		{
			for (int i = (7 - permRack.length + 1); i <= 7; i++) 
			{
				int[] movex = new int[7];
				int indexx = 0;
				int[] movey = new int[7];
				int indexy = 0;

				for (int h = 0; h < 7; h++)
					rack[h] = origRack[h];

				for (int k = 0; k < Scrabble.board.length; k++)
					for (int l = 0; l < Scrabble.board[k].length; l++)
						if (!Scrabble.permanent[k][l])
							Scrabble.board[k][l] = '0';

				for (int j = 0; j < permRack.length; j++)
				{
					Scrabble.board[i + j][7] = permRack[j];
					movex[indexx++] = i + j;
					movey[indexy++] = 7;
				}

				Scrabble.calculate(false);
				if (Scrabble.allwordsvalid)
				{
					if (Scrabble.pointsround > bestScore)
					{
						bestScore = Scrabble.pointsround;
						for (int k = 0; k < permRack.length; k++)
						{
							moveLetters[k] = permRack[k];
						}
						for (int k = 0; k < indexx; k++)
						{
							movesX[k] = movex[k];
							movesY[k] = movey[k]; 
						}
						moveSize = indexx;
					}
				}
			}
		}
	}

	// fill empty rack locations with tiles - occurs after a round
	public void fillRack()
	{
		// generates random letter from tile bag to fill rack  	
		for (int j = 0; j < 7; j ++)
		{			
			if (rack[j] == '0')
			{
				if (Scrabble.size != 0)
				{
					int num = (int)(Math.random() * Scrabble.size);
					rack[j] = Scrabble.bag[num];   
					// remove letter from bag by removing the numerical value from array to avoid duplicates
					for (int i = num; i < 99; i ++)
						Scrabble.bag[i] = Scrabble.bag[i+1];
					Scrabble.size --; 
				}
			}
		}	
	}

	// checks if the rack is empty
	public boolean rackEmpty()
	{
		boolean empty = true;
		for (int i = 0; i < 7; i ++)
			if (rack[i] != '0')
				empty = false;

		return empty;
	}

	// shuffles tiles on the rack
	public void shuffle()
	{
		int[] locs = new int[7];  // locations on the rack that are available 
		int rackleft = 7; 

		char[] oldrack = new char[7]; // rack prior to shuffling
		char[] newrack = new char[7]; // rack after shuffling


		for (int i = 0; i < 7; i ++)
			oldrack[i] = rack[i];

		for (int i = 0; i < 7; i ++)
			newrack[i] = '0';

		for (int i = 0; i < 7; i ++)
			locs[i] = i;

		// generate random location for each tile on rack
		for (int i = 0; i < 7; i ++)
		{
			if (oldrack[i] != '0')
			{
				int loc = (int)(Math.random() * rackleft);
				newrack[locs[loc]] = oldrack[i];

				// remove rack location from array
				for (int j = loc; j < 6; j ++)
				{
					locs[j] = locs[j+1];
				}

				rackleft --;
			}	
		}  

		rack = newrack; 
	}

	// recalls tiles back to rack
	public void recall()
	{
		int[] locs = new int[7]; // rack locations that are currently empty
		int rackleft = 0; // number of empty rack locations

		for (int i = 0; i < 7; i ++)
			if (rack[i] == '0')
				locs[rackleft ++] = i;


		for (int i = 0; i < 15; i ++)
			for (int j = 0; j < 15; j ++)
				if (!Scrabble.permanent[i][j] && Scrabble.board[i][j] != '0')
				{

					// placing tile onto a location on the rack
					int loc = (int)(Math.random() * rackleft);

					if (Scrabble.blank[i][j])
						rack[locs[loc]] = ' ';
					else
						rack[locs[loc]] = Scrabble.board[i][j];

					// removing tile from board if it is played in current round
					Scrabble.blank[i][j] = false;
					Scrabble.board[i][j] = '0';


					// remove rack location from array
					for (int a = loc; a < 6; a ++)
						locs[a] = locs[a+1];

					rackleft --;
				}	
	}
}