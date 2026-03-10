package com.example.projet_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataConnect extends SQLiteOpenHelper {

    // Configuration de la base de données
    public static final String DATABASE_NAME = "rendezvous.db";
    public static final int DATABASE_VERSION = 1;

    // Table Professionnel
    public static final String TABLE_PROFESSIONNEL = "Professionnel";
    public static final String COL_PROF_ID = "id_prof";
    public static final String COL_PROF_NOM = "nom";
    public static final String COL_PROF_PRENOM = "prenom";
    public static final String COL_PROF_TYPE = "type_prof";
    public static final String COL_PROF_ADRESSE = "adresse";
    public static final String COL_PROF_VILLE = "ville";
    public static final String COL_PROF_CODE_POSTAL = "code_postal";
    public static final String COL_PROF_MAIL = "mail";
    public static final String COL_PROF_TEL = "tel";

    // Table RendezVous
    public static final String TABLE_RENDEZVOUS = "RendezVous";
    public static final String COL_RDV_ID = "id_rdv";
    public static final String COL_RDV_DATE = "date_rdv";
    public static final String COL_RDV_HEURE = "heure_rdv";
    public static final String COL_RDV_ID_PROF = "id_prof"; // Clé étrangère

    // Requête de création de la table Professionnel
    private static final String CREATE_TABLE_PROFESSIONNEL = "CREATE TABLE "
            + TABLE_PROFESSIONNEL + "("
            + COL_PROF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_PROF_NOM + " TEXT,"
            + COL_PROF_PRENOM + " TEXT,"
            + COL_PROF_TYPE + " TEXT,"
            + COL_PROF_ADRESSE + " TEXT,"
            + COL_PROF_VILLE + " TEXT,"
            + COL_PROF_CODE_POSTAL + " TEXT,"
            + COL_PROF_MAIL + " TEXT,"
            + COL_PROF_TEL + " TEXT" + ")";

    // Requête de création de la table RendezVous
    private static final String CREATE_TABLE_RENDEZVOUS = "CREATE TABLE "
            + TABLE_RENDEZVOUS + "("
            + COL_RDV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_RDV_DATE + " TEXT,"
            + COL_RDV_HEURE + " TEXT,"
            + COL_RDV_ID_PROF + " INTEGER,"
            + "FOREIGN KEY(" + COL_RDV_ID_PROF + ") REFERENCES " + TABLE_PROFESSIONNEL + "(" + COL_PROF_ID + ")"
            + ")";

    public DataConnect(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROFESSIONNEL);
        db.execSQL(CREATE_TABLE_RENDEZVOUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RENDEZVOUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFESSIONNEL);
        onCreate(db);
    }

    public void insertProfessionnel(String nom, String prenom, String type, String adresse, String ville, String codePostal, String mail, String tel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_PROF_NOM, nom);
        contentValues.put(COL_PROF_PRENOM, prenom);
        contentValues.put(COL_PROF_TYPE,type);
        contentValues.put(COL_PROF_ADRESSE, adresse);
        contentValues.put(COL_PROF_VILLE,ville);
        contentValues.put(COL_PROF_CODE_POSTAL,codePostal);
        contentValues.put(COL_PROF_MAIL,mail);
        contentValues.put(COL_PROF_TEL,tel);

        db.insert(TABLE_PROFESSIONNEL,null,contentValues);
    }

    public void insertRdv(String date, String heure, int id_prof) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_RDV_DATE, date);
        contentValues.put(COL_RDV_HEURE, heure);
        contentValues.put(COL_RDV_ID_PROF, id_prof);

        db.insert(TABLE_RENDEZVOUS, null, contentValues);
    }

    public Cursor getPlanning(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        String rdvDetailsColumn = "rdv_details";
        String query = "SELECT r." + COL_RDV_ID + " as _id, " +
                "p." + COL_PROF_NOM + " || ' ' || p." + COL_PROF_PRENOM + " || ' à ' || r." + COL_RDV_HEURE + " as " + rdvDetailsColumn +
                " FROM " + TABLE_RENDEZVOUS + " r " +
                " JOIN " + TABLE_PROFESSIONNEL + " p ON r." + COL_RDV_ID_PROF + " = p." + COL_PROF_ID +
                " WHERE r." + COL_RDV_DATE + " = ?";

        return db.rawQuery(query, new String[]{date});
    }

    public Cursor getMedecin(String ville, String codePostal){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COL_PROF_ID + " as _id", COL_PROF_NOM, COL_PROF_PRENOM};
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        if(ville != null && !ville.isEmpty()) {
            selection.append(COL_PROF_VILLE + " = ?");
            selectionArgs.add(ville);
        }

        if(codePostal != null && !codePostal.isEmpty()) {
            if(selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(COL_PROF_CODE_POSTAL + " = ?");
            selectionArgs.add(codePostal);
        }

        if(selection.length() == 0) {
            return db.query(TABLE_PROFESSIONNEL, columns, null, null, null, null, COL_PROF_NOM);
        }

        String[] args = selectionArgs.toArray(new String[0]);
        return db.query(TABLE_PROFESSIONNEL, columns, selection.toString(), args, null, null, COL_PROF_NOM);
    }

    public Cursor getAllMedecin(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COL_PROF_ID + " as _id, * FROM " + TABLE_PROFESSIONNEL, null);
    }
}
