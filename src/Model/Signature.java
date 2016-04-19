package Model;
import java.io.Serializable;
import java.math.BigInteger;

public class Signature implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public final BigInteger C1, Ytilda;
    public final BigInteger[] S;

    public Signature(BigInteger C1, BigInteger[] S, BigInteger Ytilda)
    {
        this.C1 = C1;
        this.S = S;
        this.Ytilda = Ytilda;
    }

    public boolean isLinked(Signature s)
    {
        return Ytilda.equals(s.Ytilda);
    }
}
