package Server;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import Model.CryptoParameters;
import Model.ElectionParameters;

public final class ElectionToolsServer {
	private static Random rng;
    private static MessageDigest sha;

    public static BigInteger Prime, Generator, SubgroupSize;
    public static BigInteger[] PublicKeys;
    public static byte[] L;
    public static BigInteger h;

    public static String question;
    public static String[] answersText;
    public static byte[][] answers;

    public static void Initialize(String algorithm, String q, String[]ans) throws NoSuchAlgorithmException
    {
        switch (algorithm)
        {
            case "RFC5114_2_3_256":
                Prime 			= HexToUnsignedInteger("87A8E61D B4B6663C FFBBD19C 65195999 8CEEF608 660DD0F2 5D2CEED4 435E3B00 E00DF8F1 D61957D4 FAF7DF45 61B2AA30 16C3D911 34096FAA 3BF4296D 830E9A7C 209E0C64 97517ABD 5A8A9D30 6BCF67ED 91F9E672 5B4758C0 22E0B1EF 4275BF7B 6C5BFC11 D45F9088 B941F54E B1E59BB8 BC39A0BF 12307F5C 4FDB70C5 81B23F76 B63ACAE1 CAA6B790 2D525267 35488A0E F13C6D9A 51BFA4AB 3AD83477 96524D8E F6A167B5 A41825D9 67E144E5 14056425 1CCACB83 E6B486F6 B3CA3F79 71506026 C0B857F6 89962856 DED4010A BD0BE621 C3A3960A 54E710C3 75F26375 D7014103 A4B54330 C198AF12 6116D227 6E11715F 693877FA D7EF09CA DB094AE9 1E1A1597");
                Generator 		= HexToUnsignedInteger("3FB32C9B 73134D0B 2E775066 60EDBD48 4CA7B18F 21EF2054 07F4793A 1A0BA125 10DBC150 77BE463F FF4FED4A AC0BB555 BE3A6C1B 0C6B47B1 BC3773BF 7E8C6F62 901228F8 C28CBB18 A55AE313 41000A65 0196F931 C77A57F2 DDF463E5 E9EC144B 777DE62A AAB8A862 8AC376D2 82D6ED38 64E67982 428EBC83 1D14348F 6F2F9193 B5045AF2 767164E1 DFC967C1 FB3F2E55 A4BD1BFF E83B9C80 D052B985 D182EA0A DB2A3B73 13D3FE14 C8484B1E 052588B9 B7D2BBD2 DF016199 ECD06E15 57CD0915 B3353BBB 64E0EC37 7FD02837 0DF92B52 C7891428 CDC67EB6 184B523D 1DB246C3 2F630784 90F00EF8 D647D148 D4795451 5E2327CF EF98C582 664B4C0F 6CC41659");
                SubgroupSize 	= HexToUnsignedInteger("8CF83642 A709A097 B4479976 40129DA2 99B1A47D 1EB3750B A308B0FE 64F5FBD3");
                break;
            case "RFC5114_2_2_224":
                Prime 			= HexToUnsignedInteger("AD107E1E 9123A9D0 D660FAA7 9559C51F A20D64E5 683B9FD1 B54B1597 B61D0A75 E6FA141D F95A56DB AF9A3C40 7BA1DF15 EB3D688A 309C180E 1DE6B85A 1274A0A6 6D3F8152 AD6AC212 9037C9ED EFDA4DF8 D91E8FEF 55B7394B 7AD5B7D0 B6C12207 C9F98D11 ED34DBF6 C6BA0B2C 8BBC27BE 6A00E0A0 B9C49708 B3BF8A31 70918836 81286130 BC8985DB 1602E714 415D9330 278273C7 DE31EFDC 7310F712 1FD5A074 15987D9A DC0A486D CDF93ACC 44328387 315D75E1 98C641A4 80CD86A1 B9E587E8 BE60E69C C928B2B9 C52172E4 13042E9B 23F10B0E 16E79763 C9B53DCF 4BA80A29 E3FB73C1 6B8E75B9 7EF363E2 FFA31F71 CF9DE538 4E71B81C 0AC4DFFE 0C10E64F");
                Generator 		= HexToUnsignedInteger("AC4032EF 4F2D9AE3 9DF30B5C 8FFDAC50 6CDEBE7B 89998CAF 74866A08 CFE4FFE3 A6824A4E 10B9A6F0 DD921F01 A70C4AFA AB739D77 00C29F52 C57DB17C 620A8652 BE5E9001 A8D66AD7 C1766910 1999024A F4D02727 5AC1348B B8A762D0 521BC98A E2471504 22EA1ED4 09939D54 DA7460CD B5F6C6B2 50717CBE F180EB34 118E98D1 19529A45 D6F83456 6E3025E3 16A330EF BB77A86F 0C1AB15B 051AE3D4 28C8F8AC B70A8137 150B8EEB 10E183ED D19963DD D9E263E4 770589EF 6AA21E7F 5F2FF381 B539CCE3 409D13CD 566AFBB4 8D6C0191 81E1BCFE 94B30269 EDFE72FE 9B6AA4BD 7B5A0F1C 71CFFF4C 19C418E1 F6EC0179 81BC087F 2A7065B3 84B890D3 191F2BFA");
                SubgroupSize 	= HexToUnsignedInteger("801C0D34 C58D93FE 99717710 1F80535A 4738CEBC BF389A99 B36371EB");
                break;
            case "RFC5114_2_1_160":
                Prime 			= HexToUnsignedInteger("B10B8F96 A080E01D DE92DE5E AE5D54EC 52C99FBC FB06A3C6 9A6A9DCA 52D23B61 6073E286 75A23D18 9838EF1E 2EE652C0 13ECB4AE A9061123 24975C3C D49B83BF ACCBDD7D 90C4BD70 98488E9C 219A7372 4EFFD6FA E5644738 FAA31A4F F55BCCC0 A151AF5F 0DC8B4BD 45BF37DF 365C1A65 E68CFDA7 6D4DA708 DF1FB2BC 2E4A4371");
                Generator 		= HexToUnsignedInteger("A4D1CBD5 C3FD3412 6765A442 EFB99905 F8104DD2 58AC507F D6406CFF 14266D31 266FEA1E 5C41564B 777E690F 5504F213 160217B4 B01B886A 5E91547F 9E2749F4 D7FBD7D3 B9A92EE1 909D0D22 63F80A76 A6A24C08 7A091F53 1DBF0A01 69B6A28A D662A4D1 8E73AFA3 2D779D59 18D08BC8 858F4DCE F97C2A24 855E6EEB 22B3B2E5");
                SubgroupSize 	= HexToUnsignedInteger("F518AA87 81A8DF27 8ABA4E7D 64B7CB9D 49462353");
                break;
            case "ExampleDsa_160":
                Prime 			= HexToUnsignedInteger("8df2a494 492276aa 3d25759b b06869cb eac0d83a fb8d0cf7 cbb8324f 0d7882e5 d0762fc5 b7210eaf c2e9adac 32ab7aac 49693dfb f83724c2 ec0736ee 31c80291");
                Generator 		= HexToUnsignedInteger("626d0278 39ea0a13 413163a5 5b4cb500 299d5522 956cefcb 3bff10f3 99ce2c2e 71cb9de5 fa24babf 58e5b795 21925c9c c42e9f6f 464b088c c572af53 e6d78802");
                SubgroupSize 	= HexToUnsignedInteger("c773218c 737ec8ee 993b4f2d ed30f48e dace915f");
                break;
            default:
                Prime 			= HexToUnsignedInteger("8df2a494 492276aa 3d25759b b06869cb eac0d83a fb8d0cf7 cbb8324f 0d7882e5 d0762fc5 b7210eaf c2e9adac 32ab7aac 49693dfb f83724c2 ec0736ee 31c80291");
                Generator 		= HexToUnsignedInteger("626d0278 39ea0a13 413163a5 5b4cb500 299d5522 956cefcb 3bff10f3 99ce2c2e 71cb9de5 fa24babf 58e5b795 21925c9c c42e9f6f 464b088c c572af53 e6d78802");
                SubgroupSize 	= HexToUnsignedInteger("c773218c 737ec8ee 993b4f2d ed30f48e dace915f");
                break;
        }
        rng = new Random();

        question=q;
        SetAnswers(ans);
        
        PublicKeys = new BigInteger[0];
        L = new byte[0];
        h = new BigInteger("0");

        sha = MessageDigest.getInstance("SHA-512");
    }

    public static void SetPublicKeys(BigInteger[] publicKeys) throws IOException
    {
        PublicKeys = publicKeys;
        byte[][] publicKeysBytes = new byte[publicKeys.length][];
        for (int i=0;i<publicKeys.length;i++)
        	publicKeysBytes[i] = publicKeys[i].toByteArray();
        
        L = ConcatBytes(publicKeysBytes);
        h = Hash2(L);
    }
    
    private static void SetAnswers(String[] ans) {
		answersText = ans;
		answers = new byte[ans.length][];
		for (int i = 0; i < ans.length; i++)
			answers[i] = ans[i].getBytes();
	}

    public static BigInteger Hash1(byte[] data)
    {
    	byte[] hash = sha.digest(data);
        return new BigInteger(hash).mod(SubgroupSize);
    }

    public static BigInteger Hash2(byte[] data)
    {
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

    public static BigInteger HexToUnsignedInteger(String hex)
    {
    	return new BigInteger(hex.replace(" ", ""), 16);
    }

    public static BigInteger GenerateInteger(BigInteger max)
    { 
        int bytesToRepresent = max.toByteArray().length;
        byte[] bytes = new byte[bytesToRepresent];
        rng.nextBytes(bytes);
        
        return new BigInteger(bytes).mod(max);
    }

    public static void validate()
    {
        if (Generator.compareTo(new BigInteger("2"))==-1 || Generator.compareTo(Prime.subtract(BigInteger.ONE))==1)
            throw new SecurityException("Generator out of range");

        if (Generator.modPow(SubgroupSize, Prime) != BigInteger.ONE)
            throw new SecurityException("Generator is wrong");
    }

    public static String getInfo()
    {
        StringBuilder result = new StringBuilder();
        result.append("Election parameters : " + "\n");
        result.append("Generator    : " + Generator + "\n");
        result.append("SubgroupSize : " + SubgroupSize + "\n");
        result.append("Prime        : " + Prime + "\n" + "\n");

        return result.toString();
    }

	public static ElectionParameters getElectionParameters() {
		return new ElectionParameters(PublicKeys, question, answersText);
	}

	public static CryptoParameters getCryptoParameters() {
		return new CryptoParameters(Prime, Generator, SubgroupSize);
	}
}
