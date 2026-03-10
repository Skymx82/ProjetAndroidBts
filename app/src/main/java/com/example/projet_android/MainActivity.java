package com.example.projet_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    DataConnect db;

    EditText nom, prenom, adresse, ville, codePostal, tel, email;
    RadioGroup radioGroupType;
    Button btnEnregistrer, btnNavRdv, btnNavPlanning, btnNavMedecins, btnNavAccueil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DataConnect(this);

        // Récupération des vues
        nom = findViewById(R.id.editNom);
        prenom = findViewById(R.id.editPrenom);
        adresse = findViewById(R.id.editAdresse);
        ville = findViewById(R.id.editVille);
        codePostal = findViewById(R.id.editCodePostal);
        tel = findViewById(R.id.editTelephone);
        email = findViewById(R.id.editEmail);
        radioGroupType = findViewById(R.id.radioGroupType);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);

        btnNavAccueil = findViewById(R.id.btnNavAccueil);
        btnNavRdv = findViewById(R.id.btnNavRdv);
        btnNavPlanning = findViewById(R.id.btnNavPlanning);
        btnNavMedecins = findViewById(R.id.btnNavMedecins);


        // Gestion des clics sur les boutons de navigation
        btnNavRdv.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EnreRdv.class)));
        btnNavPlanning.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AffPlanning.class)));
        btnNavMedecins.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AffMedecin.class)));
    }

    public void enrePro(View view){
        String nomStr = nom.getText().toString();
        String prenomStr = prenom.getText().toString();
        String adresseStr = adresse.getText().toString();
        String villeStr = ville.getText().toString();
        String codePostalStr = codePostal.getText().toString();
        String telStr = tel.getText().toString();
        String emailStr = email.getText().toString();

        String typePro = "";
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        if (selectedId == R.id.radioGeneraliste) {
            typePro = "Généraliste";
        } else if (selectedId == R.id.radioDentiste) {
            typePro = "Dentiste";
        }

        if (nomStr.isEmpty() || prenomStr.isEmpty() || villeStr.isEmpty() || codePostalStr.isEmpty() || typePro.isEmpty()) {
            Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        db.insertProfessionnel(nomStr, prenomStr, typePro, adresseStr, villeStr, codePostalStr, emailStr, telStr);

        Toast.makeText(MainActivity.this, "Professionnel enregistré", Toast.LENGTH_SHORT).show();

        // Vider les champs après l'enregistrement
        nom.setText("");
        prenom.setText("");
        adresse.setText("");
        ville.setText("");
        codePostal.setText("");
        tel.setText("");
        email.setText("");
        radioGroupType.clearCheck();
    }
}