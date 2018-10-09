package TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

	private SecureTCPsocket socket = null;
	private volatile int ID = -1;
	private final TCPServer localServer;
	private volatile String handle = null;
	private volatile Thread thread;
	protected static final String goodBye = "Bye.",socketTimeOut="";

	public ConnectionHandler(TCPServer _tcpMan, SecureTCPsocket _encryptedSocket) {
		localServer = _tcpMan;
		socket = _encryptedSocket;
		ID = socket.getPort();
	}

	public void start() throws IOException {
		socket.open();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public String getHandle() {
		return handle;
	}

	public boolean send(String msg) {
		// System.out.println(msg);
		if (socket.send(msg)) {
			return true;
		} else {
			localServer.remove(ID);
			return false;
		}
	}

	private String receiveTransmission() {
		String temp = goodBye;
		if (socket != null) {
			try {
				temp = socket.receiveTransmission();
			} catch (IOException e) {
				temp = e.toString();
				if (temp.indexOf("java.net.SocketTimeoutException") >= 0) {
					temp = socketTimeOut;
				} else {
					localServer.remove(ID);
					stop();
				}
			} finally {

			}
		} else {
			stop();
		}
		return temp;
	}

	public int getID() {
		return ID;
	}

	public boolean logon() {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	public void stop() {
		socket.close();
		localServer.remove(ID);
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			thread = null;
		}
	}

	public void run() {
		localServer.printServer("Socket: " + getWho() + "  started.");
		while (thread != null) {
			String input = receiveTransmission();

			if (input == null) {
				input = goodBye;
				stop();
			} else if (input.indexOf(goodBye) == 0) {
			} else if (input.indexOf("COM:") == 0) {
				processCommand(input.substring(4));
			} else if (input.equalsIgnoreCase(socketTimeOut)) {
				// Socket timed out - move on - time out is short so connection handler doesn't
				// freeze up
			}
		}
		localServer.remove(ID);
	}

	private void processCommand(String recievedCommand) {
		// TODO process command in TCP needs to be built
		int endType = recievedCommand.indexOf(':') + 1;
		int endString = recievedCommand.length() - 1;
		String command = recievedCommand.substring(0, endType - 1);
		String commandData = recievedCommand.substring(endType, endString);
		int count = 0;
		for (int i = 0; i < commandData.length(); i++) {
			if (commandData.substring(i, i + 1).equals(","))
				count++;
		}
		String info[] = new String[count];
		for (int i = 0; i < count; i++) {
			int endInfo = commandData.indexOf(',');
			endString = commandData.length();
			info[i] = commandData.substring(0, endInfo);
			commandData = commandData.substring(endInfo + 1, endString);
		}
	}

	public String getWho() {
		return handle + " (" + ID + ")";
	}

	public boolean isControlNode() {
		// TODO ADD CONTROL NODE FEATURE
		return false;
	}

	public void sendCommand(int senderID, String _command) {
		// TODO sendCommand(String senderInfo, String _command) needs to be built

	}

}
