package Server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;

import Model.CryptoParameters;
import Model.ElectionParameters;
import Model.Failure;
import Model.Signature;

public class ServerThread extends Thread {
	private Socket clientSocket = null;
	private Election election;

	public ServerThread(Socket socket, Election election) {
		super("Server");
		this.clientSocket = socket;
		this.election = election;
	}

	public void run() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

			Object received;
			System.out.println("server: thread started");
			while ((received = ois.readObject()) != null) {
				if (received instanceof BigInteger) {
					if (election.state.equals("initial")) {
						BigInteger publicKey = (BigInteger) received;
						System.out.println("server: public key received : " + publicKey);
						int pi = election.registerVoter(publicKey);
						oos.writeObject(pi);
					} else {
						oos.writeObject(new Failure("Election not in initial state. Can't register new voters."));
					}
				} else if (received instanceof Signature) {
					if (election.state.equals("voting")) {
						Signature vote = (Signature) received;
						System.out.println("server: received vote from : " + vote.Ytilda);
						boolean casted = election.CastVote(vote);
						oos.writeObject(casted);
					} else {
						oos.writeObject(new Failure("Election not in voting state. Can't vote now."));
					}
				} else if (received instanceof String) {
					String request = (String)received;
					switch (request) {
					case "crypto parameters":
						if (election.state.equals("initial")) {
							System.out.println("server: asked for crypto parameters");
							CryptoParameters cp = election.getCryptoParameters();
							oos.writeObject(cp);
						} else {
							oos.writeObject(new Failure("Election not in initial state. Can't get crypto parameters."));
						}
						break;
					case "election parameters":
						if (election.state.equals("voting")) {
							System.out.println("server: asked for election parameters");
							ElectionParameters ep = election.getElectionParameters();
							oos.writeObject(ep);
						} else {
							oos.writeObject(new Failure("Election not in voting state. Can't get election parameters."));
						}
						break;
					case "results":
						if (election.state.equals("finished")) {
							System.out.println("server: asked for results");
							String results = election.getResults();
							oos.writeObject(results);
						} else {
							oos.writeObject(new Failure("Election not finished. Can't get results now."));
						}
						break;
					default:
						oos.writeObject(new Failure("No such request supported."));
						break;
					}
				} else {
					oos.writeObject(new Failure("No such communication supported."));
				}
			}
			System.out.println("server: thread stopped");
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("connection lost");
		}
	}

}
