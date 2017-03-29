package messenger.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MessengerClientImpl implements MessengerClient {

	private String userName;
	private static String COMMANDS = "Commands: \n" +
									 "\t login \n" +
									 "\t msg <username> <message> \n" +
									 "\t users \n" +
									 "\t broadcast <message> \n" +
									 "\t logout";
	
	private static String INIT_CHAR = "$ ";
	
	
	public MessengerClientImpl(String userName) {
		this.userName = userName;
	}
	public void receiveMsg(String from, String msg) throws RemoteException {
		System.out.println();
		System.out.println("<" + from + "> " + msg);
		System.out.print(INIT_CHAR);
	}
	
	private static void handleMsg(String userName, MessengerServer server,
			StringTokenizer lineTokenizer) throws RemoteException {
		String to = lineTokenizer.nextToken();
		StringBuilder msg = new StringBuilder();
		while (lineTokenizer.hasMoreTokens()) {
			msg.append(lineTokenizer.nextToken() + " ");
		}
		if (!server.sendMsg(userName, to, msg.toString())) {
			System.err.println("Message not sent!");
		}
	}
	
	
	private static void broadcastMens(String userName, MessengerServer server,
			StringTokenizer lineTokenizer) throws RemoteException {
		StringBuilder msg = new StringBuilder();
		while (lineTokenizer.hasMoreTokens()) {
			msg.append(lineTokenizer.nextToken() + " ");
		}
		server.broadcast(userName, msg.toString());
		
	}
	

	public static void main(String[] args) {
		try {
			String userName = args[0];
			String host = "localhost";
			MessengerClient client = new MessengerClientImpl(userName);
			MessengerClient stub = (MessengerClient) UnicastRemoteObject
					.exportObject(client, 0);
			Registry registry = LocateRegistry.getRegistry("localhost");
			MessengerServer server = (MessengerServer) registry
					.lookup("MessengerServer");
			Scanner input = new Scanner(System.in);
			String line = "";
			System.out.println(COMMANDS);
			System.out.print(INIT_CHAR);
			while (!(line = input.nextLine().trim().toLowerCase())
					.equals("exit")) {
				
				StringTokenizer lineTokenizer = new StringTokenizer(line, " ");
				if (lineTokenizer.hasMoreTokens()) {
					String command = lineTokenizer.nextToken();
					if (command.equals("login")) {
						server.login(client, userName);
						System.out.println();
						System.out.println("O usuário " + userName + " entrou no chat ");
						
					} else if (command.equals("msg")) {
						handleMsg(userName, server, lineTokenizer);
					} else if (command.equals("users")) {
						System.out.println(server.listUsers());
					}else if (command.equals("broadcast")) {
						broadcastMens(userName, server, lineTokenizer);
					}else if (command.equals("logout")) {
						server.logout(userName);
					} else {
						System.err.println("Unknown command: " + command + "\n" + COMMANDS);
					}
				}
				System.out.print("$ ");
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
