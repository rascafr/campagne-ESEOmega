package fr.bde_eseo.eseomega.myObjects;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;


/**
 * Classe object pour les animations
 * Paramètres : nom de l'évenement, description, lieu, date.
 * @author François
 *
 */
public class AnimObject {
	
	private Calendar calendar;
	private String name;
	private String description;
	private String place;
	private int color;
	public String weekdays[] = new DateFormatSymbols(Locale.FRANCE).getWeekdays();
	
	// Constructeur vide
	public AnimObject () {
		// nothing's here
	}
	
	// Constructeur avec juste la date (pour le header)
	public AnimObject (Calendar sCalendar) {
		calendar = sCalendar;
	}
	
	// Constructeur complet
	public AnimObject (String sName, String sDescription, String sPlace, Calendar sCalendar) {
		name = sName;
		description = sDescription;
		place = sPlace;
		calendar = sCalendar;
		color = 0x303740;
	}
	
	// Constructeur avec couleur
	public AnimObject (String sName, String sDescription, String sPlace, Calendar sCalendar, int sColor) {
		name = sName;
		description = sDescription;
		place = sPlace;
		calendar = sCalendar;
		color = sColor;
	}
	
	// Getteurs
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPlace() {
		return place;
	}
	
	public Calendar getCalendar() {
		return calendar;
	}
	
	public int getColor () {
		return color;
	}
	
	// Setteurs
	public void setName (String sName) {
		name = sName;
	}
	
	public void setDescription (String sDescription) {
		description = sDescription;
	}
	
	public void setPlace (String sPlace) {
		place = sPlace;
	}
	
	public void setCalendar (Calendar sCalendar) {
		calendar = sCalendar;
	}
	
	public void setColor (int sColor) {
		color = sColor;
	}
	
	/** Calendar utils **/
	public String getDayNameAsString() {
		
		return dayNumToString(calendar.get(Calendar.DAY_OF_WEEK)) + " " + calendar.get(Calendar.DAY_OF_MONTH) + ((calendar.get(Calendar.DAY_OF_MONTH)==1)?"er":"");
	}
	
	public String getDetails() {
		return dayNumToString(calendar.get(Calendar.DAY_OF_WEEK)) + " " + 
			   calendar.get(Calendar.DAY_OF_MONTH) + ((calendar.get(Calendar.DAY_OF_MONTH)==1)?"er":"") + " " + 
			   monthNumToString(calendar.get(Calendar.MONTH)) + " à " + calendar.get(Calendar.HOUR_OF_DAY) + 
			   "h" + calendar.get(Calendar.MINUTE) + ((calendar.get(Calendar.MINUTE)==0)?"0":"");
	}
	
	public String dayNumToString(int dayiD) {
		
		String dayName = "???";
		
		switch(dayiD) {
		
			case 1:
				dayName = "Dimanche";
				break;
			case 2:
				dayName = "Lundi";
				break;
			case 3:
				dayName = "Mardi";
				break;
			case 4:
				dayName = "Mercredi";
				break;
			case 5:
				dayName = "Jeudi";
				break;
			case 6:
				dayName = "Vendredi";
				break;
			case 7:
				dayName = "Samedi";
				break;
		}
		
		return dayName;
	}
	
	public String monthNumToString(int monthiD) {
		
		String monthName = "???";
		
		switch(monthiD) {
		
			case 0:
				monthName = "Janvier";
				break;
			case 1:
				monthName = "Février";
				break;
			case 2:
				monthName = "Mars";
				break;
			case 3:
				monthName = "Avril";
				break;
			case 4:
				monthName = "Mai";
				break;
			case 5:
				monthName = "Juin";
				break;
			case 6:
				monthName = "Juillet";
				break;
			case 7:
				monthName = "Août";
				break;
			case 8:
				monthName = "Septembre";
				break;
			case 9:
				monthName = "Octobre";
				break;
			case 10:
				monthName = "Novembre";
				break;
			case 11:
				monthName = "Décembre";
				break;
		}
		
		return monthName;
	}
}
