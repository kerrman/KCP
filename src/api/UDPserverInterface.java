package api;

public interface UDPserverInterface {

	public void printUDPclient(String string);

	public void printUDPserver(String string);

	public void writeUDPlog(String string);

	public void updateUDPWho(String[] clientHandles, String[] serverNodes);

}
