package mydht;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Classe principal para a execução do processamento da DHT
 * 
 * @author Diego G Tome
 * @version 1.0
 * @since 27/04/2016
 */
public class Main {
	/**
	 * Método principal do programa, faz a leitura das operação linha a linha e
	 * e instancia a classe DhtManager para executar o processamento da entrada.
	 * 
	 * @author Diego G Tomé
	 * @return void
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scanner;
		scanner = new Scanner(new File("/Users/diegogomestome/Desktop/dgtome 2/teste.txt")).useDelimiter("\\ |\\n");
		DhtManager dhtManager = new DhtManager();

		while (scanner.hasNext()) {
			Integer timeStamp = Integer.parseInt(scanner.next());
			String operation = scanner.next();
			Integer nodeIdentifier = Integer.parseInt(scanner.next());
			String storageKey = scanner.next();

			dhtManager.processOperation(timeStamp, operation, nodeIdentifier,
					storageKey);

		}
		scanner.close();

	}
}
