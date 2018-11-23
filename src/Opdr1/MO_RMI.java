package Opdr1;
import java.rmi.*;
import java.util.HashMap;

public interface MO_RMI extends Remote {

	public void recieveMessage(Object message, HashMap<Integer,int[]> buffer, int[] timestamp, int idSender) throws RemoteException;
	
	public void sendMessage(Object message, int idReciever) throws RemoteException;
	
}
