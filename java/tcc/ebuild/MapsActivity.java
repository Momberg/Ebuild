package tcc.ebuild;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import android.os.Environment;
import android.widget.ArrayAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Gabriel Duarte Momberg on 14/12/2015.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button Blogin, Blog, Bmenu, Bsat, Bnorm, Bhibrid, Bcancel, BX;
    String nomedaobra, datadaobra, tipodaobra, itemdotipodaobra, ID_Tempo, concatena = "";
    double lat, lng;
    EditText user;
    EditText password;
    boolean user_existe;
    LatLng markerLocation;
    ListView lista;
    ArrayAdapter<String> adapter;
    ArrayList<String> info_lista = new ArrayList();
    Dialog menuDialog_obras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bmenu = (Button) findViewById(R.id.btn_menu);
        Bmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMenuDialog();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawnmarkers(mMap);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerLocation = marker.getPosition();
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.marker_info_obra, null);
                        TextView nome_obra = (TextView) v.findViewById(R.id.nome_obra);
                        TextView data_obra = (TextView) v.findViewById(R.id.data_obra);
                        TextView fase_obra = (TextView) v.findViewById(R.id.fase_obra);
                        TextView item_fase_obra = (TextView) v.findViewById(R.id.faseitem_obra);
                        consulta_Lat_Lng(markerLocation.latitude, markerLocation.longitude);
                        consulta_info_obra(ID_Tempo);
                        nome_obra.setText(nomedaobra);
                        data_obra.setText(datadaobra);
                        fase_obra.setText(tipodaobra);
                        item_fase_obra.setText(itemdotipodaobra);
                        return v;
                    }
                });
                return false;
            }
        });

    }

    private void callLoginDialog() {
        final Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_signin);
        myDialog.setCanceledOnTouchOutside(false);

        user = (EditText) myDialog.findViewById(R.id.username);
        password = (EditText) myDialog.findViewById(R.id.password);
        Blog = (Button) myDialog.findViewById(R.id.btn_box);
        Bcancel = (Button) myDialog.findViewById(R.id.buttonCancel);
        myDialog.show();

        password.setText("admin");
        user.setText("admin");

        Blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Preencha o campo Usuário", Toast.LENGTH_LONG).show();
                } else if (password.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Preencha o campo password", Toast.LENGTH_LONG).show();
                }

                consulta_users();
                if ((user.getText().toString().length() != 0) && (password.getText().toString().length() != 0)) {
                    if (user_existe || ((user.getText().toString().equals("admin")) && (password.getText().toString().equals("admin")))) {
                        Intent intent = new Intent(view.getContext(), MapsUserActivity.class);
                        startActivity(intent);
                        myDialog.dismiss();
                        finish();
                    }

                    if (!user_existe && !((user.getText().toString().equals("admin")) && (password.getText().toString().equals("admin")))) {
                        Toast.makeText(getApplicationContext(), "Usuario inexistente ou senha incorreta", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        Bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
    }


    private void callMenuDialog() {
        final Dialog menuDialog = new Dialog(this);
        menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog.setContentView(R.layout.dialog_menu);
        menuDialog.setCanceledOnTouchOutside(false);

        Bsat = (Button) menuDialog.findViewById(R.id.btn_menu_sat);
        BX = (Button) menuDialog.findViewById(R.id.buttonX);
        menuDialog.show();

        BX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
            }
        });

        Bsat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                menuDialog.dismiss();
            }
        });

        Bnorm = (Button) menuDialog.findViewById(R.id.btn_menu_norm);
        menuDialog.show();

        Bnorm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                menuDialog.dismiss();
            }
        });

        Bhibrid = (Button) menuDialog.findViewById(R.id.btn_menu_hibrid);
        menuDialog.show();

        Bhibrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                menuDialog.dismiss();
            }
        });

        Blogin = (Button) menuDialog.findViewById(R.id.btn_login);
        menuDialog.show();

        Blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginDialog();
                menuDialog.dismiss();
            }
        });

        Button lista_obra = (Button) menuDialog.findViewById(R.id.lista_obras);
        lista_obra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_Dialog_Lista_Obras();
            }
        });
    }


    private void call_Dialog_Lista_Obras() {
        menuDialog_obras = new Dialog(this);
        menuDialog_obras.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog_obras.setContentView(R.layout.dialog_lista_show_info_obras);
        menuDialog_obras.setCanceledOnTouchOutside(false);
        menuDialog_obras.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        menuDialog_obras.show();
        imprime_lista_Obras();
        seleciona_item_lista();
        update_lista();

        Button BX = (Button) menuDialog_obras.findViewById(R.id.buttonX);
        BX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog_obras.dismiss();
            }
        });
    }

    private void update_lista(){
        adapter.clear();
        imprime_lista_Obras();
        seleciona_item_lista();
    }


    private void seleciona_item_lista(){
        adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, info_lista);
        lista = (ListView) menuDialog_obras.findViewById(R.id.lista_edita_obra);
        lista.setAdapter(adapter);
    }



//-----------------------------------------------------------------------------------
//  Código de leitura e criação de arquivos

    public void drawnmarkers(GoogleMap googleMap){
        String lstrNomeArq;
        File arq;
        String latitude_longitude, latitude, longitude;
        Double lat, lng;
        lstrNomeArq = "Lat_Lng";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((latitude_longitude = br.readLine()) != null) {
                    String [] pv = latitude_longitude.split(";");
                    latitude = pv[1];
                    longitude = pv[2];
                    lat = Double.valueOf(latitude).doubleValue();
                    lng = Double.valueOf(longitude).doubleValue();
                    LatLng pont = new LatLng(lat, lng);
                    googleMap.addMarker(new MarkerOptions().position(pont).draggable(false));
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void consulta_users(){
        String lstrNomeArq;
        File arq;
        String infos;
        String usuario, senha;
        lstrNomeArq = "Info_Users";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    usuario = pv[3];
                    senha = pv[4];
                    if((user.getText().toString().equals(usuario)) && (password.getText().toString().equals(senha))){
                        user_existe = true;
                    } if(!user_existe) {
                        user_existe = false;
                    }
                }
                br.close();
            }
        }


        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void consulta_Lat_Lng(double la, double lg){
        String lstrNomeArq;
        File arq;
        String infos;
        String latitude, longitude, ID_Lat_Lng;
        lstrNomeArq = "Lat_Lng";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    ID_Lat_Lng = pv[0];
                    latitude = pv[1];
                    longitude = pv[2];
                    lat = Double.valueOf(latitude).doubleValue();
                    lng = Double.valueOf(longitude).doubleValue();
                    if(la == lat && lg == lng){
                        ID_Tempo = ID_Lat_Lng;
                    }
                }
                br.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void consulta_info_obra(String ID){
        String lstrNomeArq;
        File arq;
        String infos;
        String ID_Info_Obra;
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    ID_Info_Obra = pv[0];
                    if (ID.equals(ID_Info_Obra)){
                        nomedaobra = pv[1];
                        datadaobra = pv[2];
                        tipodaobra = pv[3];
                        itemdotipodaobra = pv[4];
                    }
                }
                br.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void imprime_lista_Obras(){
        String lstrNomeArq;
        File arq;
        String infos;
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    concatena = ("Nome da obra: " + pv[1] + "\n" + "Data: " + pv[2] + "\n" + "Fase: " + pv[3] + "\n" + "Fase do Processo: " + pv[4]);
                    info_lista.add(concatena);
                }
                br.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro no imprime lista de: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
