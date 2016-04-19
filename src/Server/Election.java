package Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import Model.CryptoParameters;
import Model.ElectionParameters;
import Model.Signature;

public class Election {
	private List<Signature> votes;
	public String state;
	private List<BigInteger> registeredVoters;
	private String electionResults;

	public Election() throws NoSuchAlgorithmException {
		votes = new ArrayList<Signature>();
		state = "initial";
		registeredVoters = new ArrayList<BigInteger>();
		electionResults = "no results yet";

		String question = "Vote for president: ";
		String[] answers = { "Basescu", "Johannis", "Ponta" };

		ElectionToolsServer.Initialize("easy", question, answers);
	}

	private boolean verifySignature(byte[] message, Signature signature) throws IOException {
		ByteArrayOutputStream hash1Stream;
		ByteArrayOutputStream prefixStream = new ByteArrayOutputStream();
		prefixStream.write(ElectionToolsServer.L);
		prefixStream.write(signature.Ytilda.toByteArray());
		prefixStream.write(message);
		byte[] prefix = prefixStream.toByteArray();

		BigInteger c = signature.C1;
		BigInteger zDash, z2Dash;
		for (int i = 0; i < ElectionToolsServer.PublicKeys.length; i++) {
			zDash = ElectionToolsServer.Generator.modPow(signature.S[i], ElectionToolsServer.Prime)
					.multiply(ElectionToolsServer.PublicKeys[i].modPow(c, ElectionToolsServer.Prime))
					.mod(ElectionToolsServer.Prime);
			z2Dash = ElectionToolsServer.h.modPow(signature.S[i], ElectionToolsServer.Prime)
					.multiply(signature.Ytilda.modPow(c, ElectionToolsServer.Prime)).mod(ElectionToolsServer.Prime);
			hash1Stream = new ByteArrayOutputStream();
			hash1Stream.write(prefix);
			hash1Stream.write(zDash.toByteArray());
			hash1Stream.write(z2Dash.toByteArray());
			c = ElectionToolsServer.Hash1(hash1Stream.toByteArray());
		}

		return signature.C1.equals(c);
	}

	public synchronized boolean CastVote(Signature sig) throws IOException {
		if (state.equals("voting")) {
			for (byte[] answer : ElectionToolsServer.answers)
				if (verifySignature(answer, sig)) {
					checkMultipleVoting(sig);
					votes.add(sig);
					return true;
				}
		}
		return false;
	}

	private void checkMultipleVoting(Signature sig) {
		Signature toRemove = null;
		for (Signature existing : votes)
			if (sig.isLinked(existing)) {
				toRemove = existing;
				break;
			}
		if (toRemove != null)
			votes.remove(toRemove);
	}

	public synchronized String getResults() {
		if (state.equals("finished")) {
			return electionResults;
		}
		return null;
	}
	
	private void calculateResults() {
		int[] voteCount = new int[ElectionToolsServer.answersText.length];

		try {
			for (Signature vote : votes)
				for (int i = 0; i < ElectionToolsServer.answers.length; i++)
					if (verifySignature(ElectionToolsServer.answers[i], vote)) {
						voteCount[i]++;
						break;
					}
			electionResults = "";
			for (int i = 0; i < ElectionToolsServer.answers.length; i++)
				electionResults += ElectionToolsServer.answersText[i] + " : " + voteCount[i] + "\n";
		} catch (Exception e) {
			electionResults = "There are some wrong votes casted!";
		}
	}

	public synchronized int registerVoter(BigInteger publicKey) {
		if (state.equals("initial")) {
			registeredVoters.add(publicKey);
			return registeredVoters.size() - 1;
		} else
			return -1;
	}

	public synchronized ElectionParameters getElectionParameters() {
		if (state.equals("voting"))
			return ElectionToolsServer.getElectionParameters();
		return null;
	}

	public synchronized CryptoParameters getCryptoParameters() {
		if (state.equals("initial"))
			return ElectionToolsServer.getCryptoParameters();
		return null;
	}
	
	public void startElection() throws IOException {
		if (state.equals("initial")) {
			ElectionToolsServer.SetPublicKeys(registeredVoters.toArray(new BigInteger[registeredVoters.size()]));
			state = "voting";
		}
	}

	public void stopElection() {
		if (state.equals("voting")) {
			calculateResults();
			state="finished";
		}
	}

}