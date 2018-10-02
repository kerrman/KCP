package TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import encryption.Cryptic;

public class SecureTCPsocket {
	
	protected volatile Socket socket;

    private volatile PrintWriter socket_out;
    private volatile BufferedReader socket_in;
    
    private volatile Cryptic security;

    //RAW SOCKET INPUT FOR SPEED TESTS ONLY
    //Please do not use for anything else
    //No security is on this connection
	private volatile InputStream socket_raw_in;

	public SecureTCPsocket(Socket _socket) {
		socket = _socket;
		security = new Cryptic();
		//TODO SecureTCPsocket needs comments
	}

	public int getPort() {
		return socket.getPort();
	}
	

    public void open() throws IOException {
		socket.setSoTimeout(100);
    	socket_raw_in = socket.getInputStream();
        socket_in = new BufferedReader(new InputStreamReader(socket_raw_in));
        socket_out = new PrintWriter(socket.getOutputStream(), true);
        security.encryptTransmissions(socket_in,socket_out);
    }

	public boolean send(String msg) {
		if (socket_out != null) {
			socket_out.println(security.encoderRing(msg));
			return true;
		}
		return false;
	}

	public void close() {
		security.sendTermination();
		socket_out.close();
		try {
			socket_in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testSpeed() throws IOException {
		//TODO SPEED test needs to be worked on
        long total = 0;
        long start = System.currentTimeMillis();

        byte[] bytes = new byte[32*1024]; // 32K
        for(int i=1;;i++) {
            int read = socket_raw_in.read(bytes);
            if (read < 0) break;
            total += read;
            if (i % 500000 == 0) {
                long cost = System.currentTimeMillis() - start;
                System.out.printf("Read %,d bytes, speed: %,d MB/s%n", total, total/cost/1000);
            }
        }
    }

	public String receiveTransmission() throws IOException {
		String temp = socket_in.readLine();
		return temp;
	}

}
