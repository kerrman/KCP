package encryption;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Random;

public class Cryptic {
	
	Random random = new Random();

	private int PUBLIC;
	private final int PRIVATE = random.nextInt(256);

	public Cryptic() {
		// TODO Auto-generated constructor stub
	}

	public String encoderRing(String msg) {
		// TODO CRYPTIC encoder function
		return msg;
	}

	public String decoderRing(String msg) {
		// TODO CRYPTIC decoder function
		return msg;
	}

	public void encryptTransmissions(BufferedReader in, PrintWriter out) {
		String initMsg = "";
		out.println(initMsg);
		
	}

	public void sendTermination() {
		// TODO cryptic goodbye
		
	}

}
