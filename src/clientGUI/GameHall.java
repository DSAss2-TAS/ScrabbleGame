package clientGUI;

import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import client.ClientConnectionManager;

import javax.swing.JTextArea;

public class GameHall extends JPanel {

	private static GameHall instance;

	private JButton newGame;
	private JTextArea userlistText;
	private JScrollPane scroller;
//	private JLabel listLabel;
	private static ArrayList<String> playerList;

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

//		listLabel = new JLabel("Welcome");
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
				request.put("command", "NEW_GAME");
				try {
					ClientConnectionManager.getInstance().getOutput().writeUTF(request.toJSONString());
					ClientConnectionManager.getInstance().getOutput().flush();
					
				} catch (IOException ex) {
					System.out.println("Fail to send username to server.");
				}
			}
		});
	}

	public void refreshArea(JSONArray list) {
		userlistText.setText(null);
		if (playerList == null)
			playerList = new ArrayList<>(); // Used to store the subject names
		else
			playerList.removeAll(playerList);
		String name;
		Iterator<JSONObject> it = list.iterator();
		while (it.hasNext()) {
			name = (String) it.next().get("name");
			// TODO make the TextArea selectable 
			userlistText.append(name + "\n");
			playerList.add(name);
		}
	}

	public void enterRoom() {
		newGame.setEnabled(false);
	}

	public void backToHall() {
		newGame.setEnabled(true);
	}
}
