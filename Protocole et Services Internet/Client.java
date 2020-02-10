import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.Base64;
import java.security.*;
import java.util.LinkedList;

public class Client {
	private static LinkedList<Correspondant> correspondants = new LinkedList<Correspondant>();
	private static int PORT = 2000;

 	public static void main(String args[]) {
 		String ip = "";
 		// gestion des arguments
 		if (args.length == 0) {
 			ip = "localhost";
 		} else {
 			ip = args[0];
 			return;
 		}

 		// Generation clefs publique/prive
 		Security.generateRSA();

 		// affichage liste des commandes
 		help();

		// Lecture UDP
 		try {
 			InetAddress address = InetAddress.getByName(ip);
	 		Socket socket = new Socket(address, PORT);
	 		PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	 		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	 		Scanner inFromUser = new Scanner(System.in);
			String lastRequest = "";
			boolean connected = false;
			String pseudo = "";

 			new Thread() {
 				@Override
 				public void run() {
 					try {
	 					DatagramSocket clientSocket = new DatagramSocket(socket.getLocalPort());
	 					while (true) {
					 		byte[] receiveData = new byte[1024];
							DatagramPacket receivePacket = new
							DatagramPacket(receiveData, receiveData.length);
							clientSocket.receive(receivePacket);
							String messCrypted = new String(receivePacket.getData(), 0, receivePacket.getLength());
							try {
								String messDecrypted = Security.decrypt(messCrypted.getBytes());
								String[] tokens = messDecrypted.split(" ", 2);
								System.out.println(tokens[0] + " : " + tokens[1]);
							} catch(Exception e) {
								e.printStackTrace();
								//System.out.println("Erreur lors de la reception d'un message!");
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						//System.out.println("Erreur lors de la reception d'un message!");
					}
 				}
 			}.start();

 			while (true) {
 				// Ecriture TCP/UDP
 				String mess = inFromUser.nextLine();
 				lastRequest = "";
 				if (!mess.equals("")) {
 					Scanner sc = new Scanner(mess);
 					lastRequest = sc.next();

					if (lastRequest.equals("/help")) {
						help();
						continue;
 					} else if (!lastRequest.equals("/connect") && !connected) {
 						System.out.println("Il faut vous connecter avant d'effectuer d'autres requêtes!");
 						continue;
 					} else if (lastRequest.equals("/msg")) {
						if (sc.hasNext()) {
							Correspondant c = getCorrespondant(sc.next());
							if (c != null) {
								if (sc.hasNext()) {
									c.sendMessage(pseudo + " " + sc.nextLine().substring(1));
								}
							} else
								System.out.println("Correspondant inconnu!");
						}
						continue;
 					} else {
 						if (lastRequest.equals("/connect")) {
	 						if (sc.hasNext()) {
	 							pseudo = sc.next();
	 							if (pseudo.contains("#") || sc.hasNext()) {
	 								System.out.println("Un pseudo ne peut pas contenir de \"#\" ou d\'espaces!");
	 								continue;
	 							}
	 						}
 							// on ajoute notre clef public à la commande
 							mess += " " + Security.publicKey;
 						}
 						pw.println(mess);
 						pw.flush();
					}
 				}

 				// Lecture TCP
 				try {
 					mess = br.readLine();
 					if (mess != null) {
 						String[] tokens = mess.split("#");
 						if (tokens[0].equals("T")) {
 							if (lastRequest.equals("/info")) {
 								if (getCorrespondant(tokens[1]) == null) {
 									// pseudo, ip, port, publicKey
 									correspondants.add(new Correspondant(tokens[1], tokens[2], tokens[3], tokens[4]));
 								}
 								System.out.println(tokens[1] + " a été ajouté a votre liste de correspondants!");
 							} else if (lastRequest.equals("/getAnnonce") || lastRequest.equals("/getMyAnnonce") || lastRequest.equals("/help")) {
 								for (int i = 1; i < tokens.length; i++)
 									System.out.println(tokens[i]);
 							} else if (lastRequest.equals("/connect")) {
 								System.out.println("Connexion réussie!");
 								connected = true;
 							} else
 								System.out.println(tokens[1]);
 						} else if (lastRequest.equals("/connect"))
 							System.out.println("Echec de la connexion!");
 						else
 							System.out.println(tokens[1]);
 					} else {
 						System.out.println("Fin de la connexion!");
 						System.exit(0);
 					}
 				} catch (Exception e ) {
 					e.printStackTrace();
 				}
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 	}

 	static Correspondant getCorrespondant(String pseudo) {
 		for (Correspondant c : correspondants) {
			if (c.getPseudo().equals(pseudo)) {
				return c;
			}
		}

		return null;
	}

	private static void help() {
		System.out.println("Liste des commandes :");
		System.out.println("/help");
		System.out.println("/connect [pseudo]");
		System.out.println("/annonce [domaine] [prix] [description]");
		System.out.println("/getAnnonce");
		System.out.println("/getMyAnnonce");
		System.out.println("/remove [i]");
	}
}