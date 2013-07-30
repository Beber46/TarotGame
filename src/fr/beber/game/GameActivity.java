package fr.beber.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

public class GameActivity extends Activity implements OnClickListener{

	//Variable Static
	private static final String PASSE = "Passe";
	private static final String PETITE = "Petite";
	private static final String GARDE = "Garde";
	private static final String GARDESANS = "Garde Sans";
	private static final String GARDECONTRE = "Garde Contre";
	
	//Information de debug 
	private final String TAG = "BeBer - GameActivity";
		
	private Game mGame = null;
	private List<Player> mTabPlayer;
	private Button mChoix;
	private int mTourJoueur; //Cette variable va permettre de savoir qui est en train de jouer
	private int mTourDepart; //Cette variable va permettre de savoir qui doit commencer
	private int mGagnantTour=-1;
	private int mNBTour; //Détermine si tous les player ont pris une décision
	private boolean mJoueurPrend;//reseigne si le joueur a pris ou pas
	private boolean mJoueurPrendChien;//reseigne si le joueur a pris ou pas pour le chien
	
	private boolean mSurEncherePossible;
	
	private List<Carte> mCartesDefense = new ArrayList<Carte>();
	private List<Carte> mCartesAttaque = new ArrayList<Carte>();
	private List<Carte> mCartesTapis = new ArrayList<Carte>();
	
	private int mDecisionEnCours = 0;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de onCreate");
        
        Intent mIntent = getIntent();
        
        //Création de la partie
        mGame = new Game(mIntent.getIntExtra("nbjoueur",0));

        //Récuperation du bouton choix pour prendre un décision
        
        //mChoix
        mChoix = (Button)findViewById(R.id.but_choix);
        
        //Initialisation de la partie
        initGame();
        
        //On établie la liste de choix possible
        mChoix.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(GameActivity.this)
                .setTitle(R.string.title_choix_donne)
                .setItems(R.array.choix_donne,
	                new DialogInterface.OnClickListener() 
	                {
		                public void onClick(DialogInterface dialoginterface,int i) 
		                {
		                	mHandler.sendEmptyMessage(i);
		            		mChoix.setVisibility(ViewGroup.INVISIBLE);
		                }
	                })
                .show();
			}
		});
        
        
    }
    
    /**
     * Cette fonction permet d'intialiser les joueurs ainsi que leurs jeux
     */
    private void initGame(){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de initGame");
        //Le joueur de départ est 0 et init le tour
        mTourJoueur = 0;
        mTourDepart = 0;
        mNBTour = 0;
        
        //init des tableaux pour la partie
        mTabPlayer = new ArrayList<Player>();
        
        //Création du joueur Humain
        mTabPlayer.add(new Player("Beber46"));
        
        //Créatino des joueurs 
        if(mGame.getMode()==3){
	        mTabPlayer.add(new Computer("Ordinateur 1",(TextView)findViewById(R.id.txtV_PlayerRigth),(TextView)findViewById(R.id.txtV_DecisionRigth)));
	        mTabPlayer.add(new Computer("Ordinateur 2",(TextView)findViewById(R.id.txtV_PlayerLeft),(TextView)findViewById(R.id.txtV_DecisionLeft)));
        }
        else if(mGame.getMode()==4){
	        mTabPlayer.add(new Computer("Ordinateur 1",(TextView)findViewById(R.id.txtV_PlayerRigth),(TextView)findViewById(R.id.txtV_DecisionRigth)));
	        mTabPlayer.add(new Computer("Ordinateur 2",(TextView)findViewById(R.id.txtV_PlayerCenter),(TextView)findViewById(R.id.txtV_DecisionCenter)));
            mTabPlayer.add(new Computer("Ordinateur 3",(TextView)findViewById(R.id.txtV_PlayerLeft),(TextView)findViewById(R.id.txtV_DecisionLeft)));
        }
        else if(mGame.getMode()==5){
	        mTabPlayer.add(new Computer("Ordinateur 1",(TextView)findViewById(R.id.txtV_PlayerRigth),(TextView)findViewById(R.id.txtV_DecisionRigth)));
	        mTabPlayer.add(new Computer("Ordinateur 2",(TextView)findViewById(R.id.txtV_PlayerCenterRigth),(TextView)findViewById(R.id.txtV_DecisionCenterRigth)));
            mTabPlayer.add(new Computer("Ordinateur 3",(TextView)findViewById(R.id.txtV_PlayerCenterLeft),(TextView)findViewById(R.id.txtV_DecisionCenterLeft)));
            mTabPlayer.add(new Computer("Ordinateur 4",(TextView)findViewById(R.id.txtV_PlayerLeft),(TextView)findViewById(R.id.txtV_DecisionLeft)));
        }
        
        reStart();
    }
    
    /**
     * Relance la partie avec les init nécessaire
     */
    private void reStart(){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de restart");
    	//On mélanges les cartes
        mGame.melangeCartes();
        
        //je recrée les cartes des attaquants et des deffenseurs
        mCartesAttaque = new ArrayList<Carte>();
        mCartesDefense = new ArrayList<Carte>();
        mCartesTapis = new ArrayList<Carte>();
        
        //Efface les décisions des joueurs
        for(int i=1;i<mTabPlayer.size();i++){
        	((Computer)mTabPlayer.get(i)).getTextViewDecision().setText("");
        }

        //On réalise une distribution
        distribution();
        
        devoileLesCartesJoueur(0);
        
        mJoueurPrend = false;
        mJoueurPrendChien = false;
        mSurEncherePossible =false;
		mChoix.setVisibility(ViewGroup.VISIBLE);
		mDecisionEnCours=-1;
		mGagnantTour=-1;
    }

    /**
     * Cette fonction distribue l'ensemble des cartes au joueur
     */
    private void distribution(){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de distribution");
    	int i =0;
    	int maxCartes = mGame.getCartes().size();
    	Random randomGenerator = new Random();
    	
    	//On efface le chien
    	mGame.clearChienCartes();
    	
    	//J'efface ces cartes
    	for(Player joueur : mTabPlayer)
		{
    		joueur.clearAll();
		}
    	
    	if(mGame!=null){
    		
    		//Distribution des 78 cartes avec la création de chien
    		while(i<maxCartes){
    			
    			for(Player joueur : mTabPlayer)
    			{
	    			//Distribution pour le chien
	    			if(i>3 && i<(maxCartes-3) && mGame.getChienCartes().size()<mGame.getNumCarteChien())
	    			{
	    				//je me trouve dans le cas ou il s'agit ni des 3 premières cartes ni des 3 dernières et que le chien n'est pas complet
	    				//Je regarde si j'ai suffisament de cartes pour constituer un chien
	    				if(i>=(maxCartes-(3+(mGame.getNumCarteChien()-mGame.getChienCartes().size())))){//dans ce cas je l'ajoute 
	    					mGame.addChienCartes(mGame.getCarte(i));
	    					i++;
	    				}
	    				else if((randomGenerator.nextInt(10)%2)==0){
	    					mGame.addChienCartes(mGame.getCarte(i));
	    					i++;
	    				}
	    			}
	    			
	    			for(int j = 0 ;j<3;j++)
    				{
	    				if(i<maxCartes){
	    					joueur.addCarte(mGame.getCarte(i));
	    					i++;
	    				}
    				}
    			}
    			
    			
    		}
    		
    		if(WelcomeActivity.DEBUG){
    			Log.i(TAG,"Cartes distribuer : Joueur 1 = "+mTabPlayer.get(0).nbCartes()+" Joueur 2 = "+mTabPlayer.get(1).nbCartes()+" Joueur 3 = "+mTabPlayer.get(2).nbCartes());
    			if(mGame.getMode()>=4)
    				Log.i(TAG,"Cartes distribuer : Joueur 4 = "+mTabPlayer.get(3).nbCartes());
    			if(mGame.getMode()==5)
    				Log.i(TAG,"Cartes distribuer : Joueur 5 = "+mTabPlayer.get(4).nbCartes());
    			
    			Log.i(TAG,"Le chien comporte = "+mGame.getChienCartes().size()+" pour maxCartes = "+maxCartes);
    		}
    		
    	}
    	else
    		Log.e(TAG,"Le jeu n'existe pas voir pourquoi mGame = null");
    		
    }
    
    /**
     * Cette méthode permet d'afficher les cartes du joueur
     */
    private void devoileLesCartesJoueur(int marginBottom){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de devoileLesCartesJoueur");
    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100,ViewGroup.LayoutParams.WRAP_CONTENT );
		layoutParams.setMargins(0, 0, 0, marginBottom);
		LinearLayout contenueDeLaMain = (LinearLayout) findViewById(R.id.LinerLayoutContentCards);
		contenueDeLaMain.removeAllViews();
    	int i =0;
    	//Je parcours les cartes du joueur et je les place dans mon layout
    	for(Carte addCard : mTabPlayer.get(0).getCartes()){
    		
    		ImageView ICarte = new ImageView(this);
    		ICarte.setImageResource(addCard.getIdImage());
    		ICarte.setLayoutParams(layoutParams);
    		ICarte.setId(addCard.getNumImage());
    		
    		ICarte.setOnClickListener(this);
    		
    		contenueDeLaMain.addView(ICarte);
    		i++;
    	}

    	if(WelcomeActivity.DEBUG)Log.i(TAG,"Nombre d'image afficher i = "+i);
    }
    
    
    
    /**
     * Cette méthode va permettre de déterminer le choix du joueur pour le tour en cours
     * @param value
     */
    private void actionDonne(int value){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de actionDonne");
    	//Je donne la décision pour le joueur
    	mTabPlayer.get(mTourJoueur).setDecision(value);
    	
    	//donne au joueur suivant la donne
    	mTourJoueur++;
    	if(mTourJoueur>=mTabPlayer.size())
    	{
    		mTourJoueur=0;
    	}
    	else if(mTourJoueur!=0){
    		
    		//je vérifie qu'il s'agisse d'un computer
    		if(mTabPlayer.get(mTourJoueur) instanceof Computer){
        		Computer mComputer = (Computer)mTabPlayer.get(mTourJoueur);
        		
    			afficheDecision(mComputer.getName(),mComputer.meilleurChoixDonne(mGame.getMode(),mDecisionEnCours));
    		}
    	}
    	
    	if(mNBTour>=mTabPlayer.size()){
    		
    		mNBTour=0;
    		
    		if(mDecisionEnCours<=0 ){//aucune décision
    			reStart();
    		}
    		else {
    			if(WelcomeActivity.DEBUG)Log.i(TAG,"mDecisionEnCours = "+mDecisionEnCours);
        		//je vérifie si le joueur à une enchère et lui propose un autre choix si ce n'est pas lui qui a décidé
        		if(mSurEncherePossible && mTabPlayer.get(0).getDecision()!=mDecisionEnCours && mDecisionEnCours!=4)
        			surEnchere();
        		else{
        			definitionDesRoles();
        		}
    		}
    	}
    	
    }
    
    /**
     * Cette méthode permet de définir les roles de chaque joueur de la partie
     */
    private void definitionDesRoles(){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de definitionDesRoles");
		//Je défini s'il s'agit d'un attaquant ou défenseur
		for(Player p : mTabPlayer){
			if(mDecisionEnCours==p.getDecision()){
				p.setType(Player.ATTAQUANT);
				if(p instanceof Computer){
					if(p.getDecision()==1)
	    				((Computer)p).getTextViewDecision().setText(PETITE);
					else if(p.getDecision()==2)
	    				((Computer)p).getTextViewDecision().setText(GARDE);
					else if(p.getDecision()==3)
	    				((Computer)p).getTextViewDecision().setText(GARDESANS);
					else if(p.getDecision()==4)
	    				((Computer)p).getTextViewDecision().setText(GARDECONTRE);
    			}
				else{
					mJoueurPrend = true;
					mJoueurPrendChien = true;
				}
			}
			else{
				p.setType(Player.DEFENSEUR);
				if(p instanceof Computer){
    				((Computer)p).getTextViewDecision().setText("Défenseur");
    				if(mJoueurPrend==true)
    					((Computer)p).zoneInvisible();
    			}
			}
			
		}
		
		//On affiche le chien
		if(mDecisionEnCours<3)
			afficheChien("Découverte du Chien :",false);
		
		//Je trie les cartes
		mTabPlayer.get(0).triCarte();
		//Je les reaffiches
		devoileLesCartesJoueur(0);
    }
    
    /**
     * Affiche la décision des joueurs
     */
    private void afficheDecision(String name,final int decision){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de afficheDecision");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("Choix de "+name);
		builder.setInverseBackgroundForced(true);
		if(decision==0)
			builder.setMessage(PASSE);
		else if(decision==1)
			builder.setMessage(PETITE);
		else if(decision==2)
			builder.setMessage(GARDE);
		else if(decision==3)
			builder.setMessage(GARDESANS);
		else if(decision==4)
			builder.setMessage(GARDECONTRE);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int which) {
			  mHandler.sendEmptyMessage(decision);
			  dialog.dismiss();
		  }
		});
		
		AlertDialog alert = builder.create();
		alert.show();

    }
    
    /**
     * Cette fonctino a pour but d'afficher le chien lorsque la décision est prise
     */
    private void afficheChien(String title,final boolean afficheRefaire){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de affichien "+afficheRefaire);
        //on affiche notre dialogBox
		final Dialog builder = new Dialog(this);
		builder.setContentView(R.layout.dialog_chien);
		builder.setTitle(title);
		builder.setCancelable(false);
		
		LinearLayout contenueDuChien = (LinearLayout)builder.findViewById(R.id.LinearLayoutChien);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100,ViewGroup.LayoutParams.WRAP_CONTENT );
        
        if(afficheRefaire==false){
	        for(Carte addCard : mGame.getChienCartes()){
	    		
	    		ImageView ICarte = new ImageView(this);
	    		ICarte.setImageResource(addCard.getIdImage());
	    		ICarte.setLayoutParams(layoutParams);
	    		ICarte.setId(addCard.getNumImage());
	    		
	    		mTabPlayer.get(0).addCarte(addCard);//J'ajoute les cartes du chien au joueurs
	    		
	    		contenueDuChien.addView(ICarte);
	    	}
	        
        }
        else {//Affiche le chien créé par le joueur
        	for(Carte addCard : mCartesAttaque){
    		
	    		ImageView ICarte = new ImageView(this);
	    		ICarte.setImageResource(addCard.getIdImage());
	    		ICarte.setLayoutParams(layoutParams);
	    		ICarte.setId(addCard.getNumImage());
	    		
	    		contenueDuChien.addView(ICarte);
        	}
        }
        
        //set up button
        Button button = (Button) builder.findViewById(R.id.but_chienvalide);
        if(mJoueurPrendChien && afficheRefaire==false)
        	button.setText(R.string.txt_dodog);
        
        button.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) { 
        		if(afficheRefaire==true){
        			mJoueurPrendChien=false;
        			((LinearLayout)findViewById(R.id.LinearLayoutChienAjout)).setVisibility(ViewGroup.INVISIBLE);
        			for(Player p : mTabPlayer){
        				if(p instanceof Computer){
        					((Computer)p).zoneVisible();
        				}
        			}
        		}
        		else if(mJoueurPrendChien==true){
        			((LinearLayout)findViewById(R.id.LinearLayoutChienAjout)).setVisibility(ViewGroup.VISIBLE);
        		}
        		
        		builder.dismiss();
    	}});

    	Button buttonAfficheRefaire = (Button) builder.findViewById(R.id.but_chiennonvalide);
        if(afficheRefaire==true){
        	buttonAfficheRefaire.setVisibility(ViewGroup.VISIBLE);
        	buttonAfficheRefaire.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Je nettoie la zone du chien
					((LinearLayout)findViewById(R.id.LinearLayoutChienAjout)).removeAllViews();
					
					for(Carte carte : mCartesAttaque){ //Je remet en place les cartes du joueur
					  mTabPlayer.get(0).addCarte(carte);
					  if(WelcomeActivity.DEBUG)Log.w(TAG,"on rajoute "+carte.getName());
					}
				  
					mCartesAttaque = new ArrayList<Carte>();//Je reinit son bloc
					mTabPlayer.get(0).triCarte();
					devoileLesCartesJoueur(0);
					
				  
					builder.dismiss();
        	}});
        }
        else
        	buttonAfficheRefaire.setVisibility(ViewGroup.INVISIBLE);
		
		builder.show();
    }
    
    /**
     * Surenchère possible si l'utilisateur à pris un décision
     */
    private void surEnchere(){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de surEnchere");
    	int idArray;
    	
    	if(mDecisionEnCours==1)
    		idArray = R.array.choix_donne_sans_Petite;
    	else if(mDecisionEnCours==2)
    		idArray = R.array.choix_donne_sans_Garde;
    	else 
    		idArray = R.array.choix_donne_sans_GardeSans;
    	
    	new AlertDialog.Builder(GameActivity.this)
        .setTitle(R.string.title_choix_donne)
        .setItems(idArray,
            new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialoginterface,int i) 
                {
                	if(mDecisionEnCours<(i+mDecisionEnCours)){
                		
                		//je mets à "passe" la décision de tous les joueurs computer
                		for(Player p :mTabPlayer){
                			if(p instanceof Computer)
                				p.setDecision(0);
                		}
                		
                		//je change la decission du joueur HUMAIN (par défaut 0)
                    	mTabPlayer.get(0).setDecision(i);
                    	mDecisionEnCours=mDecisionEnCours+i;
                	}

                	definitionDesRoles();
                }
            })
        .show();
    	
    	mSurEncherePossible = false;
    	
    	if(WelcomeActivity.DEBUG)Log.i(TAG,"Surenchère effectuée");
    }
    
    /**
     * Définit le tour de jeu
     */
    private void actionJeu(){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de actionJeu");
    	mTourJoueur++;
    	mNBTour++;
    	if(mNBTour<mTabPlayer.size()){
	    	if(mTourJoueur<mTabPlayer.size()){
	    		mHandler.sendEmptyMessage(6);
	    	}
	    	else{ //je remonte la zone de cartes du joueur
	    		mTourJoueur=0;
	    		devoileLesCartesJoueur(0);
	    	}
    	}
    	else{ //Fin du tour

        	if(WelcomeActivity.DEBUG)Log.d(TAG+" jeu","Manche terminée");
        	
    		mNBTour=0;
    		mTourDepart=donneGagnantManche(mTourDepart);
    		mTourJoueur=mTourDepart;
    		if(mTourJoueur==mTabPlayer.size() || mTourJoueur==0){
    			//je remonte la zone de cartes du joueur
	    		mTourJoueur=0;
	    		devoileLesCartesJoueur(0);
    		}
    		else {
    			if(mTourJoueur==(mTabPlayer.size()+1)){
        			mTourJoueur=1;
    			}
    			mHandler.sendEmptyMessage(6);
    		}
    		
    		
    	}
    	
    	if(WelcomeActivity.DEBUG)Log.i(TAG+" jeu","mNBTour = "+mNBTour+" mTourJoueur = "+mTourJoueur);
    }
    /**
     * 
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * ICICICICICICICIICICICICIC
     * @param joueurDepart
     * @return
     */
    private int donneGagnantManche(int joueurDepart){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de donneGagnantManche");
    	int i,j,carteBest;
    	i=0;
    	j=0;
    	carteBest=0;

    	if(WelcomeActivity.DEBUG)Log.d(TAG+" jeu","Joueur de départ = "+mTabPlayer.get(joueurDepart).getName());
    	
    	for(Carte carte: mCartesTapis){
    		if(carte.getNumImage()>carteBest){
    			carteBest=carte.getNumImage();
    			j=i;
    		}
    		i++;
    	}

    	if(WelcomeActivity.DEBUG)Log.d(TAG+" jeu","Meilleur carte = "+mCartesTapis.get(j).getName()+" Joueur "+joueurDepart+" j"+j);
    	i=1;
    	while(i<=j){
    		joueurDepart+=1;
    		if(joueurDepart>3)
    			joueurDepart=0;
    		i++;
    	}
    	
    	mGagnantTour = mTabPlayer.get(joueurDepart).getType();

    	if(WelcomeActivity.DEBUG)Log.d(TAG+" jeu","mGagnantTour = "+mGagnantTour+" Joueur = "+mTabPlayer.get(joueurDepart).getName());
    	
    	//J'efface le tapis
		RelativeLayout contextPrincipale = (RelativeLayout)findViewById(R.id.gameParent);
		for(Carte carte : mCartesTapis){
			
			if(mGagnantTour==Player.ATTAQUANT){
				mCartesAttaque.add(carte);
			}
			else {
				mCartesDefense.add(carte);
			}
			
			ImageView mCarteRecup = (ImageView)findViewById(carte.getIdImage());
    		contextPrincipale.removeView(mCarteRecup);
		}
		
		mCartesTapis = new ArrayList<Carte>();
		
		mGagnantTour=0;
		
		return joueurDepart;
    }
    
    /**
     * Cette méthode va permettre de jouer 
     * 
     * Valeur 0: Dans ce cas le joueur passe
     * Valeur 1: Dans ce cas le joueur fait une petite
     * Valeur 2: Dans ce cas le joueur fait une garde
     * Valeur 3: Dans ce cas le joueur fait une garde sans
     * Valeur 4: Dans ce cas le joueur fait une garde contre
     */
	private final Handler mHandler = new Handler() {
    	
		@Override
        public void handleMessage(Message msg) { 
			
			switch (msg.what) {
			case 0:
				if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" passe son tour");
				//Compte le nomnre de décision
		    	mNBTour++;
				actionDonne(0);
			break;
			
			case 1:
				if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" fait une petite sur son tour");
				if(mDecisionEnCours<1){
					mDecisionEnCours=1;
					if(!(mTabPlayer.get(mTourJoueur) instanceof Computer))
						mSurEncherePossible=true;
				}
				//Compte le nomnre de décision
		    	mNBTour++;
				actionDonne(1);
			break;
			
			case 2:
				if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" fait une garde sur son tour");
				if(mDecisionEnCours<2){
					mDecisionEnCours=2;
					if(!(mTabPlayer.get(mTourJoueur) instanceof Computer))
						mSurEncherePossible=true;
				}
				//Compte le nomnre de décision
		    	mNBTour++;
				actionDonne(2);
			break;
			
			case 3:
				if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" fait une garde sans sur son tour");
				if(mDecisionEnCours<3){
					mDecisionEnCours=3;
					if(!(mTabPlayer.get(mTourJoueur) instanceof Computer))
						mSurEncherePossible=true;
				}
				//Compte le nomnre de décision
		    	mNBTour++;
				actionDonne(3);
			break;

			case 4:
				if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" fait une garde contre sur son tour");
				if(mDecisionEnCours<4){
					mDecisionEnCours=4;
					if(!(mTabPlayer.get(mTourJoueur) instanceof Computer))
						mSurEncherePossible=true;
				}
				//Compte le nomnre de décision
		    	mNBTour++;
				actionDonne(4);
			break;

			case 5://Action de jeu
				if(mNBTour!=0 || mTourJoueur==0)
					if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" joue "+mCartesTapis.get(mNBTour).getName());
				actionJeu();
			break;
			
			case 6: //Action de de réflexion
				new Thread(new Runnable() {
					
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(7);
					}
				}).start();
				
			break;
			
			case 7: //Action suite à la réflexion

				Computer c = (Computer)mTabPlayer.get(mTourJoueur);
				Carte carteReturn = c.choixCarte(mCartesTapis);
				
				if(mTourJoueur==1)
					positionTapis(carteReturn,mTourJoueur,-50,160);
				else if(mTourJoueur==2)
					positionTapis(carteReturn,mTourJoueur,0,230);
				else if(mTourJoueur==3)
					positionTapis(carteReturn,mTourJoueur,50,160);
				
				mHandler.sendEmptyMessage(5);
			break;
			default:
				if(WelcomeActivity.DEBUG)Log.i(TAG,"Le joueur "+mTabPlayer.get(mTourJoueur).getName()+" fait quelque chose i = "+msg.what);
			break;
			}
        }
    };

    /**
     * Il s'agit du clique pour les cartes du joueurs
     */
	public void onClick(View v) {
		
		if(v instanceof ImageView && mDecisionEnCours>0){
			//Je regarde qu'il ne s'agit pas d'un clic pour définir le chien
			if(mJoueurPrendChien==false)
				actionCliqueCarteJoueur(v);
			else
				definitionDuChienParLeJoueur(v);
		}
	}
	
	
	/**
	 * Je clique sur le chien
	 */
	private OnClickListener onClickDog = new OnClickListener() {
		
		public void onClick(View v) {
			Carte removeCarte = null;
			int i =0;
			//Recherche de la carte cliquer
			while(removeCarte==null && i<mCartesAttaque.size()){
				
				if(mCartesAttaque.get(i).getNumImage()==v.getId()){
					removeCarte = mCartesAttaque.get(i);
				}
				else
					i++;
			}
			
			if(removeCarte!=null){
				mCartesAttaque.remove(i);
				
				//Je reaffiche le chien
				((LinearLayout)findViewById(R.id.LinearLayoutChienAjout)).removeAllViews();
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(70,100);
				
				for(Carte carte : mCartesAttaque){
					ImageView affCarte = new ImageView(GameActivity.this);
					affCarte.setId(carte.getNumImage());
					affCarte.setLayoutParams(layoutParams);
					affCarte.setImageResource(carte.getIdImage());
					affCarte.setOnClickListener(onClickDog);
					
					((LinearLayout)findViewById(R.id.LinearLayoutChienAjout)).addView(affCarte);
				}
				
				mTabPlayer.get(0).addCarte(removeCarte);
				mTabPlayer.get(0).triCarte();
				devoileLesCartesJoueur(0);
				
			}
		}
	};
	
	/**
	 * Cette méthode a pour but de définir l'action pour un chien
	 */
	private void definitionDuChienParLeJoueur(View v){

        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de definitionDuChienParLeJoueur");
		int i =0;
		List<Carte> mainCarte =mTabPlayer.get(0).getCartes();
		Carte removeCarte = null;
		
		if(WelcomeActivity.DEBUG)Log.i(TAG,"Définition du chien par le joueur");
		
		if(mDecisionEnCours<3)//Nous devons établir un chien
		{
			
			//Recherche de la carte cliquer
			while(removeCarte==null && i<mainCarte.size()){
				
				if(mainCarte.get(i).getNumImage()==v.getId()){
					removeCarte = mainCarte.get(i);
				}
				else
					i++;
			}
			
			
			if(removeCarte!=null){//J'efface la selection du jour est la place dans son bloc
				mTabPlayer.get(0).removeCarte(removeCarte);
				//je rajoute la dite carte dans le chien
				mCartesAttaque.add(removeCarte);
				
				//Je l'affiche
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(70,100);
				ImageView affCarte = new ImageView(this);
				affCarte.setId(removeCarte.getNumImage());
				affCarte.setLayoutParams(layoutParams);
				affCarte.setImageResource(removeCarte.getIdImage());
				affCarte.setOnClickListener(onClickDog);
				
				((LinearLayout)findViewById(R.id.LinearLayoutChienAjout)).addView(affCarte);
			}
			else
				Log.e(TAG,"Aucune carte de trouver");
			
			devoileLesCartesJoueur(0);
			
			if(mCartesAttaque.size()==mGame.getChienCartes().size()){//Validation du chien par le joueur
				afficheChien("Valider le chien :", true);
			}/*
			else {
				Toast.makeText(this, "Vous venez d'ajouter au chien "+removeCarte.getName()+" plus que "+(mGame.getChienCartes().size()-mCartesAttaque.size())+" à ajouter", Toast.LENGTH_SHORT).show();
			}*/
			
		}
		else if(mDecisionEnCours==3){//Le chien est directement placé dans la bloc attaquant
			
			for(Carte carte : mGame.getChienCartes()){
				mCartesAttaque.add(carte);
			}
			
		}
		else 
		{//Le chien est directement placé dans la bloc defenseur
			
			for(Carte carte : mGame.getChienCartes()){
				mCartesDefense.add(carte);
			}
			
		}
		
		
	}
	
	/**
	 * Cette méthode permet d'effectuer une action spécifique sur un clique de carte du joueur
	 * @param v
	 */
	private void actionCliqueCarteJoueur(View v){
        if(WelcomeActivity.DEBUG)Log.d(TAG+" fonction","Appelle de actionCliqueCarteJoueur");
		int i =0;
		List<Carte> mainCarte =mTabPlayer.get(0).getCartes();
		Carte removeCarte = null;;
		
		while(removeCarte==null && i<mainCarte.size()){
			
			if(mainCarte.get(i).getNumImage()==v.getId()){
				removeCarte = mainCarte.get(i);
			}
			else
				i++;
		}
		
		
		if(removeCarte!=null){
			positionTapis(removeCarte,0,0,60);
			mHandler.sendEmptyMessage(5);
		}
		else
			Log.e(TAG,"Aucune carte de trouver");
		
		devoileLesCartesJoueur(-200);
	}
	
	private void positionTapis(Carte carte,int joueur,int width,int heigth){
		
		RelativeLayout contextPrincipale = (RelativeLayout)findViewById(R.id.gameParent);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(85,ViewGroup.LayoutParams.WRAP_CONTENT );
		layoutParams.setMargins((contextPrincipale.getWidth()/2)-width, (contextPrincipale.getHeight()/2)-heigth, 0, 0);
    	
		ImageView affCarte = new ImageView(GameActivity.this);
		affCarte.setLayoutParams(layoutParams);
		affCarte.setId(carte.getIdImage());
		affCarte.setImageResource(carte.getIdImage());
		contextPrincipale.addView(affCarte);
    	
		mTabPlayer.get(joueur).removeCarte(carte);
		mCartesTapis.add(carte);
	}
	
/* 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
*/
}
