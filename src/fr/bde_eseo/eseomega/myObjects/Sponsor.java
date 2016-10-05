package fr.bde_eseo.eseomega.myObjects;

/**
 * Classe object pour les sponsors
 * Paramètres : Nom, Lien vers le site internet (URL), chemin d'accès de l'image (référence : dossier assets)
 * @author François
 *
 */
public class Sponsor {
	
	private String name;
	private String url;
	private String img_path;
	
	// Constructeur vide
	public Sponsor () {
		name = "Erreur";
		url = "www.eseo.fr";
		img_path = "";				
	}
	
	// Constructeur complet
	public Sponsor (String sName, String sUrl, String sPath) {
		name = sName;
		url = sUrl;
		img_path = sPath;
	}
	
	// Getteurs
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getImgPath() {
		return img_path;
	}
	
	// Setteurs
	public void setName (String sName) {
		name = sName;
	}
	
	public void setUrl (String sUrl) {
		url = sUrl;
	}
	
	public void setImgPath (String sPath) {
		img_path = sPath;
	}

}
