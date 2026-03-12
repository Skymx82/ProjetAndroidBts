package com.example.projet_android;

import android.content.Context;
import android.database.Cursor;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests instrumentés pour le projet Android.
 * Ces tests s'exécutent sur un appareil Android ou un émulateur.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /**
     * Vérifie que le contexte de l'application est correct.
     * On s'assure que le nom du package correspond à celui attendu.
     */
    @Test
    public void useAppContext() {
        // Contexte de l'application sous test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.projet_android", appContext.getPackageName());
    }

    /**
     * Teste l'ajout d'un nouveau professionnel (médecin) dans la base de données.
     * On vérifie que le nombre total de médecins augmente de 1 après l'insertion.
     */
    @Test
    public void testAddMedecin() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataConnect db = new DataConnect(appContext);

        Cursor cursorBefore = db.getAllMedecin();
        int initialCount = cursorBefore.getCount();
        cursorBefore.close();

        db.insertProfessionnel("Dupont", "Jean", "Généraliste", "1 rue de la Paix", "Paris", "75001", "jean.dupont@email.com", "0102030405");

        Cursor cursorAfter = db.getAllMedecin();
        int finalCount = cursorAfter.getCount();
        cursorAfter.close();

        assertEquals("Le nombre de médecins devrait avoir augmenté de 1", initialCount + 1, finalCount);
    }

    /**
     * Teste l'enregistrement d'un rendez-vous.
     * Ce test insère d'abord un médecin, récupère son ID, puis ajoute un rendez-vous
     * et vérifie sa présence dans le planning à la date donnée.
     */
    @Test
    public void testAddRdv() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataConnect db = new DataConnect(appContext);

        // Insérer un médecin pour obtenir un ID valide
        db.insertProfessionnel("Curie", "Marie", "Généraliste", "10 Rue Curie", "Paris", "75005", "marie@curie.fr", "0600000000");
        Cursor cursorMed = db.getAllMedecin();
        cursorMed.moveToLast();
        int profId = cursorMed.getInt(cursorMed.getColumnIndexOrThrow("_id"));
        cursorMed.close();

        String testDate = "2023-10-27";
        Cursor cursorBefore = db.getPlanning(testDate);
        int initialCount = cursorBefore.getCount();
        cursorBefore.close();

        db.insertRdv(testDate, "14:30", profId);

        Cursor cursorAfter = db.getPlanning(testDate);
        int finalCount = cursorAfter.getCount();
        cursorAfter.close();

        assertEquals("Le nombre de rendez-vous pour cette date devrait avoir augmenté de 1", initialCount + 1, finalCount);
    }
}