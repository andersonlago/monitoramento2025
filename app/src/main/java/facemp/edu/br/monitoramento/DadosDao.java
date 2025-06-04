package facemp.edu.br.monitoramento;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DadosDao {

    private Context context;
    private Banco banco;

    public DadosDao(Context context) {
        this.context = context;
        banco = new Banco(context);
    }
    public void  inserir(Dados dados){
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("codigo_unico", dados.getCodigo_unico());;
        cv.put("latitude", dados.getLatitude());
        cv.put("longitude", dados.getLongitude());
        cv.put("velocidade", dados.getVelocidade());
        cv.put("dt_hora", dados.getDt_hora());
        cv.put("direcao", dados.getDirecao());
        cv.put("bateria", dados.getBateria());
        cv.put("endereco", dados.getEndereco());
        cv.put("provedor", dados.getProvedor());
        cv.put("precisao", dados.getPrecisao());
        cv.put("enviado", dados.getEnviado());
        //cv.put("id", dados.getId());
        db.insert("dados", null, cv);
    }

    public void  atualizar(Dados dados){

    }

    public void  excluir(Dados dados){

    }

    public ArrayList<Dados> listar(){
        SQLiteDatabase db = banco.getReadableDatabase();
        ArrayList<Dados> ldados = new ArrayList<>();
        String sql = "SELECT * FROM dados";
        db.rawQuery(sql, null).moveToFirst();
        while (db.rawQuery(sql, null).moveToNext()) {
            Dados dados1 = new Dados();
            dados1.setCodigo_unico(db.rawQuery(sql, null).getString(1));
            dados1.setLatitude(db.rawQuery(sql, null).getString(2));
            dados1.setLongitude(db.rawQuery(sql, null).getString(3));
            dados1.setVelocidade(db.rawQuery(sql, null).getString(4));
            dados1.setDt_hora(db.rawQuery(sql, null).getString(5));
            dados1.setDirecao(db.rawQuery(sql, null).getString(6));
            dados1.setBateria(db.rawQuery(sql, null).getString(7));
            dados1.setEndereco(db.rawQuery(sql, null).getString(8));
            dados1.setProvedor(db.rawQuery(sql, null).getString(9));
            dados1.setPrecisao(db.rawQuery(sql, null).getString(10));
            dados1.setEnviado(db.rawQuery(sql, null).getInt(11));
            ldados.add(dados1);
        }
        return ldados;
    }
}
