package client;

import network.TCPConnection;
import network.TCPConnectionObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindows extends JFrame  implements ActionListener, TCPConnectionObserver {

    private static final String IP_ADDRESS = "localhost";

    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindows();
            }
        });

    }
    private  final JTextArea log = new JTextArea();
    private final JTextField name = new JTextField("nickName");
    private final JTextField message = new JTextField("hello");

    private TCPConnection connection;


    private ClientWindows(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        setAlwaysOnTop(true);

        try {
            connection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage("Connection exc" + e);;
        }

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        message.addActionListener(this);
        add(message, BorderLayout.SOUTH);
        add(name, BorderLayout.NORTH);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = message.getText();
        if(msg.equals("")){
            return;
        }
        connection.sendMessage(name.getText() + ": " + msg);


    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String str) {
        printMessage(str);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exc" + e);
    }

    private synchronized void printMessage(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}

