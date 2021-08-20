package ng.assist.UIs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class JavaProgressSocketFactory extends SocketFactory {

    private int sendBufferSize = 2048;


    @Override
     public Socket createSocket(){
        return setSendBufferSize(new Socket());
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return setSendBufferSize(new Socket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return setSendBufferSize(new Socket(host, port,localHost,localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return setSendBufferSize(new Socket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return setSendBufferSize(new Socket(address,port,localAddress,localPort));
    }

    private Socket setSendBufferSize(Socket socket){
        try {
            socket.setSendBufferSize(sendBufferSize);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return  socket;
    }


}
