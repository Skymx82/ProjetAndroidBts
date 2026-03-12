package com.example.projet_android;

import android.content.Context;
import android.database.Cursor;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.projet_android", appContext.getPackageName());
    }

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

        assertEquals(initialCount + 1, finalCount);
    }

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

        assertEquals(initialCount + 1, finalCount);
    }
}