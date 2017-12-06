/*
        GUI class holds all of the functionality that the GUI needs.
        It holds all of the logic for the user interactions
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;


public class GUI extends JFrame implements ActionListener{

    private JPanel container = new JPanel(new BorderLayout());      // whole container
    private JPanel upperPanel = new JPanel(new BorderLayout());
    private JPanel lowerPanel = new JPanel(new BorderLayout());
    private JPanel messageReceived = new JPanel();                  // where user can read message that was received
    private JPanel composedMessage = new JPanel();                  // where user can write message to be sent
    private JPanel userPanel = new JPanel(new BorderLayout());      // where people that are in the chat are displayed
    private JPanel sendPanel = new JPanel();                        // where send button is located
    private JPanel users = new JPanel(new GridLayout());            // where the user and send panel will be displayed

    private JTextField messageIn = new JTextField();                // textField where message is to be displayed
    private JTextField messageOut = new JTextField();               // textField where message is to be composed

    private JButton send = new JButton("Send");                // button to send message

    public GUI(){
        super("Messenger App");
        getContentPane().setBackground(Color.gray);
        add(container);                                             // add container to frame
        setupMenu();                                                // setup menu its items
        setupPanels();                                              // setup panels inside the container
        setSize( 800, 800 );  //window size
        setVisible( true );
    }

    public void actionPerformed(ActionEvent e) {

    }

    private void setupMenu(){
        JMenu fileMenu = new JMenu("File");                     // file menu

        JMenuItem aboutItem = new JMenuItem("About");
        fileMenu.add(aboutItem);
        aboutItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                    }
                }
        );

        JMenuItem helpItem = new JMenuItem("Help");
        fileMenu.add(helpItem);
        helpItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                    }
                }
        );

        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");           // exit menu item
        fileMenu.add(exitItem);
        exitItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }
        );
        JMenuBar bar = new JMenuBar();      // adds menu items to the menu bar
        setJMenuBar(bar);
        bar.add(fileMenu);
    }

    private void setupPanels(){
        messageIn.setEditable(false);                       // make message received not editable
        messageIn.setPreferredSize( new Dimension( 500, 345 ));
        messageReceived.add(messageIn);                     // add to messageReceived panel
        upperPanel.add(messageReceived, BorderLayout.WEST);

        send.setPreferredSize(new Dimension(100, 50));
        sendPanel.add(send);
        userPanel.add(sendPanel, BorderLayout.SOUTH);
        upperPanel.add(userPanel);

        messageOut.setEditable(true);                       // make textField editable
        messageOut.setPreferredSize(new Dimension(800, 400));
        lowerPanel.add(messageOut);

        container.add(upperPanel, BorderLayout.NORTH);
        container.add(lowerPanel, BorderLayout.SOUTH);
    }
}
