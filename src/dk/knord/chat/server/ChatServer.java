package dk.knord.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import dk.knord.chat.server.KNordHeaderFields.Requests;
import dk.knord.chat.server.KNordHeaderFields.Responses;
import dk.knord.chat.server.gui.IServerConsole;
import dk.knord.chat.server.gui.ServerConsole;

public class ChatServer {
	private static IServerConsole serverConsole;
	private List<ChatHandler> chatters;

	// private long startTime;
	// private final long TIMEOUT = 5000;

	public ChatServer(IServerConsole serverConsole) {
		chatters = new ArrayList<ChatHandler>();
		ChatServer.serverConsole = serverConsole;

		try {
			ServerSocket listener = new ServerSocket(4711);

			print("Chat server written by Andrius Ordojan, Paul Frunza, John Frederiksen");
			print("Server listening on port 4711");

			while (true) {
				Socket socket = listener.accept();

				print("Connection accepted from "
						+ socket.getInetAddress().getHostAddress());

				BufferedReader input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(socket.getOutputStream());
				String username = null;
				String message = input.readLine();

				StringTokenizer st = new StringTokenizer(message);
				// make sure it is a connect message.
				if (st.nextToken().equals(Requests.Connect)
						&& st.hasMoreTokens()) {
					// second line must be empty
					if (input.readLine().equals("")) {
						username = st.nextToken();
						// create new Chatter and ChatHandler
						Chatter chatter = createChatter(username, socket);
						ChatHandler chatHandler = new ChatHandler(chatter, this);
						chatters.add(chatHandler);
						chatHandler.setDaemon(true);
						chatHandler.start();
						print("New Chatter created named: " + chatter.Name);
						sendListToAll();
					} else {
						output.println(KNordHeaderFields.Responses.Unsupported);
						output.println();
						output.println(KNordHeaderFields.Responses.Disconnect);
						output.println();

						print("Disconnecting from "
								+ socket.getInetAddress().getHostAddress()
								+ " - UNSUPPORTED");

						socket.close();

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	protected synchronized static void print(String text) {
		serverConsole.print(text + "\r\n");
	}

	private Chatter createChatter(String name, Socket socket) {
		if (name == null)
			throw new IllegalArgumentException();
		if (socket == null)
			throw new IllegalArgumentException();

		name = name.trim();

		if (KNordHeaderFields.KeyWords.isKeyWord(name))
			name = "_" + name;

		int chatterAmount = chatters.size() - 1;

		int id = chatterAmount > 0 ? chatters.get(chatterAmount)
				.getChatter().Id + 1 : 0;

		int sameNames = 0;

		for (int i = 0; i < chatterAmount; i++) {
			if (chatters.get(i).getChatter().Name.contains(name))
				sameNames++;
		}

		if (sameNames > 0) {
			sameNames++;
			name += "_" + sameNames;
		}

		return new Chatter(id, name, socket);
	}

	protected void deleteChatter(ChatHandler chatter) {
		for (int index = 0; index < chatters.size(); index++) {
			if (chatters.get(index).equals(chatter)) {
				chatter.setRunning(false);
				chatters.remove(chatter);

				try {
					chatter.getChatter().Socket.close();
					print("Closed connection to " + chatter.getChatter().Name
							+ ".");
				} catch (IOException e) {
					e.printStackTrace();
				}
				sendListToAll();
			}
		}
	}

	protected void sendMessage(String target, String text, ChatHandler source) {
		// TODO This shits broken yo. the protocol I mean MESSAGE source
		boolean noSuchAlias = true;		
		for (int i = 0; i < chatters.size(); i++) {
			if (chatters.get(i).getChatter().Name.equals(target)) {
				String message = Responses.Message + " "
						+ source.getChatter().Name + "\n" + text + "\n";
				chatters.get(i).sendResponse(message);
				noSuchAlias = false;
			}
		}
		if (noSuchAlias)
			source.sendResponse(Responses.NoSuchAlias);
	}

	protected void broadcastMessage(String msg, String source) {
		StringBuilder response;

		for (ChatHandler c : chatters) {
			response = new StringBuilder();
			response.append(KNordHeaderFields.Responses.Message + " " + source);
			response.append("\r\n");
			response.append(msg);
			response.append("\r\n");

			c.sendResponse(response.toString());
		}
	}

	protected void listChatters(ChatHandler chatHandler) {
		StringBuilder response = new StringBuilder();
		response.append(KNordHeaderFields.Responses.List);
		response.append("\r\n");

		for (ChatHandler ch : chatters) {
			response.append(ch.getChatter().Name);
			response.append("\r\n");
		}

		// response.append("\r\n");

		chatHandler.sendResponse(response.toString());
	}

	protected void unknown(ChatHandler chatHandler) {
		StringBuilder response = new StringBuilder();
		response.append(KNordHeaderFields.Responses.Unknown);
		response.append("\r\n");

		chatHandler.sendResponse(response.toString());
	}

	protected void sendListToAll() {
		for (int index = 0; index < chatters.size(); index++) {
			listChatters(chatters.get(index));
		}
	}

	public static void main(String[] args) {
		IServerConsole serverConsole = new ServerConsole();
		new ChatServer(serverConsole);
	}
}
