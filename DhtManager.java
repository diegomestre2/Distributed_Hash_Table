package mydht;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Classe responsável por gerenciar a DHT sendo constituida por Nodes e processar as entradas.
 * @author diegogomestome
 * @version 1.0
 * @since 27/04/2016
 */
public class DhtManager {

	TreeMap<Integer, Node> dht = new TreeMap<Integer, Node>();

	/**
	 * Método responsável por receber os dados de entrada e instanciar o método responsável pela operação do parâmetro.
	 * @param timeStamp
	 * @param operation
	 * @param nodeIdentifier
	 * @param storageKey
	 */
	public void processOperation(Integer timeStamp, String operation,
			Integer nodeIdentifier, String storageKey) {

		Method method;
		try {
			method = this.getClass().getMethod("processOperation" + operation,
					Integer.class, Integer.class, String.class);
			method.invoke(this, timeStamp, nodeIdentifier, storageKey);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Método responsável por processar a operação de entrada de Node na DHT.
	 * @param timeStamp
	 * @param nodeIdentifier
	 * @param storageKey
	 */
	public void processOperationE(Integer timeStamp, Integer nodeIdentifier,
			String storageKey) {

		dht.put(nodeIdentifier, new Node(timeStamp, nodeIdentifier, storageKey));

		for (Node node : dht.values()) {

			List<Integer> listSuccessors = getListOfSuccessors(node);
			node.updateRouteTable(dht.size(), listSuccessors, dht.lastKey());
		}
	}

	/**
	 * Método responsável por processar a operação de daída de Node da DHT
	 * @param timeStamp
	 * @param nodeIdentifier
	 * @param storageKey
	 */
	public void processOperationS(Integer timeStamp, Integer nodeIdentifier,
			String storageKey) {
		Node nodeRunningAway = dht.get(nodeIdentifier);
		Node nextNode = new Node(timeStamp, nodeIdentifier, storageKey);

		if (nodeRunningAway.getStorageKey().size() > 0) {
			Entry<Integer, Node> nextEntry = dht.higherEntry(nodeIdentifier);
			if (nextEntry != null) {
				nextNode = nextEntry.getValue();
				nextNode.getStorageKey()
						.addAll(nodeRunningAway.getStorageKey());
			} else {
				nextNode = dht.firstEntry().getValue();
				nextNode.getStorageKey()
						.addAll(nodeRunningAway.getStorageKey());
			}
//			System.out.println("Node saindo: "
//					+ nodeRunningAway.getNodeIdentifier() + " Node : "
//					+ nextNode.getNodeIdentifier() + " recebendo: "
//					+ nextNode.getStorageKey().size());
			dht.put(nextNode.getNodeIdentifier(), nextNode);
		}
		dht.remove(nodeIdentifier);

		for (Node node : dht.values()) {

			List<Integer> listSuccessors = getListOfSuccessors(node);
			node.updateRouteTable(dht.size(), listSuccessors, dht.lastKey());
		}

	}
	/**
	 * Método responsável por realizar a operação de inclusão de chave na DHT.
	 * @param timeStamp
	 * @param nodeIdentifier
	 * @param storageKey
	 */
	public void processOperationI(Integer timeStamp, Integer nodeIdentifier,
			String storageKey) {
		Integer newKey = Integer.parseInt(storageKey);
		Integer inputNode = nodeIdentifier;
		findNodeForNewKey(newKey, inputNode);

	}
	/**
	 * Método responsável por processar o Lookup de chave na DHT
	 * @param timeStamp
	 * @param nodeIdentifier
	 * @param storageKey
	 */
	public void processOperationL(Integer timeStamp, Integer nodeIdentifier,
			String storageKey) {

		Integer keyToBeStored = Integer.parseInt(storageKey);
		String printLookUp = timeStamp + " L " + storageKey + " {";
		Integer previousKey = nodeIdentifier;
		Integer nextKey = nodeIdentifier;
		List<Integer> listOfSuccessors = dht.get(nextKey).getSuccessors();
		while (!(previousKey < keyToBeStored && keyToBeStored <= nextKey)) {
			for (Integer key : listOfSuccessors) {
				previousKey = nextKey;
				nextKey = key;
				if (previousKey < keyToBeStored && keyToBeStored <= nextKey) {
					break;
				}
			}
			listOfSuccessors = dht.get(nextKey).getSuccessors();
			printLookUp = printLookUp.concat(nextKey + ",");
		}
		printLookUp = printLookUp.substring(0, printLookUp.length() - 1)
				.concat("}");
		System.out.println(printLookUp);
		for (Node node : dht.values()) {
			node.printFingerTable(timeStamp);
		}
	}
	/**
	 * Método responsável por identificar e retornar uma lista com os sucessores do Node parâmetro.
	 * @param node
	 */
	private List<Integer> getListOfSuccessors(Node node) {
		Map<Integer, Node> nodesAfter = dht.subMap(node.getNodeIdentifier(),
				false, dht.lastKey(), true);
		Map<Integer, Node> nodesBefore = dht.subMap(dht.firstKey(),
				node.getNodeIdentifier());
		List<Integer> listOfSuccessors = new ArrayList<Integer>();
		listOfSuccessors.addAll(nodesAfter.keySet());
		listOfSuccessors.addAll(nodesBefore.keySet());
		return listOfSuccessors;
	}
	/**
	 * Método responsável por encontrar o Node correto e armazenar uma nova chave.
	 * @param newKey
	 * @param inputNode
	 */
	private void findNodeForNewKey(Integer newKey, Integer inputNode) {
		List<Integer> listSuccessors = getListOfSuccessors(dht.get(inputNode));
		Integer nextSuccessor = 0;
		Integer nextKey = 0;
		Integer previousKey = inputNode;
		nextKey = listSuccessors.get(nextSuccessor);
		while (listSuccessors.size() > nextSuccessor + 1
				&& (!(previousKey < newKey && newKey <= nextKey))) {
			if (newKey > dht.lastKey() && previousKey == dht.lastKey()) {
				break;
			}
			previousKey = nextKey;
			nextKey = listSuccessors.get(++nextSuccessor);
		}
		if (listSuccessors.size() == nextSuccessor + 1 && newKey < inputNode) {
			nextKey = inputNode;
		}
		//System.out.println("Gravando a chave: " + newKey + " em " + nextKey);
		dht.get(nextKey).getStorageKey().add(newKey);
	}
}
