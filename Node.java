package mydht;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Classe reponsável por representar o objeto Node e calcular e armazenar a Finger Table e tabela de rotas.
 * @author diegogomestome
 * @version 1.0
 * @since 27/04/2016
 */
public class Node {

	private Integer timestamp;
	private Integer nodeIdentifier;
	private List<Integer> storageKey = new ArrayList<Integer>();
	private List<Integer> successors = new ArrayList<Integer>();
	private static Integer HAS_NO_STORAGE_KEY = 0;
	private Hashtable<Integer, Integer> routeTable = new Hashtable<Integer, Integer>();

	public Node(Integer timestamp, Integer nodeIdentifier, String storageKey) {
		super();
		this.timestamp = timestamp;
		this.nodeIdentifier = nodeIdentifier;
		if (storageKey.equals("-")) {
			this.storageKey.add(HAS_NO_STORAGE_KEY);
		} else {
			this.storageKey.add(Integer.parseInt(storageKey));
		}
	}

	public List<Integer> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<Integer> successors) {
		this.successors = successors;
	}

	public Hashtable<Integer, Integer> getRouteTable() {
		return routeTable;
	}

	public void setRouteTable(Hashtable<Integer, Integer> routeTable) {
		this.routeTable = routeTable;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getNodeIdentifier() {
		return nodeIdentifier;
	}

	public void setNodeIdentifier(Integer nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	public List<Integer> getStorageKey() {
		return storageKey;
	}

	public void setStorageKey(Integer storageKey) {
		this.storageKey.add(storageKey);
	}

	public void updateRouteTable(int dhtSize, List<Integer> possibleSuccessors,
			int biggestKey) {
		int floor = calculateFloor(dhtSize);
		int input = 0;
		int nextSuccessorIndex = 0;
		int previousKey = 0;
		int nextKey = 0;
		successors = new ArrayList<Integer>();

		if (possibleSuccessors.size() >= 1) {
			previousKey = this.nodeIdentifier;
			for (int i = 0; successors.size() < floor; i++) {
				input = calculateNewImput(biggestKey, i);
				nextKey = possibleSuccessors.get(nextSuccessorIndex);
				while (possibleSuccessors.size() > nextSuccessorIndex + 1
						&& (!(previousKey < input && input <= nextKey))) {
					if ((previousKey < input && previousKey == biggestKey || (previousKey == biggestKey && (input == 0 || input == nextKey)))) {
						break;
					}
					previousKey = nextKey;
					nextKey = possibleSuccessors.get(++nextSuccessorIndex);
				}
				if(!(possibleSuccessors.size() > nextSuccessorIndex + 1) && input > nextKey){
					nextKey = this.nodeIdentifier;
				}
				routeTable.put(input, nextKey);
				if (!successors.contains(nextKey)) {
					successors.add(nextKey);
				}
			}
		}
	}

	private int calculateFloor(int size) {
		return (int) (Math.log(size) / Math.log(2));
	}

	private int calculateNewImput(int biggest, int i) {
		return (int) (this.getNodeIdentifier() + Math.pow(2, i))
				% (int) Math.pow(2,
						(32 - Integer.numberOfLeadingZeros(biggest)));
	}

	public void printFingerTable(Integer timeStamp2) {
		String fingerTable = timeStamp2 + " T " + nodeIdentifier + " {";
		for (Integer integer : successors) {
			fingerTable = fingerTable.concat(integer + ",");
		}
		fingerTable = fingerTable.substring(0, fingerTable.length() - 1)
				.concat("}");
		System.out.println(fingerTable);

	}
}
