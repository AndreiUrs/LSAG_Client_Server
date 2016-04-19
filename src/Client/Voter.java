package Client;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import Model.CryptoParameters;
import Model.ElectionParameters;
import Model.Signature;

public class Voter {
	public String name;
	public int pi;
    private BigInteger privateKey;
    public BigInteger publicKey;
    public boolean electionParametersSet;

    public Voter(String name, CryptoParameters cp) throws NoSuchAlgorithmException, IOException
    {
        this.name = name;
        ElectionToolsClient.Initialize(cp);
        generateKeyPair();
        electionParametersSet=false;
        pi=0;
    }

    private void generateKeyPair()
    {
        privateKey = ElectionToolsClient.GenerateInteger(ElectionToolsClient.SubgroupSize);
        publicKey = ElectionToolsClient.Generator.modPow(privateKey, ElectionToolsClient.Prime);
    }
    
    public void setElectionParameters(ElectionParameters ep) throws IOException {
    	ElectionToolsClient.SetElectionParameters(ep);
    	electionParametersSet=true;
    }

    public Signature GenerateSignature(byte[] message, int identity) throws IOException
    {
        BigInteger yTilda = ElectionToolsClient.h.modPow(privateKey, ElectionToolsClient.Prime);
        int n = ElectionToolsClient.PublicKeys.length;
        byte[] prefix = ElectionToolsClient.ConcatBytes(ElectionToolsClient.L, yTilda.toByteArray(), message);

        BigInteger u = ElectionToolsClient.GenerateInteger(ElectionToolsClient.SubgroupSize);
        BigInteger[] c = new BigInteger[n];
        c[(identity + 1) % n] = ElectionToolsClient.Hash1(
        							ElectionToolsClient.ConcatBytes(
        									prefix, 
        									ElectionToolsClient.Generator.modPow(u, ElectionToolsClient.Prime).toByteArray(), 
        									ElectionToolsClient.h.modPow(u, ElectionToolsClient.Prime).toByteArray()));
        BigInteger[] s = new BigInteger[n];
        //for i (pi+1, pi+2, ... n, 1, 2, ... pi)
        for (int i = (identity + 1) % n; i != identity; i = (i + 1) % n)
        {
            s[i] = ElectionToolsClient.GenerateInteger(ElectionToolsClient.SubgroupSize);
            c[(i + 1) % n] = ElectionToolsClient.Hash1(
            					ElectionToolsClient.ConcatBytes(
            							prefix,
            							ElectionToolsClient.Generator.modPow(s[i], ElectionToolsClient.Prime).multiply(ElectionToolsClient.PublicKeys[i].modPow(c[i], ElectionToolsClient.Prime)).mod(ElectionToolsClient.Prime).toByteArray(),
            							ElectionToolsClient.h.modPow(s[i], ElectionToolsClient.Prime).multiply(yTilda.modPow(c[i], ElectionToolsClient.Prime)).mod(ElectionToolsClient.Prime).toByteArray()));
        }
        s[identity] = u.subtract(privateKey.multiply(c[identity])).mod(ElectionToolsClient.SubgroupSize);

        return new Signature(c[0], s, yTilda);
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(name + " : " + "\n");
        result.append("Private Key : " + privateKey + "\n");
        result.append("Public  Key : " + publicKey + "\n" + "\n");

        return result.toString();
    }
}
