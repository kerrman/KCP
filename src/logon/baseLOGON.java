package logon;

import encryption.Cryptic;

public abstract class baseLOGON {
	
	protected volatile String handle;
	protected volatile String authServerIP;
	protected volatile int sessionID;
	protected volatile int authToken;

	protected volatile Cryptic decoderRing;

	public baseLOGON() {
		// TODO Auto-generated constructor stub
	}

}
