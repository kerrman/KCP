package api;

public interface TCPserverInterface {

	public void printTCPclient(String string);

	public void printTCPserver(String string);

	public void writeTCPlog(String string);

	public void updateTCPWho(String[] clientHandles, String[] serverNodes);

}
