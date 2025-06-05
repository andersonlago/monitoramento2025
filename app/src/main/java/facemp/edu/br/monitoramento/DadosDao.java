package facemp.edu.br.monitoramento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        db.close();
    }

    public void  atualizar(Dados dados){

    }

    public void  excluir(Dados dados){

    }

    public ArrayList<Dados> listar(){
        SQLiteDatabase db = banco.getReadableDatabase();
        ArrayList<Dados> ldados = new ArrayList<>();
        String sql = "SELECT * FROM dados";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.moveToNext()) {
            Dados dados1 = new Dados();
            dados1.setCodigo_unico(c.getString(1));
            dados1.setLatitude(c.getString(2));
            dados1.setLongitude(c.getString(3));
            dados1.setVelocidade(c.getString(4));
            dados1.setDt_hora(c.getString(5));
            dados1.setDirecao(c.getString(6));
            dados1.setBateria(c.getString(7));
            dados1.setEndereco(c.getString(8));
            dados1.setProvedor(c.getString(9));
            dados1.setPrecisao(c.getString(10));
            dados1.setEnviado(c.getInt(11));
            ldados.add(dados1);
        }
        return ldados;
    }
}
