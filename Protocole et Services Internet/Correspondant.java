import java.net.*;

public class Correspondant {
	private String pseudo;
	private String ip;
	private String port;
	private String publicKey;

	Correspondant(String pseudo, String ip, String port, String publicKey) {
		this.pseudo = pseudo;
		this.ip = ip;
		this.port = port;
		this.publicKey = publicKey;
	}

	public void sendMessage(String mess) {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(ip);
			byte[] sendData = new byte[1024];
			mess = Security.encrypt(mess, publicKey);
			sendData = mess.getBytes();
			DatagramPacket sendPacket = new
			DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(port));
			clientSocket.send(sendPacket);
		} catch (Exception e) {
			System.out.println("Erreur lors de l'envoie du message!");
		}
	}

	public String getPseudo() {
		return pseudo;
	}
}