package clientGUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.DataOutputStream;
import java.io.IOException;

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

	private JButton passButton;
	private JPanel jPanelNorth;
	private JPanel jPanelSouth;
	private JPanel jPanelCenter;
	private JPanel jPanelWest;
	private JPanel jPanelWest1;
	private JPanel jPanelWest2;
	private JPanel jPanelWest3;

	private String title;
	private String inputStr;
	private ScrabbleButton scraButton[][];
	private ScrabbleButton currentButton;
	private int currentRow, currentColumn;
	private DataOutputStream output;

	public static GameRoom getInstance(String roomID) {
		instance = new GameRoom(roomID);
		return instance;
	}

	public static GameRoom getInstance() {
		return instance;
	}

	// initialize
	private GameRoom(String roomID) {
		title = "Welcome to " + ClientConnectionManager.getInstance().getUsername() + "'s Room, Number " + roomID;
		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		scraButton = new ScrabbleButton[20][20];
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				scraButton[i][j] = new ScrabbleButton(i, j); // create buttons
				jPanelCenter.add(scraButton[i][j]);// add buttons to the game
													// panel
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
		invite = new JButton("Invite");
		passButton = new JButton("Pass");
		System.out.println("Here is game room constructor");

	}

	// initialize GUI
	private void buildGUI() {
		// TODO disable buttons except ready and invite until game start
		// north part
		System.out.println("Here is build GUI");
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
		jPanelSouth.add(passButton);
		add(BorderLayout.SOUTH, jPanelSouth);

		ready.setEnabled(false);
		passButton.setEnabled(false);
		// this.pack();
		revalidate();
		repaint();
	}

	// run client
	public void initialization() {
		buildGUI();
		ClientConnectionManager connectionManager = ClientConnectionManager.getInstance();
		output = connectionManager.getOutput();
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

	// listen button click
	ActionListener scraButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			inputStr = inputText.getText();
			if (inputStr.length() == 1 ){
				if('a' <= inputStr.charAt(0) && inputStr.charAt(0) <= 'p'
						|| 'A' <= inputStr.charAt(0) && inputStr.charAt(0) <= 'P') {
					currentButton = (ScrabbleButton) e.getSource();
					currentButton.setText(inputStr);
					currentRow = currentButton.getRow();
					currentColumn = currentButton.getColumn();
					String[] options = {"Horizontal", "Vertical"};
					JSONObject request = new JSONObject();
					request.put("command", "INSERT");
					request.put("content", currentButton.getText());
					request.put("row", currentRow);
					request.put("column", currentColumn);
					int option = JOptionPane.showOptionDialog(instance, "Please select the direction of your word.", "Choose Direction.",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,options[0]);
					if ( option == JOptionPane.YES_OPTION) {
						
						request.put("direction", "horizontal");
						
					}else if(option == JOptionPane.NO_OPTION){
						request.put("direction", "vertical");
					}else{	// option==JOptionPane.CLOSED_OPTION
						JOptionPane.showMessageDialog(instance, "The result would be Horizontal if you close it without selection.");
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
	int wordscore;
	int countLeft;
	int countRight;

	public int countWord(boolean h, int x, int y) {
		if (h == true) {
			for (countLeft = 0; scraButton[x][y - 1].getText() != null; y--) {
				countLeft++;
			}
			for (countRight = 0; scraButton[x][y + 1].getText() != null; y++) {
				countRight++;
			}
			return wordscore = countLeft + countRight + 1;
		}

		else {
			for (countLeft = 0; scraButton[x - 1][y].getText() != null; x--) {
				countLeft++;
			}
			for (countRight = 0; scraButton[x + 1][y].getText() != null; x++) {
				countRight++;
			}
			return wordscore = countLeft + countRight + 1;
		}
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

	// send message to server
	// ActionListener SayListener = new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// String aText = sayText.getText();
	// if (aText.equals("")) {
	// JOptionPane.showMessageDialog(clientFrame, "message cannot be
	// empty");
	// } else {
	// try {
	// writer.write(initInput + "：" + aText + "\n");
	// writer.flush();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// sayText.setText("");
	// }
	// }
	// };
	// passButton.addActionListener(SayListener);
	// sayText.addActionListener(SayListener);

	public void delete() {
		instance = null;
	}

	public void waitToStart() {
		ready.setEnabled(true);

	}

	public void gameStart() {
		invite.setEnabled(false);
		ready.setEnabled(false);
	}
}