package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import game.GameListener;

public class ClientConnectionManager {

	private static ClientConnectionManager instance;
	private Socket clientSocket;
	private String username;
	private DataInputStream input;
	private DataOutputStream output;

	public static ClientConnectionManager getInstance() {
		return instance;
	}

	public static ClientConnectionManager getInstance(Socket clientSocket) {
		instance = new ClientConnectionManager(clientSocket);
		return instance;
	}

	private ClientConnectionManager(Socket clientSocket) {
		this.clientSocket = clientSocket;
		username = "";
		try {
			input = new DataInputStream(clientSocket.getInputStream());
			output = new DataOutputStream(clientSocket.getOutputStream());
			// inputStr = input.readUTF();

			Thread t = new Thread(new GameListener(input, output));
			t.start();
		} catch (IOException ex) {
			System.out.println("IOException: Something wrong when define Reader and Writer in Login Window!");
		}
	}

	public Socket getClientSocket() {
		return clientSocket;

	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public DataInputStream getInput() {
		return input;
	}

	public DataOutputStream getOutput() {
		return output;
	}
}
