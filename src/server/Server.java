package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

// The server class is for create a server socket and dispatch threads for each ConnectionManager.
// The default IP address is dstas.ddns.net
public class Server {
	private static int port = 44444;
	private static int counter;
	private static ServerSocket server;

	public static void main(String[] args) {
		// port = Integer.parseInt(args[0]);
		// Declare the port number
		CmdArgs cmdLineParam = new CmdArgs();
		CmdLineParser parser = new CmdLineParser(cmdLineParam);
		counter = 0;
		try {
			parser.parseArgument(args);
			port = cmdLineParam.getPort();
			if (port > 65535 || port < 6000) {
				System.out.println("Oops,suggested port number range: 6000 to 65535...\n");
				System.exit(0);
			}
			server = new ServerSocket(port);
			System.out.println("Listening at port " + server.getLocalPort() + ". Waiting for client connections...\n");
			while (true) {
				// Wait for connections.
				Socket clientSocket = server.accept();
				counter++;
				System.out.println("The Client " + counter + ": Applying for connection!");
				// Start a new thread for a connection
				ConnectionManager connection = new ConnectionManager(clientSocket, counter, ServerStatus.getInstance());
				Thread t = new Thread(connection);
				t.start();
				System.out.println("The Client " + counter + " has connected!");
			}
		} catch (SocketException e) {
			System.out.println("SocketException: The client socket is closed!");
		} catch (IOException so) {
			System.out.println("IOException: Something wrong when define PrintWriter!");
		} catch (CmdLineException e) {
			System.out.println("Command Line Exception: Input Arguments error!");
		}
	}
}
