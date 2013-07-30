package fr.beber.game;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class WelcomeActivity extends Activity {

	//Information de debug 
	private final String TAG = "BeBer";
	public static final boolean DEBUG = true;

	//Il s'agit des trois boutons intiales
	private Button mNewGame;
	private Button mContinue;
	private Button mProfil;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        if(DEBUG)Log.i(TAG,"Bienvenu");
        
        //Initialisation des boutons
        mNewGame=(Button)findViewById(R.id.but_NewGame);
        mContinue=(Button)findViewById(R.id.but_Continue);
        mProfil=(Button)findViewById(R.id.but_Profil);
        
        //Je me place dans mon activity nouveau jeux
        mNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	new AlertDialog.Builder(WelcomeActivity.this)
                .setTitle(R.string.nb_joueur)
                .setItems(R.array.choix_nb_joueur,
	                new DialogInterface.OnClickListener() 
	                {
		                public void onClick(DialogInterface dialoginterface,int i) 
		                {
	                		if(DEBUG)Log.i(TAG,"Clique sur i = "+i);
	                		Intent myIntent = new Intent(WelcomeActivity.this, GameActivity.class);
	                		myIntent.putExtra("nbjoueur", i+3);
	            			startActivityForResult(myIntent, 0);
		                }
	                })
                .show();
            	
                
            }

        });

        //Je me place dans mon activity nouveau jeux
        mContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), GameActivity.class);
    			startActivityForResult(myIntent, 0);
            }

        });
        
        //Je me place dans mon activity nouveau jeux
        mProfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ProfilActivity.class);
    			startActivityForResult(myIntent, 0);
            }

        });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_welcome, menu);
        return true;
    }
*/
}
