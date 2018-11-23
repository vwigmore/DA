package Opdr1;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Main {
	static List<MO> processes = new ArrayList<>();

	
	public static void main(String args[]) {	
		System.out.println("hello");
		if (System.getSecurityManager() == null) {
	        System.setSecurityManager(new SecurityManager());
	    }
		System.out.println("starting the main");
		try {
			for (int i=0; i<1; i++) {
				MO obj = new MO(i, 3);
				MO_RMI stub = (MO_RMI)	UnicastRemoteObject.exportObject(obj, 0);
		        java.rmi.Naming.bind("rmi://localhost/MO"+i, stub);
		        processes.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Thread1 t1 = new Thread1();
//		Thread2 t2 = new Thread2();
//		t1.run();
//		t2.run();
//		
//		for(int i=0; i<2; i++){
//		      new Thread("" + i){
//		        public void run(){
//		        	try {
//		        		List<String> hosts = new ArrayList<>();
//		        		hosts.add("localhost");
//		        		hosts.add("145.94.165.174");
//
//		        		String name = "MO" + this.getName();
//		        		MO_RMI process = null;
//		        		for (String s : hosts) {
//		        			try {
//		        				process = (MO_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
//		        				break;
//		        			} catch (NotBoundException e) {
//
//		        			}
//		        		}
//		        		for(int j=0; j<3; j++) {
//		        			int rand = (int) Math.floor(Math.random()*4);
//			        		while(rand==Integer.parseInt(this.getName())) {
//			        			 rand = (int) Math.floor(Math.random()*4);
//			        		}
//			    			process.sendMessage("This is a message from id:" + this.getName() + " for id:"+rand, rand);
//
//		        		}
//		        		
//
//		    		} catch (Exception e) {
//		    			e.printStackTrace();
//		    		}	
//		        }
//		      }.start();
//		    }
		
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

class Thread1 implements Runnable {
	@Override
	public void run() {
		try {
			Main.processes.get(0).sendMessage("Message1", 1);
			Thread.sleep(5000);
			Main.processes.get(0).sendMessage("Message2", 2);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
class Thread2 implements Runnable {
	@Override
	public void run() {
		try {
			Main.processes.get(2).sendMessage("Message3", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}