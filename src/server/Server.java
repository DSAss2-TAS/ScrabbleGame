package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
  

public class Server {
	private static int port;
	// public static ServerStatus thisServer = new ServerStatus();
	// Identifies the user number connected
	private static int counter;
	private static ServerStatus serverStatus;
	private static ServerSocket server;
//	private static ServerStatus serverStatus;

	public static void main(String[] args) {
		// port = Integer.parseInt(args[0]);
		// Declare the port number
				port = 4444;
				counter = 0;
				try {
					server = new ServerSocket(port);
					serverStatus = ServerStatus.getInstance();
					System.out.println("Waiting for client to connect...\n");
					while (true) {
						// Wait for connections.
						Socket clientSocket = server.accept();
						counter++;
						System.out.println("The Client " + counter + ": Applying for connection!");
						// Start a new thread for a connection
						ConnectionManager connection = new ConnectionManager(clientSocket, counter, serverStatus);
						Thread t = new Thread(connection);
						t.start();
						serverStatus.clientConnected(connection);
						System.out.println("The Client " + counter + " has connected!");
					}
				} catch (SocketException e) {
					System.out.println("SocketException: The client socket is closed!");
				} catch (IOException so) {
					System.out.println("IOException: Something wrong when define PrintWriter!");
				}
	}
}
