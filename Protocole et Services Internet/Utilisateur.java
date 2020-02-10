import java.util.ArrayList;

public class Utilisateur {
	private String pseudo;
	private ArrayList<Annonce> annonces;
	private String publicKey;
	private String ip;
	private int port;

	Utilisateur(String pseudo, String publicKey, String ip, int port) {
		this.pseudo = pseudo;
		this.publicKey = publicKey;
		this.ip = ip;
		this.port = port;
		annonces = new ArrayList<Annonce>();
	}

	void addAnnonce(Annonce annonce) {
		annonces.add(annonce);
	}

	Annonce removeAnnonce(int id) {
		return annonces.remove(id);
	}

	ArrayList<Annonce> getAnnonces() {
		return new ArrayList<Annonce>(annonces);
	}

	String getPseudo() {
		return pseudo;
	}

	String getPublicKey() {
		return publicKey;
	}

	String getIP() {
		return ip;
	}

	int getPort() {
		return port;
	}
}