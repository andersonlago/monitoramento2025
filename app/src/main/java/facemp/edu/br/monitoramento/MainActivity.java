package facemp.edu.br.monitoramento;

import android.content.Context;
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
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import kotlin.reflect.KFunction;

public class MainActivity extends AppCompatActivity {

    private EditText txt_api, txt_codigo_unico;
    private Button btn_iniciar, btn_dados;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView txt_dados_gps;
    private static WebView webView;

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

        txt_api.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                salvarPreferencias(txt_api.getText().toString(), txt_codigo_unico.getText().toString());
                return false;
            }
        });

        txt_codigo_unico.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                salvarPreferencias(txt_api.getText().toString(), txt_codigo_unico.getText().toString());
                return false;
            }
        });

        //Inicia serviço
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*btn_dados.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DadosActivity.class));
        });*/

        btn_iniciar.setOnClickListener(v -> {
            if (!btn_iniciar.getText().equals("PARAR")) {
                btn_iniciar.setText("PARAR");
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

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {

                        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                        int battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                        String dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"))
                                .format(new Date(location.getTime()));


                        String precisao = String.format(Locale.US, "%.2f", location.getAccuracy());
                        String latitude = String.format(Locale.US, "%.6f", location.getLatitude());
                        String longitude = String.format(Locale.US, "%.6f", location.getLongitude());
                        String velocidade ;
                        String direcao = String.format(Locale.US, "%.2f", location.getBearing());
                        String provider = (location.getProvider() != null) ? location.getProvider() : "";

                        if (location.hasSpeed() && location.getAccuracy() <= limitePrecisao) {
                            if (location.getSpeed() >= limiteVelocidade) {
                                //velocidade em km por hora
                                velocidade = String.format(Locale.US, "%.2f", (location.getSpeed() * 3.6f) );
                            } else {
                                velocidade = "0.00";
                            }
                        } else {
                            velocidade = "0.00";
                        }

                        String endereco = getAddress(getApplicationContext(), location);

                        String textoGps = String.format(Locale.getDefault(),
                                "Lat: %s, Long: %s, Hora: %s, Precisão: %s m, Velocidade: %s km/h, Direção: %s graus, Bateria: %d%% , Provedor_dados: %s",
                                latitude, longitude, dataFormatada, precisao, velocidade, direcao, battery, provider);
                        txt_dados_gps.setText(textoGps);

                        webView.loadUrl(buildUrl(latitude, longitude, velocidade, dataFormatada, direcao, battery, endereco, provider, precisao));
                    }
                 };
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 5, locationListener);
                }else {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                        {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 5, locationListener);
                        }
                        else {
                            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1, 5, locationListener);
                        }
                    }
            }else {
                btn_iniciar.setText("INICIAR");
                btn_iniciar.setBackgroundColor(Color.GREEN);
            }
        });
    }

    private void salvarPreferencias(String api, String codigoUnico) {
        SharedPreferences sharedPreferences = getSharedPreferences("configuracoes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("api", api);
        editor.putString("codigoUnico", codigoUnico);
        editor.apply();
    }

    private void carregarPreferencias() {
        SharedPreferences sharedPreferences = getSharedPreferences("configuracoes", MODE_PRIVATE);
        txt_api.setText(sharedPreferences.getString("api", ""));
        txt_codigo_unico.setText(sharedPreferences.getString("codigoUnico", ""));
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