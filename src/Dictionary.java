import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// this class allows user players to access the dictionary
public class Dictionary implements ActionListener
{
	ImageIcon icon;
	static JTextField word = new JTextField(20);  // textfields for word
	static JDialog frame;
	static String message = "";
	static char[] displayrack = new char[7];

	// the constructor for the application
	public Dictionary()
	{	
		frame = new JDialog(Scrabble.frame, "Dictionary", true);
		initialize();

		icon = new ImageIcon("files/dictionary.png");   

		JPanel panel = new JPanel()
		{  
			// draws the backround images and custimizations
			protected void paintComponent(Graphics g)
			{
				g.drawImage(icon.getImage(), 0,0, null);
				g.drawString(message, 35, 130);

				// draw the rack
				for (int i = 0; i < 7; i ++)
					if (displayrack[i] == ' ')
						g.drawImage(Scrabble.letters[26].getImage(), 300 + i * 45, 75, this);
					else if (displayrack[i] == '0')
						;
					else
						g.drawImage(Scrabble.letters[displayrack[i] - 'a'].getImage(), 300 + i * 45, 75, this);

				repaint();
				super.paintComponent(g);
			}
		};    
		panel.setOpaque(false);
		panel.setLayout(null);
		word.addActionListener(this);
		// adding the textboxes with appropriate settings
		word.setOpaque(false);
		word.setText("");
		word.setBounds(35, 82, 216, 28);
		frame.setResizable(false);
		panel.add(word);
		frame.add(panel);   
		frame.setSize(662 + 5, 341 + 25);
		frame.setVisible(true);
	}

	public static void initialize()
	{
		word.setText("");
		message = "";

		// determines the players rack, including those tiles that are put on the board
		int[] locs = new int[7]; // rack locations that are currently empty
		int rackleft = 0; // number of empty rack locations
		for (int i = 0; i < 7; i ++)
			if (Scrabble.p[Scrabble.currentp].rack[i] == '0')
				locs[rackleft ++] = i;

		for (int i = 0; i < 7; i ++)
			displayrack[i] = Scrabble.p[Scrabble.currentp].rack[i];

		for (int i = 0; i < 15; i ++)
			for (int j = 0; j < 15; j ++)
				if (!Scrabble.permanent[i][j] && Scrabble.board[i][j] != '0')
				{

					// placing tile onto a location on the rack
					int loc = (int)(Math.random() * rackleft);

					if (Scrabble.blank[i][j])
						displayrack[locs[loc]] = ' ';
					else
						displayrack[locs[loc]] = Scrabble.board[i][j];


					// remove rack location from array
					for (int a = loc; a < 6; a ++)
						locs[a] = locs[a+1];

					rackleft --;
				}	
	}



	// main method runs the application
	public static void main(String []args) 
	{
		new Dictionary();
	}   

	// user checks whether a string is a word
	public void actionPerformed (ActionEvent e)
	{
		String input = word.getText();
		if (Scrabble.isWord(input))
			message = input.toUpperCase().charAt(0) + input.toLowerCase().substring(1) + " is a valid word.";
		else 
			message = input.toUpperCase().charAt(0) + input.toLowerCase().substring(1) + " is not a valid word.";

	}	
}