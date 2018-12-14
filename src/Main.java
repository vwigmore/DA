import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		if (args[0].equals("1")) {
		new Thread("1") {
			public void run() {
				try {
					HashMap<Integer, Integer> map1 = new HashMap<>();
					map1.put(2, 7);
					map1.put(3, 15);
					
					Node obj1 = new Node(1, map1, 4);
					Node_RMI stub1 = (Node_RMI) UnicastRemoteObject.exportObject(obj1, 0);
					java.rmi.Naming.rebind("rmi://localhost/Node" + 1, stub1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start(); 
		} else if (args[0].equals("2")) {
		new Thread("2") {
			public void run() {
				try {
					HashMap<Integer, Integer> map2 = new HashMap<>();
					map2.put(1, 50);
					map2.put(3, 16);

					Node obj2 = new Node(2, map2, 6);
					Node_RMI stub2 = (Node_RMI) UnicastRemoteObject.exportObject(obj2, 0);
					java.rmi.Naming.rebind("rmi://localhost/Node" + 2, stub2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		} else if (args[0].equals("3")) {
			new Thread("3") {
				public void run() {
					try {
						HashMap<Integer, Integer> map3 = new HashMap<>();
						map3.put(2, 16);
						map3.put(4, 10);

						Node obj3 = new Node(3, map3, 6);
						Node_RMI stub3 = (Node_RMI) UnicastRemoteObject.exportObject(obj3, 0);
						java.rmi.Naming.rebind("rmi://localhost/Node" + 3, stub3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else if (args[0].equals("5")) {
			new Thread("5") {
				public void run() {
					try {
						HashMap<Integer, Integer> map5 = new HashMap<>();
						map5.put(6, 11);

						Node obj5 = new Node(5, map5, 6);
						Node_RMI stub5 = (Node_RMI) UnicastRemoteObject.exportObject(obj5, 0);
						java.rmi.Naming.rebind("rmi://localhost/Node" + 5, stub5);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else if (args[0].equals("6")) {
			new Thread("6") {
				public void run() {
					try {
						HashMap<Integer, Integer> map6 = new HashMap<>();
						map6.put(5, 11);
						map6.put(1, 20);

						Node obj6 = new Node(6, map6, 6);
						Node_RMI stub6 = (Node_RMI) UnicastRemoteObject.exportObject(obj6, 0);
						java.rmi.Naming.rebind("rmi://localhost/Node" + 6, stub6);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else if (args[0].equals("4")) {
		new Thread("4") {
			public void run() {
				try {
					HashMap<Integer, Integer> map4 = new HashMap<>();
					map4.put(3, 10);
					Node obj4 = new Node(4, map4, 6);
					Node_RMI stub4 = (Node_RMI) UnicastRemoteObject.exportObject(obj4, 0);
					java.rmi.Naming.rebind("rmi://localhost/Node" + 4, stub4);
					
					List<String> hosts = new ArrayList<>();
	        		hosts.add("localhost");
//	        		hosts.add("145.94.165.137");

	        		String name = "Node" + this.getName();
	        		Node_RMI process = null;
					for (String s : hosts) {
						try {
							process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
							break;
						} catch (NotBoundException e) {
						}
					}
					
					Thread.sleep(Math.round(Math.random()*3000));
        			process.wakeup();
        			
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		}
	}
}
