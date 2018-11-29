package Opdr2;

import java.rmi.*;
import java.util.List;

public interface Component_RMI extends Remote {

	public void sendRequest(Object request, int idSender) throws RemoteException;

	public void sendToken(List<Object> token, int idSender) throws RemoteException;
}
