package TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris
 *
 */
public class TCPServer implements Runnable {

	private static volatile ServerSocket server = null;
	private static volatile Thread thread = null;
	private static volatile int port = 9000;
	protected TCPinterface controller;
	private static volatile List<ConnectionHandler> clients;
	protected final static int ALL = -1, SERVER = -2, FINDFAIL = -10;

	public TCPServer(TCPinterface _controller) {
		this.controller = _controller;
		clients = new ArrayList<ConnectionHandler>();
	}

	public void run() {
		while (thread != null) {
			try {
				printServer("Waiting for new connection ...");
				addThread(server.accept());
			} catch (IOException ioe) {
				printServer("Server accept error: " + ioe);
				stop();
			}
		}
	}

	public void start(int p) {
		port = p;
		server = null;
		openSocket();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	private void openSocket() {
		try {
			server = new ServerSocket(port);
			printServer("Port " + port + " open.");
			// start();
		} catch (IOException ioe) {
			printServer("Can not bind to port " + port + ": " + ioe.getMessage());
		}
	}

	public void stop() {
		remove(ALL);// -1 is the all ID int
		update();
		if (server != null) {
			try {
				server.close();
				server = null;
			} catch (IOException e) {
				printServer("TCPServer.Stop() Fail:" + e);
			}

		}
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			thread = null;
		}
	}

	/**
	 * <code>TCPServer.checkClientID(int </code>ID<code>)</code>
	 * 
	 * @param ID
	 *            int value that is searched for in clients list
	 * @return True if ID value of the Client is found in List, False if ID is not
	 *         found.
	 * 
	 */

	public boolean checkClientID(int ID) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getID() == ID)
				return true;
		return false;
	}

	/**
	 * <code>TCPServer.checkControlNode(int </code>ID<code>)</code>
	 * 
	 * @param ID
	 *            int value that is used to search for client in Client list
	 * @return True if Client is found to be a Control Node in List, False if ID is
	 *         not found or Client is not a Control Node.
	 * 
	 */

	public boolean checkControlNode(int ID) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getID() == ID) {
				if (clients.get(i).isControlNode()) {
					return true;
				}else
					return false;
			}
		return false;
	}

	/**
	 * <code>TCPServer.getClientHandles()</code>
	 * 
	 * @param None
	 *            - Gets all Handles from current Clients connected.
	 * @return The Handle value of all the Clients.
	 * 
	 */

	public String[] getClientHandles() {
		String[] handles = new String[clients.size()];
		for (int i = 0; i < clients.size(); i++)
			handles[i] = clients.get(i).getHandle();
		return handles;
	}

	/**
	 * <code>TCPServer.getClientIDs()</code>
	 * 
	 * @param None
	 *            - Gets all IDs from current Clients connected.
	 * @return The ID value of all the Clients.
	 * 
	 */
	public int[] getClientIDs() {
		int[] IDs = new int[clients.size()];
		for (int i = 0; i < clients.size(); i++)
			IDs[i] = clients.get(i).getID();
		return IDs;
	}

	/**
	 * <code>TCPServer.getControlNodes(int <code>retType</code>)</code>
	 * 
	 * @param retType
	 *            is used to tell command what type of info to return from current Clients connected if they are a control node.
	 * @return Depending on retType, either ID, Index, or Handle value of all the Clients that are a Control Node are returned.
	 * 
	 */
	public int[] getControlNodes() {
		int[] IDs = new int[clients.size()];
		for (int i = 0; i < clients.size(); i++)
			IDs[i] = clients.get(i).getID();
		return IDs;
	}

	/**
	 * <code>TCPServer.findClientHandleIndex(String </code>handle<code>)</code>
	 * 
	 * @param handle
	 *            String value that is searched for in clients list
	 * @return The index value of the Client List, if a current client connection
	 *         has the handle specified. Otherwise it returns <code>FINDFAIL</code>
	 *         a value of {@value #FINDFAIL}.
	 * 
	 */

	public int findClientHandleIndex(String handle) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getHandle().equals(handle)) {
				return i;
			}
		return FINDFAIL;
	}

	/**
	 * <code>TCPServer.findClientHandleID(String </code>handle<code>)</code>
	 * 
	 * @param handle
	 *            String value that is searched for in clients list
	 * @return The ID of the Client if a current client connection has the handle
	 *         specified. Otherwise it returns <code>FINDFAIL</code> a value of
	 *         {@value #FINDFAIL}.
	 * 
	 */
	public int findClientHandleID(String handle) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getHandle().equals(handle)) {
				return clients.get(i).getID();
			}
		return FINDFAIL;
	}

	/**
	 * <code>TCPServer.findClientHandleIndex(String </code>handle<code>)</code>
	 * 
	 * @param ID
	 *            int value that is searched for in clients list
	 * @return The index value of the Client List, if a current client connection
	 *         has the handle specified. Otherwise it returns <code>FINDFAIL</code>
	 *         a value of {@value #FINDFAIL}.
	 * 
	 */

	public int findClientIDIndex(int ID) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getID() == ID)
				return i;
		return FINDFAIL;
	}

	public synchronized void sendClient(String toWho, String input, String sender) {
		int senderID;
		int clientID;
		if (toWho.equalsIgnoreCase("all")) {
			clientID = ALL;
		} else {
			clientID = clients.get(findClientHandleIndex(toWho)).getID();
		}
		if (sender.equalsIgnoreCase("server")) {
			senderID = SERVER;
		} else {
			senderID = clients.get(findClientHandleIndex(sender)).getID();
		}
		if (clientID == FINDFAIL || senderID == FINDFAIL) {
			printServer("Unable to send:" + input + " to " + toWho + " from " + sender);
		} else if (clientID == ALL) {
			sendMsgAll(input, senderID);
		} else {
			sendPrivateMsg(clientID, input, senderID);
		}
	}

	public synchronized void sendPrivateMsg(int toWhoID, String input, int senderID) {
		String senderInfo, toWhoInfo;
		if (senderID == SERVER) {
			senderInfo = "SERVER";
		} else {
			senderInfo = clients.get(senderID).getWho();
		}
		if (toWhoID == ALL) {
			sendMsgAll(input, senderID);
		} else {
			toWhoInfo = clients.get(toWhoID).getWho();
			if (input.equals("Bye.")) {
				clients.get(findClientIDIndex(toWhoID)).send("Bye.");
				printServer("[CLIENT] " + senderInfo + ": Said Goodbye.");
				remove(toWhoID);
			}
			clients.get(findClientIDIndex(toWhoID)).send(senderInfo + " (private) " + ": " + input);
			printClient(senderInfo + " >> [" + toWhoInfo + "] " + ": " + input);
		}
	}

	public synchronized void sendMsgAll(String input, int senderID) {
		String senderInfo;
		if (senderID == SERVER) {
			senderInfo = "SERVER";
		} else {
			senderInfo = clients.get(senderID).getWho();
		}
		for (int i = 0; 0 < clients.size(); i++) {
			clients.get(i).send(senderInfo + ":" + input);
		}
	}

	public synchronized void sendMsgAll(String input, String sender) {
		int senderID = findClientHandleID(sender);
		sendMsgAll(input, senderID);
	}

	public synchronized void sendCommandAll(String input, String sender) {
		int senderID = findClientHandleID(sender);
		sendCommandAll(input, senderID);
	}

	public synchronized void sendCommandAll(String _command, int senderID) {
		String senderInfo;
		if (senderID == SERVER) {
			senderInfo = "SERVER";
		} else {
			senderInfo = clients.get(senderID).getWho();
		}
		for (int i = 0; 0 < clients.size(); i++) {
			clients.get(i).sendCommand(senderInfo, _command);
		}
	}

	public synchronized void remove(int ID) {
		int posC;
		if (ID == ALL) {
			for (int t = 0; 0 < clients.size(); t++) {
				goodByeAndClose(t);
			}
		} else {
			posC = findClientIDIndex(ID);
			goodByeAndClose(posC);
		}
		update();
	}

	private void goodByeAndClose(int clientIndexPosition) {
		String whoToRemove;
		ConnectionHandler toTerminate;
		if (clientIndexPosition >= 0 && clientIndexPosition < clients.size()) {
			toTerminate = clients.get(clientIndexPosition);
			whoToRemove = toTerminate.getWho();
			printServer("Removing " + whoToRemove + " connection.");
			sendCommandAll(controller.REMOVE, "Server");
			toTerminate.stop();
		} else {
			printServer("Unable to terminate conection ID:" + clientIndexPosition);
		}
	}

	private void addThread(Socket socket) {
		ConnectionHandler tempConnect;
		printServer("Connection accepted: " + socket.toString());
		tempConnect = new ConnectionHandler(this, new SecureTCPsocket(socket));
		try {
			tempConnect.start();
			if (tempConnect.logon()) {

			} else {
				printServer("Didn't start thread/n/rClosing socket: " + tempConnect.toString());
				tempConnect.stop();
			}
		} catch (IOException ioe) {
			printServer("Error opening thread: " + ioe);
		}
		update();
	}

	private synchronized void printClient(String text) {
		controller.printClient("TCP Server:" + text + "\r\n");
	}

	synchronized void printServer(String text) {
		controller.printServer("TCP Server:" + text + "\r\n");
	}

	private synchronized void writeLog(String Text) {
		controller.logWrite("TCP Server:" + Text + "\r\n");
	}

	protected synchronized void update() {
		String[] clientHandles = getClientHandles();
		String[] serverNodes = getServerNodes();
		controller.update(clientHandles, serverNodes);
	}
}
