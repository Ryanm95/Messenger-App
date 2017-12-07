
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class client extends JFrame implements ActionListener
{
    // GUI items
    JButton sendButton;
    JButton connectButton;
    JTextField machineInfo;
    JTextField portInfo;
    JTextField message;
    JTextArea history;
    JTextField primeOne;
    JTextField primeTwo;
    private JMenuBar bar = new JMenuBar();


    // Network Items
    boolean connected;
    Socket echoSocket;
    PrintWriter out;
    BufferedReader in;

    // set up GUI
    public client()
    {
        super( "Client" );

        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout (new BorderLayout ());

        // set up the North panel
        JPanel upperPanel = new JPanel ();
        upperPanel.setLayout (new GridLayout (4,2));
        container.add (upperPanel, BorderLayout.NORTH);

        // create buttons
        connected = false;

        upperPanel.add ( new JLabel ("Message: ", JLabel.RIGHT) );
        message = new JTextField ("");
        message.addActionListener( this );
        upperPanel.add( message );

        sendButton = new JButton( "Send Message" );
        sendButton.addActionListener( this );
        sendButton.setEnabled (false);
        upperPanel.add( sendButton );

        connectButton = new JButton( "Connect to Server" );
        connectButton.addActionListener( this );
        upperPanel.add( connectButton );

        upperPanel.add ( new JLabel ("Server Address: ", JLabel.RIGHT) );
        machineInfo = new JTextField ("127.0.0.1");
        upperPanel.add( machineInfo );

        upperPanel.add ( new JLabel ("Server Port: ", JLabel.RIGHT) );
        portInfo = new JTextField ("");
        upperPanel.add( portInfo );

        JPanel primes = new JPanel(new GridLayout(3,1));
        primeOne = new JTextField();
        primes.add(primeOne);

        primeTwo = new JTextField();
        primes.add(primeTwo);
        container.add(primes, BorderLayout.LINE_START);

        history = new JTextArea ( 10, 20 );
        history.setEditable(false);
        container.add( new JScrollPane(history), BorderLayout.SOUTH );

        setupMenu();   //builds menu
        setSize( 500, 400 );
        setVisible( true );

    } // end CountDown constructor

    // handle button event
    public void actionPerformed( ActionEvent event )
    {
        if ( connected &&
                (event.getSource() == sendButton ||
                        event.getSource() == message ) )
        {
            doSendMessage();
        }
        else if (event.getSource() == connectButton)
        {
            doManageConnection();
        }
    }

    private void setupMenu(){
        JMenu fileMenu = new JMenu("File");                     // file menu

        JMenuItem aboutItem = new JMenuItem("About");
        fileMenu.add(aboutItem);
        aboutItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "Set up about\n", "About", JOptionPane.PLAIN_MESSAGE)
        );

        JMenuItem serverItem = new JMenuItem("Make Server");
        fileMenu.add(serverItem);
        serverItem.addActionListener(
                e -> {
                    server application = new server();
                    application.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                }
        );

        JMenuItem helpItem = new JMenuItem("Help");
        fileMenu.add(helpItem);
        helpItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "Set up help\n", "Help", JOptionPane.PLAIN_MESSAGE)
        );

        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");           // exit menu item
        fileMenu.add(exitItem);
        exitItem.addActionListener(
                e -> System.exit(0)
        );


        JMenu primeMenu = new JMenu("Prime Numbers");                     // file menu

        JMenuItem pickItem = new JMenuItem("Auto-Pick Numbers");
        primeMenu.add(pickItem);
        pickItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "Set up auto pick\n", "Auto Pick From File", JOptionPane.PLAIN_MESSAGE)
        );

        JMenuItem enterItem = new JMenuItem("Enter Numbers");
        primeMenu.add(enterItem);
        enterItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "Set up enter numbers\n", "Enter Prime Numbers", JOptionPane.PLAIN_MESSAGE)
        );

        setJMenuBar(bar);
        bar.add(fileMenu);
        bar.add(primeMenu);
    }

    public void doSendMessage()
    {
        try
        {
            out.println(message.getText());
            message.setText("");
        }
        catch (Exception e)
        {
            history.insert ("Error in processing message ", 0);
        }
    }

    public void doManageConnection()
    {
        if (connected == false)
        {
            String machineName = null;
            int portNum = -1;
            try {
                machineName = machineInfo.getText();
                portNum = Integer.parseInt(portInfo.getText());
                echoSocket = new Socket(machineName, portNum );
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        echoSocket.getInputStream()));

                // start a new thread to read from the socket
                new CommunicationReadThread (in, this);

                sendButton.setEnabled(true);
                connected = true;
                connectButton.setText("Disconnect from Server");
            } catch (NumberFormatException e) {
                history.insert ( "Server Port must be an integer\n", 0);
            } catch (UnknownHostException e) {
                history.insert("Don't know about host: " + machineName , 0);
            } catch (IOException e) {
                history.insert ("Couldn't get I/O for "
                        + "the connection to: " + machineName , 0);
            }

        }
        else
        {
            try
            {
                out.close();
                in.close();
                echoSocket.close();
                sendButton.setEnabled(false);
                connected = false;
                connectButton.setText("Connect to Server");
            }
            catch (IOException e)
            {
                history.insert ("Error in closing down Socket ", 0);
            }
        }


    }

} // end class EchoServer3

// Class to handle socket reads
//   THis class is NOT written as a nested class, but perhaps it should
class CommunicationReadThread extends Thread
{
    //private Socket clientSocket;
    private client gui;
    private BufferedReader in;


    public CommunicationReadThread (BufferedReader inparam, client ec3)
    {
        in = inparam;
        gui = ec3;
        start();
        gui.history.insert ("Communicating with Port\n", 0);

    }

    public void run()
    {
        System.out.println ("New Communication Thread Started");

        try {
            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                System.out.println ("Client: " + inputLine);
                gui.history.insert ("You: " + inputLine + "\n", 0);

                if (inputLine.equals("Bye."))
                    break;

            }

            in.close();
            //clientSocket.close();
        }
        catch (IOException e)
        {
            System.err.println("Problem with Client Read");
            //System.exit(1);
        }
    }
}





