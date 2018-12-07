import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Opdr2.Component;
import Opdr2.Component_RMI;

public class Main {

	static List<Node> processes = new ArrayList<>();
	
	// java -Djava.security.policy=my.policy -Djava.rmi.server.hostname=145.94.225.109 Main
		public static void main(String args[]) {	
			if (System.getSecurityManager() == null) {
		        System.setSecurityManager(new SecurityManager());
		    }
			System.out.println("starting the main");
			try {
				
				HashMap<Integer, Integer> map1 = new HashMap<>();
				map1.put(3, 50); map1.put(2,20); map1.put(4,16);
				HashMap<Integer, Integer> map2 = new HashMap<>();
				map2.put(1, 20); map2.put(3, 7); map2.put(4, 33);
				HashMap<Integer, Integer> map3 = new HashMap<>();
				map3.put(1, 50); map3.put(2, 7);
				HashMap<Integer, Integer> map4 = new HashMap<>();
				map4.put(1, 16); map4.put(2, 33);
				
					Node obj1 = new Node(1, map1);
					Node_RMI stub1 = (Node_RMI) UnicastRemoteObject.exportObject(obj1, 0);
					java.rmi.Naming.bind("rmi://localhost/Node"+1, stub1);
				    processes.add(obj1);
				    
				    Node obj2 = new Node(2, map2);
					Node_RMI stub2 = (Node_RMI) UnicastRemoteObject.exportObject(obj2, 0);
					java.rmi.Naming.bind("rmi://localhost/Node"+2, stub2);
				    processes.add(obj2);
				    
				    Node obj3 = new Node(3, map3);
					Node_RMI stub3 = (Node_RMI) UnicastRemoteObject.exportObject(obj3, 0);
					java.rmi.Naming.bind("rmi://localhost/Node"+3, stub3);
				    processes.add(obj3);
				    
				    Node obj4 = new Node(4, map4);
					Node_RMI stub4 = (Node_RMI) UnicastRemoteObject.exportObject(obj4, 0);
					java.rmi.Naming.bind("rmi://localhost/Node"+4, stub4);
				    processes.add(obj4);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			for(int i=1; i<=4; i++){
			      new Thread("" + i){
			        public void run(){
			        	try {
	
			        		List<String> hosts = new ArrayList<>();
			        		hosts.add("localhost");
//			        		hosts.add("145.94.165.137");
	
			        		String name = "Node" + this.getName();
			        		Node_RMI process = null;
			        		for (String s : hosts) {
			        			try {
			        				process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
			        				break;
			        			} catch (NotBoundException e) {
	
			        			}
			        		}
			        		if (Integer.parseInt(this.getName())==4) {
			        			Thread.sleep((long) (Math.random()*3000));
			        			
			        			process.wakeup();
			        		}
			        		
			    		} catch (Exception e) {
			    			e.printStackTrace();
			    		}	
			        }
			      }.start();
			    }
			

			System.out.println("Finished");
			
			
		}
	
}
