package fr.bde_eseo.eseomega.myObjects;

import fr.bde_eseo.eseomega.R;

/**
 * Classe object pour les challenges
 * Paramètres : nom, requis, points
 * @author François
 *
 */
public class ChallengeObject {
	
	private String name;
	private String rules;
	private String points;
	private boolean isDone;
	private int color;
	
	public final static int FLAT_BLUE = R.drawable.circle_challenge_blue;
	public final static int FLAT_RED = R.drawable.circle_challenge_red;
	public final static int FLAT_VIOLET = R.drawable.circle_challenge_violet;
	public final static int FLAT_CYAN = R.drawable.circle_challenge_cyan;
	public final static int FLAT_ORANGE = R.drawable.circle_challenge_orange;
	public final static int FLAT_PINK = R.drawable.circle_challenge_pink;
	public final static int FLAT_GREEN = R.drawable.circle_challenge_green;
	
	public final static int [] FLAT_COLORS = {FLAT_BLUE, FLAT_RED, FLAT_VIOLET, FLAT_CYAN, FLAT_ORANGE, FLAT_PINK, FLAT_GREEN};
	
	// Constructeur vide
	public ChallengeObject () {
		// nothing's here
	}
	
	// Constructeur de catégorie
	public ChallengeObject (String name) {
		this.name = name;
	}
	
	// Constructeur normal
	public ChallengeObject (String sName, String sRules, String sPoints, int color) {
		name = sName;
		rules = sRules;
		points = sPoints;
		this.isDone = false;
		this.color = color;
	}
	
	// Constructeur étendu
	public ChallengeObject (String sName, String sRules, String sPoints, int color, boolean isDone) {
		name = sName;
		rules = sRules;
		points = sPoints;
		this.isDone = isDone;
		this.color = color;
	}
	
	
	// Getteurs
	public String getName() {
		return name;
	}
	
	public String getRules() {
		return rules;
	}
	
	public String getPoints() {
		return points;
	}
	
	public boolean getAchievement() {
		return this.isDone;
	}
	
	public int getColor() {
		return this.color;
	}
	
	// Setteurs
	public void setName (String sName) {
		name = sName;
	}
	
	public void setDescription (String sRules) {
		rules = sRules;
	}
	
	public void setPlace (String sPoints) {
		points = sPoints;
	}
	
	public void setAchievement(boolean isDone) {
		this.isDone = isDone;
	}
	
	public void setColor (int color) {
		this.color = color;
	}
	
	// + add method to convert points string to int
	
}
