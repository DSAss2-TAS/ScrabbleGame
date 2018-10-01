package clientGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import client.ClientConnectionManager;

public class LoginWindow extends JPanel {
	// connection manager client output and input username
	// get reader get writer get username
	// constructor (Socket clientsocket)
	// sendMsg while writer!=null and clientsocket isConnected()

	// loginPanel create listenThread getReader from ConnectionManager while
	// loginPanel serpreferredsize new dimension

	private static LoginWindow instance;
	private JLabel usernameLabel;
	private JTextField usernameText;
	private JButton loginButton;

	private String inputStr;

	public static LoginWindow getInstance() {
		if (instance == null) {
			instance = new LoginWindow();
		}
		return instance;
	}

	public LoginWindow() {
		setLayout(null);

		usernameLabel = new JLabel("username:");
		usernameLabel.setBounds(50, 100, 150, 40);
		add(usernameLabel);

		usernameText = new JTextField();
		usernameText.setBounds(150, 100, 150, 40);
		add(usernameText);

//		serverReply = new JLabel(inputStr);
//		serverReply.setBounds(50, 50, 300, 40);
//		add(serverReply);

		loginButton = new JButton("Login");
		loginButton.setBounds(150, 250, 100, 40);
		loginButton.setFocusPainted(false);
		add(loginButton);
		startUp();

	}

	public void startUp() {
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO check name existence in server game hall user list
				inputStr = usernameText.getText();
				if (inputStr.equals("")) {
					JOptionPane.showMessageDialog(MainFrame.getInstance(), "The username cannot be empty!");
				} else {
					JSONObject request = new JSONObject();
					request.put("command", "login");
					request.put("username", inputStr);
					try {
						ClientConnectionManager.getInstance().getOutput().writeUTF(request.toJSONString());
						ClientConnectionManager.getInstance().getOutput().flush();
						MainFrame.getInstance().gameHallStartUp();
					} catch (IOException ex) {
						System.out.println("Fail to send username to server.");
					}
				}
			}
		});
	}

}
