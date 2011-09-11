package dk.knord.chat.server.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import dk.knord.chat.server.ChatServer;

public class ServerConsole implements IServerConsole {
	
	private JFrame frmChatServer;
	private JTextArea textArea;

	public ServerConsole() {
		initialize();
		frmChatServer.setVisible(true);
	}

	private void initialize() {
		frmChatServer = new JFrame();
		frmChatServer.setTitle("Chat Server Console");
		frmChatServer.setBounds(100, 100, 450, 300);
		frmChatServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatServer.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ChatServer.disconnect();

				frmChatServer.dispose();
			}
		});
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frmChatServer.getContentPane().setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		frmChatServer.getContentPane().add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		// auto scroll
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// word wrapping
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
	}

	@Override
	public void print(String text) {
		getTextArea().append(text);
	}

	protected JTextArea getTextArea() {
		return textArea;
	}

	@Override
	public JFrame getJFrame() {
		return frmChatServer;
	}
	
}
