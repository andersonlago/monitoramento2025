package facemp.edu.br.monitoramento;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Localiza extends Service implements LocationListener {
    public Localiza() {
    }
    LocationManager locationManager;
    String provider = null;

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            provider = LocationManager.PASSIVE_PROVIDER;
        }
        // Tempo = 30.000 ms (30 segundos), Distância = 5 metros
              locationManager.requestLocationUpdates(provider, 1000, 5, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String carregarPreferencia(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location.getAccuracy() > MainActivity.limitePrecisao) return;

        // Calcular velocidade em km/h
        String velocidade = (location.hasSpeed() && location.getSpeed() >= MainActivity.limiteVelocidade)
                ? String.format(Locale.US, "%.2f", location.getSpeed() * 3.6f)
                : "0.00";

        // Obter nível da bateria com segurança
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int battery = (bm != null) ? bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) : 0;

        // Formatar data/hora
        String dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"))
                .format(new Date(location.getTime()));

        // Formatadores e dados geográficos
        Locale us = Locale.US;
        String latitude = String.format(us, "%.6f", location.getLatitude());
        String longitude = String.format(us, "%.6f", location.getLongitude());
        String precisao = String.format(us, "%.2f", location.getAccuracy());
        String direcao = String.format(us, "%.2f", location.getBearing());
        String provider = (location.getProvider() != null) ? location.getProvider() : "";

        // Endereço (geocodificação reversa)
        String endereco = MainActivity.getAddress(getApplicationContext(), location);

        // Texto para exibir na tela

        // URL para envio (por exemplo, para um WebView)
        String url = MainActivity.buildUrl( carregarPreferencia(MainActivity.KEY_API),
                carregarPreferencia(MainActivity.KEY_CODIGO_UNICO),
                latitude, longitude, velocidade, dataFormatada,
                direcao, battery, endereco, provider, precisao);

        MainActivity.sendData(url);
    }
}