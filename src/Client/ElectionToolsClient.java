package Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import Model.CryptoParameters;
import Model.ElectionParameters;

public final class ElectionToolsClient {
	private static Random rng;
	private static MessageDigest sha;

	public static BigInteger Prime, Generator, SubgroupSize;
	public static BigInteger[] PublicKeys;
	public static byte[] L;
	public static BigInteger h;

	public static String question;
	public static String[] answersText;
	public static byte[][] answers;

	public static void Initialize(CryptoParameters cp) throws NoSuchAlgorithmException, IOException {
		Prime = cp.Prime;
		Generator = cp.Generator;
		SubgroupSize = cp.SubgroupSize;
		

		rng = new SecureRandom();
		sha = MessageDigest.getInstance("SHA-512");
	}

	public static void SetElectionParameters(ElectionParameters ep) throws IOException {
		question = ep.question;
		SetAnswers(ep.answersText);
		
		PublicKeys = ep.PublicKeys;
		byte[][] publicKeysBytes = new byte[PublicKeys.length][];
		for (int i = 0; i < PublicKeys.length; i++)
			publicKeysBytes[i] = PublicKeys[i].toByteArray();

		L = ConcatBytes(publicKeysBytes);
		h = Hash2(L);
	}

	private static void SetAnswers(String[] ans) {
		answersText = ans;
		answers = new byte[ans.length][];
		for (int i = 0; i < ans.length; i++)
			answers[i] = ans[i].getBytes();
	}

	public static BigInteger Hash1(byte[] data) {
		byte[] hash = sha.digest(data);
		return new BigInteger(hash).mod(SubgroupSize);
	}

	public static BigInteger Hash2(byte[] data) {
		byte[] hash = sha.digest(data);
		BigInteger x = new BigInteger(hash).mod(SubgroupSize);
		return Generator.modPow(x, Prime);
	}

	public static byte[] ConcatBytes(byte[]... args) throws IOException {
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		for (byte[] bs : args)
			resultStream.write(bs);

		return resultStream.toByteArray();
	}

	public static BigInteger HexToUnsignedInteger(String hex) {
		return new BigInteger(hex.replace(" ", ""), 16);
	}

	public static BigInteger GenerateInteger(BigInteger max) {
		int bytesToRepresent = max.toByteArray().length;
		byte[] bytes = new byte[bytesToRepresent];
		rng.nextBytes(bytes);

		return new BigInteger(bytes).mod(max);
	}

	public static void validate() {
		if (Generator.compareTo(new BigInteger("2")) == -1 || Generator.compareTo(Prime.subtract(BigInteger.ONE)) == 1)
			throw new SecurityException("Generator out of range");

		if (Generator.modPow(SubgroupSize, Prime) != BigInteger.ONE)
			throw new SecurityException("Generator is wrong");
	}

	public static String getInfo() {
		StringBuilder result = new StringBuilder();
		result.append("Election parameters : " + "\n");
		result.append("Generator    : " + Generator + "\n");
		result.append("SubgroupSize : " + SubgroupSize + "\n");
		result.append("Prime        : " + Prime + "\n" + "\n");

		return result.toString();
	}
}
