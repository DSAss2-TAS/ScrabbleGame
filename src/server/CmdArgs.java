package server;

import org.kohsuke.args4j.Option;

public class CmdArgs {

	@Option(required = false, name = "-p", usage = "Port number")
	private int port = 44444;

	public int getPort() {
		return port;
	}

}