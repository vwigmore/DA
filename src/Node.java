import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class Node implements Node_RMI {

	private int id;
	private HashMap<Integer, Integer> w; // <idReciever, weight>
	private HashMap<Integer,String> SE; // <weight, edgeState>
	private String SN;
	private int FN = 0;
	private int LN = 0;
	
	private int in_branch = -1;
	private int test_edge = -1;
	private int best_edge = -1;
	private int best_weight = Integer.MAX_VALUE;
	private int find_count = 0;
	
	private ArrayList<ArrayList<Object>> messageQueue;
	
	
	public Node(int id, HashMap<Integer, Integer> edges) {
		this.id = id;
		w = edges;
		
		SE = new HashMap<>();
		for (Integer i : edges.values()) {
			SE.put(i, "?_in_MST");
		}
		SN = "sleeping";
		
		messageQueue = new ArrayList<>();
	}
	
	public void wakeup() throws RemoteException {
		int tempWeight = Integer.MAX_VALUE;
		for (Integer i : SE.keySet()) {
			if (tempWeight> i) {
				tempWeight = i;
			}
		}
		SE.put(tempWeight, "in_MST");
		LN = 0;
		SN = "found";
		find_count = 0;
		
		
		int idReciever = 0;
		for (Integer i : w.keySet()) {
			if (w.get(i)==tempWeight) {
				idReciever = i;
			}
		}
		List<String> hosts = new ArrayList<>();
		hosts.add("localhost");
//		hosts.add("145.94.165.137");
		String name = "Node" + idReciever;
		Node_RMI process = null;
		for (String s : hosts) {
			try {
				process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
				break;
			} catch (Exception e) {
			}
		}
		process.connect(id, tempWeight, 0);
	}
	
	private void test() throws RemoteException {
		boolean test  = false;
		int tempWeight = Integer.MAX_VALUE;
		
		for (Integer i : SE.keySet()) {
			if (SE.get(i).equals("?_in_MST")) {
				if (tempWeight>i) {
					tempWeight = i;
				}
				test = true;
			}
		}
		if (test) {
			test_edge = tempWeight;
			
			
			
			int idReciever = 0;
			for (Integer i : w.keySet()) {
				if (w.get(i)==tempWeight) {
					idReciever = i;
				}
			}
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
//			hosts.add("145.94.165.137");
			String name = "Node" + idReciever;
			Node_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.test(id, tempWeight, LN, FN);
		} else {
			test_edge = 0;
			report();
		}
	}
	
	
	private void report() throws RemoteException {
		if (find_count==0 && test_edge==0) {
			SN = "found";
			
			int idReciever = 0;
			for (Integer i : w.keySet()) {
				if (w.get(i)==in_branch) {
					idReciever = i;
				}
			}
			
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
//			hosts.add("145.94.165.137");
			String name = "Node" + idReciever;
			Node_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.report(id, in_branch, best_weight);
		}
	}
	
	private void change_root() throws RemoteException {
		if (SE.get(best_edge).equals("in_MST")) {
			
			int idReciever = 0;
			for (Integer i : w.keySet()) {
				if (w.get(i)==best_edge) {
					idReciever = i;
				}
			}
			
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
//			hosts.add("145.94.165.137");
			String name = "Node" + idReciever;
			Node_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.change_root(id, best_edge);
			
		} else {		
			int idReciever = 0;
			for (Integer i : w.keySet()) {
				if (w.get(i)==best_edge) {
					idReciever = i;
				}
			}
			
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
//			hosts.add("145.94.165.137");
			String name = "Node" + idReciever;
			Node_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.connect(id, best_edge, LN);
			
			SE.put(best_edge, "in_MST");
		}
	}
	
	private void handleMessageQueue() throws RemoteException {
		boolean deleted = false;
		
		loop:
		for (ArrayList<Object> message : messageQueue) {
			if (message.get(0).equals("connect")) {
				if ((int) message.get(3)<LN || !SE.get((int)message.get(2)).equals("?_in_MST")) {
					this.connect((int)message.get(1), (int)message.get(2), (int)message.get(3));
					messageQueue.remove(message);
					deleted = true;
					break loop;
				}
			} else if (message.get(0).equals("test")) {
				if ((int)message.get(3)<=LN) {
					this.test((int)message.get(1), (int)message.get(2), (int)message.get(3), (int)message.get(4));
					messageQueue.remove(message);
					deleted = true;
					break loop;
				}
			} else if (message.get(0).equals("report")){
				if ((int)message.get(2)!=in_branch || !SN.equals("find")) {
					this.report((int)message.get(1),(int)message.get(2), (int)message.get(3));
					messageQueue.remove(message);
					deleted = true;
					break loop;
				}
			}
		}
		
		if (deleted) {
			handleMessageQueue();
		}
	}


	@Override
	public void connect(int idSender, int weight, int L) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "connect");
		
		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (SN.equals("sleeping")) {
			wakeup();
		}
		if (L<LN) {
			SE.put(weight, "in_MST");
			
			List<String> hosts = new ArrayList<>();
			hosts.add("localhost");
//			hosts.add("145.94.165.137");
			String name = "Node" + idSender;
			Node_RMI process = null;
			for (String s : hosts) {
				try {
					process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
					break;
				} catch (Exception e) {
				}
			}
			process.initiate(id, weight, LN, FN, SN);
			
			if (SN.equals("find")) {
				find_count++;
			}
		} else {
			if (SE.get(weight).equals("?_in_MST")) {
				
				ArrayList<Object> message = new ArrayList<>();
				message.add("connect");
				message.add(idSender);
				message.add(weight);
				message.add(L);
				messageQueue.add(message);
				
			} else {
				List<String> hosts = new ArrayList<>();
				hosts.add("localhost");
//				hosts.add("145.94.165.137");
				String name = "Node" + idSender;
				Node_RMI process = null;
				for (String s : hosts) {
					try {
						process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
						break;
					} catch (Exception e) {
					}
				}
				process.initiate(id, weight, LN+1, weight, "find");
			}
		}
		handleMessageQueue();
	}


	@Override
	public void initiate(int idSender, int weight, int L, int F, String S) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "initiate");

		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		LN = L;
		FN = F;
		SN = S;
		in_branch = weight;
		best_edge = 0;
		best_weight = Integer.MAX_VALUE;
		for (Entry<Integer, String> entry : SE.entrySet()) {
			if (entry.getKey()!=weight && entry.getValue().equals("in_MST")) {
				
				int idReciever = 0;
				for (Integer i : w.keySet()) {
					if (w.get(i)==entry.getKey()) {
						idReciever = i;
					}
				}
				
				
				List<String> hosts = new ArrayList<>();
				hosts.add("localhost");
//				hosts.add("145.94.165.137");
				String name = "Node" + idReciever;
				Node_RMI process = null;
				for (String s : hosts) {
					try {
						process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
						break;
					} catch (Exception e) {
					}
				}
				process.initiate(id, entry.getKey(), L, F, S);
				
				if (S.equals("find")) {
					find_count++;
				}
			}
		}
		if (S.equals("find")) {
			test();
		}
		
		handleMessageQueue();
	}

	@Override
	public void test(int idSender, int weight, int L, int F) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "test");

		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (SN.equals("sleeping")) {
			wakeup();
		}
		if (L>LN) {
			
			ArrayList<Object> message = new ArrayList<>();
			message.add("test");
			message.add(idSender);
			message.add(weight);
			message.add(L);
			message.add(F);
			messageQueue.add(message);
		} else {
			if (F!=FN) {
				
				List<String> hosts = new ArrayList<>();
				hosts.add("localhost");
//				hosts.add("145.94.165.137");
				String name = "Node" + idSender;
				Node_RMI process = null;
				for (String s : hosts) {
					try {
						process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
						break;
					} catch (Exception e) {
					}
				}
				process.accept(id, weight);
			} else {
				if (SE.get(weight).equals("?_in_MST")) {
					SE.put(weight, "not_in_MST");
				}
				if (test_edge!=weight) {
					
					List<String> hosts = new ArrayList<>();
					hosts.add("localhost");
//					hosts.add("145.94.165.137");
					String name = "Node" + idSender;
					Node_RMI process = null;
					for (String s : hosts) {
						try {
							process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
							break;
						} catch (Exception e) {
						}
					}
					process.reject(id, weight);
				} else {
					test();
				}
			}
		}
		handleMessageQueue();
	}

	@Override
	public void reject(int idSender, int weight) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "reject");

		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (SE.get(weight).equals("?_in_MST")) {
			SE.put(weight, "not_in_MST");
		}
		test();
		handleMessageQueue();
	}

	@Override
	public void accept(int idSender, int weight) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "accept");

		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		test_edge = 0;
		if (weight<best_weight) {
			best_edge = weight;
			best_weight = weight;
		}
		report();
		handleMessageQueue();
	}

	@Override
	public void report(int idSender, int weight, int W) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "report");

		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (weight!=in_branch) {
			find_count--;
			if (W<best_weight) {
				best_weight = W;
				best_edge = weight;
			}
			report();
		} else {
			if (SN.equals("find")) {
				
				ArrayList<Object> message = new ArrayList<>();
				message.add("report");
				message.add(idSender);
				message.add(weight);
				message.add(W);
				messageQueue.add(message);
			} else {
				if (W>best_weight) {
					change_root();
				} else {
					if (W==best_weight && W==Integer.MAX_VALUE) {
						System.out.println("Halt");
						for (Integer i : SE.keySet()) {
							if (SE.get(i).equals("in_MST")) {
								System.out.println("edge from id:"+ id + " edge in MST:"+ i);
							}
								
						}
						
 					}
				}
			}
		}
		handleMessageQueue();
	}

	@Override
	public void change_root(int idSender, int weight) throws RemoteException {
		System.out.println("own_id:"+id+ "sender_id"+idSender+ "change_root");

		try {
			Thread.sleep((long)Math.random()*3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		change_root();
		handleMessageQueue();
	}


	
	
	
}
