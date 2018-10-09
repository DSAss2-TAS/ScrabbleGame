package clientGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.ClientConnectionManager;

public class ConnectionWindow extends JPanel{
	
	private static ConnectionWindow instance;
	private JLabel portLabel;
	private JLabel addressLabel;
	private JTextField portText;
	private JTextField addressText;
	private JButton connectButton;
	
	public static ConnectionWindow getInstance() {
		if (instance == null) {
			instance = new ConnectionWindow();
		}
		return instance;
	}
	private ConnectionWindow(){
		setLayout(null);
		portLabel = new JLabel("Port Number:");
		portLabel.setBounds(50, 50, 150, 40);
		add(portLabel);
		addressLabel = new JLabel("IP Address:");
		addressLabel.setBounds(50, 150, 150, 40);
		add(addressLabel);
		portText = new JTextField();
		portText.setBounds(150, 50, 150, 40);
		add(portText);
//		setVisible(false);
//		setVisible(true);
		addressText = new JTextField("localhost");
		addressText.setBounds(150, 150, 150, 40);
		add(addressText);
		connectButton = new JButton("Connect");
		connectButton.setBounds(150, 250, 100, 40);
		connectButton.setFocusPainted(false);
		add(connectButton);
		startUp();
	}
	
	public void startUp(){
		// listen connection request, try to connect to server.
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String serverIP = addressText.getText();
				String serverPort = portText.getText();

				if (serverIP.equals("")) {
					JOptionPane.showMessageDialog(instance, "Please enter the IP address!");
				} else if (serverPort.equals("")) {
					JOptionPane.showMessageDialog(instance, "Please enter the port number!");
				} else {
					try {
						// create a connection manager at client 
						ClientConnectionManager.getInstance(new Socket(serverIP, Integer.parseInt(serverPort)));
						System.out.println("Connection established...\n");
						MainFrame.getInstance().loginStartUp();

					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(instance, "Wrong data type. Please enter the port number!");
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(instance, "Unknown host exception. Please check the IP address!");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(instance,
								"Fail to connect to server. Please check the IP address and port number");
					}
				}

			}
		});
		Object[] possibleValues = { "First", "Second", "Third" };

		 Object selectedValue = JOptionPane.showInputDialog(null,
		             "Choose one", "Input",
		             JOptionPane.INFORMATION_MESSAGE, null,
		             possibleValues, possibleValues[0]);

		
	}

}
