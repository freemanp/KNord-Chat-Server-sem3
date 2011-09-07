package server;

import java.net.Socket;

public class Chatter {
	public final int Id;
	public final String Name;
	public final Socket Socket;
	
	public Chatter(int id, String name, Socket socket) {
		this.Id = id;
		this.Name = name;
		this.Socket = socket;
	}
}
