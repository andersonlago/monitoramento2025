package facemp.edu.br.monitoramento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText txt_api, txt_codigo_unico;
    private Button btn_iniciar, btn_dados;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView txt_dados_gps;
    private static WebView webView;

    private static final String PREFS_NAME = "configuracoes";
    private static final String KEY_API = "api";
    private static final String KEY_CODIGO_UNICO = "codigoUnico";

    float limiteVelocidade = 0.5f;  // m/s
    float limitePrecisao = 15.0f;   // metros


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_api = findViewById(R.id.txt_api);
        txt_codigo_unico = findViewById(R.id.txt_codigo_unico);
        btn_iniciar = findViewById(R.id.btn_iniciar);
        btn_dados = findViewById(R.id.btn_dados);
        txt_dados_gps = findViewById(R.id.txt_dados_gps);
        webView = findViewById(R.id.web_view);

        carregarPreferencias();


        txt_api.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) { // Lost focus
                salvarPreferencias(txt_api.getText().toString(), txt_codigo_unico.getText().toString());
            }
        });

        txt_codigo_unico.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) { // Lost focus
                salvarPreferencias(txt_api.getText().toString(), txt_codigo_unico.getText().toString());
            }
        });


        //Inicia serviço
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn_dados.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DadosActivity.class)));
        btn_iniciar.setBackgroundColor(Color.GREEN);
        btn_iniciar.setOnClickListener(v -> {
            if (btn_iniciar.getTag() ==null || !((Boolean) btn_iniciar.getTag())) {
                btn_iniciar.setText("PARAR");
                btn_iniciar.setTag(true);
                btn_iniciar.setBackgroundColor(Color.RED);

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                locationListener = location -> {
                    if (location.getAccuracy() > limitePrecisao) return;

                    // Calcular velocidade em km/h
                    String velocidade = (location.hasSpeed() && location.getSpeed() >= limiteVelocidade)
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
                    String endereco = getAddress(getApplicationContext(), location);

                    // Texto para exibir na tela
                    String textoGps = String.format(Locale.getDefault(),
                            "Lat: %s, Long: %s, Hora: %s, Precisão: %s m, Velocidade: %s km/h, Direção: %s graus, Bateria: %d%%, Provedor de Localização: %s",
                            latitude, longitude, dataFormatada, precisao, velocidade, direcao, battery, provider);

                    txt_dados_gps.setText(textoGps);

                    // URL para envio (por exemplo, para um WebView)
                    String url = buildUrl(latitude, longitude, velocidade, dataFormatada, direcao, battery, endereco, provider, precisao);

                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setUseWideViewPort(true);
                    webSettings.setLoadWithOverviewMode(true);
                    webSettings.setAllowFileAccess(true);
                    webSettings.setAllowContentAccess(true);
                    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                    webSettings.setBlockNetworkImage(false);
                    webSettings.setTextZoom(100);
                    webSettings.setSupportZoom(true);
                    webSettings.setBuiltInZoomControls(false);
                    webSettings.setDisplayZoomControls(false);
                    webSettings.setDefaultTextEncodingName("UTF-8");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        webSettings.setAllowFileAccessFromFileURLs(true);
                        webSettings.setAllowUniversalAccessFromFileURLs(true);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    }
                    webSettings.setDomStorageEnabled(true);
                    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                    webView.loadUrl(url);
                };
                String provider = null;
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else {
                    provider = LocationManager.PASSIVE_PROVIDER;
                }
                    // Tempo = 30.000 ms (30 segundos), Distância = 5 metros
                    locationManager.requestLocationUpdates(provider, 1000, 5, locationListener);
            }else {
                btn_iniciar.setText("INICIAR");
                btn_iniciar.setTag(false);
                btn_iniciar.setBackgroundColor(Color.GREEN);
            }
        });
    }

    private void salvarPreferencias(String api, String codigoUnico) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sharedPreferences.edit()
        .putString(KEY_API, api)
        .putString(KEY_CODIGO_UNICO, codigoUnico)
        .apply();
    }

    private void carregarPreferencias() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        txt_api.setText(sharedPreferences.getString(KEY_API, ""));
        txt_codigo_unico.setText(sharedPreferences.getString(KEY_CODIGO_UNICO, ""));
    }

    private String buildUrl(String latitude, String longitude, String velocidade, String dataFormatada, String direcao, int battery, String endereco, String provedor, String precisao)  {
        try {
               return String.format(Locale.getDefault(), "%s?codigo_unico=%s&latitude=%s&longitude=%s&velocidade=%s&dt_hora=%s&direcao=%s&bateria=%d&endereco=%s&provedor=%s&precisao=%s",
                    txt_api.getText().toString(), txt_codigo_unico.getText().toString(), latitude, longitude, velocidade, URLEncoder.encode(dataFormatada, "UTF-8"), direcao, battery, URLEncoder.encode(endereco, "UTF-8"), provedor,precisao);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAddress(Context c, Location l) {
        Geocoder g = new Geocoder(c, Locale.getDefault());
        try {
            return g.getFromLocation(l.getLatitude(), l.getLongitude(), 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            return "";
        }
    }

    }