package clientGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import game.Player;
import game.ScrabbleButton;

public class GameRoom extends JFrame {
	private static final int GRID_SIZE = 20;
	private static GameRoom instance;
	private JTextField usernamet;
	private JTextField scoret;
	private JTextField myTurn;
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
	private Player players[];
	private String username, inputChar;
	private ScrabbleButton scraButton[][];
	private ScrabbleButton currentButton;
	private int currentRow, currentColumn;
	private int indexInRoom = 0, changedScore = 0;
	private int numberOfPlayer, currentTurn;
	private DataOutputStream output;
	private ArrayList<ScrabbleButton> changedTile;
	private ArrayList<ScrabbleButton> availableTile;
	private Color defaultColor;

	public static GameRoom getInstance(String roomID, String hostName, String username) {
		instance = new GameRoom(roomID, hostName, username);
		return instance;
	}

	public static GameRoom getInstance() {
		return instance;
	}

	// initialize
	private GameRoom(String roomID, String hostName, String username) {
		numberOfPlayer = 1;
		players = new Player[4];
		players[0] = new Player(hostName);
		this.username = username;
		setTitle("Welcome to " + hostName + "'s Room, ID " + roomID);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setSize(600, 600);
		jPanelNorth = new JPanel();
		usernamet = new JTextField(username, 10);
		usernamet.setEditable(false);
		scoret = new JTextField("0", 2);
		scoret.setEditable(false);
		myTurn = new JTextField(5);
		myTurn.setEditable(false);
		//////////////////////////////
		players[0].setScoreField(scoret);
		players[0].setTurnField(myTurn);
		///////////////
		jPanelCenter = new JPanel();
		jPanelCenter.setLayout(new GridLayout(20, 20));

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

		inputText = new JTextField("Enter a letter...", 20);
		inputText.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				if (inputText.getText().trim().equals("")) {
					inputText.setText("Enter a letter...");
				} else {
					// do nothing
				}
			}

			public void focusGained(FocusEvent e) {
				if (inputText.getText().trim().equals("Enter a letter...")) {
					inputText.setText("");
				} else {
					// do nothing
				}
			}
		});
		ready = new JButton("Ready");
		invite = new JButton("Invite");
		passButton = new JButton("ass");

		// initialize GUI
		// TODO disable buttons except ready and invite until game start
		// north part
		jPanelNorth.add(new JLabel("Username: ", JLabel.LEFT));
		jPanelNorth.add(usernamet);
		jPanelNorth.add(new JLabel("Score: ", JLabel.LEFT));
		jPanelNorth.add(scoret);
		jPanelNorth.add(myTurn);
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
		p1namet.setText(players[0].getName());
		players[0].setScoreField(p1score);
		players[0].setTurnField(p1turn);
		if (!p1name.equals("")) {// if there is another player besides the host
			players[1] = new Player(p1name);
			p2namel.setText("Player 1 Name: ");
			p2namet.setText(p1name);
			players[1].setScoreField(p2score);
			players[1].setTurnField(p2turn);
			if (!p2name.equals("")) {// if there are another two players besides
										// the host
				players[2] = new Player(p2name);
				p3namel.setText("Player 2 Name: ");
				p3namet.setText(p2name);
				players[2].setScoreField(p3score);
				players[2].setTurnField(p3turn);
				// the room is full
				indexInRoom = 3;
				numberOfPlayer = 4;
			} else {
				indexInRoom = 2;
				numberOfPlayer = 3;
			}

		} else {
			// client is the second player
			indexInRoom = 1;
			numberOfPlayer = 2;
		}
		players[indexInRoom] = new Player(username);
		players[indexInRoom].setScoreField(scoret);
		players[indexInRoom].setTurnField(myTurn);
		waitToStart();
	}

	public void addNewPlayer(String name) {
		if (players[1] == null) { // only host in room
			players[1] = new Player(name);
			p1namel.setText("Player 1 Name: ");
			p1namet.setText(name);
			players[1].setScoreField(p1score);
			players[1].setTurnField(p1turn);
			numberOfPlayer++;
			waitToStart(); // enable ready button, players wait to start
		} else if (!name.equals(username)) { // if the new player himself
												// receive the command, do
												// nothing
			if (players[2] == null) {
				players[2] = new Player(name);
				p2namel.setText("Player 2 Name: ");
				p2namet.setText(name);
				players[2].setScoreField(p2score);
				players[2].setTurnField(p2turn);
				numberOfPlayer++;
			} else {
				players[3] = new Player(name);
				p3namel.setText("Player 3 Name: ");
				p3namet.setText(name);
				players[3].setScoreField(p3score);
				players[3].setTurnField(p3turn);
				numberOfPlayer++;
			}
		}

	}

	public void waitToStart() {
		ActionListener readyListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSONObject request = new JSONObject();
				request.put("command", "READY");
				try {
					output.writeUTF(request.toJSONString());
					output.flush();

				} catch (IOException ex) {
					System.out.println("Fail to send READY request in GameRoom.");
				}
				ready.setEnabled(false);
			}
		};
		ready.addActionListener(readyListener);
		ready.setEnabled(true);
	}

	// listen 400 button click
	ActionListener scraButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			inputChar = inputText.getText();
			if (inputChar.length() == 1) {
				if ('a' <= inputChar.charAt(0) && inputChar.charAt(0) <= 'z'
						|| 'A' <= inputChar.charAt(0) && inputChar.charAt(0) <= 'Z') {
					currentButton = (ScrabbleButton) e.getSource();
					currentButton.setText(inputChar);
					currentRow = currentButton.getRow();
					currentColumn = currentButton.getColumn();
					String[] options = { "Horizontal", "Vertical" };
					JSONObject request = new JSONObject();
					request.put("command", "PLACE_CHAR");
					request.put("content", inputChar);
					request.put("row", currentRow);
					request.put("column", currentColumn);
					int option = JOptionPane.showOptionDialog(instance, "Please select the direction of your word.",
							"Choose Direction.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
					if (option == JOptionPane.YES_OPTION) {
						request.put("direction", "horizontal");

					} else if (option == JOptionPane.NO_OPTION) {
						request.put("direction", "vertical");
					} else { // option==JOptionPane.CLOSED_OPTION
						JOptionPane.showMessageDialog(instance,
								"The result would be Horizontal if you close it without selection.");
						request.put("direction", "horizontal");
					}
					try {
						output.writeUTF(request.toJSONString());
						output.flush();

					} catch (IOException ex) {
						System.out.println("Fail to send PLACE_CHAR request in GameRoom.");
					}

				} else {
					JOptionPane.showMessageDialog(instance, "Invalid input, Please enter a letter.");
				}
			} else {
				JOptionPane.showMessageDialog(instance, "Invalid input, Please only enter one character.");
			}
		}
	};

	ActionListener passListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JSONObject request = new JSONObject();
			request.put("command", "PASS");
			try {
				output.writeUTF(request.toJSONString());
				output.flush();

			} catch (IOException ex) {
				System.out.println("Fail to send PASS request in GameRoom.");
			}

		}
	};

	public void gameStart() {
		scraButton = new ScrabbleButton[20][20];
		invite.setEnabled(false);
		ready.setEnabled(false);
		currentTurn = 0;

		changedTile = new ArrayList<ScrabbleButton>();
		availableTile = new ArrayList<ScrabbleButton>();
		if (currentTurn == indexInRoom) { // this player is host who could place
											// a letter first
			for (int i = 0; i < GRID_SIZE; i++) {
				for (int j = 0; j < GRID_SIZE; j++) {
					scraButton[i][j] = new ScrabbleButton(i, j); // create tiles
					scraButton[i][j].addActionListener(scraButtonListener);
					jPanelCenter.add(scraButton[i][j]);// add buttons to the
														// game pane
					availableTile.add(scraButton[i][j]);
				}
			}
			myTurn.setText("Your Turn!");
			passButton.setEnabled(true);
		} else { // this player is not the host must wait to his/her own turn
			for (int i = 0; i < GRID_SIZE; i++) {
				for (int j = 0; j < GRID_SIZE; j++) {
					scraButton[i][j] = new ScrabbleButton(i, j); // create tiles
					scraButton[i][j].addActionListener(scraButtonListener);
					scraButton[i][j].setEnabled(false);
					jPanelCenter.add(scraButton[i][j]);// add buttons to the
														// game panel
					availableTile.add(scraButton[i][j]);
				}
			}
			///////////////// changed here!!!!!!!!!!
			players[currentTurn].showTurnField();
			///////////////// changed here!!!!!!!!!!
		}
		passButton.addActionListener(passListener);
		defaultColor = passButton.getBackground();
		revalidate();
		repaint();
	}

	public void countWord(String c, int x, int y, boolean h) {
		int countLeft = 0;
		int countRight = 0;
		int x1 = x, x2 = x, y1 = y, y2 = y;
		scraButton[x][y].setText(c);
		scraButton[x][y].setBackground(Color.CYAN);
		scraButton[x][y].setOpaque(true);
		scraButton[x][y].setEnabled(false);
		changedTile.add(scraButton[x][y]);
		availableTile.remove(scraButton[x][y]);
		if (h == true) {
			for (countLeft = 0; y1 > 0 && !scraButton[x][y1 - 1].getText().equals(" "); y1--) {
				scraButton[x][y1 - 1].setBackground(Color.CYAN);
				scraButton[x][y1 - 1].setOpaque(true);
				changedTile.add(scraButton[x][y1 - 1]);
				countLeft++;
			} // count the filled buttons at left
			for (countRight = 0; y2 < 19 && !scraButton[x][y2 + 1].getText().equals(" "); y2++) {
				scraButton[x][y2 + 1].setBackground(Color.CYAN);
				scraButton[x][y2 + 1].setOpaque(true);
				changedTile.add(scraButton[x][y2 + 1]);
				countRight++;
			} // count the filled buttons at right
			System.out.println("countLeft is " + countLeft + " countRight is " + countRight);
		} else {
			for (countLeft = 0; x1 > 0 && !scraButton[x1 - 1][y].getText().equals(" "); x1--) {
				scraButton[x1 - 1][y].setBackground(Color.CYAN);
				scraButton[x1 - 1][y].setOpaque(true);
				changedTile.add(scraButton[x1 - 1][y]);
				countLeft++;
			} // count the filled buttons on the top
			for (countRight = 0; x2 < 19 && !scraButton[x2 + 1][y].getText().equals(" "); x2++) {
				scraButton[x2 + 1][y].setBackground(Color.CYAN);
				scraButton[x2 + 1][y].setOpaque(true);
				changedTile.add(scraButton[x2 + 1][y]);
				countRight++;
			} // count the filled buttons under it
			System.out.println("countLeft is " + countLeft + " countRight is " + countRight);
		}
		changedScore = countLeft + countRight + 1;
	}

	public void refreshScore() {
		players[currentTurn].addScore(changedScore);

	}

	public void alternateTurn() {
		if (changedTile.size() != 0) { // reset the highlighted tiles
			for (ScrabbleButton b : changedTile) {
				b.setBackground(defaultColor);
				b.setOpaque(false);
			}
			changedTile.removeAll(changedTile);
		}

		// player finish his turn and alternates it to nest player
		if (currentTurn == indexInRoom) {
			passButton.setEnabled(false);
			myTurn.setText("");
			for (ScrabbleButton b : availableTile) {
				b.setEnabled(false);
			}
			System.out.println("here is alternate turn reset");
		} else { // other player finish last turn, reset the turn TextField.
			players[currentTurn].cleanTurnField();
		}
		currentTurn++;
		if (currentTurn == numberOfPlayer) {
			currentTurn = 0;
		}
		// if the player has the next turn
		if (currentTurn == indexInRoom) {
			passButton.setEnabled(true);
			myTurn.setText("Your Turn!");
			for (ScrabbleButton b : availableTile) {
				b.setEnabled(true);
			}
		} else {
			players[currentTurn].showTurnField();
		}
	}

	public void getWinnerScore() {
		int highestScore = 0, winner = -1, score;
		for (int i = 0; i < numberOfPlayer; i++) {
			score = players[i].getScore();
			if (score > highestScore) {
				highestScore = score;
				winner = i;
			}
		}
		if (winner == -1) {
			JOptionPane.showMessageDialog(instance, "Game Over, No one got highest score", "Game Result",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			boolean plural = false;
			String winnerName = players[winner].getName();
			for (int i = 0; i < numberOfPlayer; i++) {
				score = players[i].getScore();
				if (score == highestScore && i != winner) {
					winnerName = winnerName + " and " + players[i].getName();
					plural = true;
				}
			}
			if (!plural) {
				if (winner == indexInRoom) {
					JOptionPane.showMessageDialog(instance, "Congratulations, You won! Total Score is " + highestScore,
							"Game Result", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(instance,
							"Lucky next time, The winner is " + winnerName + ". Highest Score is " + highestScore,
							"Game Result", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				if (players[indexInRoom].getScore() == highestScore) {
					JOptionPane.showMessageDialog(instance,
							"Cheers, The winners are " + winnerName + ". Total Score is " + highestScore, "Game Result",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(instance,
							"Lucky next time, The winners are " + winnerName + ". Highest Score is " + highestScore,
							"Game Result", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		instance.dispose();
		GameHall.getInstance().backToHall();
		delete();
	}

	public boolean isFull() {
		return (availableTile.size() == 0);
	}

	public void delete() {
		instance = null;
	}
}