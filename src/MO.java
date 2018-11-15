import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MO extends UnicastRemoteObject implements MO_RMI {

	HashMap<Integer,int[]> buffer;
	List<Object[]> messageBuffer;
	
	int[] timestamp;
	int id;
	
	protected MO(int id, int numberProc) throws RemoteException {
		super();
		this.id = id;
		
		timestamp = new int[numberProc];
		Arrays.fill(timestamp, 0);
		messageBuffer = new ArrayList<>();
		buffer = new HashMap<>();
	}
	
	public void sendMessage(Object message, int idReciever) throws Exception {
		String name = "MO" + idReciever;
		Registry registry = LocateRegistry.getRegistry("localhost");
		MO process = (MO) registry.lookup(name);
		timestamp[id] = timestamp[id] + 1;
		process.recieveMessage(message, buffer, timestamp, id);
		buffer.put(idReciever, timestamp);
	}



	@Override
	public void recieveMessage(Object m, HashMap<Integer, int[]> b, int[] t, int idSender)
			throws RemoteException {
			
		if ( !b.containsKey(id) || checkDeliver(timestamp, b.get(id))) {
			this.deliver(m, b);
			
			for (Object[] i : messageBuffer) {
				if (checkDeliver(timestamp, (int[]) i[2])) {
					deliver(i[0], (HashMap<Integer, int[]>) i[3]);
				}
			}
		} else {
			// add to buffer
			messageBuffer.add(new Object[] {m, idSender, t, b});
		}
		
		timestamp[id] = timestamp[id] +1;
	}
	
	private void deliver(Object message, HashMap<Integer, int[]> b) {
		System.out.println(message.toString());
		for (Integer i : b.keySet()) {
			if (buffer.containsKey(i)) {
				int[] temp = buffer.remove(i);
				buffer.put(i, compareArray(temp, b.get(i)));
			} else {
				buffer.put(i, b.get(i));
			}
		}
	}
	
	private int[] compareArray(int[] x, int[] y) {
		for (int i : x) {
			if (x[i] < y[i]) {
				return y;
			}
		}
		return x;
	}
	
	
	private boolean checkDeliver(int[] x, int[] y) {
		for (int i : x) {
			if (x[i] < y[i]) {
				return false;
			}
		}
		return true;
	}

	public static void main(String args[]) {
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}

