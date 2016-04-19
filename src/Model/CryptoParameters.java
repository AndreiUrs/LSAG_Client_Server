package Model;

import java.io.Serializable;
import java.math.BigInteger;

public class CryptoParameters implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public BigInteger Prime, Generator, SubgroupSize;
	
	public CryptoParameters(BigInteger prime, BigInteger generator, BigInteger subgroupSize) {
		this.Prime=prime;
		this.Generator = generator;
		this.SubgroupSize = subgroupSize;
	}
}
