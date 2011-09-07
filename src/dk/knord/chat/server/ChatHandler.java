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
	
	public ChatHandler(Chatter chatter, ChatServer server) throws IOException {
		if (chatter == null) throw new IllegalArgumentException();
		if (server == null) throw new IllegalArgumentException();

		this.chatter = chatter;
		this.server = server;

		input = new BufferedReader(new InputStreamReader(chatter.Socket.getInputStream()));
		output = new PrintWriter(chatter.Socket.getOutputStream());
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				StringTokenizer st;
				
				if(input.ready()) {
					String line =  input.readLine();
					
					if (line.startsWith(KNordHeaderFields.Requests.Connect)) {
						
					}
					else if (line.startsWith(KNordHeaderFields.Requests.Disconnect)) {
						server.deleteChatter(this);
					}
					else if (line.startsWith(KNordHeaderFields.Requests.Message)) {
						st = new StringTokenizer(line);
						String target = st.nextToken();
						String msg = input.readLine();
						server.sendMessage(target, msg);
					}
					else if (line.startsWith(KNordHeaderFields.Requests.MessageAll)) {
						server.broadcastMessage(input.readLine());
					}
					else if (line.startsWith(KNordHeaderFields.Requests.List)) {
						server.listChatters(this);
					}
					else
						server.Unknown(this);
				}
				
//				connection.getOutput().println(text);
//				connection.getOutput().flush();
			}
			
			chatter.Socket.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
	}
	
	public Chatter getChatter() {
		return chatter;
	}
	
	public void sendResponse(String response) {
		output.println(response);
	}
	
}



