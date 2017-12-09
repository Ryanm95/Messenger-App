/*
    Server class: Handles the server side of the connection. The server allows multiple users to connect to one central server.
                  The server receives an encrypted message and sends to the users for the user to decrypt.
 */

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class server extends JFrame {

    // GUI items
    JButton ssButton;
    JLabel machineInfo;
    JLabel portInfo;
    JTextArea history;
    private boolean running;

    // Network Items
    boolean serverContinue;
    ServerSocket serverSocket;
    Vector <PrintWriter> outStreamList;
    Vector <String> names;  //names of people in the program

    // set up GUI
    public server()
    {
        super( "Echo Server" );

        // set up the shared outStreamList and names vector
        outStreamList = new Vector<PrintWriter>();
        names = new Vector<String>();

        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout( new FlowLayout() );

        // create buttons
        running = false;
        ssButton = new JButton( "Start Listening" );
        ssButton.addActionListener( e -> doButton (e) );
        container.add( ssButton );

        String machineAddress = null;
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            machineAddress = addr.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            machineAddress = "127.0.0.1";
        }
        machineInfo = new JLabel (machineAddress);
        container.add( machineInfo );
        portInfo = new JLabel (" Not Listening ");
        container.add( portInfo );

        history = new JTextArea ( 10, 40 );
        history.setEditable(false);
        container.add( new JScrollPane(history) );

        setSize( 500, 250 );
        setVisible( true );

    } // end CountDown constructor

    // handle button event
    public void doButton( ActionEvent event )
    {
        if (running == false)
        {
            new ConnectionThread (this);
        }
        else
        {
            serverContinue = false;
            ssButton.setText ("Start Listening");
            portInfo.setText (" Not Listening ");
        }
    }


} // end class EchoServer4


class ConnectionThread extends Thread
{
    server gui;

    public ConnectionThread (server es3)
    {
        gui = es3;
        start();
    }

    public void run()
    {
        gui.serverContinue = true;

        try
        {
            gui.serverSocket = new ServerSocket(0);
            gui.portInfo.setText("Listening on Port: " + gui.serverSocket.getLocalPort());
            System.out.println ("Connection Socket Created");
            try {
                while (gui.serverContinue)
                {
                    System.out.println ("Waiting for Connection");
                    gui.ssButton.setText("Stop Listening");
                    new CommunicationThread (gui.serverSocket.accept(), gui, gui.outStreamList, gui.names);
                }
            }
            catch (IOException e)
            {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        }
        finally
        {
            try {
                gui.serverSocket.close();
            }
            catch (IOException e)
            {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }
}


class CommunicationThread extends Thread
{
    //private boolean serverContinue = true;
    private Socket clientSocket;
    private server gui;
    private Vector<PrintWriter> outStreamList;
    private Vector<String> names;
    PrintWriter out;


    // Function that gets all the names and
    // combines them into a string
    private String combineNames(){
        String name = null;
        for (String n : names) {
            if (n != null){
                name += n + " ";
            }
        }
        return name;

    }



    public CommunicationThread (Socket clientSoc, server ec3,
                                Vector<PrintWriter> oSL, Vector<String> n)
    {
        clientSocket = clientSoc;
        gui = ec3;
        outStreamList = oSL;
        names = n;

        BufferedReader in;
        String inputLine;

        //read in the clients username
        try {
            in = new BufferedReader(
                    new InputStreamReader( clientSocket.getInputStream()));
            inputLine = in.readLine();

            out = new PrintWriter(clientSocket.getOutputStream(),
                    true);

            outStreamList.add(out);
            names.add(inputLine);

            gui.history.insert (inputLine + " :Connected\n", 0); //display connected

            //send list of people to client side
            for ( PrintWriter out1: outStreamList ) {
                String name = combineNames();
                //out1.println (name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    public void run()
    {
        System.out.println ("New Communication Thread Started");

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader( clientSocket.getInputStream()));

            String inputLine;
            String pubKey;
            String encrypt;

            while ((inputLine = in.readLine()) != null)
            {
                pubKey = in.readLine();
                encrypt = in.readLine();

                System.out.println ("Server: " + encrypt);
                gui.history.insert (encrypt+"\n", 0);

                // Loop through the outStreamList and send to all "active" streams
                //out.println(inputLine);
                for ( PrintWriter out1: outStreamList )
                {
                    System.out.println ("Sending Message");
                    out1.println (inputLine);
                    out1.println(pubKey);
                }

                if (inputLine.equals("Bye."))
                    break;

                if (inputLine.equals("End Server."))
                    gui.serverContinue = false;
            }

            outStreamList.remove(out);
            out.close();
            in.close();
            clientSocket.close();
        }
        catch (IOException e)
        {
            System.err.println("Problem with Communication Server");
            //System.exit(1);
        }
    }
}
