import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ServerTCP implements Runnable {
	private Socket socket;
	private Utilisateur user;
	static private ArrayList<Utilisateur> users = new ArrayList<Utilisateur>();

	ServerTCP(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			boolean quit = false;

			while (true) {
				String mess;
				if ((mess = br.readLine()) != null) {
					if (!mess.equals("")) {
						Scanner sc = new Scanner(mess);
						switch (sc.next()) {
							case "/annonce":
							try {
								if (user != null) {
									Categorie categorie = Categorie.valueOf(sc.next());
									int prix = Integer.parseInt(sc.next());
									String description = sc.nextLine().substring(1);

									if (!description.contains("#")) {
										Annonce annonce = new Annonce(user.getPseudo(), categorie, prix, description);
										user.addAnnonce(annonce);

										bw.write("T#Annonce mise en ligne!\n");
									} else
										throw new Exception();
								} else
								bw.write("F#Vous n'êtes pas indentifié!\n");
							} catch (Exception e) {
								e.printStackTrace();
								bw.write("F#Mauvais format!\n");
							}
							break;
							case "/getAnnonce":
							if (sc.hasNext())
								bw.write("F#Mauvais format!\n");
							else
								bw.write(getAnnonces());
							break;
							case "/connect":
							try {
								String pseudo = sc.next();
								if (checkPseudoDispo(pseudo)) {
									user = new Utilisateur(pseudo, sc.next(), socket.getInetAddress().getHostAddress(), socket.getPort());
									users.add(user);
									System.out.println("Connexion de " + pseudo);
									bw.write("T\n");
								} else
								bw.write("F\n");
							} catch (Exception e) {
								e.printStackTrace();
								bw.write("F#Mauvais format!\n");
							}
							break;
							case "/getMyAnnonce":
							if (user != null) {
								if (sc.hasNext())
									bw.write("F#Mauvais format!\n");
								else
									bw.write(getMyAnnonces());
							} else
								bw.write("F#Vous n'êtes pas indentifié!\n");
							break;
							case "/remove":
							if (user != null) {
								try {
									int id_annonce = Integer.parseInt(sc.next());
									if (!sc.hasNext()) {
										user.removeAnnonce(id_annonce);
										bw.write("T#Annonce supprimée!\n");
									} else
										bw.write("F#Mauvais format!\n");
								} catch (NoSuchElementException e) {
									bw.write("F#Mauvais format!\n");
								} catch (Exception e) {
									bw.write("T#Annonce inexistante!\n");
								}
							} else
							bw.write("F#Vous n'êtes pas identifié\n");
							break;
							case "/info":
							try {
								String pseudo = sc.next();
								if (!checkPseudoDispo(pseudo))
									bw.write(info(pseudo));
								else
									bw.write("F#Le pseudo de l'utilisateur n'existe pas dans la base de donnée!\n");
							} catch (Exception e) {
								bw.write("F#Mauvais format!\n");
							}
							break;
							case "/quit":
							quit = true;
							break;
							default:
							bw.write("F#Commande inconnue!\n");
						}
						bw.flush();

						if (quit)
							break;
					}
				} else
				break;
			}
			br.close();
			bw.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Server.nbrCLient--;
			if (user != null)
				System.out.println("Déconnexion de " + user.getPseudo());
		}
	}

	private String getAnnonces() {
		String s = "T";

		for (Utilisateur u : users) {
			int i = 0;
			for (Annonce annonce : u.getAnnonces()) {
				s += "#" + annonce.show(i);
				i++;
			}
		}
		if (s.equals("T"))
			s = "T#Aucune annonce enregistrée pour le moment";

		s += '\n';
		return s;
	}

	private String getMyAnnonces() {
		String s = "T";

		int i = 0;
		for (Annonce annonce : user.getAnnonces()) {
			s += "#" + annonce.show(i);
			i++;
		}
		if (s.equals("T"))
			s = "T#Vous n'avez posté aucune anonnce";

		s += '\n';
		return s;
	}

	private boolean checkPseudoDispo(String pseudo) {
		for (Utilisateur u : users) {
			if (u.getPseudo().equals(pseudo)) {
				return false;
			}
		}
		return true;
	}

	private String info(String pseudo) {
		for (Utilisateur u : users) {
			if (u.getPseudo().equals(pseudo)) {
				String s = "T#" + pseudo + "#" + u.getIP() + "#" + u.getPort() +  "#" + u.getPublicKey();
				s += '\n';
				return s;
			}
		}

		return null;
	}
}