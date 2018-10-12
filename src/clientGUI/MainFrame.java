package clientGUI;

import java.awt.event.WindowAdapter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;

import client.ClientConnectionManager;

public class MainFrame extends JFrame {
	private static MainFrame instance;

	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}

	private MainFrame() {
		setTitle("Welcome to Scrabble Game");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setResizable(false);
		setVisible(true);
		setBounds(300, 300, 400, 400);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(instance, "Are you sure you want to exit Scrabble Game?",
						"Exit Game?", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					ClientConnectionManager connectionManager = ClientConnectionManager.getInstance();
					if (connectionManager == null) {
						System.exit(0);
					}
					JSONObject request = new JSONObject();
					request.put("command", "EXIT");
					request.put("content", connectionManager.getUsername());
					try {

						connectionManager.getOutput().writeUTF(request.toJSONString());
						connectionManager.getOutput().flush();
					} catch (IOException e) {
						System.out.println("Fail to send EXIT command in MainFrame.");
					}

				}
			}
		});
	}

	public void connectionStartUp() {

		add(ConnectionWindow.getInstance());
		revalidate();
		repaint();
	}

	public void loginStartUp() {
		remove(ConnectionWindow.getInstance());
		add(LoginWindow.getInstance());
		revalidate();
		repaint();
	}

	public void gameHallStartUp() {
		remove(LoginWindow.getInstance());
		add(GameHall.getInstance());
		revalidate();
		repaint();
	}

}
