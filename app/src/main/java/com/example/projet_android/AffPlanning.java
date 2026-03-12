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
 * Activité affichant le planning des rendez-vous.
 * L'utilisateur peut sélectionner une date sur le calendrier pour voir
 * les rendez-vous prévus ce jour-là.
 */
public class AffPlanning extends AppCompatActivity {

    DataConnect db;
    CalendarView calendarRdv;
    String selectedDate;
    Button btnNavRdv, btnNavPlanning, btnNavMedecins, btnNavAccueil, btnAfficherRdv;
    Spinner spinnerRDV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aff_planning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DataConnect(this);

        calendarRdv = findViewById(R.id.calendarPlanning);
        btnNavAccueil = findViewById(R.id.btnNavAccueil);
        btnNavRdv = findViewById(R.id.btnNavRdv);
        btnNavPlanning = findViewById(R.id.btnNavPlanning);
        btnNavMedecins = findViewById(R.id.btnNavMedecins);
        spinnerRDV = findViewById(R.id.spinnerRdv);
        btnAfficherRdv = findViewById(R.id.btnAfficherRdv);

        // Gestion des clics sur les boutons de navigation
        btnNavAccueil.setOnClickListener(v -> startActivity(new Intent(AffPlanning.this, MainActivity.class)));
        btnNavRdv.setOnClickListener(v -> startActivity(new Intent(AffPlanning.this, EnreRdv.class)));
        btnNavPlanning.setOnClickListener(v -> startActivity(new Intent(AffPlanning.this, AffPlanning.class)));
        btnNavMedecins.setOnClickListener(v -> startActivity(new Intent(AffPlanning.this, AffMedecin.class)));

        // Initialisation de la date sélectionnée
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

        btnAfficherRdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherRdv();
            }
        });
    }

    /**
     * Récupère et affiche les rendez-vous pour la date sélectionnée.
     */
    public void afficherRdv(){
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner une date", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Cursor data = db.getPlanning(selectedDate);

            if (data != null && data.getCount() > 0) {
                String[] from = {"rdv_details"};
                int[] to = {android.R.id.text1};

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, data, from, to, 0);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRDV.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Aucun rendez-vous trouvé", Toast.LENGTH_SHORT).show();
                spinnerRDV.setAdapter(null);
                if (data != null) {
                    data.close();
                }
            }
        } catch (SQLiteException e) {
            Log.e("AffPlanning", "Erreur base de données", e);
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
