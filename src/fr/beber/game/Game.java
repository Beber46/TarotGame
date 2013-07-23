package fr.beber.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.knowledgecommunity.game.R;

import android.util.Log;


public class Game {

	//Information de debug 
	private final String TAG = "Knowledge community - Game";
	
	private Date dateCreate; //date à laquel la partie à commencer
	private int mode; //Mode de partie : 3 ou 4 ou 5
	private int numCarteChien; //Nombre de cartes présentes dans le chien
	private int tour; //Définit le numéro du joueur qui doit jouer 0,1,2,3,4 max
	private static List<Carte> cartes = new ArrayList<Carte>(); //il s'agit de l'ensemble des cartes présentes au tarot
	private List<Carte> chienCartes = new ArrayList<Carte>();
	
	private String[] nomCarte ={
			"none",
			"Coeur",
			"Carreau",
			"Trefle",
			"Pique",
			"Atout"
	};
	private int[] drawableCarteID ={
			R.drawable.n101,
			R.drawable.n102,
			R.drawable.n103,
			R.drawable.n104,
			R.drawable.n105,
			R.drawable.n106,
			R.drawable.n107,
			R.drawable.n108,
			R.drawable.n109,
			R.drawable.n110,
			R.drawable.n111,
			R.drawable.n112,
			R.drawable.n113,
			R.drawable.n114,
			R.drawable.n301,
			R.drawable.n302,
			R.drawable.n303,
			R.drawable.n304,
			R.drawable.n305,
			R.drawable.n306,
			R.drawable.n307,
			R.drawable.n308,
			R.drawable.n309,
			R.drawable.n310,
			R.drawable.n311,
			R.drawable.n312,
			R.drawable.n313,
			R.drawable.n314,
			R.drawable.n201,
			R.drawable.n202,
			R.drawable.n203,
			R.drawable.n204,
			R.drawable.n205,
			R.drawable.n206,
			R.drawable.n207,
			R.drawable.n208,
			R.drawable.n209,
			R.drawable.n210,
			R.drawable.n211,
			R.drawable.n212,
			R.drawable.n213,
			R.drawable.n214,
			R.drawable.n401,
			R.drawable.n402,
			R.drawable.n403,
			R.drawable.n404,
			R.drawable.n405,
			R.drawable.n406,
			R.drawable.n407,
			R.drawable.n408,
			R.drawable.n409,
			R.drawable.n410,
			R.drawable.n411,
			R.drawable.n412,
			R.drawable.n413,
			R.drawable.n414,
			R.drawable.n502,
			R.drawable.n503,
			R.drawable.n504,
			R.drawable.n505,
			R.drawable.n506,
			R.drawable.n507,
			R.drawable.n508,
			R.drawable.n509,
			R.drawable.n510,
			R.drawable.n511,
			R.drawable.n512,
			R.drawable.n513,
			R.drawable.n514,
			R.drawable.n515,
			R.drawable.n516,
			R.drawable.n517,
			R.drawable.n518,
			R.drawable.n519,
			R.drawable.n520
	};
	
	/**
	 * Constructeur, définition du mode de jeux
	 * @param mode
	 */
	public Game(int mode){
		this.mode = mode;
		this.dateCreate = new Date();
		
		if(this.mode==3 || this.mode==4){ // Le chien comporte donc 6 cartes
			this.numCarteChien = 6;
		}
		else if(this.mode==5){//le chien comporte donc 3 cartes
			this.numCarteChien = 3;
		}
		else
			Log.e(TAG,"le mode de jeu n'est pas connu");
		
		
		this.tour= 0; // On définit le joueur par défaut qui commence (humain)
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,"Constructeur : mode = "+this.mode+" le chien = "+this.numCarteChien+" Date = "+this.dateCreate.toString());
	
		//Partie création des cartes
		if(cartes.size()==0)
			if(this.creationJeuxCarte()==false)
				Log.e(TAG, "Attention le nombre de cartes n'est pas ");
		
		//On mélange les cartes
		this.melangeCartes();
	}
	
	/**
	 * Cette méthode a pour but de créer l'ensemble du juex de cartes pour pouvoir jouer
	 * @return true si c'est bon sinon false
	 */
	private boolean creationJeuxCarte(){
		
		Carte maCarte; //Permet de créer une carte
		int nbCartes = 0; //compte le nombre de cartes créées
		int numCarte = 1;
		int tour = 1;
		
		
		for(int idCarte : drawableCarteID){
			
			if(tour<5)//Pour l'ensemble des cartes que je crée autre que atout
			{
				if(numCarte<=10)
					maCarte = new Carte(numCarte+" "+nomCarte[tour], idCarte, (tour*100+numCarte), 0.5);
				else if(numCarte==11)
					maCarte = new Carte("Valet "+nomCarte[tour], idCarte, (tour*100+numCarte), 1.5);
				else if(numCarte==12)
					maCarte = new Carte("Cavalier "+nomCarte[tour], idCarte, (tour*100+numCarte), 2.5);
				else if(numCarte==13)
					maCarte = new Carte("Dame "+nomCarte[tour], idCarte, (tour*100+numCarte), 3.5);
				else 
					maCarte = new Carte("Roi "+nomCarte[tour], idCarte, (tour*100+numCarte), 4.5);
				
				if(WelcomeActivity.DEBUG)Log.d(TAG,"Creation de la carte "+maCarte.getName()+" numCart = "+maCarte.getNumImage());
				numCarte++;
				if(numCarte>14){
					numCarte=1;
					tour++;
				}
			}
			else{
				numCarte++;
				
				maCarte = new Carte(numCarte+" "+nomCarte[tour], idCarte, (tour*100+numCarte), 0.5);
				if(WelcomeActivity.DEBUG)Log.d(TAG,"Creation de la carte "+maCarte.getName()+" numCart = "+maCarte.getNumImage());
			}

			this.addCarte(maCarte);
			
			nbCartes++;
		}
		
		//Ajout du petit
		maCarte = new Carte("Petit ", R.drawable.petit, 501, 4.5);
		this.addCarte(maCarte);
		nbCartes++;
		
		//Ajout de l'excuse
		maCarte = new Carte("Excuse ", R.drawable.excuse, 500, 4.5);
		this.addCarte(maCarte);
		nbCartes++;
		
		//Ajout deu 21
		maCarte = new Carte("21 ", R.drawable.vingtun, 521, 4.5);
		this.addCarte(maCarte);
		nbCartes++;
			
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,"Creation du jeux de cartes : nombre de cartes creees = "+Game.cartes.size());
		if(Game.cartes.size()==nbCartes){
			return true;
		}
		
		return false;
	} 
	
	/**
	 * Cette méthode permet de mélanger les cartes
	 */
	public void melangeCartes(){
		Collections.shuffle(Game.cartes);
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,"Mélange les cartes");
	}
	
	public Date getDateCreate() {return dateCreate;}
	public void setDateCreate(Date dateCreate) {this.dateCreate = dateCreate;}
	public int getMode() {return mode;}
	public void setMode(int mode) {this.mode = mode;}
	public int getNumCarteChien() {return numCarteChien;}
	public void setNumCarteChien(int numCarteChien) {this.numCarteChien = numCarteChien;}
	public int getTour() {return tour;}
	public void setTour(int tour) {this.tour = tour;}
	
	public List<Carte> getCartes() {return cartes;}
	public void setCartes(List<Carte> cartes) {Game.cartes = cartes;}
	private void addCarte(Carte var){ Game.cartes.add(var);}
	public Carte getCarte(int index){ return Game.cartes.get(index);}
	
	public List<Carte> getChienCartes() {return chienCartes;}
	public void setChienCartes(List<Carte> chienCartes) {this.chienCartes = chienCartes;}
	public void addChienCartes(Carte var){ this.chienCartes.add(var);}
	public void clearChienCartes(){ this.chienCartes.clear();}
	public int nbChienCartes(){ return this.chienCartes.size();}
}
