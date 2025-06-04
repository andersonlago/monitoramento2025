package facemp.edu.br.monitoramento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Banco extends SQLiteOpenHelper {



    public Banco(@Nullable Context context) {
        super(context, "dados", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" CREATE TABLE dados ( id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " +
                "codigo_unico	TEXT,	latitude	TEXT,	" +
                "longitude	TEXT,	velocidade	TEXT, dt_hora	TEXT,	" +
                "direcao	TEXT,	bateria	TEXT,	endereco	TEXT,	" +
                "provedor	TEXT,	precisao	TEXT, enviado	INTEGER	" +
                " )");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
