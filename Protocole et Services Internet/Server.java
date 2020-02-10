import java.net.*;

public class Server {
	static int nbrCLient;
	private static int PORT = 2000;

	public static void main(String args[]) {
		try {
			ServerSocket ss = new ServerSocket(PORT);
			while (true) {
				Socket s = ss.accept();
				new Thread(new ServerTCP(s)).start();
				nbrCLient++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}