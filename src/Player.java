// a class that creates a player
// credit: the choose, enumerate, and swap methods are modified from
// http://introcs.cs.princeton.edu/java/23recursion/PermutationsK.java.html

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

    public void choose(char[] a, int k) {
        enumerate(a, 0, k);
    }

    private void enumerate(char[] a, int n, int k) {
    	String current = "";
    	for (int i = 0; i < n; i++) {
    		if (a[i] == ' ' || a[i] == '0')
    			return;
    		current += a[i];
    	}

    	if (!((Scrabble.wordsTrie).startsWith(current.toLowerCase())))
    		return;
    	
        if (k == 0) {
			char[] newArray = new char[n];
			for (int i = 0; i < n; i++)
			{
				newArray[i] = a[i];
			}
			moveHelper(newArray);
			return;
        }

        for (int i = n; i < a.length; i++) {
            swap(a, i, n);
            enumerate(a, n+1, k-1);
            swap(a, i, n);
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
		else // not first play
		{
			for (int i = 0; i < Scrabble.board.length; i++)
			{
				for (int j = 0; j < Scrabble.board[i].length; j++)
				{
					if (Scrabble.permanent[i][j])
					{

						// horizontal					
						int left;  
						int right; 

						for (left = i; left >= 0 && Scrabble.board[left][j] != '0'; left --)
							;

						for (right = i; right < 15 && Scrabble.board[right][j] != '0'; right ++)
							;

						int[] movex = new int[7];
						int indexx = 0;
						int[] movey = new int[7];
						int indexy = 0;

						if (left < 0)
							left = right;

						if (left < 15)
						{
							int lettersCovered = permRack.length;
							int minleft;
							for (minleft = left; minleft > 0 && lettersCovered >= 2; minleft--)
							{
								if (!Scrabble.permanent[minleft][j])
								{
									lettersCovered --;
								}
							}

							if (lettersCovered != 1)
							{
								int k;
								for (k = 0; k < 15 && Scrabble.permanent[k][j]; k++)
									;
								minleft = k;
							}


							int permanents = 0;
							for (; minleft <= right && minleft < 15; minleft++) 
							{
								if (!Scrabble.permanent[minleft][j])
								{
									Scrabble.debug = false;
									movex = new int[7];
									indexx = 0;
									movey = new int[7];
									indexy = 0;

									for (int h = 0; h < Scrabble.board.length; h++)
									{
										for (int k = 0; k < Scrabble.board[h].length; k++)
										{
											if (!Scrabble.permanent[h][k])
												Scrabble.board[h][k] = '0';
										}
									}

									permanents = 0;

									for (int h = 0; h < permRack.length && minleft + h + permanents < 15; h++)
									{
										if (Scrabble.permanent[minleft + h + permanents][j])
										{
											permanents++;
											h--;
										}
										else
										{
											Scrabble.board[minleft + h + permanents][j] = permRack[h];
											movex[indexx++] = minleft + h + permanents;
											movey[indexy++] = j;
										}

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
												Scrabble.board[movesX[k]][movesY[k]] = moveLetters[k];
											}
											Scrabble.calculate(false);

											moveSize = indexx;
										}
									}
								}						
							}
						}


						// vertical				
						int up;  
						int down; 

						for (up = j; up >= 0 && Scrabble.board[i][up] != '0'; up --)
							;

						for (down = i; down < 15 && Scrabble.board[i][down] != '0'; down ++)
							;

						movex = new int[7];
						indexx = 0;
						movey = new int[7];
						indexy = 0;

						if (up < 0)
							up = down;

						if (up < 15)
						{
							int lettersCovered = permRack.length;
							int minup;
							for (minup = up; minup > 0 && lettersCovered >= 2; minup--)
							{
								if (!Scrabble.permanent[i][minup])
								{
									lettersCovered --;
								}
							}

							if (lettersCovered != 1)
							{
								int k;
								for (k = 0; k < 15 && Scrabble.permanent[i][k]; k++)
									;
								minup = k;
							}


							int permanents = 0;
							for (; minup <= down && minup < 15; minup++) 
							{
								if (!Scrabble.permanent[i][minup])
								{
									Scrabble.debug = false;
									movex = new int[7];
									indexx = 0;
									movey = new int[7];
									indexy = 0;

									for (int h = 0; h < Scrabble.board.length; h++)
									{
										for (int k = 0; k < Scrabble.board[h].length; k++)
										{
											if (!Scrabble.permanent[h][k])
												Scrabble.board[h][k] = '0';
										}
									}

									permanents = 0;

									for (int h = 0; h < permRack.length && minup + h + permanents < 15; h++)
									{
										if (Scrabble.permanent[i][minup + h + permanents])
										{
											permanents++;
											h--;
										}
										else
										{
											Scrabble.board[i][minup + h + permanents] = permRack[h];
											movey[indexx++] = minup + h + permanents;
											movex[indexy++] = i;
										}

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
												Scrabble.board[movesX[k]][movesY[k]] = moveLetters[k];
											}
											Scrabble.calculate(false);

											moveSize = indexx;
										}
									}
								}

							}
						}
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