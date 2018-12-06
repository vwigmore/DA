package Opdr2;


import java.rmi.*;
import java.util.ArrayList;

public interface Component_RMI extends Remote {

	public void recieveRequest(int idSender, int Ni) throws RemoteException;

	public void recieveToken(ArrayList<Object> token, int idSender) throws RemoteException;
	
	public void makeRequest() throws RemoteException;
}
