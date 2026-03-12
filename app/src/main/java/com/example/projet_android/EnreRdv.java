package com.example.projet_android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activité permettant d'enregistrer un nouveau rendez-vous.
 * L'utilisateur sélectionne une date sur le calendrier et un médecin dans une liste.
 */
public class EnreRdv extends AppCompatActivity {

    DataConnect db;
    CalendarView calendarRdv;
    String selectedDate;
    Button btnEnregistrerRdv, btnNavRdv, btnNavPlanning, btnNavMedecins, btnNavAccueil;
    Spinner spinnerMedecins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enre_rdv);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DataConnect(this);

        calendarRdv = findViewById(R.id.calendarRdv);
        btnEnregistrerRdv = findViewById(R.id.btnEnregistrerRdv);
        btnNavAccueil = findViewById(R.id.btnNavAccueil);
        btnNavRdv = findViewById(R.id.btnNavRdv);
        btnNavPlanning = findViewById(R.id.btnNavPlanning);
        btnNavMedecins = findViewById(R.id.btnNavMedecins);
        spinnerMedecins = findViewById(R.id.spinnerMedecin);

        // Gestion des clics sur les boutons de navigation
        btnNavAccueil.setOnClickListener(v -> startActivity(new Intent(EnreRdv.this, MainActivity.class)));
        btnNavRdv.setOnClickListener(v -> startActivity(new Intent(EnreRdv.this, EnreRdv.class)));
        btnNavPlanning.setOnClickListener(v -> startActivity(new Intent(EnreRdv.this, AffPlanning.class)));
        btnNavMedecins.setOnClickListener(v -> startActivity(new Intent(EnreRdv.this, AffMedecin.class)));

        // Initialisation de la date sélectionnée au format jour/mois/année
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = sdf.format(new Date(calendarRdv.getDate()));

        calendarRdv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate = sdf.format(calendar.getTime());
            }
        });

        btnEnregistrerRdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enreRdv();
            }
        });

        AffMed();
    }

    /**
     * Charge la liste des médecins depuis la base de données et l'affiche dans le Spinner.
     */
    private void AffMed(){
        try {
            Cursor data = db.getAllMedecin();

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
            Log.e("EnreRdv", "Erreur base de données", e);
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Enregistre le rendez-vous dans la base de données après vérification de la sélection.
     */
    public void enreRdv(){
        Cursor selectedMedecin = (Cursor) spinnerMedecins.getSelectedItem();
        if (selectedMedecin == null) {
            Toast.makeText(this, "Veuillez sélectionner un médecin", Toast.LENGTH_SHORT).show();
            return;
        }
        int idProf = selectedMedecin.getInt(selectedMedecin.getColumnIndexOrThrow("_id"));

        // Heure par défaut pour le rendez-vous
        String heureRdv = "10:00";

        try {
            db.insertRdv(selectedDate, heureRdv, idProf);
            Toast.makeText(this, "Rendez-vous enregistré pour le " + selectedDate, Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {
            Log.e("EnreRdv", "Erreur base de données", e);
            Toast.makeText(this, "Erreur lors de l'enregistrement du RDV", Toast.LENGTH_SHORT).show();
        }
    }
}
