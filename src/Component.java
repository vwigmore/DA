

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Component implements Component_RMI{

	int[] N;
	String[] S;
	int id;
	List<Object> token = null;
	int numProc;
	
	
	public Component(int id, int numProc) {
		this.numProc = numProc;
		this.id = id;
		N = new int[numProc];
		Arrays.fill(N, 0);
		S = new String[numProc];
		if (id==0) {
			S[0] = "H";
			for(int i=1; i<numProc; i++) {
				S[i] = "O";
			}
			token = new ArrayList<>();
			int[] TN = new int[numProc];
			String[] TS = new String[numProc];
			Arrays.fill(TN, 0);
			Arrays.fill(TS, "O");
			token.add(TN);
			token.add(TS);
			
		} else {
			for (int i = 0; i < numProc; i++) {
				if (i < id) {
					S[i] = "R";
				} else {
					S[i] = "O";
				}

			}
		}
	}
	
	


	@Override
	public void recieveRequest(int idSender, int Ni) throws RemoteException {
		N[idSender] = Ni;
		switch (S[id]) {
		case "E":
			S[idSender] = "R";
		case "O":
			S[idSender] = "R";
		case "R":
			if (S[idSender] != "R") {
				S[idSender] = "R";

				List<String> hosts = new ArrayList<>();
				hosts.add("localhost");
				hosts.add("145.94.167.4");
				String name = "Component" + idSender;
				Component_RMI process = null;
				for (String s : hosts) {
					try {
						process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
						break;
					} catch (Exception e) {
					}
				}
				process.recieveRequest(id, N[id]);
				System.out.println("Process_id:"+id+" sending request to:"+ idSender + "caseR");
			}
		case "H":
			S[idSender] = "R";
			S[id] = "O";
			((String[]) token.get(1))[idSender] = "R";
			((int[]) token.get(0))[idSender] = Ni;

			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
			hosts.add("145.94.167.4");
			String name = "Component" + idSender;
			Component_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.recieveToken(token, idSender);
			System.out.println("Process_id:"+id+" sending token to:"+ idSender);
		}
	}


	@Override
	public void recieveToken(List<Object> token, int idSender) throws RemoteException {
		S[id] = "E";
		this.criticalSection();
		S[id] = "O";
		((String[])token.get(1))[id] = "O";
		for (int i=0; i<numProc; i++) {
			if (N[i]> ((int[])token.get(0))[i]) {
				((int[])token.get(0))[i] = N[i];
				((String[])token.get(1))[i] = S[i];
			} else {
				N[i] = ((int[])token.get(0))[i];
				S[i] = ((String[])token.get(1))[i];
			}
		}
		String[] temp = new String[numProc];
		Arrays.fill(temp, "O");
		if (Arrays.equals(temp, S)) {
			S[id] = "H";
		} else {
			
			ArrayList<Integer> allR = new ArrayList<>();
			for (int i=0; i<numProc; i++) {
				if (S[i]=="R") {
					allR.add(i);
				}
			}
			int idRand = allR.get((int)Math.floor(Math.random()*allR.size()));
			
						
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
			hosts.add("145.94.167.4");
			String name = "Component" + idRand;
			Component_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.recieveToken(token, idSender);
			System.out.println("Process_id:"+id+" sending token to:"+ idRand);
			
		}
		try {
			Thread.sleep((long) (Math.random()*3000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.makeRequest();
	}

	public void criticalSection() {
		
		System.out.println("Process_id:"+id+" entering CS");
		try {
			Thread.sleep((long) (Math.random()*3000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		


	@Override
	public void makeRequest() throws RemoteException {
		
		if (S[id]=="H") {
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
			hosts.add("145.94.167.4");
			String name = "Component" + id;
			Component_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			
			System.out.println("Process_id:"+id+" sending request to:"+ id);
			process.recieveRequest(id, N[id]);
		} else {
			S[id] = "R";
			N[id] += 1;
			
			for (int i=0; i<numProc; i++) {
				if (i!=id && S[i]=="R") {
					
					List<String> hosts = new ArrayList<>();
					hosts.add("localhost");
					hosts.add("145.94.167.4");
					String name = "Component" + i;
					Component_RMI process = null;
					for (String s : hosts) {
						try {
							process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
							break;
						} catch (Exception e) {
						}
					}
					process.recieveRequest(id, N[id]);
					System.out.println("Process_id:"+id+" sending request to:"+ i);
				}
			}
			
		}
	}

}
