package Opdr2;

import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import Opdr1.MO;
import Opdr2.Component;
import Opdr2.Component_RMI;

public class Main {

	static List<Component> processes = new ArrayList<>();

	
	public static void main(String args[]) {	
		if (System.getSecurityManager() == null) {
	        System.setSecurityManager(new SecurityManager());
	    }
		System.out.println("starting the main");
		try {
			for (int i=0; i<Integer.parseInt(args[0]); i++) {
				Component obj = new Component(i, Integer.parseInt(args[0]));
				Component_RMI stub = (Component_RMI) UnicastRemoteObject.exportObject(obj, 0);
				
				if (i<Math.floor(Integer.parseInt(args[0])/2) ) {
					java.rmi.Naming.bind("rmi://"+ "ip" +"/Component"+i, stub);
			        processes.add(obj);
				} else {
					java.rmi.Naming.bind("rmi://localhost/Component"+i, stub);
			        processes.add(obj);
				}
		        
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		for(int i=0; i<Integer.parseInt(args[0]); i++){
		      new Thread("" + i){
		        public void run(){
		        	try {

		        		List<String> hosts = new ArrayList<>();
		        		hosts.add("localhost");
//		        		hosts.add("145.94.165.174");

		        		String name = "Component" + this.getName();
		        		Component_RMI process = null;
		        		for (String s : hosts) {
		        			try {
		        				process = (Component_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
		        				break;
		        			} catch (NotBoundException e) {

		        			}
		        		}
		   
		        			Thread.sleep((long) (Math.random()*3000));
		        			
		        			process.makeRequest();
		        			// making request 
//		        			process.sendRequest("request", Integer.parseInt(this.getName()));
		        		
		        		
		        		
		        		
		        		
		        		
//		        		for(int j=0; j<3; j++) {
//		        			int rand = (int) Math.floor(Math.random()*4);
//			        		while(rand==Integer.parseInt(this.getName())) {
//			        			 rand = (int) Math.floor(Math.random()*4);
//			        		}
//			    			process.sendMessage("This is a message from id:" + this.getName() + " for id:"+rand, rand);
//		        		}
		        		
		        		
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}	
		        }
		      }.start();
		    }
		
//		try {
//			processes.get(0).sendMessage("Message1", 1);
//			processes.get(0).sendMessage("Message2", 2);
//			processes.get(2).sendMessage("Message3", 1);
//
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		System.out.println("Finished");
		
		
	}
	
}
