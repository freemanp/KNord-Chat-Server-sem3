package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.gui.IServerConsole;
import server.gui.ServerConsole;

public class ChatServer {
	private static IServerConsole serverConsole;
	private List<ChatHandler> chatters;

	public ChatServer(IServerConsole serverConsole) {
		chatters = new ArrayList<ChatHandler>();
		ChatServer.serverConsole = serverConsole;

		try {
			ServerSocket listener = new ServerSocket(4711);

			print("Chat server writen by Andrius Ordojan, Paul Frunza, John Frederiksen");
			print("Server listening on port 4711");

			while (true) {
				Socket socket = listener.accept();

				print("Connection accepted from " + socket.getInetAddress().getHostAddress());

				Chatter chatter = createChatter("andrius", socket);
				ChatHandler chatHandler = new ChatHandler(chatter, this);
				chatters.add(chatHandler);
				Thread t = new Thread(chatHandler);
				t.setDaemon(true);
				t.start();

				print("New Chatter created named: " + chatter.Name);
			}
		} 
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	protected static void print(String text) {
		serverConsole.print(text + "\r\n");
	}

	private Chatter createChatter(String name, Socket socket) {
		if (name == null) throw new IllegalArgumentException();
		if (socket == null) throw new IllegalArgumentException();

		int chatterAmount = chatters.size();

		int id = chatterAmount > 0 
				? chatters.get(chatterAmount).getChatter().Id + 1 
				: 0;
		
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
		for (ChatHandler ch : chatters) {
			if (ch.equals(chatter)) {
				chatter.interrupt();
				chatters.remove(chatter);
			}
		}
	}

	protected void sendMessage(String target, String text) {
		// TODO This shits broken yo. the protocol I mean  MESSAGE source
		for (int i = 0; i < chatters.size(); i++) {
			if(chatters.get(i).getChatter().Name.equals(target)) {
				chatters.get(i).sendResponse(text);
			}
		}
	}

	protected void broadcastMessage(String msg) {
		StringBuilder response;

		for (ChatHandler c : chatters) {
			response = new StringBuilder();
			response.append(KNordHeaderFields.Responses.Message);
			response.append("\r\n");
			response.append(msg);
			response.append("\r\n");
			response.append("\r\n");
			
			c.sendResponse(msg);
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

		response.append("\r\n");

		chatHandler.sendResponse(response.toString());
	}

	protected void Unknown(ChatHandler chatHandler) {
		StringBuilder response = new StringBuilder();
		response.append(KNordHeaderFields.Responses.Unknown);
		response.append("\r\n");
		response.append("\r\n");

		chatHandler.sendResponse(response.toString());
	}

	public static void main(String[] args) {
		IServerConsole serverConsole = new ServerConsole();
		new ChatServer(serverConsole);
	}
}
