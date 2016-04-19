package Server;

import java.io.IOException;
import java.util.Scanner;

public class ElectionThread extends Thread{
	private Election election;
	private Scanner scanner;
	
	public ElectionThread (Election election) {
		this.election=election;
	}
	
	public void run() {
		scanner = new Scanner(System.in);
		while(true) {
			String input ="";
			switch (election.state) {
			case "initial":
				System.out.println("election state : " + election.state);
				System.out.println("press enter to start voting");
				input = scanner.nextLine();
				try {
					election.startElection();
				} catch (IOException e) {
					System.out.println("Exception: election not started.");
				}
				System.out.println();
				break;
			case "voting":
				System.out.println("election state : " + election.state);
				System.out.println("press enter to stop election");
				input = scanner.nextLine();
				election.stopElection();
				System.out.println();
				break;
			case "finished":
				System.out.println("election state : " + election.state);
				System.out.println("press enter to print results");
				input = scanner.nextLine();
				System.out.println(election.getResults());
				System.out.println();
				break;
			default:
				break;
			}
			if (input.equals("exit"))
				System.exit(1);
		}
	}
}
