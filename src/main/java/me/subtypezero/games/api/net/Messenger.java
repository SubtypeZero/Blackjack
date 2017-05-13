package me.subtypezero.games.api.net;

import java.io.*;
import java.net.Socket;

public class Messenger {

	public static void sendMessage(Socket socket, Message message) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeByte(message.getType()); // Write message type
			out.writeUTF(message.getData()); // Write data
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message getResponse(Socket socket) {
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			int type = in.readByte(); // Read message type
			String data = in.readUTF(); // Read data
			return new Message(type, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
