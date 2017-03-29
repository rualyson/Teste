package messenger.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;


public class MessengerServerImpl implements MessengerServer {

	private HashMap<String, MessengerClient> users;

	public MessengerServerImpl() {
		this.users = new HashMap<String, MessengerClient>();
	}

	public void login(MessengerClient client, String userName)
			throws RemoteException {
		System.out.println("Login: " + userName);
		users.put(userName, client);
	}
	

	@Override
	public boolean sendMsg(String from, String to, String msg)
			throws RemoteException {
		System.out.println("Sending msg from " + from + " to " + to);
		MessengerClient toClient = users.get(to);
		if (toClient == null) {
			return false;
		}
		toClient.receiveMsg(from, msg);
		return true;
	}
	

	
	public void broadcast(String from, String msg) throws RemoteException{
		
		System.out.println("Broadcast by: " + from);
		for (MessengerClient client: users.values()){
			client.receiveMsg(from, msg);
		}
	}
	
	
	@Override
	public String listUsers() throws RemoteException {
		System.out.println("Listing logged users.");
		return users.keySet().toString();
	}

	@Override
	public void logout(String userName) throws RemoteException {
		System.out.println("Logout: " + userName);
		users.remove(userName);
	}

	public static void main(String[] args) {
		try {
			String host = "127.0.0.1";
			LocateRegistry.createRegistry(1099);
			MessengerServerImpl obj = new MessengerServerImpl();
			MessengerServer stub = (MessengerServer) UnicastRemoteObject
					.exportObject(obj, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry(host);
			registry.rebind("MessengerServer", stub);

			System.err.println("Server is running...");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
	
	
}