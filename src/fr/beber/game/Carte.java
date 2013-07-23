package fr.beber.game;

import android.util.Log;

public class Carte {

	//Information de debug 
	private final String TAG = "Knowledge community - Carte";
	
	private String name; //Nom de la carte
	private int idImage; //id de l'image dans le drawable
	private int numImage; // s'il s'agit du coeur trefle pique...
	private boolean status; //si la carte a été joué ou pas
	private double point; //Attribution de point par carte

	/**
	 * Constructeur de la classe, nécessite un nom de carte, une image et une catégorie.
	 * Paar défaut son status est à faux ce qu'il signifie que la carte n'a pass été joué
	 * 
	 * @param mName,image,categorie
	 */
	public Carte(String mName,int image,int numImage,double point){
		this.name = mName;
		this.idImage = image;
		this.numImage = numImage;
		this.status = false;
		this.point = point;
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,"Constructeur : carte = "+this.name+" numImage = "+this.numImage+" point = "+this.point);
	}
	
	
	public String getName() {return name;}
	public void setName(String var) {this.name = var;}
	public int getIdImage() {return idImage;}
	public void setIdImage(int idImage) {this.idImage = idImage;}
	public int getNumImage() {return numImage;}
	public void setNumImage(int numImage) {this.numImage = numImage;}
	public boolean isStatus() {return status;}
	public void setStatus(boolean status) {this.status = status;}
	public double getPoint() {return point;}
	public void setPoint(double point) {this.point = point;}
	
	
}
