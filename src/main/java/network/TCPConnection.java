package network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
    private final Socket socket;

    private final TCPConnectionObserver eventListener;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionObserver eventListener, String ipAddress, int port) throws IOException {
        this(eventListener, new Socket(ipAddress, port));
    }

    public TCPConnection(final TCPConnectionObserver eventListener, Socket socket) throws IOException {
        this.socket = socket;

        in =  new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        out =  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        this.eventListener = eventListener;

        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()){
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        thread.start();
        socket.getOutputStream();

    }

    public synchronized void sendMessage(String msg){
        try {
            out.write(msg + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
