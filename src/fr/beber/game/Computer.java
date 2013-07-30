package fr.beber.game;

import java.util.List;
import java.util.Random;

import net.knowledgecommunity.game.R;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Computer extends Player{
	
	//Information de debug 
	private final String TAG = "BeBer - Computer";
	
	private int position; //définition de la position, 0 si défense et 1 si attaque
	private int[][] NBValCartes = new int[5][7]; //compteur de carte en main
	private TextView textViewNom = null;
	private TextView textViewDecision = null;
	
	//POINT
	public int coeur=0;
	public double ptCoeur=0;
	public int pique=0;
	public double ptPique=0;
	public int trefle=0;
	public double ptTrefle=0;
	public int carreau=0;
	public double ptCarreau=0;
	
	public Computer(String mName,TextView tVC,TextView tVD){
		super(mName);
		this.textViewNom = tVC;
		this.textViewDecision = tVD;

    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    	this.textViewDecision.setLayoutParams(layoutParams);
    	
    	zoneVisible();
				
		textViewNom.setText(getName());
	}
	
	public void zoneVisible(){
		LinearLayout parent = (LinearLayout)textViewNom.getParent();
		parent.setVisibility(ViewGroup.VISIBLE);
	}
	
	public void zoneInvisible(){
		LinearLayout parent = (LinearLayout)textViewNom.getParent();
		parent.setVisibility(ViewGroup.INVISIBLE);
	}
	
	/**
	 * Détermine le choix que devra faire l'ordinateur afin qu'il choississe une donne
	 */
	public int meilleurChoixDonne(int mode,int decisionEnCours){
		
		double scoreMain = 0;
		int i,j;
		
		NBValCartes[0][0]=0; //Cartes qui vale rien cad 1-10 et 2-6 en atout
		NBValCartes[0][1]=0; //Valet
		NBValCartes[0][2]=0; //Cavalier
		NBValCartes[0][3]=0; //Dame
		NBValCartes[0][4]=0; //Roi
		NBValCartes[0][5]=0; //Atout fort 7-20
		NBValCartes[0][6]=0; //Bout
		
		//INIT COEUR
		NBValCartes[1][1]=0; //Valet
		NBValCartes[1][2]=0; //Cavalier
		NBValCartes[1][3]=0; //Dame
		NBValCartes[1][4]=0; //Roi

		//INIT CARREAU
		NBValCartes[2][1]=0; //Valet
		NBValCartes[2][2]=0; //Cavalier
		NBValCartes[2][3]=0; //Dame
		NBValCartes[2][4]=0; //Roi

		//INIT TREFLE
		NBValCartes[3][1]=0; //Valet
		NBValCartes[3][2]=0; //Cavalier
		NBValCartes[3][3]=0; //Dame
		NBValCartes[3][4]=0; //Roi

		//INIT PIQUE
		NBValCartes[4][1]=0; //Valet
		NBValCartes[4][2]=0; //Cavalier
		NBValCartes[4][3]=0; //Dame
		NBValCartes[4][4]=0; //Roi
		
		if(cartes.size()>0){
			
			//On compte le nombre de points de sa main et le nombre d'atout
			for(Carte carte : cartes){
				
				
				//Je regarde si je me trouve dans des atouts
				if(carte.getNumImage()>=500){
					
					if(carte.getNumImage()<507 && carte.getNumImage()>501){//Atout faible
						NBValCartes[0][0]++;
					}
					else if(carte.getNumImage()>=507 && carte.getNumImage()<521){//Atout fort
						NBValCartes[0][5]++;
					}
					else {//Je suis dans le cas d'un bout
						NBValCartes[0][6]++;
					}
					
				}
				else {
					
					i=1;
					j=0;//Vérifie si on trouve une valeur
					
					//je parcours les types de cartes (coeur,carreau,pique trefle)
					while(j==0 && i<5){
						
						if(carte.getNumImage()<=(i*100+10)){//Cartes faibles
							NBValCartes[0][0]++;
							j=1;
						}
						else {
							
							if(carte.getNumImage()<=(i*100+11)){//je suis un valet
								NBValCartes[0][1]++;
								NBValCartes[i][1]++;
								j=1;
							}
							else if(carte.getNumImage()<=(i*100+12)){//je suis un cavalier
								NBValCartes[0][2]++;
								NBValCartes[i][2]++;
								j=1;
							}
							else if(carte.getNumImage()<=(i*100+13)){//je suis une dame
								NBValCartes[0][3]++;
								NBValCartes[i][3]++;
								j=1;
							}
							else if(carte.getNumImage()<=(i*100+14)){//je suis un roi
								NBValCartes[0][4]++;
								NBValCartes[i][4]++;
								j=1;
							}
							
						}
						
						i++;
						
					}
				}
				
				//Addition le nombre de point
				scoreMain += carte.getPoint();
			}
			

			if(WelcomeActivity.DEBUG)Log.i(TAG,getName()+" le nombre en faible :"+NBValCartes[0][0]+" le nombre valet "+NBValCartes[0][1]+
					" le nombre Cavalier "+NBValCartes[0][2]+" le nombre Dame "+NBValCartes[0][3]+
					" le nombre Roi "+NBValCartes[0][4]+" le nombre Atout Fort "+NBValCartes[0][5]+
					" le nombre bout "+NBValCartes[0][6]+" scoreMain "+String.valueOf(scoreMain));
			
			if(WelcomeActivity.DEBUG)Log.d(TAG,"COEUR le nombre valet "+NBValCartes[1][1]+
					" le nombre Cavalier "+NBValCartes[1][2]+" le nombre Dame "+NBValCartes[1][3]+
					" le nombre Roi "+NBValCartes[1][4]);

			if(WelcomeActivity.DEBUG)Log.d(TAG,"CARREAU le nombre valet "+NBValCartes[2][1]+
					" le nombre Cavalier "+NBValCartes[2][2]+" le nombre Dame "+NBValCartes[2][3]+
					" le nombre Roi "+NBValCartes[2][4]);

			if(WelcomeActivity.DEBUG)Log.d(TAG,"TREFLE le nombre valet "+NBValCartes[3][1]+
					" le nombre Cavalier "+NBValCartes[3][2]+" le nombre Dame "+NBValCartes[3][3]+
					" le nombre Roi "+NBValCartes[3][4]);

			if(WelcomeActivity.DEBUG)Log.d(TAG,"PIQUE le nombre valet "+NBValCartes[4][1]+
					" le nombre Cavalier "+NBValCartes[4][2]+" le nombre Dame "+NBValCartes[4][3]+
					" le nombre Roi "+NBValCartes[4][4]);
			
			
		}
		else
			Log.e(TAG,"Erreur l'ordinateur n'a pas de cartes en main");

		return pourcentageChoixFinal(scoreMain,decisionEnCours);
	}
	
	/**
	 * Va permettre de définir quel carte doit jouer l'ordi
	 * @param idTourCarte
	 * @return
	 */
	public Carte choixCarte(List<Carte> cartesJouees){
		
		int i =0;
		int max = cartesJouees.size();
		Carte atoutFort = new Carte("erreur", R.drawable.none, 0, 0);
		Carte petitAtout= new Carte("erreur", R.drawable.none, 600, 0);
		Carte petitCartes= new Carte("erreur", R.drawable.none, 600, 0);
		
		if(max>0)
		{
			//On regarde si cela a été coupé
			for(Carte carte: cartesJouees){
				if(carte.getNumImage()>=500 && atoutFort.getNumImage()<carte.getNumImage())
					atoutFort=carte;
			}
			
			while(i<this.cartes.size()){
				if(Math.floor(this.cartes.get(i).getNumImage()/100)==Math.floor(cartesJouees.get(0).getNumImage()/100) && this.cartes.get(i).getNumImage()>cartesJouees.get(0).getNumImage()){
					return this.cartes.get(i);
				}
				else if(Math.floor(this.cartes.get(i).getNumImage()/100)==5 && petitAtout.getNumImage()>this.cartes.get(i).getNumImage()){
					if(petitAtout.getNumImage()==600 || (atoutFort.getNumImage()!=0 && atoutFort.getNumImage()<this.cartes.get(i).getNumImage()) || 
							(atoutFort.getNumImage()!=0 && petitAtout.getNumImage()<atoutFort.getNumImage())){
						petitAtout=this.cartes.get(i);
					}
				}
				else if(petitCartes.getNumImage()>this.cartes.get(i).getNumImage()){
					petitCartes=this.cartes.get(i);
				}
				i++;
			}
			if(petitAtout.getNumImage()!=600){
				return petitAtout;
			}
			else {
				return petitCartes;
			}
		}
		else{
			Random r = new Random();
			
			return this.cartes.get(r.nextInt(this.cartes.size()-1));
		}
	}
	
	/** 
	 * Détermine la decision en fonction du score
	 * @param score
	 * @param nbBout
	 * @return
	 */
	private int pourcentageChoixFinal(double score,int decisionEnCours){
		
		int nbBout = NBValCartes[0][6];
		int decision = 0;//au départ je passe
		int scoreAtteindre = 0;
		double pourcentage = 0;
		
		if(nbBout==0){
			scoreAtteindre = 56;
		}
		else if(nbBout==1){
			scoreAtteindre = 51;
		}
		else if(nbBout==2){
			scoreAtteindre = 41;
		}
		else if(nbBout==3){
			scoreAtteindre = 36;
		}
		else
			Log.e(TAG,"Erreur nombre de bout incorrecte "+nbBout);
		
		if(scoreAtteindre!=0){
			
			pourcentage = score/(double)scoreAtteindre;
			if(nbBout>0){
				
				if(pourcentage<0.70 && pourcentage>0.5){
					decision=1;
				}
				else if(pourcentage<0.85 && pourcentage>=0.70){
					decision=2;
				}
				else if(pourcentage<0.95 && pourcentage>=0.85){
					decision=3;
				}
				else if(pourcentage>=0.95){
					decision=4;
				}
				
			}
		}
		
		if(decisionEnCours>=decision)
			decision=0;
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,getName()+" decision = "+decision+" pourcentage = "+String.valueOf(pourcentage)+" score "+score+" scoreAtteindre"+scoreAtteindre);
		
		if(decision==0)
			this.textViewDecision.setText(R.string.passe);
		else if(decision==1)
			this.textViewDecision.setText(R.string.petite);
		else if(decision==2)
			this.textViewDecision.setText(R.string.garde);
		else if(decision==3)
			this.textViewDecision.setText(R.string.gardesans);
		else if(decision==4)
			this.textViewDecision.setText(R.string.gardecontre);
		
		
		return decision;
	}
	
	/**
	 * Définition du chien par l'ordinateur
	 * 
	 */
	public void faireLeChien(List<Carte> dog){
		
	}
	
	
	
	public int getPosition() {return position;}
	public void setPosition(int position) {this.position = position;}
	public TextView getTextViewNom() {return textViewNom;}
	public void setTextViewNom(TextView textViewNom) {
		LinearLayout parent = (LinearLayout)this.textViewNom.getParent();
		if(parent.getVisibility()==ViewGroup.VISIBLE){
			parent.setVisibility(ViewGroup.INVISIBLE);
		}
		
		this.textViewNom = textViewNom;
	}

	public TextView getTextViewDecision() {return textViewDecision;}
	public void setTextViewDecision(TextView textViewDecision) {this.textViewDecision = textViewDecision;}
}
