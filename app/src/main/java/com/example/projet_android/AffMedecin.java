package com.example.projet_android;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.sqlite.SQLiteException;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class AffMedecin extends AppCompatActivity {

    DataConnect db;
    EditText ville, codePostal;
    Spinner spinnerMedecins;
    Button btnAfficherMedecins, btnNavRdv, btnNavPlanning, btnNavMedecins, btnNavAccueil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aff_medecin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ville = findViewById(R.id.editVille);
        codePostal = findViewById(R.id.editCodePostal);
        spinnerMedecins = findViewById(R.id.spinnerMedecins);
        btnAfficherMedecins = findViewById(R.id.btnAfficherMedecins);

        db = new DataConnect(this);

        btnNavAccueil = findViewById(R.id.btnNavAccueil);
        btnNavRdv = findViewById(R.id.btnNavRdv);
        btnNavPlanning = findViewById(R.id.btnNavPlanning);
        btnNavMedecins = findViewById(R.id.btnNavMedecins);

        // Gestion des clics sur les boutons de navigation
        btnNavAccueil.setOnClickListener(v -> startActivity(new Intent(AffMedecin.this, MainActivity.class)));
        btnNavRdv.setOnClickListener(v -> startActivity(new Intent(AffMedecin.this, EnreRdv.class)));
        btnNavPlanning.setOnClickListener(v -> startActivity(new Intent(AffMedecin.this, AffPlanning.class)));
        btnNavMedecins.setOnClickListener(v -> startActivity(new Intent(AffMedecin.this, AffMedecin.class)));

        btnAfficherMedecins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                affPro();
            }
        });
    }

    public void affPro(){
        String villeStr = ville.getText().toString().trim();
        String codePostalStr = codePostal.getText().toString().trim();

        if (villeStr.isEmpty() && codePostalStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir au moins un champ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Cursor data = db.getMedecin(villeStr, codePostalStr);

            if (data != null && data.getCount() > 0) {
                String[] from = {DataConnect.COL_PROF_NOM};
                int[] to = {android.R.id.text1};

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, data, from, to, 0);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedecins.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Aucun médecin trouvé", Toast.LENGTH_SHORT).show();
                spinnerMedecins.setAdapter(null);
                if (data != null) {
                    data.close();
                }
            }
        } catch (SQLiteException e) {
            Log.e("AffMedecin", "Erreur base de données", e);
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
