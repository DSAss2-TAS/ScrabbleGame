package clientGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import client.ClientConnectionManager;
import game.ScrabbleButton;

public class GameRoom extends JFrame {
	private static GameRoom instance;
	private JTextField usernameText;
	private JTextField scoreText;
	private JTextField turnLabel;
	private JButton ready;
	private JButton invite;

	private JLabel p1namel, p2namel, p3namel;
	private JTextField p1namet, p2namet, p3namet;
	private JTextField p1score, p2score, p3score;
	private JTextField p1turn, p2turn, p3turn;
	private JTextField inputText;
	private JButton passButton;
	private JPanel jPanelNorth;
	private JPanel jPanelSouth;
	private JPanel jPanelCenter;
	private JPanel jPanelWest, jPanelWest1, jPanelWest2, jPanelWest3;

	private String players[], username;
	private String inputChar;
	private ScrabbleButton scraButton[][];
	private ScrabbleButton currentButton;
	private int currentRow, currentColumn;
	private int indexInRoom = 0;
	private boolean horizontal;
	private DataOutputStream output;

	public static GameRoom getInstance(String roomID, String hostName, String username) {
		instance = new GameRoom(roomID, hostName, username);
		return instance;
	}

	public static GameRoom getInstance() {
		return instance;
	}

	// initialize
	private GameRoom(String roomID, String hostName, String username) {
		players = new String[4];
		players[0] = hostName;
		this.username = username;
		setTitle("Welcome to " + hostName + "'s Room, ID " + roomID);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setSize(600, 600);
		jPanelNorth = new JPanel();
		usernameText = new JTextField(username, 10);
		usernameText.setEditable(false);
		scoreText = new JTextField("0", 2);
		scoreText.setEditable(false);
		turnLabel = new JTextField(5);
		turnLabel.setEditable(false);

		jPanelCenter = new JPanel();
		jPanelCenter.setLayout(new GridLayout(20, 20));
		scraButton = new ScrabbleButton[20][20];

		p1namel = new JLabel("Empty", JLabel.LEFT);
		p2namel = new JLabel("Empty", JLabel.LEFT);
		p3namel = new JLabel("Empty", JLabel.LEFT);
		p1namet = new JTextField(10);
		p1namet.setEditable(false);
		p2namet = new JTextField(10);
		p2namet.setEditable(false);
		p3namet = new JTextField(10);
		p3namet.setEditable(false);
		p1score = new JTextField("0", 5);
		p1score.setEditable(false);
		p2score = new JTextField("0", 5);
		p2score.setEditable(false);
		p3score = new JTextField("0", 5);
		p3score.setEditable(false);
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

		inputText = new JTextField("Please enter a letter...", 20);
		ready = new JButton("Ready");
		invite = new JButton("Invite");
		passButton = new JButton("Pass");

		// initialize GUI
		// TODO disable buttons except ready and invite until game start
		// north part
		jPanelNorth.add(new JLabel("Username: ", JLabel.LEFT));
		jPanelNorth.add(usernameText);
		jPanelNorth.add(new JLabel("Score: ", JLabel.LEFT));
		jPanelNorth.add(scoreText);
		jPanelNorth.add(turnLabel);
		jPanelNorth.add(ready);
		jPanelNorth.add(invite);
		add(BorderLayout.NORTH, jPanelNorth);

		jPanelWest1.setLayout(new BoxLayout(jPanelWest1, BoxLayout.Y_AXIS));
		jPanelWest2.setLayout(new BoxLayout(jPanelWest2, BoxLayout.Y_AXIS));
		jPanelWest3.setLayout(new BoxLayout(jPanelWest3, BoxLayout.Y_AXIS));
		jPanelWest1.add(p1namel);
		jPanelWest2.add(p2namel);
		jPanelWest3.add(p3namel);
		jPanelWest1.add(p1namet);
		jPanelWest2.add(p2namet);
		jPanelWest3.add(p3namet);
		jPanelWest1.add(new JLabel("Score: ", JLabel.LEFT));
		jPanelWest1.add(p1score);
		jPanelWest2.add(new JLabel("Score: ", JLabel.LEFT));
		jPanelWest2.add(p2score);
		jPanelWest3.add(new JLabel("Score: ", JLabel.LEFT));
		jPanelWest3.add(p3score);
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
		jPanelSouth.add(new JLabel("Input: ", JLabel.LEFT));
		jPanelSouth.add(inputText);
		jPanelSouth.add(passButton);
		add(BorderLayout.SOUTH, jPanelSouth);

		ready.setEnabled(false);
		passButton.setEnabled(false);
		// this.pack();
		revalidate();
		repaint();
	}

	// TODO server send 3 players info to all run client
	public void initialization() {

		ClientConnectionManager connectionManager = ClientConnectionManager.getInstance();
		output = connectionManager.getOutput();

		ActionListener inviteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputStr = JOptionPane.showInputDialog(instance,
						"Please enter the name of the player who you want to invite.");
				JSONObject request = new JSONObject();
				request.put("command", "INVITE");
				request.put("content", inputStr);
				try {

					output.writeUTF(request.toJSONString());
					output.flush();
				} catch (IOException ex) {
					System.out.println("Fail to send INVITE request in GameRoom.");
				}
			}
		};
		invite.addActionListener(inviteListener);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(instance, "Are you sure you want to quit this game?", "Exit Game?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

					JSONObject request = new JSONObject();
					request.put("command", "QUIT");
					request.put("content", connectionManager.getUsername());
					try {

						output.writeUTF(request.toJSONString());
						output.flush();
					} catch (IOException e) {
						System.out.println("Fail to send EXIT request in GameRoom.");
					}

				}
			}
		});
	}

	// invoked when client is invited to a room as a guest
	public void setPlayers(String p1name, String p2name) {
		p1namel.setText("Host Name: ");
		p1namet.setText(players[0]);
		if (!p1name.equals("")) {
			players[1] = p1name;
			p2namel.setText("Player 1 Name: ");
			p2namet.setText(p1name);
			if (!p2name.equals("")) {
				players[2] = p2name;
				p3namel.setText("Player 2 Name: ");
				p3namet.setText(p2name);
				// the room is full
				indexInRoom = 3;
			} else {
				indexInRoom = 2;
			}
		} else {
			indexInRoom = 1;
		}
		players[indexInRoom] = username;
		waitToStart();
	}

	public void addNewPlayer(String name) {
		if(players[1]==null){	// only host in room
			players[1] = name;
			p1namel.setText("Player 1 Name: ");
			p1namet.setText(name);
			waitToStart();		// enable ready button, players wait to start
		}else if(!name.equals(username)){	// if the new player himself receive the command, do nothing
			if(players[2]==null){
				players[2] = name;
				p2namel.setText("Player 2 Name: ");
				p2namet.setText(name);
			}else{
				players[3] = name;
				p3namel.setText("Player 3 Name: ");
				p3namet.setText(name);
			}
		}

	}

	ActionListener readyListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JSONObject request = new JSONObject();
			request.put("command", "READY");
			request.put("content", inputChar);
			try {
				output.writeUTF(request.toJSONString());
				output.flush();

			} catch (IOException ex) {
				System.out.println("Fail to send READY request in GameRoom.");
			}
		}
	};
	
	// listen 400 button click
	ActionListener scraButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			inputChar = inputText.getText();
			if (inputChar.length() == 1) {
				if ('a' <= inputChar.charAt(0) && inputChar.charAt(0) <= 'p'
						|| 'A' <= inputChar.charAt(0) && inputChar.charAt(0) <= 'P') {
					currentButton = (ScrabbleButton) e.getSource();
					currentButton.setText(inputChar);
					currentRow = currentButton.getRow();
					currentColumn = currentButton.getColumn();
					String[] options = { "Horizontal", "Vertical" };
					JSONObject request = new JSONObject();
					request.put("command", "INSERT");
					request.put("content", inputChar);
					request.put("row", currentRow);
					request.put("column", currentColumn);
					int option = JOptionPane.showOptionDialog(instance, "Please select the direction of your word.",
							"Choose Direction.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
					if (option == JOptionPane.YES_OPTION) {
						horizontal = true;
						request.put("direction", "horizontal");

					} else if (option == JOptionPane.NO_OPTION) {
						horizontal = false;
						request.put("direction", "vertical");
					} else { // option==JOptionPane.CLOSED_OPTION
						JOptionPane.showMessageDialog(instance,
								"The result would be Horizontal if you close it without selection.");
						horizontal = true;
						request.put("direction", "horizontal");
					}
					try {
						output.writeUTF(request.toJSONString());
						output.flush();

					} catch (IOException ex) {
						System.out.println("Fail to send INSERT request in GameRoom.");
					}

					// TODO highlight the current word line.
				}
				JOptionPane.showMessageDialog(instance, "Invalid input, Please only enter one alphabet.");
			}
		}
	};

	ActionListener passListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

		}
	};

	public void waitToStart() {
		ready.setEnabled(true);
		ready.addActionListener(readyListener);
	}

	public void gameStart() {
		invite.setEnabled(false);
		ready.setEnabled(false);
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				scraButton[i][j] = new ScrabbleButton(i, j); // create buttons
				scraButton[i][j].addActionListener(scraButtonListener);
				jPanelCenter.add(scraButton[i][j]);// add buttons to the game
													// panel
			}
		}
		revalidate();
		repaint();
	}

	ArrayList<JButton> changedButton = new ArrayList<JButton>();;

	public int countWord() {
		int wordscore = 0;
		int countLeft = 0;
		int countRight = 0;
		changedButton.removeAll(changedButton);
		int x = currentRow;
		int y = currentColumn;
		if (horizontal == true) {
			for (countLeft = 0; scraButton[x][y - 1].getText() != null; y--) {
				scraButton[x][y - 1].setBackground(Color.CYAN);
				scraButton[x][y - 1].setOpaque(true);
				changedButton.add(scraButton[x][y - 1]);
				countLeft++;
			}
			for (countRight = 0; scraButton[x][y + 1].getText() != null; y++) {
				scraButton[x][y + 1].setBackground(Color.CYAN);
				scraButton[x][y + 1].setOpaque(true);
				changedButton.add(scraButton[x][y + 1]);
				countRight++;
			}
		}

		else {
			for (countLeft = 0; scraButton[x - 1][y].getText() != null; x--) {
				scraButton[x - 1][y].setBackground(Color.CYAN);
				scraButton[x - 1][y].setOpaque(true);
				changedButton.add(scraButton[x - 1][y]);
				countLeft++;
			}
			for (countRight = 0; scraButton[x + 1][y].getText() != null; x++) {
				scraButton[x + 1][y].setBackground(Color.CYAN);
				scraButton[x + 1][y].setOpaque(true);
				changedButton.add(scraButton[x + 1][y]);
				countRight++;
			}
		}
		changedButton.add(scraButton[x][y]);
		return wordscore = countLeft + countRight + 1;
	}

	// submitButton.addActionListener(nicknameListener);
	// inputText.addActionListener(nicknameListener);
	// inputText.addFocusListener(new FocusListener() {
	// @Override
	// public void focusGained(FocusEvent e) {
	// }
	//
	// @Override
	// public void focusLost(FocusEvent e) {
	// String aText = inputText.getText();
	// if (!aText.equals("")) {
	// initInput = aText;
	// }
	// }
	// });

	public void delete() {
		instance = null;
	}
}