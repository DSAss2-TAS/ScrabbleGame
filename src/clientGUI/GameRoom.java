package clientGUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.ClientConnectionManager;
import game.ScrabbleButton;


public class GameRoom extends JFrame{
	private static GameRoom instance;
	private JLabel usernameLabel;
	private JLabel scoreLabel;
	private JLabel inputLabel;
	private JTextField usernameText;
	private JTextField scoreText;
	private JTextField inputText;
	private JTextField turnLabel;
	private JLabel player1name;
	private JLabel player2name;
	private JLabel player3name;
	private JLabel player1score;
	private JLabel player2score;
	private JLabel player3score;
	private JTextField p1nameText;
	private JTextField p2nameText;
	private JTextField p3nameText;
	private JTextField p1scoreText;
	private JTextField p2scoreText;
	private JTextField p3scoreText;
	private JTextField p1turn;
	private JTextField p2turn;
	private JTextField p3turn;

	private JButton ready;
	private JButton invite;
	private JButton help;
	private JButton quit;

	private JButton submitButton;
	private JButton passButton;
	private JPanel jPanelNorth;
	private JPanel jPanelSouth;
	private JPanel jPanelCenter;
	private JPanel jPanelWest;
	private JPanel jPanelWest1;
	private JPanel jPanelWest2;
	private JPanel jPanelWest3;

	private String title;
	private ScrabbleButton scranbutton[][];

	public static GameRoom getInstance() {
		if (instance == null) {
			instance = new GameRoom();
		}
		return instance;
	}

	// initialize
	private GameRoom() {
		title = ClientConnectionManager.getInstance().getUsername() + "'s Room";
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setSize(600, 600);
		jPanelNorth = new JPanel();
		usernameLabel = new JLabel("Username: ", JLabel.LEFT);
		usernameText = new JTextField(10);
		usernameText.setEditable(false);
		scoreLabel = new JLabel("Score: ", JLabel.LEFT);
		scoreText = new JTextField("0", 2);
		scoreText.setEditable(false);
		turnLabel = new JTextField(5);
		turnLabel.setEditable(false);
		
		jPanelCenter = new JPanel();
		jPanelCenter.setLayout(new GridLayout(20, 20));
		scranbutton = new ScrabbleButton[20][20];
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				scranbutton[i][j] = new ScrabbleButton(i, j); // create buttons
				jPanelCenter.add(scranbutton[i][j]);// add buttons to the game panel
			}
		}
		player1name = new JLabel("Player 1 name: ", JLabel.LEFT);
		player2name = new JLabel("Player 2 name: ", JLabel.LEFT);
		player3name = new JLabel("Player 3 name: ", JLabel.LEFT);
		p1nameText = new JTextField(5);
		p1nameText.setEditable(false);
		p2nameText = new JTextField(5);
		p2nameText.setEditable(false);
		p3nameText = new JTextField(5);
		p3nameText.setEditable(false);
		player1score = new JLabel("Score: ", JLabel.LEFT);
		p1scoreText = new JTextField("0", 5);
		p1scoreText.setEditable(false);
		player2score = new JLabel("Score: ", JLabel.LEFT);
		p2scoreText = new JTextField("0", 5);
		p2scoreText.setEditable(false);
		player3score = new JLabel("Score: ", JLabel.LEFT);
		p3scoreText = new JTextField("0", 5);
		p3scoreText.setEditable(false);
		p1turn = new JTextField(5);
		p1turn.setEditable(false);
		p2turn = new JTextField(5);
		p2turn.setEditable(false);
		p3turn = new JTextField(5);
		p3turn.setEditable(false);

		jPanelWest1 = new JPanel();
		jPanelWest2 = new JPanel();
		jPanelWest3 = new JPanel();
		jPanelWest = new JPanel();

		jPanelSouth = new JPanel();

		inputLabel = new JLabel("Input: ", JLabel.LEFT);
		inputText = new JTextField("Please enter a letter...", 20);
		ready = new JButton("Ready");
		help = new JButton("Help");
		invite = new JButton("Invite");
		quit = new JButton("Quit");
		submitButton = new JButton("Submit");

		passButton = new JButton("Pass");
		startUp();
	}

	// initialize GUI
	private void buildGUI() {

		// north part
		jPanelNorth.add(usernameLabel);
		jPanelNorth.add(usernameText);
		jPanelNorth.add(scoreLabel);
		jPanelNorth.add(scoreText);
		jPanelNorth.add(turnLabel);
		jPanelNorth.add(ready);
		jPanelNorth.add(invite);
		add(BorderLayout.NORTH, jPanelNorth);

		jPanelWest1.setLayout(new BoxLayout(jPanelWest1, BoxLayout.Y_AXIS));
		jPanelWest2.setLayout(new BoxLayout(jPanelWest2, BoxLayout.Y_AXIS));
		jPanelWest3.setLayout(new BoxLayout(jPanelWest3, BoxLayout.Y_AXIS));
		jPanelWest1.add(player1name);
		jPanelWest2.add(player2name);
		jPanelWest3.add(player3name);
		jPanelWest1.add(p1nameText);
		jPanelWest2.add(p2nameText);
		jPanelWest3.add(p3nameText);
		jPanelWest1.add(player1score);
		jPanelWest1.add(p1scoreText);
		jPanelWest2.add(player2score);
		jPanelWest2.add(p2scoreText);
		jPanelWest3.add(player3score);
		jPanelWest3.add(p3scoreText);
		jPanelWest1.add(p1turn);
		jPanelWest2.add(p2turn);
		jPanelWest3.add(p3turn);
		jPanelWest.setLayout(new BoxLayout(jPanelWest, BoxLayout.Y_AXIS));
		jPanelWest.add(jPanelWest1);
		jPanelWest.add(jPanelWest2);
		jPanelWest.add(jPanelWest3);
		add(BorderLayout.WEST, jPanelWest);

		// center part
		add(BorderLayout.CENTER, jPanelCenter);

		// south part
		jPanelSouth.add(inputLabel);
		jPanelSouth.add(inputText);
		jPanelSouth.add(submitButton);
		jPanelSouth.add(passButton);
		add(BorderLayout.SOUTH, jPanelSouth);

		// clientFrame.pack();
	}

	// run client
	public void startUp() {
		buildGUI();

		// listen nickname
//		ActionListener nicknameListener = new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String aText = inputText.getText();
//				if (!aText.equals("")) {
//					initInput = aText;
//				}
//			}
//		};
//		submitButton.addActionListener(nicknameListener);
//		inputText.addActionListener(nicknameListener);
//		inputText.addFocusListener(new FocusListener() {
//			@Override
//			public void focusGained(FocusEvent e) {
//			}
//
//			@Override
//			public void focusLost(FocusEvent e) {
//				String aText = inputText.getText();
//				if (!aText.equals("")) {
//					initInput = aText;
//				}
//			}
//		});

		// send message to server
//		ActionListener SayListener = new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String aText = sayText.getText();
//				if (aText.equals("")) {
//					JOptionPane.showMessageDialog(clientFrame, "message cannot be empty");
//				} else {
//					try {
//						writer.write(initInput + "：" + aText + "\n");
//						writer.flush();
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					sayText.setText("");
//				}
//			}
//		};
//		passButton.addActionListener(SayListener);
		// sayText.addActionListener(SayListener);

	}

}