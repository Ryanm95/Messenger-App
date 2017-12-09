import java.math.BigInteger;
import java.util.Random;

public class RSA
{
    // two prime numbers
    private BigInteger P;
    private BigInteger Q;
    // product of prime numbers
    private BigInteger N;
    // (p-1)(q-1)
    private BigInteger PHI;
    // encryption value
    private BigInteger E;
    // decryption value
    private BigInteger D;
    private int bitlength = 1024;
    private Random     r;


    // class constructor
    public RSA(int prime1, int prime2)
    {
        r = new Random();
        // if the user chooses for the file to choose prime numbers
        this.P = BigInteger.probablePrime(bitlength, r);
        this.Q = BigInteger.probablePrime(bitlength, r);
        // calculate N by multiplying
        this.N = this.P.multiply(this.Q);
        // calculate PHI by subtracting and multiplying
        this.PHI = this.P.subtract(BigInteger.ONE).multiply(this.Q.subtract(BigInteger.ONE));
        // choose a valid E
        this.E = BigInteger.probablePrime(bitlength / 2, r);

        while (this.PHI.gcd(this.E).compareTo(BigInteger.ONE) > 0 && this.E.compareTo(this.PHI) < 0)
        {
            this.E.add(BigInteger.ONE);
        }
        // chose a valid D
        this.D = this.E.modInverse(this.PHI);
    }

    // Encrypt message
    public byte[] encrypt(String message)
    {
        // convert the string into an array of bytes
        byte[] bytes = message.getBytes();
        // encrypt the message and then send the encrypted message back
        return (new BigInteger(bytes)).modPow(this.E, this.N).toByteArray();
    }

    // Decrypt message
    public String decrypt(byte[] encryptedMessage, BigInteger dVal, BigInteger nVal)
    {
        // perform the inverse using the key
        BigInteger value = new BigInteger(encryptedMessage).modPow(dVal, nVal);
        // turn the number into an array of bytes
        byte[] decryptedBytes = value.toByteArray();
        // convert the decrypted byte array to a string
        String decryptedMessage = new String(decryptedBytes);
        // the message has been decrypted
        return decryptedMessage;
    }

    // return the public key to the user
    public String getPubKey()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(this.D);
        sb.append(".");
        sb.append(this.N);

        return sb.toString();
    }
}