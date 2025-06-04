package facemp.edu.br.monitoramento;

import androidx.annotation.NonNull;

public class Dados {
    private String codigo_unico;
    private String latitude;
    private String longitude;
    private String velocidade;
    private String dt_hora;
    private String direcao;
    private String bateria;
    private String endereco;
    private String provedor;
    private String precisao;
    private int enviado;
    private int id;

    public Dados(){

    }

    public Dados(String codigo_unico, String latitude, String longitude, String velocidade, String dt_hora, String direcao, String bateria, String endereco, String provedor, String precisao, int enviado, int id) {
        this.codigo_unico = codigo_unico;
        this.latitude = latitude;
        this.longitude = longitude;
        this.velocidade = velocidade;
        this.dt_hora = dt_hora;
        this.direcao = direcao;
        this.bateria = bateria;
        this.endereco = endereco;
        this.provedor = provedor;
        this.precisao = precisao;
        this.enviado = enviado;
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return getId() + " - " + getCodigo_unico() + " - " + getLatitude() + " - " + getLongitude() + " - " + getVelocidade() + " - " + getDt_hora() + " - " + getDirecao() + " - " + getBateria() ;
    }

    public String getCodigo_unico() {
        return codigo_unico;
    }

    public void setCodigo_unico(String codigo_unico) {
        this.codigo_unico = codigo_unico;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(String velocidade) {
        this.velocidade = velocidade;
    }

    public String getDt_hora() {
        return dt_hora;
    }

    public void setDt_hora(String dt_hora) {
        this.dt_hora = dt_hora;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public String getBateria() {
        return bateria;
    }

    public void setBateria(String bateria) {
        this.bateria = bateria;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getProvedor() {
        return provedor;
    }

    public void setProvedor(String provedor) {
        this.provedor = provedor;
    }

    public String getPrecisao() {
        return precisao;
    }

    public void setPrecisao(String precisao) {
        this.precisao = precisao;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
