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
    //Vector <pair<String,PrintWriter>> outStreamList;
    Vector <PrintWriter> names;

    // set up GUI
    public server()
    {
        super( "Server" );

        // set up the shared outStreamList
        //outStreamList = new Vector<PrintWriter>();
        names = new Vector<PrintWriter>();

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
                    new CommunicationThread (gui.serverSocket.accept(), gui, /*gui.outStreamList,*/ gui.names);
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
    //private Vector <pair<String,PrintWriter>> outStreamList;
    private Vector<PrintWriter> names;



    public CommunicationThread (Socket clientSoc, server ec3,
                                /*Vector<PrintWriter> oSL,*/ Vector<PrintWriter> n)
    {
        clientSocket = clientSoc;
        gui = ec3;
        names = n;
        //outStreamList = oSL;
        gui.history.insert ("Comminucating with Port " + clientSocket.getLocalPort()+"\n", 0);
        start();
    }

    public void run()
    {
        System.out.println ("New Communication Thread Started");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            outStreamList.add(out);
            System.out.println (out);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader( clientSocket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                gui.history.insert ("Client: " + inputLine+"\n", 0);

                //TODO::: This is what we change
                // Loop through the outStreamList and send to all "active" streams
                //out.println(inputLine);
                for ( PrintWriter out1: outStreamList )
                {
                    out1.println (inputLine);
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






