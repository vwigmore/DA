package Opdr2;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Component implements Component_RMI{

	int[] N;
	String[] S;
	int id;
	static ArrayList<Object> token = null;
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
		System.out.print("When recieving request id:"+id+ " S:");
		for(int i=0; i<numProc; i++) {
			System.out.print(S[i]+ " ");
		}
		System.out.println();
		N[idSender] = Ni;
		switch (S[id]) {
		case "E":
			S[idSender] = "R";
			break;
		case "O":
			S[idSender] = "R";
			break;
		case "R":
			if (!S[idSender].equals("R")) {
				S[idSender] = "R";

				List<String> hosts = new ArrayList<>();
				hosts.add("localhost");
				hosts.add("145.94.165.137");
				String name = "Component" + idSender;
				Component_RMI process = null;
				for (String s : hosts) {
					try {
						process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
						break;
					} catch (Exception e) {
					}
				}
				System.out.println("Process_id:"+id+" sending request to:"+ idSender + "caseR");
				process.recieveRequest(id, N[id]);
			}
			break;
		case "H":
			System.out.print(id+ "when H, S:");
			for(int i=0; i<numProc; i++) {
				System.out.print(S[i]+ " ");
			}
			System.out.println();
			
			S[idSender] = "R";
			S[id] = "O";
			((String[]) token.get(1))[idSender] = "R";
			((int[]) token.get(0))[idSender] = Ni;

			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
			hosts.add("145.94.165.137");
			String name = "Component" + idSender;
			Component_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			System.out.println("Process_id:"+id+" sending token to:"+ idSender+ "recReq");
			System.out.print("After sending:");
			for(int i=0; i<numProc; i++) {
				System.out.print(S[i]+ " ");
			}
			System.out.println();
			process.recieveToken((ArrayList<Object>) token.clone(), id);
			break;
		}
	}


	@Override
	public void recieveToken(ArrayList<Object> token, int idSender) throws RemoteException {
		System.out.print(id +" recieveToken token:");
		for(int i=0; i<numProc; i++) {
			System.out.print(((String[])token.get(1))[i]+ " ");
		}
		System.out.println();
		
		System.out.print(id+ " recieveToken S:");
		for(int i=0; i<numProc; i++) {
			System.out.print(S[i]+ " ");
		}
		System.out.println();
		
		this.token = token;
		S[id] = "E";
		this.criticalSection();
		S[id] = "O";
		((String[])this.token.get(1))[id] = "O";
		
//		System.out.print("before merging S:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(S[i]+ " ");
//		}
//		System.out.print(" TS:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(((String[])this.token.get(1))[i]+ " ");
//		}
//		System.out.print("merging N:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(N[i]+ " ");
//		}
//		System.out.print(" TN:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(((int[])this.token.get(0))[i]+ " ");
//		}
//		System.out.println();
		
		
		for (int i=0; i<numProc; i++) {
			if (N[i]> ((int[])this.token.get(0))[i]) {
				((int[])this.token.get(0))[i] = N[i];
				((String[])this.token.get(1))[i] = S[i];
			} else {
				N[i] = ((int[])this.token.get(0))[i];
				S[i] = ((String[])this.token.get(1))[i];
			}
		}
		
//		System.out.print("after merging S:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(S[i]+ " ");
//		}
//		System.out.print(" TS:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(((String[])this.token.get(1))[i]+ " ");
//		}
//		System.out.print("merging N:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(N[i]+ " ");
//		}
//		System.out.print(" TN:");
//		for(int i=0; i<numProc; i++) {
//			System.out.print(((int[])this.token.get(0))[i]+ " ");
//		}
//		System.out.println();
		
		String[] temp = new String[numProc];
		Arrays.fill(temp, "O");
		if (Arrays.equals(temp, S)) {
			S[id] = "H";
		} else {
			
			
			ArrayList<Integer> allR = new ArrayList<>();
			for (int i=0; i<numProc; i++) {
				if (S[i].equals("R")) {
					allR.add(i);
				}
			}
			int idRand = allR.get((int)Math.floor(Math.random()*allR.size()));
			
						
//			List<String> hosts = new ArrayList<>();
//			hosts.add("localhost");
//			hosts.add("145.94.167.4");
//			String name = "Component" + idRand;
//			Component_RMI process = null;
//			for (String s : hosts) {
//				try {
//					process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
//					break;
//				} catch (Exception e) {
//				}
//			}
			System.out.println("Process_id:"+id+" sending token to:"+ idRand+ "recTok");
			new Thread() {
				public void run() {
					try {
						List<String> hosts = new ArrayList<>();
						hosts.add("localhost");
						hosts.add("145.94.165.137");
						String name = "Component" + idRand;
						Component_RMI process = null;
						for (String s : hosts) {
							try {
								process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
								break;
							} catch (Exception e) {
							}
						}
						process.recieveToken((ArrayList<Object>) Component.token.clone(), id);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
			
		}
		try {
			Thread.sleep((long) (Math.random()*5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("make new request id:"+id);
		this.makeRequest();
	}

	public void criticalSection() {
		
		System.out.println("Process_id:"+id+" entering CS");
		try {
			Thread.sleep((long) (Math.random()*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		


	@Override
	public void makeRequest() throws RemoteException {
		
		if (S[id].equals("H")) {
			
			
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
			hosts.add("145.94.165.137");
			String name = "Component" + id;
			Component_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			
			System.out.println("Process_id:"+id+" sending request to:"+ id+ "makeReq");
			process.recieveRequest(id, N[id]);
		} else {
			S[id] = "R";
			N[id] += 1;
			
			for (int i=0; i<numProc; i++) {
				if (i!=id && S[i].equals("R")) {
					
					List<String> hosts = new ArrayList<>();
					hosts.add("localhost");
					hosts.add("145.94.165.137");
					String name = "Component" + i;
					Component_RMI process = null;
					for (String s : hosts) {
						try {
							process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
							break;
						} catch (Exception e) {
						}
					}
					System.out.println("Process_id:"+id+" sending request to:"+ i+ " makeReq");
					process.recieveRequest(id, N[id]);
				}
			}
			
		}
	}

}
