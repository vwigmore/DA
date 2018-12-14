import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Node implements Node_RMI {

	private int id;
	private HashMap<Integer, Integer> w; // <idReciever, weight>
	private HashMap<Integer, String> SE; // <weight, edgeState>
	private String SN;
	private int FN = 0;
	private int LN = 0;

	private int in_branch = 0;
	private int test_edge = 0;
	private int best_edge = 0;
	private int best_weight = Integer.MAX_VALUE;
	private int find_count = 0;

	private ArrayList<ArrayList<Object>> messageQueue;

	boolean halt = false;

	/** for message ordering **/
	HashMap<Integer, int[]> buffer;
	List<Object[]> messageBuffer;
	int[] timestamp;

	Object msg_temp;
	HashMap<Integer, int[]> b_temp = new HashMap<>();
	int[] t_temp;
	int idSender_temp;
	boolean check = false;

	public Node(int id, HashMap<Integer, Integer> edges, int numberProc) {
		this.id = id;
		w = edges;

		SE = new HashMap<>();
		for (Integer i : edges.values()) {
			SE.put(i, "?_in_MST");
		}
		SN = "sleeping";

		messageQueue = new ArrayList<>();

		timestamp = new int[numberProc];
		Arrays.fill(timestamp, 0);
		messageBuffer = new ArrayList<>();
		buffer = new HashMap<>();
	}

	@Override
	public void message(int idSender, HashMap<Integer, int[]> b, int[] t, List<Object> message) throws RemoteException {
		new Thread() {
			public void run() {
				if ((!b.containsKey(id)) || checkDeliver(timestamp, b.get(id))) {
					deliver(new Object[] { idSender, message }, b, t);

					boolean loop = true;
					while (loop) {
						loop = false;
						for (Object[] i : messageBuffer) {

							if (checkDeliver(timestamp, ((HashMap<Integer, int[]>) i[3]).get(id))) {
								deliver(i[0], (HashMap<Integer, int[]>) i[3], (int[]) i[2]);
								messageBuffer.remove(i);
								loop = true;
								break;
							}
						}
					}
				} else {
					messageBuffer.add(new Object[] { new Object[] { idSender, message }, idSender, t.clone(), b.clone() });
				}
			}
		}.start();

	}

	private synchronized void deliver(Object m, HashMap<Integer, int[]> b, int[] t) {
		for (int i = 0; i < timestamp.length; i++) {
			if (timestamp[i] < t[i]) {
				timestamp[i] = t[i];
			}
		}

		for (Integer i : b.keySet()) {
			if (buffer.containsKey(i)) {
				int[] temp = buffer.remove(i);
				buffer.put(i, compareArray(temp, b.get(i)).clone());
			} else {
				buffer.put(i, b.get(i).clone());
			}
		}
		timestamp[id - 1] = timestamp[id - 1] + 1;

		int idSender = (int) ((Object[]) m)[0];
		List<Object> message = (List<Object>) ((Object[]) m)[1];

		System.out.print(message.get(0) + " delivered at: ");
		for (int i = 0; i < timestamp.length; i++) {
			System.out.print(timestamp[i] + ",");
		}
		System.out.println();

		if (((String) message.get(0)).equals("connect")) {
			connect(idSender, (int) message.get(1), (int) message.get(2));
		} else if (((String) message.get(0)).equals("initiate")) {
			initiate(idSender, (int) message.get(1), (int) message.get(2), (int) message.get(3), (String) message.get(4));
		} else if (((String) message.get(0)).equals("test")) {
			test(idSender, (int) message.get(1), (int) message.get(2), (int) message.get(3));
		} else if (((String) message.get(0)).equals("reject")) {
			reject(idSender, (int) message.get(1));
		} else if (((String) message.get(0)).equals("accept")) {
			accept(idSender, (int) message.get(1));
		} else if (((String) message.get(0)).equals("report")) {
			report(idSender, (int) message.get(1), (int) message.get(2));
		} else if (((String) message.get(0)).equals("change_root")) {
			change_root(idSender, (int) message.get(1));
		} else if (((String) message.get(0)).equals("halt")) {
			halt();
		} else {
			System.out.println("ERROR SHOULD NOT OCCUR");
		}

	}

	@Override
	public void wakeup() throws RemoteException {
		System.out.println("wakeup id:" + id);
		int tempWeight = Integer.MAX_VALUE;
		for (Integer i : SE.keySet()) {
			if (tempWeight > i) {
				tempWeight = i;
			}
		}
		SE.put(tempWeight, "in_MST");
		LN = 0;
		SN = "found";
		find_count = 0;

		try {

			int idReciever = 0;
			for (Integer i : w.keySet()) {
				if (w.get(i) == tempWeight) {
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
			ArrayList<Object> temp = new ArrayList<>();
			temp.add("connect");
			temp.add(tempWeight);
			temp.add(0);

			timestamp[id - 1] = timestamp[id - 1] + 1;
			System.out.println("id:" + id + " sending connect to:" + idReciever);

			process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("wakeup end id:" + id);
	}

	private void test() {
		System.out.println("id:" + id + " test");
		boolean test = false;
		int tempWeight = Integer.MAX_VALUE;

		for (Integer i : SE.keySet()) {
			if (SE.get(i).equals("?_in_MST")) {
				if (tempWeight > i) {
					tempWeight = i;
				}
				test = true;
			}
		}
		if (test) {
			System.out.println("now testing in id:" + id);
			test_edge = tempWeight;

			try {
				int idReciever = 0;
				for (Integer i : w.keySet()) {
					if (w.get(i) == test_edge) {
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
				ArrayList<Object> temp = new ArrayList<>();
				temp.add("test");
				temp.add(test_edge);
				temp.add(LN);
				temp.add(FN);
				timestamp[id - 1] = timestamp[id - 1] + 1;
				System.out.println("id:" + id + " sending test to:" + idReciever);
				process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			test_edge = 0;
			report();
		}
	}

	private void report() {
		System.out.println("id:" + id + " report");
		System.out.println("find_count:" + find_count + " test_edge:" + test_edge);
		if (find_count <= 0 && test_edge == 0) {
			SN = "found";
			System.out.println("in_branch:" + in_branch);

			try {
				int idReciever = 0;
				for (Integer i : w.keySet()) {
					if (w.get(i) == in_branch) {
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
				ArrayList<Object> temp = new ArrayList<>();
				temp.add("report");
				temp.add(in_branch);
				temp.add(best_weight);
				timestamp[id - 1] = timestamp[id - 1] + 1;
				System.out.println("id:" + id + " sending report to:" + idReciever);
				process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void change_root() {
		System.out.println("id:" + id + " change_root");

		System.out.println("best_edge:" + best_edge);
		if (SE.get(best_edge).equals("in_MST")) {

			try {
				int idReciever = 0;
				for (Integer i : w.keySet()) {
					if (w.get(i) == best_edge) {
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
				ArrayList<Object> temp = new ArrayList<>();
				temp.add("change_root");
				temp.add(best_edge);
				timestamp[id - 1] = timestamp[id - 1] + 1;
				System.out.println("id:" + id + " sending change_root to:" + idReciever);
				process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			try {
				int idReciever = 0;
				for (Integer i : w.keySet()) {
					if (w.get(i) == best_edge) {
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
				ArrayList<Object> temp = new ArrayList<>();
				temp.add("connect");
				temp.add(best_edge);
				temp.add(LN);
				timestamp[id - 1] = timestamp[id - 1] + 1;
				System.out.println("id:" + id + " sending connect to:" + idReciever);
				process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
			} catch (Exception e) {
				e.printStackTrace();
			}

			SE.put(best_edge, "in_MST");
		}
	}

	private void handleMessageQueue() {
		try {

			ArrayList<Object> temp_message = null;
			for (ArrayList<Object> message : messageQueue) {
				if (message.get(0).equals("connect")) {
					if ((int) message.get(3) < LN || !SE.get((int) message.get(2)).equals("?_in_MST")) {
						temp_message = message;
					}
				} else if (message.get(0).equals("test")) {
					if ((int) message.get(3) <= LN) {
						temp_message = message;
					}
				} else if (message.get(0).equals("report")) {
					if ((int) message.get(2) != in_branch || !SN.equals("find")) {
						temp_message = message;
					}
				}
			}
			if (temp_message != null) {
				messageQueue.remove(temp_message);
				System.out.println("removed message from queue");
				if (temp_message.get(0).equals("connect")) {
					this.connect((int) temp_message.get(1), (int) temp_message.get(2), (int) temp_message.get(3));
				} else if (temp_message.get(0).equals("test")) {
					this.test((int) temp_message.get(1), (int) temp_message.get(2), (int) temp_message.get(3), (int) temp_message.get(4));
				} else if (temp_message.get(0).equals("report")) {
					this.report((int) temp_message.get(1), (int) temp_message.get(2), (int) temp_message.get(3));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(int idSender, int weight, int L) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("own_id:" + id + " sender_id" + idSender + " connect" + " L:" + L);

		if (SN.equals("sleeping")) {
			try {
				wakeup();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		System.out.println("id:" + id + "in connect");
		if (L < LN) {
			SE.put(weight, "in_MST");

			try {
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
				ArrayList<Object> temp = new ArrayList<>();
				temp.add("initiate");
				temp.add(weight);
				temp.add(LN);
				temp.add(FN);
				temp.add(SN);
				timestamp[id - 1] = timestamp[id - 1] + 1;
				System.out.println("id:" + id + " sending initiate to:" + idSender);
				process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
			} catch (Exception e) {
				e.printStackTrace();
			}

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

				try {
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
					ArrayList<Object> temp = new ArrayList<>();
					temp.add("initiate");
					temp.add(weight);
					temp.add(LN + 1);
					temp.add(weight);
					temp.add("find");
					timestamp[id - 1] = timestamp[id - 1] + 1;
					System.out.println("id:" + id + " sending initiate to:" + idSender);
					process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		handleMessageQueue();
	}

	public void initiate(int idSender, int weight, int L, int F, String S) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("own_id:" + id + " sender_id" + idSender + " initiate" + " weight:" + weight + " L:" + L
				+ " F:" + F + " S:" + S);

		LN = L;
		FN = F;
		SN = S;
		in_branch = weight;
		best_edge = 0;
		best_weight = Integer.MAX_VALUE;
		for (Entry<Integer, String> entry : SE.entrySet()) {
			if (entry.getKey() != weight && entry.getValue().equals("in_MST")) {

				try {
					int idReciever = 0;
					for (Integer i : w.keySet()) {
						if (w.get(i) == entry.getKey()) {
							idReciever = i;
						}
					}
					List<String> hosts = new ArrayList<>();
					hosts.add("localhost");
//					hosts.add("145.94.165.137");
					String name = "Node" + idReciever;
					Node_RMI process = null;
					for (String s : hosts) {
						try {
							process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
							break;
						} catch (Exception e) {
						}
					}
					ArrayList<Object> temp = new ArrayList<>();
					temp.add("initiate");
					temp.add(entry.getKey());
					temp.add(L);
					temp.add(F);
					temp.add(S);
					timestamp[id - 1] = timestamp[id - 1] + 1;
					System.out.println("id:" + id + " sending initiate to:" + idReciever);
					process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
				} catch (Exception e) {

				}

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

	public void test(int idSender, int weight, int L, int F) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("own_id:" + id + " sender_id" + idSender + " test" + " L:" + L + " F:" + F);

		if (SN.equals("sleeping")) {
			try {
				wakeup();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		if (L > LN) {

			ArrayList<Object> message = new ArrayList<>();
			message.add("test");
			message.add(idSender);
			message.add(weight);
			message.add(L);
			message.add(F);
			messageQueue.add(message);
			System.out.println("id:" + id + " added message to queue");
		} else {
			System.out.println("F:" + F + " FN:" + FN);
			if (F != FN) {

				try {
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
					ArrayList<Object> temp = new ArrayList<>();
					temp.add("accept");
					temp.add(weight);
					timestamp[id - 1] = timestamp[id - 1] + 1;
					System.out.println("id:" + id + " sending accept to:" + idSender);
					process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				if (SE.get(weight).equals("?_in_MST")) {
					SE.put(weight, "not_in_MST");
				}
				System.out.println("test_edge:" + test_edge + " weight:" + weight);
				if (test_edge > 0 && test_edge != weight) {

					try {
						List<String> hosts = new ArrayList<>();
						hosts.add("localhost");
//						hosts.add("145.94.165.137");
						String name = "Node" + idSender;
						Node_RMI process = null;
						for (String s : hosts) {
							try {
								process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
								break;
							} catch (Exception e) {
							}
						}
						ArrayList<Object> temp = new ArrayList<>();
						temp.add("reject");
						temp.add(weight);
						timestamp[id - 1] = timestamp[id - 1] + 1;
						System.out.println("id:" + id + " sending reject to:" + idSender);
						process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					test();
				}
			}
		}
		handleMessageQueue();
	}

	public void reject(int idSender, int weight) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("own_id:" + id + " sender_id" + idSender + " reject");

		if (SE.get(weight).equals("?_in_MST")) {
			SE.put(weight, "not_in_MST");
			System.out.println("not in MST weight:" + weight);
		}
		test();
		handleMessageQueue();
	}

	public void accept(int idSender, int weight) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("own_id:" + id + " sender_id" + idSender + " accept");

		test_edge = 0;
		if (weight < best_weight) {
			best_edge = weight;
			best_weight = weight;
		}
		report();
		handleMessageQueue();
	}

	public void report(int idSender, int weight, int W) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("own_id:" + id + " sender_id" + idSender + " report" + " W:" + W);
		System.out.println("in_branch:" + in_branch + " weight:" + weight + " test_edge:" + test_edge);

		if (weight != in_branch) {
			find_count--;
			if (W < best_weight) {
				best_weight = W;
				best_edge = weight;
			}

			report();
		} else {
			System.out.println("SN:" + SN);
			if (SN.equals("find")) {

				ArrayList<Object> message = new ArrayList<>();
				message.add("report");
				message.add(idSender);
				message.add(weight);
				message.add(W);
				messageQueue.add(message);
				System.out.println("id:" + id + " added message to queue ");
			} else {
				System.out.println("W:" + W + " best_weight:" + best_weight);
				if (W > best_weight) {
					change_root();
				} else {
					if (W == best_weight && W == Integer.MAX_VALUE) {
						halt();
					}
				}
			}
		}
		handleMessageQueue();
	}

	public void change_root(int idSender, int weight) {
		try {
			Thread.sleep(Math.round(Math.random() * 3000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("own_id:" + id + " sender_id" + idSender + " change_root");

		change_root();
		handleMessageQueue();
	}

	private void halt() {
		if (!halt) {
			System.out.println("HALT");
			halt = true;
			for (Integer i : SE.keySet()) {
				if (SE.get(i).equals("in_MST")) {
					System.out.println("edge from id:" + id + " edge in MST:" + i);

					int idReciever = 0;
					for (Integer j : w.keySet()) {
						if (w.get(j) == i) {
							idReciever = j;
						}
					}

					List<String> hosts = new ArrayList<>();
					hosts.add("localhost");
//					hosts.add("145.94.165.137");
					String name = "Node" + idReciever;
					Node_RMI process = null;
					for (String s : hosts) {
						try {
							process = (Node_RMI) java.rmi.Naming.lookup("rmi://" + s + "/" + name);
							break;
						} catch (Exception e) {
						}
					}
					ArrayList<Object> temp = new ArrayList<>();
					temp.add("halt");
					temp.add(i);

					timestamp[id - 1] = timestamp[id - 1] + 1;
					System.out.println("id:" + id + " sending halt to:" + idReciever);

					try {
						process.message(id, (HashMap<Integer, int[]>) buffer.clone(), timestamp.clone(), temp);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private int[] compareArray(int[] x, int[] y) {
		if (y != null) {
			for (int i = 0; i < x.length; i++) {
				if (x[i] < y[i]) {
					return y;
				}
			}
		}
		return x;
	}

	private boolean checkDeliver(int[] x, int[] y) {

		if (y != null) {
			for (int i = 0; i < x.length; i++) {
				if (x[i] < y[i]) {
					return false;
				}
			}
		}
		return true;
	}

}
