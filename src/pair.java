import java.io.PrintWriter;

/**
 * @author Edgar Martinez-Ayala, Ryan Moran, and  Ahmed
 * Pair class - Class that contains an object that
 * 			    holds a pair. This pair is used to implement
 * 				hold the adress of each client and the name
 * 			    of that client.
 *
 */

public class pair{
    private String name;
    private PrintWriter address;

    public pair(String n, PrintWriter addr) {
        this.name = n;
        this.address = addr;
    }

    //Getters and setters of pos1 and pos2
    public String getName(){
        return this.name;
    }
    public PrintWriter getAddress(){
        return this.address;
    }

    public void setName(String n){
        this.name = n;
    }
}