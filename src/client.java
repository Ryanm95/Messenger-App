/*
    Client class: Client class holds the user GUI and all of its functionality. It allows for the user to
                  connect to the central server so that the users can send messages to each other.
                  The client encrypts the message to be sent to the server. Once the server sends the messages,
                  the clients that receive the messages have to decrypt the message and then the message can be read.
 */

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.*;
import java.math.BigInteger;


public class client extends JFrame implements ActionListener
{
    // GUI items
    JButton sendButton;
    JButton connectButton;
    JTextField machineInfo;
    JTextField portInfo;
    JTextField message;
    JTextArea history;
    private JLabel header = new JLabel("Users In Chat");
    private JMenuBar bar = new JMenuBar();
    private JTextField numberOne = new JTextField(3);
    private JTextField numberTwo = new JTextField(3);
    private JTextField answer = new JTextField(7);
    private JDialog dialog = new JDialog();
    Vector<String> members;                                     // names of people in the chat
    Vector<JButton> memberButton;                               // buttons with members names on them
    Vector<String> selectedMembers;                             // names of members that mesage will sent to

    // Network Items
    boolean connected;
    Socket echoSocket;
    PrintWriter out;
    BufferedReader in;
    public String Name;
    Vector <String> names;

    // class constructor
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

        // add the desired objects to the panel
        upperPanel.add ( new JLabel ("Server Address: ", JLabel.RIGHT) );
        machineInfo = new JTextField ("127.0.0.1");
        upperPanel.add( machineInfo );

        // add the desired objects to the panel
        upperPanel.add ( new JLabel ("Server Port: ", JLabel.RIGHT) );
        portInfo = new JTextField ("");
        upperPanel.add( portInfo );

        // add the desired objects to the panel
        upperPanel.add ( new JLabel ("", JLabel.RIGHT) );
        connectButton = new JButton( "Connect to Server" );
        connectButton.addActionListener( this );
        upperPanel.add( connectButton );

        // text area to hold all the client messages to
        history = new JTextArea ( 10, 20 );
        history.setEditable(false);
        container.add( new JScrollPane(history));

        // panels and buttons used for client
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel sendMessage = new JPanel(new GridLayout(2,2));
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));

        sendMessage.add ( new JLabel ("Message: ", JLabel.RIGHT) );
        message = new JTextField ("");
        message.addActionListener( this );
        sendMessage.add( message );

        sendButton = new JButton( "Send Message" );
        sendButton.addActionListener( this );
        sendButton.setEnabled (false);

        sendMessage.add ( new JLabel ("", JLabel.RIGHT) );
        sendMessage.add(sendButton);

        bottomPanel.add(sendMessage);
        bottomPanel.add(groupPanel, BorderLayout.WEST);
        groupPanel.add(header);

        container.add(bottomPanel, BorderLayout.SOUTH);

        setupMenu();   //builds menu
        setSize( 800, 500 );
        setVisible( true );
        getNameDialog();
    }

    // handle button event
    public void actionPerformed( ActionEvent event )
    {
        // if a connection has already been established, send the message
        if (connected && (event.getSource() == sendButton || event.getSource() == message ))
        {
            doSendMessage();
        }
        // if a connection has not been established
        else if (event.getSource() == connectButton)
        {
            doManageConnection();
        }
    }

    // method to set up the menus
    private void setupMenu(){

        JMenu fileMenu = new JMenu("File");

        // create the about menu the program description
        JMenuItem aboutItem = new JMenuItem("About");
        fileMenu.add(aboutItem);
        aboutItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "The 5th programing assignment for CS 342: \n" +
                                "  Networked Chat with RSA Encryption/Decryption\n" +
                                "  Authors: Edgar Martinez-Ayala, Ryan Moran, and Ahmed Khan\n" +
                                "  Program Written on 12/4/17\n", "About", JOptionPane.PLAIN_MESSAGE)
        );

        // menu option to make this client a server as well
        JMenuItem serverItem = new JMenuItem("Make Server");
        fileMenu.add(serverItem);
        serverItem.addActionListener(
                e -> {
                    server application = new server();
                    application.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
        );

        // menu item that explains how to use the program
        JMenuItem helpItem = new JMenuItem("Help");
        fileMenu.add(helpItem);
        helpItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "                                                            Welcome to Network Chat!\n\n" +
                        "The name you entered on start up will be the way all other users distinguish you from others\n\n " +
                                "Hosting Connection: click File -> Make Server\n                     " +
                                "Give the IP address and port number displayed to any users that want to connect\n" +
                                "Connecting to a Server:\n                     " +
                                "Enter desired server's IP address and port number and click connect\n\n" +
                                "Prime Numbers: Used for encrypting your messages when sending to other users.\n" +
                                "                      Manual entry:        Prime Numbers -> Enter Numbers\n                     " +
                                " Auto generation:  Prime Numbers -> Auto-Pick Numbers\n\n", "Help", JOptionPane.PLAIN_MESSAGE)
        );

        fileMenu.addSeparator();

        // menu item that will exit the program
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        exitItem.addActionListener(
                e -> System.exit(0)
        );


        // menu to allow user to enter 2 prime numbers or auto generate prime numbers
        JMenu primeMenu = new JMenu("Prime Numbers");

        // auto generate the prime numbers using the file
        JMenuItem pickItem = new JMenuItem("Auto-Pick Numbers");
        primeMenu.add(pickItem);
        pickItem.addActionListener(
                e -> JOptionPane.showMessageDialog( this,
                        "Auto picked 2 different prime numbers\n", "Auto Pick From File", JOptionPane.PLAIN_MESSAGE)
        );

        // allow the user to enter the 2 prime numbers
        JMenuItem enterItem = new JMenuItem("Enter Numbers");
        primeMenu.add(enterItem);
        enterItem.addActionListener(
                e -> {
                    setupDialog();
                }
        );

        setJMenuBar(bar);
        bar.add(fileMenu);
        bar.add(primeMenu);
    }

    // creates the name dialog that is shown to the user to enter
    // his/her name
    private void getNameDialog(){

        JLabel nameLabel = new JLabel("Name: ");

        // button to be clicked when the user enters their name
        JButton button = new JButton("Enter");
        button.addActionListener(new buttonL());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // set the constrains for the grid
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        panel.add(answer, gbc);
        gbc.gridy++;
        panel.add(button, gbc);

        // add the dialog box to the panel
        dialog.add(panel);
        dialog.setSize(300,150);
        dialog.setVisible(true);
    }


    class buttonL implements  ActionListener{          // actionlistener for name button
        public void actionPerformed(ActionEvent e){
            Name = answer.getText();
            dialog.setVisible(false);
        }
    }

    // allows the dialog to be set up
    private void setupDialog(){
        // labels for the user entering the prime numbers
        JLabel primeOne = new JLabel("First Number: ");
        JLabel primeTwo = new JLabel("Second Number: ");
        JButton button = new JButton("Check/Enter");
        button.addActionListener(new buttonListener());
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // set the constraints for the grid
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(primeOne, gbc);
        gbc.gridy++;
        panel.add(primeTwo, gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        panel.add(numberOne, gbc);
        gbc.gridy++;
        panel.add(numberTwo, gbc);
        gbc.gridy++;
        panel.add(button, gbc);

        // add the dialog box to the panel
        dialog.add(panel);
        dialog.setSize(300,150);
        dialog.setVisible(true);
    }

    // listens for the button actions
    class buttonListener implements  ActionListener{          // actionlistener for prime button
        public void actionPerformed(ActionEvent e){
            int numOne = Integer.parseInt(numberOne.getText());
            int numTwo = Integer.parseInt(numberTwo.getText());

            // if the first number entered is not prime
            if(!isPrime(numOne)){
                JOptionPane.showMessageDialog(client.this,
                        "First number is not prime... try another number",
                        "Not Prime",JOptionPane.PLAIN_MESSAGE );
            }
            // if the second number entered is not prime
            if(!isPrime(numTwo)){
                JOptionPane.showMessageDialog(client.this,
                        "Second number is not prime... try another number",
                        "Not Prime",JOptionPane.PLAIN_MESSAGE );
            }
            // if the two entered numbers aren't different
            if(numOne == numTwo){
                JOptionPane.showMessageDialog(client.this,
                        "Numbers cannot be the same... try changing one of the numbers",
                        "Not Prime",JOptionPane.PLAIN_MESSAGE );
            }
            // if the two entered numbers aren't big enough
            if(numOne * numTwo <= 127){
                JOptionPane.showMessageDialog(client.this,
                        "The 2 numbers aren't large enough",
                        "Too Small",JOptionPane.PLAIN_MESSAGE );
            }
            else if(isPrime(numTwo) && isPrime(numOne)){
                JOptionPane.getRootFrame().dispose();
            }
        }
    }

    // method to check if the number passed to it is prime
    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i < Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // method to send a message to the server
    public void doSendMessage()
    {
        // create an object to encrypt message
        RSA rsa = new RSA(3,7);

        try
        {
            // get the public key for the client
            String pubKey = rsa.getPubKey();

            // encrypt the message
            byte[] encrypt = rsa.encrypt(Name + " : " + message.getText());

            // turn the encryption into a string
            String encryption = Arrays.toString(encrypt);
            // turn the string into a string that is able to be sent over the sockets
            String bytes = bytesToString(message.getText().getBytes());

            // send the message
            out.println(encryption);
            out.println(pubKey);
            out.println(bytes);

            message.setText("");
        }
        catch (Exception e)
        {
            history.insert ("Error in processing message ", 0);
        }
    }

    // method to connect to a desired connection
    public void doManageConnection()
    {
        // if the connection hsa not been estbalished yet
        if (connected == false)
        {
            String machineName = null;
            int portNum = -1;
            try {
                // get the data needed for establishing a connection
                machineName = machineInfo.getText();
                portNum = Integer.parseInt(portInfo.getText());
                echoSocket = new Socket(machineName, portNum );
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        echoSocket.getInputStream()));

                // start a new thread to read from the socket
                new CommunicationReadThread (in, this);

                // allow the send button to be used
                sendButton.setEnabled(true);
                connected = true;
                System.out.println(Name);
                out.println(Name);
                System.out.println(Name);

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
        // if the user wants to close the connection
        else
        {
            try
            {
                // close any open sockets/streams
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

    // method to convert an array of bytes to a string
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


// Class to handle socket reads
class CommunicationReadThread extends Thread
{
    //private Socket clientSocket;
    private client gui;
    private BufferedReader in;

    // class constructor
    public CommunicationReadThread (BufferedReader inparam, client ec3)
    {
        in = inparam;
        gui = ec3;
        start();
        gui.history.insert ("Communicating with Server\n", 0);

    }

    // run method
    public void run()
    {
        System.out.println ("New Communication Thread Started");

        // create an object to decrypt the message received
        RSA rsa = new RSA(3,7);
        try {
            String inputLine;

            // catch the message sent by the server
            while ((inputLine = in.readLine()) != null)
            {
                // get the public key
                String pubKey = in.readLine();
                byte[] bytes = stringToBytes(inputLine);

                // parse the public key
                BigInteger e = parseVal(pubKey, true);
                BigInteger n = parseVal(pubKey, false);

                // decrypt the message
                String decrypt = rsa.decrypt(bytes, e, n);

                //check to see if list is updated
                if (inputLine.substring(0, 2).equals(null)){
                    System.out.println("received active list = " + inputLine);
                }
                else {
                    gui.history.insert(decrypt + "\n", 0);
                }

                // close connection
                if (inputLine.equals("Bye."))
                    break;

            }
            in.close();
        }
        catch (IOException e)
        {
            System.err.println("Problem with Client Read");
            //System.exit(1);
        }
    }

    // method to parse the public key
    private static BigInteger parseVal(String value, boolean beforeDec)
    {
        // use a string builder to parse the data
        StringBuilder sb = new StringBuilder();

        // the public key is delimited using a period
        int index = value.indexOf('.');

        // parse the e value
        if (beforeDec)
        {
            // add each number to the string builder until period is seen
            for (int i = 0; i < index; i++)
            {
                sb.append(value.charAt(i));
            }
        }
        // parse the n value
        else
        {
            index++;

            // add each number to the string builder that is after the period
            for (int i = index; i < value.length(); i++)
            {
                sb.append(value.charAt(i));
            }
        }
        // the value is parsed, turn it into a big integer and return
        return new BigInteger(sb.toString());
    }

    // method to convert a string to an array of bytes
    private static byte[] stringToBytes(String response)
    {
        // parse the data using the commas
        String[] byteValues = response.substring(1, response.length() - 1).split(",");

        // create the byte array that will hold the encrypted message
        byte[] bytes = new byte[byteValues.length];

        // convert the encryption
        for (int i=0, len=bytes.length; i<len; i++)
        {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }
}