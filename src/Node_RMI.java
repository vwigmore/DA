import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node_RMI extends Remote {

	public void connect(int idSender , int weight, int L) throws RemoteException;
	
	public void initiate(int idSender, int weight,int L, int F, String S) throws RemoteException;
	
	public void test(int idSender, int weight,int L, int F) throws RemoteException;
	
	public void reject(int idSender,int weight) throws RemoteException;
	
	public void accept(int idSender,int weight) throws RemoteException;
	
	public void report(int idSender, int weight, int W) throws RemoteException;
	
	public void change_root(int idSender, int weight) throws RemoteException;

	public void wakeup() throws RemoteException;
	
	
}
