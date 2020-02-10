public class Annonce {
	private String pseudo;
	private Categorie categorie;
	private int prix;
	private String description;

	Annonce(String pseudo, Categorie categorie, int prix, String description) {
		this.pseudo = pseudo;
		this.categorie = categorie;
		this.prix = prix;
		this.description = description;
	}

	/*@Override
	public String toString() {
		return "pseudo: " + pseudo
		+ ", categorie: " + categorie
		+ ", prix: " + prix
		+ ", description:" + description;
	}*/

	public String show(int id) {
		return "Annonce numéro " + id
		+ " de " + pseudo
		+ " du domaine " + categorie
		+ " au prix de " + prix + " euro(s)."
		+ " Descriptif : " + description;
	}
}