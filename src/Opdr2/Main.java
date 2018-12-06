package Opdr2;


import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Main {

	static List<Component> processes = new ArrayList<>();

	
	// java -Djava.security.policy=my.policy -Djava.rmi.server.hostname=145.94.226.221 Main 4 4
	public static void main(String args[]) {	
		if (System.getSecurityManager() == null) {
	        System.setSecurityManager(new SecurityManager());
	    }
		System.out.println("starting the main");
		try {
			for (int i=2; i<Integer.parseInt(args[0]); i++) {
				Component obj = new Component(i, Integer.parseInt(args[1]));
				Component_RMI stub = (Component_RMI) UnicastRemoteObject.exportObject(obj, 0);
				
//				if (i<Math.floor(Integer.parseInt(args[0])/2) ) {
//					java.rmi.Naming.bind("rmi://"+ "145.94.167.4" +"/Component"+i, stub);
//			        processes.add(obj);
//				} else {
					java.rmi.Naming.bind("rmi://localhost/Component"+i, stub);
			        processes.add(obj);
//				}
		        
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		for(int i=0; i<Integer.parseInt(args[1]); i++){
//		      new Thread("" + i){
//		        public void run(){
//		        	try {
//
//		        		List<String> hosts = new ArrayList<>();
//		        		hosts.add("localhost");
//		        		hosts.add("145.94.165.137");
//
//		        		String name = "Component" + this.getName();
//		        		Component_RMI process = null;
//		        		for (String s : hosts) {
//		        			try {
//		        				process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
//		        				break;
//		        			} catch (NotBoundException e) {
//
//		        			}
//		        		}
//		   
//		        			Thread.sleep((long) (Math.random()*3000));
//		        			
//		        			process.makeRequest();
//
//		        		
//		        		
//		    		} catch (Exception e) {
//		    			e.printStackTrace();
//		    		}	
//		        }
//		      }.start();
//		    }
		

		System.out.println("Finished");
		
		
	}
	
}
