package Opdr2;

import java.rmi.*;
import java.util.List;

public interface Component_RMI extends Remote {

	public void recieveRequest(int idSender, int Ni) throws RemoteException;

	public void recieveToken(List<Object> token, int idSender) throws RemoteException;
	
	public void makeRequest() throws RemoteException;
}
