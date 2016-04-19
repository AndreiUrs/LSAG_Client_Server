package Client;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

import Model.CryptoParameters;
import Model.ElectionParameters;
import Model.Failure;
import Model.Signature;

public class Client {

	private static String hostname;
	private static int port = 7777;
	private static Voter voter;
	private static Scanner scanner;
	private static Socket socket;

	public static void main(String[] args) throws IOException {
		try {
			scanner = new Scanner(System.in);
			System.out.println("host : ");
			hostname = scanner.nextLine();
			System.out.println("your name : ");
			String name = scanner.nextLine();

			socket = new Socket(hostname, port);

			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

			Object received;
			oos.writeObject("crypto parameters");
			if ((received = ois.readObject()) != null) {
				if (received instanceof CryptoParameters) {
					CryptoParameters cp = (CryptoParameters) received;
					try {
						voter = new Voter(name, cp);
					} catch (NoSuchAlgorithmException e) {
						System.out.println("problem generating voter");
						System.exit(1);
					}
				} else {
					System.out.println("did not receive crypto parameters from server. Can't create voter.");
					System.exit(1);
				}
			}

			while (true) {
				printOptions();
				try {
					int option = scanner.nextInt();
					switch (option) {
					case 1:
						oos.writeObject(voter.publicKey);
						if ((received = ois.readObject()) != null) {
							if (received instanceof Integer) {
								int pi = (Integer) received;
								voter.pi = pi;
								System.out.println("succesfully registered in election");
							} else if (received instanceof Failure) {
								Failure fail = (Failure) received;
								System.out.println(fail.text);
							} else
								throw new ClassNotFoundException();
						}
						break;
					case 2:
						oos.writeObject("election parameters");
						if ((received = ois.readObject()) != null) {
							if (received instanceof ElectionParameters) {
								ElectionParameters ep = (ElectionParameters) received;
								voter.setElectionParameters(ep);
							} else if (received instanceof Failure) {
								Failure fail = (Failure) received;
								System.out.println(fail.text);
							} else
								throw new ClassNotFoundException();
						}
						break;
					case 3:
						if (voter.electionParametersSet) {
							byte[] message = getVoterChoice();
							Signature vote = voter.GenerateSignature(message, voter.pi);
							oos.writeObject(vote);
							if ((received = ois.readObject()) != null) {
								if (received instanceof Boolean) {
									boolean accepted = (Boolean) received;
									if (accepted)
										System.out.println("your vote was accepted");
									else
										System.out.println("your vote was not accepted");
								} else if (received instanceof Failure) {
									Failure fail = (Failure) received;
									System.out.println(fail.text);
								} else
									throw new ClassNotFoundException();
							}
						} else
							System.out.println("you have to get election parameters first");
						break;
					case 4:
						oos.writeObject("results");
						if ((received = ois.readObject()) != null) {
							if (received instanceof String) {
								String results = (String) received;
								System.out.println("results are : ");
								System.out.println(results);
							} else if (received instanceof Failure) {
								Failure fail = (Failure) received;
								System.out.println(fail.text);
							} else
								throw new ClassNotFoundException();
						}
						break;
					default:
						throw new InputMismatchException();
					}
				} catch (InputMismatchException e) {
					System.out.println("please type in an integer between 1 and 4");
				}
			}

		} catch (IOException e) {
			System.out.println("can't connect to server");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println("received unsupported object from server.");
		}
	}

	private static byte[] getVoterChoice() {
		printElectionDetails();
		int choice=0;
		try {
			choice = scanner.nextInt();
			if (choice >= ElectionToolsClient.answersText.length)
				throw new InputMismatchException();
		} catch (InputMismatchException e) {
			System.out.println("type in a number between 0 and " + (ElectionToolsClient.answersText.length - 1));
			return getVoterChoice();
		}
		return ElectionToolsClient.answers[choice];
	}

	private static void printElectionDetails() {
		System.out.println(ElectionToolsClient.question);
		for (int i = 0; i < ElectionToolsClient.answersText.length; i++) {
			System.out.println(i + " " + ElectionToolsClient.answersText[i]);
		}
		System.out.println();
		System.out.println("your choise : ");
	}

	private static void printOptions() {
		System.out.println("Voter : " + voter.name);
		System.out.println("Press 1 for registering to election");
		System.out.println("Press 2 for getting election parameters");
		System.out.println("Press 3 for voting");
		System.out.println("Press 4 for getting election results");
	}
}