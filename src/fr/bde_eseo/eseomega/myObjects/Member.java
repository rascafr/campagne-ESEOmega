package fr.bde_eseo.eseomega.myObjects;

/**
 * Classe object pour les membres de la liste BDE.
 * Paramètres : Nom, Description, chemin d'accès de l'image (référence : dossier assets)
 * @author François
 *
 */
public class Member {
	
	private String name;
	private String description;
	private String img_path;
	
	// Constructeur vide
	public Member () {
		name = "Inconnu";
		description = "Fonction non connue";
		img_path = "";				
	}
	
	// Constructeur complet
	public Member (String sName, String sDescription, String sPath) {
		name = sName;
		description = sDescription;
		img_path = sPath;
	}
	
	// Constructeur module
	public Member (String sName) {
		name = sName;
	}
	
	// Getteurs
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getImgPath() {
		return img_path;
	}
	
	// Setteurs
	public void setName (String sName) {
		name = sName;
	}
	
	public void setUrl (String sDescription) {
		description = sDescription;
	}
	
	public void setImgPath (String sPath) {
		img_path = sPath;
	}
}
