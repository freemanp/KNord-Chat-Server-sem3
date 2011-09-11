package dk.knord.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class ChatHandler extends Thread {
	private Chatter chatter;
	private ChatServer server;
	private BufferedReader input;
	private PrintWriter output;
	private boolean running = true;

	public ChatHandler(Chatter chatter, ChatServer server) throws IOException {
		if (chatter == null)
			throw new IllegalArgumentException();
		if (server == null)
			throw new IllegalArgumentException();

		this.chatter = chatter;
		this.server = server;

		input = new BufferedReader(new InputStreamReader(
				chatter.Socket.getInputStream()));
		output = new PrintWriter(chatter.Socket.getOutputStream());
	}

	/**
	 * Waits for input from the client and returns a command object. If the
	 * command is not recognized, null is returned.
	 * 
	 * @return Command object if the command sent is correct, null if
	 *         unrecognized
	 * @throws IOException
	 */
	private Command readInput() throws Exception {
		String line = input.readLine();
		while (line.length() < 4)
			line = input.readLine();

		boolean ok = false;
		for (int i = 0; i < KNordHeaderFields.Requests.commandsList.length; i++) {
			if (line.startsWith(KNordHeaderFields.Requests.commandsList[i])) {
				ok = true;
				break;
			}
		}

		if (ok) {
			StringTokenizer st = new StringTokenizer(line);
			Command c = new Command(st.nextToken());
			if (st.hasMoreTokens())
				c.extra = st.nextToken();

			// if (input.ready()) {
			StringBuilder sb = new StringBuilder();
			String previousLine = "something";

			while (!(line.equals("") && previousLine.equals(""))) {
				line = input.readLine();
				sb.append(line + "\r\n");
				previousLine = line;
			}

			String content = sb.toString().trim();
			if (content.length() > 0)
				c.content = content;
			// }

			return c;
		}

		return null;
	}

	@Override
	public void run() {
		try {
			while (running) {

				Command c = readInput();

				if (c != null) {
					ChatServer.print("Command received: " + c.name);
					if (c.extra != null)
						ChatServer.print("extra: >" + c.extra + "<");
					if (c.content != null)
						ChatServer.print("content: >" + c.content + "<");

					if (c.name.equals(KNordHeaderFields.Requests.Connect)) {

					} else if (c.name
							.equals(KNordHeaderFields.Requests.Disconnect)) {
						server.deleteChatter(this);
					} else if (c.name
							.equals(KNordHeaderFields.Requests.Message)) {
						if (c.extra != null) {
							if (c.content != null) {
								if (c.extra.equals("ALL")) {
									server.broadcastMessage(c.content,
											getChatter().Name);
								} else {
									server.sendMessage(c.extra, c.content, this);
								}
							} else {
								server.unknown(this);
							}
						} else {
							server.unknown(this);
						}
					} else if (c.name.equals(KNordHeaderFields.Requests.List)) {
						server.listChatters(this);
					}
				} else {
					server.unknown(this);
				}
			}

			chatter.Socket.close();
		} catch (Exception ioe) {
			ioe.printStackTrace(System.err);
		}
	}

	public Chatter getChatter() {
		return chatter;
	}

	public void sendResponse(String response) {
		output.println(response);
		output.flush();
		ChatServer.print("Sent to " + getChatter().Name + ":\n> " + response
				+ "<");
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

}