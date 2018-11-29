package Opdr2;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Component implements Component_RMI{

	int[] reqNum;
	String[] procStat;
	int id;
	List<Object> token = null;
	
	
	public Component(int id, int numProc) {
		this.id = id;
		reqNum = new int[numProc];
		procStat = new String[numProc];
		if (id==0) {
			procStat[0] = "H";
			for(int i=1; i<numProc; i++) {
				procStat[i] = "O";
			}
			token = new ArrayList<>();
			int[] TN = new int[numProc];
			String[] TS = new String[numProc];
			Arrays.fill(TN, 0);
			Arrays.fill(TS, "O");
			token.add(TN);
			token.add(TS);
			
		} else {
			for(int i=0; i<numProc; i++) {
				if (i<id) {
					procStat[i] = "R";
				} else {
					procStat[i] = "O";
				}
				
			}
		}
		
		
		
		
		
	}


	@Override
	public void sendRequest(Object request, int idSender) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sendToken(List<Object> token, int idSender) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
