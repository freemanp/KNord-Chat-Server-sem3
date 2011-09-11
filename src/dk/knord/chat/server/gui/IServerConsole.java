package dk.knord.chat.server.gui;

import javax.swing.JFrame;

public interface IServerConsole {
	void print(String text);
	JFrame getJFrame();
}
