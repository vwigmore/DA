import java.rmi.RemoteException;
import java.util.HashMap;

public class Node implements Node_RMI {

	private int id;
	private HashMap<Integer, Double> edgesWeights;
	private HashMap<Integer,String> SE;
	private String SN;
	private String FN;
	private int LN;
	
	private int in_branch = -1;
	private int test_edge = -1;
	private int best_edge = -1;;
	private int best_weight = Integer.MAX_VALUE;
	private int find_count = 0;
	
	
	public Node(int id, HashMap<Integer, Double> edges) {
		this.id = id;
		edgesWeights = edges;
		
		for (Integer i : edges.keySet()) {
			SE.put(i, "?_in_MST");
		}
		SN = "sleeping";
	}


	@Override
	public void connect(int idSender, int L) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initiate(int idSender, int L, String F, String S) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void test(int idSender, int L, String F) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void reject(int idSender) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void accept(int idSender) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void report(int idSender, int W) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void change_root() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
