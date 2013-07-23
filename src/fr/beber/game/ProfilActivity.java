package fr.beber.game;

import net.knowledgecommunity.game.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ProfilActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profil, menu);
        return true;
    }
}
