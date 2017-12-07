import java.math.BigInteger;
import java.util.Random;

//
public class RSA
{	// open class

    // two prime numbers
    private BigInteger p;
    private BigInteger q;

    // product of prime numbers
    private BigInteger N;

    // (p-1)(q-1)
    private BigInteger phi;

    // encryption value
    private BigInteger e;

    // decryption value
    private BigInteger d;

    // ????
    private int bitlength = 1024;
    private Random     r;


    // class constructor 1
    public RSA(int val1, int val2)
    {
        //int pVal, int qVal
        r = new Random();

        // calc p
        //p = BigInteger.valueOf(new Long(pVal));
        //q = BigInteger.valueOf(new Long(qVal));


        p = BigInteger.probablePrime(bitlength, r);
        q = BigInteger.probablePrime(bitlength, r);

        N = p.multiply(q);

        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        e = BigInteger.probablePrime(bitlength / 2, r);


        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
        {
            e.add(BigInteger.ONE);
        }


        d = e.modInverse(phi);
    }
    // class constructor 2
    public RSA(BigInteger e, BigInteger d, BigInteger N)
    {
        this.e = e;
        this.d = d;
        this.N = N;
    }


    // Encrypt message
    public byte[] encrypt(String message)//byte[] message)
    {
        byte[] bytes = message.getBytes();

        return (new BigInteger(bytes)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    public String decrypt(byte[] message, BigInteger dValue, BigInteger nValue)
    {
        return new String((new BigInteger(message)).modPow(dValue, nValue).toByteArray());

    }


    public BigInteger getD()
    {
        return this.d;
    }
    public BigInteger getN()
    {
        return this.N;
    }


}	// close class



/*
import java.util.Scanner;

public class MainMethod
{

	public static void main(String[] args)
    {
    	Scanner scan = new Scanner(System.in);

        String teststring;

        System.out.println("Enter the plain text:");
        teststring = scan.nextLine();

        System.out.println("Encrypting String: " + teststring);
        System.out.println("String in Bytes: " + bytesToString(teststring.getBytes()));


        RSA rsa = new RSA();

        // encrypt
        byte[] encrypted = rsa.encrypt(teststring);

        // decrypt
        String decrypted = rsa.decrypt(encrypted, rsa.getD(), rsa.getN());

        // System.out.println("Decrypting Bytes: " + bytesToString(decrypted));
        System.out.println("Decrypted String: " + decrypted);


        scan.close();
    }



	private static String bytesToString(byte[] encrypted)
    {
        String test = "";
        for (byte b : encrypted)
        {
            test += Byte.toString(b);
        }
        return test;
    }

}
 */