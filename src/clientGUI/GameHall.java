package clientGUI;

import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.json.simple.JSONObject;

import client.ClientConnectionManager;
import server.ConnectionManager;
import server.ServerStatus;

import javax.swing.JTextArea;

public class GameHall extends JPanel {

	private static GameHall instance;

	private JButton newGame;
	private JTextArea userlistText;
	private JScrollPane scroller;
	private String inputStr;
	private ArrayList<String> playerList;

	public static GameHall getInstance() {
		if (instance == null) {
			instance = new GameHall();
		}
		return instance;
	}

	public GameHall() {
		setLayout(null);

		newGame = new JButton("New game");
		newGame.setBounds(100, 50, 200, 40);
		newGame.setFocusPainted(false);
		add(newGame);

		userlistText = new JTextArea();
		userlistText.setEditable(false);
		userlistText.setFocusable(false);
		userlistText.setLineWrap(true);
		userlistText.setWrapStyleWord(true);
		// add(userlistText);

		scroller = new JScrollPane(userlistText);
		scroller.setSize(300, 225);
		scroller.setLocation(50, 125);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller);
		// TODO display user list.
		playerList = ServerStatus.getInstance().getPlayerList();
		System.out.println(playerList==null);
		for (String player : playerList) {
			//System.out.println(playerList.toString());
			System.out.println(player);
			userlistText.append(player+"\n");
		}
		//userlistText.append(ClientConnectionManager.getInstance().getUsername()+"\n");
		setVisible(false);
		setVisible(true);
		startUp();
	}

	public void startUp() {
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// send new game command to server
				JSONObject request = new JSONObject();
				request.put("command", "newGame");
				try {
					ClientConnectionManager.getInstance().getOutput().writeUTF(request.toJSONString());
					ClientConnectionManager.getInstance().getOutput().flush();
					MainFrame.getInstance().gameRoomStartUp();
					enterRoom();
				} catch (IOException ex) {
					System.out.println("Fail to send username to server.");
				}
			}
		});
	}
	public void enterRoom(){
		newGame.setEnabled(false);
	}
	public void backToHall(){
		newGame.setEnabled(true);
	}
}
