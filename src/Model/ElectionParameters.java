package Model;

import java.io.Serializable;
import java.math.BigInteger;

public class ElectionParameters implements Serializable {
	private static final long serialVersionUID = 1L;

	public BigInteger[] PublicKeys;

	public String question;
	public String[] answersText;

	public ElectionParameters(BigInteger[] publicKeys, String question, String[] answersText) {
		this.PublicKeys = publicKeys;
		this.question = question;
		this.answersText = answersText;
	}
}
