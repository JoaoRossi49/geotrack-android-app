package com.android.geotrack;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;
import android.util.Log;
import android.Manifest;



public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private EditText passwordInput;
    private Button signInBtn;
    private TextView result;
    private TextView coordenadas;
    private String url;
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

        userInput = findViewById(R.id.userInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInBtn = findViewById(R.id.signInBtn);
        result = findViewById(R.id.coordenadas);
        coordenadas = findViewById(R.id.coordenadas);

    }

    public void makeApiCall(String url){
        try {
            JSONObject postData = new JSONObject();
            postData.put("username", userInput.getText());
            postData.put("password", passwordInput.getText());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.i("API", "Resposta recebida com sucesso");
                        Log.i("API", response.getString("authenticated"));
                        String auth = response.getString("authenticated");
                        if(auth == "true"){
                            result.setText("Usuário autenticado com sucesso!");
                        }else{
                            result.setText("Usuário não autenticado!");
                        }

                    } catch (Exception e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("API", "Ocorreu um erro na chamada da API");
                    Log.e("API", error.getMessage());
                }
            });
            Volley.newRequestQueue(this).add(request);
        }catch(Exception e){

        }
    }

    public void logIn(View view){
        Log.i("BTN", "Apertou botão");
        url = "http://192.168.86.6:8000/api/login/";
        makeApiCall(url);
        getLocation();
    }

    public void getLocation(){
        Log.i("COORDENDAS",Manifest.permission.ACCESS_FINE_LOCATION);
        Log.i("COORDENDAS",Manifest.permission.ACCESS_COARSE_LOCATION);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Log.i("COORDENADA", String.valueOf(latitude));
                            Log.i("COORDENADA", String.valueOf(longitude));
                            coordenadas.setText(String.valueOf(latitude)+" "+String.valueOf(longitude));

                        } else {
                            Log.e("COORDENADA", "Não foi possível acessar as coordenadas");
                        }
                    }
                });

    }

}