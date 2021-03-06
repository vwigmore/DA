package Opdr1;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MO implements MO_RMI {

	HashMap<Integer,int[]> buffer;
	List<Object[]> messageBuffer;
	
	int[] timestamp;
	int id;
	List<String> hosts;
	
	Object msg_temp;
	HashMap<Integer, int[]> b_temp = new HashMap<>();
	int[] t_temp;
	int idSender_temp;
	boolean check = false;
	
	protected MO(int id, int numberProc) throws RemoteException {
		super();
		this.id = id;
	
		timestamp = new int[numberProc];
		Arrays.fill(timestamp, 0);
		messageBuffer = new ArrayList<>();
		buffer = new HashMap<>();
		
		hosts = new ArrayList<>();
		hosts.add("localhost");
		hosts.add("145.94.165.174");
		/**
		 * java -Djava.security.policy=my.policy -Djava.rmi.server.hostname=145.94.226.184 MO 1 4
		 */
		
	}
	
	public void sendMessage(Object message, int idReciever) throws RemoteException {
		double rand = Math.round(Math.random()*5000);
		 try {
			Thread.sleep((long) rand);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		String name = "MO" + idReciever;
		MO_RMI process = null;
		for (String s : hosts) {
			try {
				process = (MO_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
				break;
			} catch (Exception e) {

			}
		}

		timestamp[id] = timestamp[id] + 1;
		System.out.println("send message:"+message);
		process.recieveMessage(message, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), id);
		buffer.put(idReciever, timestamp.clone());
	}

	@Override
	public void recieveMessage(Object m, HashMap<Integer, int[]> b, int[] t, int idSender) throws RemoteException {
		
		System.out.print(" buffer: ");
		if (!b.isEmpty()) {
			for (Integer i : b.keySet()) {
				System.out.print(i+"(");
				int[] temp = b.get(i);
				for (int j = 0; j < temp.length; j++) {
					System.out.print(temp[j] + ",");
				}
				System.out.print("),");
			}
		}
		System.out.println();
		
		if (idSender==1 && !check) {
			msg_temp = m;
			b_temp = b;
			t_temp = t;
			idSender_temp = idSender;
			return;
		}
		if (idSender==2) {
			check = true;
		}
		
		System.out.println("recieved message: "+m);
		
		
		if ((!b.containsKey(id)) || checkDeliver(timestamp, b.get(id))) {
			this.deliver(m, b, t);

			boolean loop = true;
			while (loop) {
				loop = false;
				for (Object[] i : messageBuffer) {
					
					
					if (checkDeliver(timestamp, ((HashMap<Integer,int[]>)i[3]).get(id))) {
						deliver(i[0], (HashMap<Integer, int[]>) i[3], (int[]) i[2]);
						messageBuffer.remove(i);
						loop = true;
						break;
					}
				}
			}
		} else {
			messageBuffer.add(new Object[] { m, idSender, t.clone(), b.clone()});
		}
		if (idSender==2) {
			recieveMessage(msg_temp, b_temp, t_temp, idSender_temp);
		}
	}
	
	private synchronized void deliver(Object message, HashMap<Integer, int[]> b, int[] t) {
		System.out.println(message.toString() + " send at: ");
		for (int i=0; i<t.length; i++) {
			System.out.print(t[i]+",");
		}
		System.out.print(" delivered at: ");
		for (int i=0; i<timestamp.length; i++) {
			System.out.print(timestamp[i]+",");
		}
		
		System.out.print(" buffer: ");
		if (!b.isEmpty()) {
			for (Integer i : b.keySet()) {
				System.out.print(i+"(");
				int[] temp = b.get(i);
				for (int j = 0; j < temp.length; j++) {
					System.out.print(temp[j] + ",");
				}
				System.out.print("),");
			}
		}
		System.out.println();

		for (int i=0; i<timestamp.length; i++) {
			if (timestamp[i] < t[i]) {
				timestamp[i] = t[i];
			}
		}

		for (Integer i : b.keySet()) {
			if (buffer.containsKey(i)) {
				int[] temp = buffer.remove(i);
				buffer.put(i, compareArray(temp, b.get(i)).clone());
			} else {
				buffer.put(i, b.get(i).clone());
			}
		}
		timestamp[id] = timestamp[id] + 1;
	}
	
	private int[] compareArray(int[] x, int[] y) {
		if (y != null) {
			for (int i=0; i<x.length; i++) {
				if (x[i] < y[i]) {
					return y;
				}
			}
		}
		return x;
	}
	
	
	private boolean checkDeliver(int[] x, int[] y) {		

		if (y != null) {
			for (int i=0; i<x.length; i++) {
				if (x[i] < y[i]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main(String args[]) {
		try {
			if (System.getSecurityManager() == null) {
		        System.setSecurityManager(new SecurityManager());
		    }
			
			// args[0] = id, args[1] = numberProc
			MO obj = new MO(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			MO_RMI stub = (MO_RMI)	UnicastRemoteObject.exportObject(obj, 0);
	        java.rmi.Naming.bind("rmi://localhost/MO"+args[0], stub);
	        
	        Thread.sleep(5000);
			System.out.println("Server ready");
			
			// from here send messages
			for (int i=0; i<10; i++) {
				Thread.sleep(Math.round(Math.random()*3000));
				
				int idrec = (int) Math.floor(Math.random()*Integer.parseInt(args[1]));
				while (idrec==Integer.parseInt(args[0])) {
					idrec = (int) Math.floor(Math.random()*Integer.parseInt(args[1]));
				}	
				obj.sendMessage("This is a message from id:" + args[0] + " for id:"+idrec, idrec);

			}	
			System.out.println("Finished sending messages");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}

