package fr.beber.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

public class Player{

	//variable global
	public static final int ATTAQUANT = 0;
	public static final int DEFENSEUR = 1;
	
	//Information de debug 
	private final String TAG = "BeBer - Player";
	
	protected String name; //Il s'agit du nom du joueur
	protected int decision; //détermine la décision en cours
	protected List<Carte> cartes = new ArrayList<Carte>(); //Il s'agit des cartes qu'il possède en main
	private int type; //attaquant ou defenseur

	/**
	 * Constructeur de Player, on définit son nom
	 * @param mName
	 */
	public Player(String mName){
		this.name = mName;
		this.decision = -1;
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,"Constrcuteur : name = "+this.name+" décision = "+this.decision);
	}
	
	public void showCards(){
		
		String message ="";
		for(Carte carte :cartes){
			message +=" "+carte.getNumImage();
		}
		
		Log.d(TAG,"Show cards "+name+" "+message);
	}
	
	/**
	 * Cette méthode va permettre trier les cartes
	 */
	public void triCarte(){
		Collections.sort(cartes,new MonCompare());
	}
	
	/**
	 * Cette méthode va me permettre de comparer 2 éléments
	 * @author Knowlegde
	 *
	 */
	private class MonCompare implements Comparator<Carte>{

		public int compare(Carte A, Carte B) {
			if(A.getNumImage()>B.getNumImage()){
				return -1;
			}
			else
				return 0;
		}
		
	}
	
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public List<Carte> getCartes(){ return cartes;}
	public void setCartes(List<Carte> cartes){ this.cartes = cartes;}
	public void clearAll(){ this.cartes.clear(); this.decision = -1; if(WelcomeActivity.DEBUG)Log.w(TAG,"this.cartes "+this.cartes.size());}
	public void addCarte(Carte var){ this.cartes.add(var);}
	public void removeCarte(Carte var){ 
		this.cartes.remove(var);
	}
	public int nbCartes(){return this.cartes.size();}
	public int getDecision() {return decision;}
	public void setDecision(int decision) {this.decision = decision;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
}
