package tcc.ebuild;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.Environment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONObject;

/**
 * Created by Gabriel Duarte Momberg on 14/12/2015.
 */

public class MapsUserActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button LBmenu, LBsat, LBnorm, LBhibrid, LBlogout, LBbuscaend, LBcancel, LBcancel2, LBbusca, LBX, LBadd, LBremove, LBinfo, LBcadastro;
    double lat, lng;
    int adcmarker = 0, IDlaln = 0, closed = 0, numID_latlng = 0,IDinfomarker = 0 , numID_infomarker = 0, ID_Temp = 0, form = 0, viewbutton1 = 0 , viewbutton2 = 0;
    int ID_anterior_info_obra = 0;
    boolean adc_info_obra = false;
    String laln, temprua = null, tempbairro = null, tempcidade = null, info_marker, ID_Tempo = "", concatena = "", item_selecionado;
    ArrayList<String> info_lista = new ArrayList();
    String nomedaobra, datadaobra, tipodaobra, itemdotipodaobra;
    EditText textrua, textbairro, textcidade;
    LatLng markerLocation;
    ListView lista;
    ArrayAdapter<String> adapter;
    int ID_Lista = 0, selecionado = 0;
    Dialog menuDialog_obras;
    String[] interno = new String[] {"Verificar a necessidade","Elaboração dos estudos técnicos preliminares","Licença ambiental prévia","Elaboração do projeto básico","Elaboração do projeto executivo"};
    String[] externo = new String[] {"Publicação do edital","Licitação","Contrataçao e designação do fiscal da obra","Pagamento seguindo o cronograma físico-financeiro e ordem cronológica","Recebimento da obra","Devolução de garantia","Registros finais"};
    int item = 0;
    RadioButton int_ext;
    RadioGroup GrupoRadio;
    String item_selecionado_dialog, camp1, camp2, recadastro;
    ListView lista_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_logado);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_log);
        mapFragment.getMapAsync(this);

        LBmenu = (Button) findViewById(R.id.btn_menu);
        LBmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMenuDialogLogado();
            }
        });

        final LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.button_add, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));

        LBinfo = (Button) findViewById(R.id.Bform);
        LBinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FormularioActivity.class);
                startActivity(intent);
                form = 1;
                adc_info_obra = true;
            }
        });


        LBadd = (Button) findViewById(R.id.Badd);
        LBadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (form == 0) {
                    Toast.makeText(getBaseContext(), "Preencha o formulário", Toast.LENGTH_SHORT).show();
                }
                if (adcmarker == 1 && form == 1) {
                    countNum_latlng();
                    if (numID_latlng == 0) {
                        IDlaln = 1;
                    }
                    if (numID_latlng > 1 || numID_latlng == 1) {
                        IDlaln = numID_latlng + 1;
                    }
                    laln = (Integer.toString(IDlaln) + ";" + Double.toString(lat) + ";" + Double.toString(lng) + " \n");
                    click_Salvar(view);
                    click_add_info_marker(info_marker);
                    adcmarker = 0;
                    LBadd.setVisibility(View.INVISIBLE);
                    LBcancel.setVisibility(View.INVISIBLE);
                    LBinfo.setVisibility(View.INVISIBLE);
                    viewbutton1 = 0;
                    form = 0;
                    adc_info_obra = false;
                }
                if (adcmarker > 1) {
                    Toast.makeText(getBaseContext(), "Coloque apenas um marcador por vez", Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    adcmarker = 0;
                    drawnmarkers(mMap);
                    LBadd.setVisibility(View.INVISIBLE);
                    LBcancel.setVisibility(View.INVISIBLE);
                    LBinfo.setVisibility(View.INVISIBLE);
                    viewbutton1 = 0;
                    adc_info_obra = false;
                }
            }
        });

        LBcancel = (Button) findViewById(R.id.Bcancel);
        LBcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                drawnmarkers(mMap);
                adcmarker = 0;
                LBadd.setVisibility(View.INVISIBLE);
                LBcancel.setVisibility(View.INVISIBLE);
                LBinfo.setVisibility(View.INVISIBLE);
                viewbutton1 = 0;
                form = 0;
                if (adc_info_obra) {
                    countNum_info_obras();
                    removeinfo_obra_por_ID(ID_anterior_info_obra);
                }
            }
        });

        LBadd.setVisibility(View.INVISIBLE);
        LBcancel.setVisibility(View.INVISIBLE);

        LBremove = (Button) findViewById(R.id.Bremove);
        LBremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removemarkers(markerLocation.latitude, markerLocation.longitude);
                removemarkers_info();
                removeinfo_obra();
                renumera_ID_Lat_Lng();
                renumera_ID_Info_Markers();
                renumera_ID_Info_Obra();
                mMap.clear();
                drawnmarkers(mMap);
                adcmarker = 0;
                LBremove.setVisibility(View.INVISIBLE);
                LBcancel2.setVisibility(View.INVISIBLE);
                LBinfo.setVisibility(View.INVISIBLE);
                viewbutton2 = 0;
            }
        });

        LBcancel2 = (Button) findViewById(R.id.Bcancel2);
        LBcancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                drawnmarkers(mMap);
                LBremove.setVisibility(View.INVISIBLE);
                LBcancel2.setVisibility(View.INVISIBLE);
                LBinfo.setVisibility(View.INVISIBLE);
                viewbutton2 = 0;
            }
        });

        LBremove.setVisibility(View.INVISIBLE);
        LBcancel2.setVisibility(View.INVISIBLE);
        LBinfo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawnmarkers(mMap);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                if (adcmarker == 0 && viewbutton2 == 0) {
                    mMap.addMarker(new MarkerOptions().position(point).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    lat = point.latitude;
                    lng = point.longitude;
                    adcmarker = adcmarker + 1;
                    callDialogInfoMarker();
                    if (adcmarker == 1 && viewbutton2 == 0) {
                        LBadd.setVisibility(View.VISIBLE);
                        LBcancel.setVisibility(View.VISIBLE);
                        LBinfo.setVisibility(View.VISIBLE);
                        viewbutton1 = 1;
                    }
                } else if (adcmarker == 1 && viewbutton2 == 0) {
                    Toast.makeText(getApplication(), "Adicione um marcador por vez", Toast.LENGTH_LONG).show();
                } else if (adcmarker == 0 && viewbutton2 == 1) {
                    Toast.makeText(getApplication(), "Informação de obra aberta", Toast.LENGTH_LONG).show();
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerLocation = marker.getPosition();
                if (viewbutton1 == 0) {
                    LBremove.setVisibility(View.VISIBLE);
                    LBcancel2.setVisibility(View.VISIBLE);
                    viewbutton2 = 1;
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
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            viewbutton2 = 0;
                            LBremove.setVisibility(View.INVISIBLE);
                            LBcancel2.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                return false;
            }
        });
    }

    private void callMenuDialogLogado() {
        final Dialog menuDialog = new Dialog(this);
        menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog.setContentView(R.layout.dialog_menu_logado);
        menuDialog.setCanceledOnTouchOutside(false);

        LBsat = (Button) menuDialog.findViewById(R.id.btn_menu_sat_log);
        LBX = (Button) menuDialog.findViewById(R.id.buttonX);
        menuDialog.show();

        LBX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
            }
        });

        LBsat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                menuDialog.dismiss();
            }
        });

        LBnorm = (Button) menuDialog.findViewById(R.id.btn_menu_norm_log);
        menuDialog.show();

        LBnorm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                menuDialog.dismiss();
            }
        });

        LBhibrid = (Button) menuDialog.findViewById(R.id.btn_menu_hibrid_log);
        menuDialog.show();

        LBhibrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                menuDialog.dismiss();
            }
        });


        LBlogout = (Button) menuDialog.findViewById(R.id.btn_menu_logout);
        menuDialog.show();

        LBlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
                menuDialog.dismiss();
                finish();
            }
        });

        LBbuscaend = (Button) menuDialog.findViewById(R.id.btn_buscaend);
        menuDialog.show();

        LBbuscaend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adcmarker == 0){
                    callDialogFormulario();
                    menuDialog.dismiss();
                } else {
                    Toast.makeText(getApplication(),"Adicione um marcador por vez", Toast.LENGTH_LONG).show();
                }
            }
        });

        LBcadastro = (Button) menuDialog.findViewById(R.id.Bcadastro);
        LBcadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_dialog_menu_cadastro();
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
        menuDialog.show();
    }

    private void callDialogInfoMarker() {
        final Dialog formDialog = new Dialog(this);
        formDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        formDialog.setCanceledOnTouchOutside(false);
        formDialog.setContentView(R.layout.dialog_info_marker);
        formDialog.setCancelable(true);

        textrua = (EditText) formDialog.findViewById(R.id.rua_num);
        textbairro = (EditText) formDialog.findViewById(R.id.bairro);
        textcidade = (EditText) formDialog.findViewById(R.id.cidade);

        if((closed == 1) && (!temprua.equals("") || !tempbairro.equals("") || !tempcidade.equals(""))) {
            textrua.setText(temprua, TextView.BufferType.EDITABLE);
            textbairro.setText(tempbairro, TextView.BufferType.EDITABLE);
            textcidade.setText(tempcidade, TextView.BufferType.EDITABLE);
        }


        LBX = (Button) formDialog.findViewById(R.id.buttonX);

        LBX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temprua = textrua.getText().toString();
                tempbairro = textbairro.getText().toString();
                tempcidade = textcidade.getText().toString();
                closed = 1;
                formDialog.dismiss();
            }
        });

        LBbusca = (Button) formDialog.findViewById(R.id.btn_busca);
        formDialog.show();


        countNum_infomarker();
        if(numID_infomarker == 0){
            IDinfomarker = 1;
        }
        if(numID_infomarker > 1 || numID_infomarker == 1){
            IDinfomarker = numID_infomarker + 1;
        }
        LBbusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info_marker = (Integer.toString(IDinfomarker) + ";" + textrua.getText() + ";" + textbairro.getText() + ";" + textcidade.getText() + "\n");
                closed = 0;
                if (textrua.getText().toString().equals("") || textbairro.getText().toString().equals("") || textcidade.getText().toString().equals("")) {
                    Toast.makeText(getBaseContext(), "Preencha os campos acima", Toast.LENGTH_SHORT).show();
                    return;
                }
                formDialog.dismiss();
            }
        });
    }

    private void callDialogFormulario() {
        final Dialog formDialog = new Dialog(this);
        formDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        formDialog.setCanceledOnTouchOutside(false);
        formDialog.setContentView(R.layout.dialog_info_marker_busca);
        formDialog.setCancelable(true);

        textrua = (EditText) formDialog.findViewById(R.id.rua_num);
        textbairro = (EditText) formDialog.findViewById(R.id.bairro);
        textcidade = (EditText) formDialog.findViewById(R.id.cidade);

        if((closed == 1) && (!temprua.equals("") || !tempbairro.equals("") || !tempcidade.equals(""))) {
            textrua.setText(temprua, TextView.BufferType.EDITABLE);
            textbairro.setText(tempbairro, TextView.BufferType.EDITABLE);
            textcidade.setText(tempcidade, TextView.BufferType.EDITABLE);
        }


        LBX = (Button) formDialog.findViewById(R.id.buttonX);

        LBX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temprua = textrua.getText().toString();
                tempbairro = textbairro.getText().toString();
                tempcidade = textcidade.getText().toString();
                closed = 1;
                formDialog.dismiss();
            }
        });

        LBbusca = (Button) formDialog.findViewById(R.id.btn_busca);
        formDialog.show();


        countNum_infomarker();
        if(numID_infomarker == 0){
            IDinfomarker = 1;
        }
        if(numID_infomarker > 1 || numID_infomarker == 1){
            IDinfomarker = numID_infomarker + 1;
        }
        LBbusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the place entered
                String location = (textrua.getText().toString() + ", " + textbairro.getText().toString() + " - " + textcidade.getText().toString());

                String url = "https://maps.googleapis.com/maps/api/geocode/json?";

                try {
                    // encoding special characters like space in the user input place
                    location = URLEncoder.encode(location, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String address = "address=" + location;

                String sensor = "sensor=false";


                // url , from where the geocoding data is fetched
                url = url + address + "&" + sensor;

                // Instantiating DownloadTask to get places from Google Geocoding service
                // in a non-ui thread
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading the geocoding places
                downloadTask.execute(url);
                info_marker = (Integer.toString(IDinfomarker) + ";" + textrua.getText() + ";" + textbairro.getText() + ";" + textcidade.getText() + "\n");
                if(textrua.getText().toString().equals("") || textbairro.getText().toString().equals("") || textcidade.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Preencha os campos acima", Toast.LENGTH_SHORT).show();
                    return;
                }
                closed = 0;
                formDialog.dismiss();
                viewbutton1 = 1;
            }
        });

    }

    private void call_dialog_menu_cadastro(){
        final Dialog menuDialog = new Dialog(this);
        menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog.setContentView(R.layout.dialog_menu_usuario);
        menuDialog.setCanceledOnTouchOutside(false);

        LBX = (Button) menuDialog.findViewById(R.id.buttonX);
        LBX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
            }
        });

        LBcadastro = (Button) menuDialog.findViewById(R.id.Bcadastro);
        menuDialog.show();
        LBcadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CadastroUserActivity.class);
                startActivity(intent);
                menuDialog.dismiss();

            }
        });

        Button LBedita = (Button) menuDialog.findViewById(R.id.Beditcadastro);
        menuDialog.show();
        LBedita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), edita_userActivity.class);
                startActivity(intent);
                menuDialog.dismiss();

            }
        });
    }

    private void call_Dialog_Lista_Obras() {
        menuDialog_obras = new Dialog(this);
        menuDialog_obras.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog_obras.setContentView(R.layout.dialog_lista_info_obras);
        menuDialog_obras.setCanceledOnTouchOutside(false);
        menuDialog_obras.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        menuDialog_obras.show();
        imprime_lista_Obras();
        seleciona_item_lista();
        update_lista();

        Button cancelar = (Button) menuDialog_obras.findViewById(R.id.Bcancela);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog_obras.dismiss();
            }
        });

        Button remover_obra = (Button) menuDialog_obras.findViewById(R.id.Bremove);
        remover_obra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selecionado == 1) {
                    removeinfo_obra_por_ID(ID_Lista);
                    remove_info_Marker_por_ID(ID_Lista);
                    remove_Lat_Lng_por_ID(ID_Lista);
                    renumera_ID_Info_Markers();
                    renumera_ID_Info_Obra();
                    renumera_ID_Lat_Lng();
                    update_lista();
                    mMap.clear();
                    drawnmarkers(mMap);
                } else {
                    Toast.makeText(getApplication(), "Selecione uma obra", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button edita_obra = (Button) menuDialog_obras.findViewById(R.id.Bedita);
        edita_obra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selecionado == 1) {
                    pega_info_edita_obra(ID_Lista);
                    dialog_edita_obra();
                    update_lista();
                    mMap.clear();
                    drawnmarkers(mMap);
                    selecionado = 0;
                } else {
                    Toast.makeText(getApplication(), "Selecione uma obra", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void seleciona_item_lista(){
        adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, info_lista);
        lista = (ListView) menuDialog_obras.findViewById(R.id.lista_edita_obra);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                item_selecionado = (String) arg0.getAdapter().getItem(arg2);
                ID_Lista = arg2 + 1;
                selecionado = 1;
            }
        });
    }

    private void update_lista(){
        adapter.clear();
        imprime_lista_Obras();
        seleciona_item_lista();
    }

    private void dialog_edita_obra(){
        final Dialog formDialog = new Dialog(this);
        formDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        formDialog.setCanceledOnTouchOutside(false);
        formDialog.setContentView(R.layout.dialog_edita_obra);
        formDialog.setCancelable(true);
        formDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        formDialog.show();

        final EditText NomeObraDialog = (EditText) formDialog.findViewById(R.id.nome);
        final EditText DataInicObraDialog = (EditText) formDialog.findViewById(R.id.data);
        NomeObraDialog.setText(camp1);
        DataInicObraDialog.setText(camp2);


        GrupoRadio = (RadioGroup) formDialog.findViewById(R.id.radio_Ext_Int);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, interno);
        lista_dialog = (ListView) formDialog.findViewById(R.id.list_Int_Ext);
        lista_dialog.setAdapter(adapter);
        int temId = GrupoRadio.getCheckedRadioButtonId();
        int_ext = (RadioButton) formDialog.findViewById(temId);
        lista_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                item_selecionado_dialog = (String) arg0.getAdapter().getItem(arg2);
                item = 1;
                Toast.makeText(getBaseContext(), item_selecionado_dialog, Toast.LENGTH_SHORT).show();
            }
        });


        GrupoRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = GrupoRadio.getCheckedRadioButtonId();
                int_ext = (RadioButton) formDialog.findViewById(selectedId);
                if ((int_ext = (RadioButton) formDialog.findViewById(selectedId)) != null) {
                    if (int_ext.getText().equals("Interna")) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, interno);
                        lista_dialog = (ListView) formDialog.findViewById(R.id.list_Int_Ext);
                        lista_dialog.setAdapter(adapter);
                    }
                    if (int_ext.getText().equals("Externa")) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, externo);
                        lista_dialog = (ListView) formDialog.findViewById(R.id.list_Int_Ext);
                        lista_dialog.setAdapter(adapter);
                    }
                    lista_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            item_selecionado_dialog = (String) arg0.getAdapter().getItem(arg2);
                            item = 1;
                            Toast.makeText(getBaseContext(), item_selecionado_dialog, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if ((int_ext = (RadioButton) formDialog.findViewById(selectedId)) == null) {
                    Toast.makeText(getBaseContext(), "Selecione um checkbox", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button salvar = (Button) formDialog.findViewById(R.id.Bsalvar);
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!NomeObraDialog.toString().equals("") || !DataInicObraDialog.toString().equals("")) && (item == 1)) {
                    recadastro = (NomeObraDialog.getText().toString() + ";" + DataInicObraDialog.getText().toString() + ";" + int_ext.getText().toString() + ";" + item_selecionado_dialog);
                    reescreve_info_obra(ID_Lista);
                    formDialog.dismiss();
                    update_lista();
                } else {
                    Toast.makeText(getBaseContext(), "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancelar = (Button) formDialog.findViewById(R.id.Bcancela);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formDialog.dismiss();
            }
        });
    }


//--------------------------------------------------------------------------------------------------
//  Criação de arquivo e leitura de arquivos:

    public void reescreve_info_obra(int ID) {
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if (arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((inf_user = br.readLine()) != null) {
                    String[] pv = inf_user.split(";");
                    String [] temppv = recadastro.split(";");
                    ID_Info_User = Integer.parseInt(pv[0]);
                    if (ID == ID_Info_User) {
                        String aux = (pv[0] + ";" + temppv[0] + ";" + temppv[1] + ";" + temppv[2] + ";" + temppv[3]);
                        salvar.add(aux);
                    } else {
                        String aux = (pv[0] + ";" + pv[1] + ";" + pv[2] + ";" + pv[3] + ";" + pv[4]);
                        salvar.add(aux);
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for (int i = 0; i < salvar.size(); i++) {
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void pega_info_edita_obra(int ID){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((inf_user = br.readLine()) != null) {
                    String [] pv = inf_user.split(";");
                    ID_Info_User = Integer.parseInt(pv[0]);
                    if(ID == ID_Info_User){
                        camp1 = pv[1];
                        camp2 = pv[2];
                    }
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void removeinfo_obra_por_ID(int ID){
        String lstrNomeArq;
        File arq;
        String infos;
        int ID_Info;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    ID_Info = Integer.parseInt(pv[0]);
                    if(ID_Info != ID){
                        salvar.add(infos);
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void remove_info_Marker_por_ID(int ID){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Markers";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((inf_user = br.readLine()) != null) {
                    String [] pv = inf_user.split(";");
                    ID_Info_User = Integer.parseInt(pv[0]);
                    if(ID != ID_Info_User){
                        salvar.add(inf_user);
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void remove_Lat_Lng_por_ID(int ID){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Lat_Lng";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((inf_user = br.readLine()) != null) {
                    String [] pv = inf_user.split(";");
                    ID_Info_User = Integer.parseInt(pv[0]);
                    if(ID != ID_Info_User){
                        salvar.add(inf_user);
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
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

    public void click_Salvar(View v) {
        String lstrNomeArq;
        File arq;
        byte[] dados;
        FileOutputStream fos;
        BufferedOutputStream bos;
        try {
            lstrNomeArq = "Lat_Lng";
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                dados = laln.getBytes();
                bos = new BufferedOutputStream(new FileOutputStream(arq, true));
                bos.write(dados);
                bos.flush();
                bos.close();
            }
            if(!arq.exists()) {
                dados = laln.getBytes();
                fos = new FileOutputStream(arq);
                fos.write(dados);
                fos.flush();
                fos.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void click_add_info_marker(String infoMarker) {
        String lstrNomeArq;
        File arq;
        byte[] dados;
        FileOutputStream fos;
        BufferedOutputStream bos;
        try {
            lstrNomeArq = "Info_Markers";
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                dados = infoMarker.getBytes();
                bos = new BufferedOutputStream(new FileOutputStream(arq, true));
                bos.write(dados);
                bos.flush();
                bos.close();
            }
            if(!arq.exists()) {
                dados = infoMarker.getBytes();
                fos = new FileOutputStream(arq);
                fos.write(dados);
                fos.flush();
                fos.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

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
                    googleMap.addMarker(new MarkerOptions().position(pont).draggable(true));
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void countNum_latlng(){
        String lstrNomeArq;
        File arq;
        String latitude_longitude;
        int num;
        lstrNomeArq = "Lat_Lng";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((latitude_longitude = br.readLine()) != null) {
                    String [] pv = latitude_longitude.split(";");
                    num = Integer.parseInt(pv[0]);
                    numID_latlng = num;
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void countNum_infomarker(){
        String lstrNomeArq;
        File arq;
        String latitude_longitude;
        int num;
        lstrNomeArq = "Info_Markers";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((latitude_longitude = br.readLine()) != null) {
                    String [] pv = latitude_longitude.split(";");
                    num = Integer.parseInt(pv[0]);
                    numID_infomarker = num;
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void countNum_info_obras(){
        String lstrNomeArq;
        File arq;
        String id;
        int num;
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((id = br.readLine()) != null) {
                    String [] pv = id.split(";");
                    num = Integer.parseInt(pv[0]);
                    ID_anterior_info_obra = num;
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void removemarkers(double la, double lg){
        String lstrNomeArq;
        File arq;
        String latitude_longitude, latitude, longitude;
        int ID_Lat_Lng;
        Double lat, lng;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Lat_Lng";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((latitude_longitude = br.readLine()) != null) {
                    String [] pv = latitude_longitude.split(";");
                    ID_Lat_Lng = Integer.parseInt(pv[0]);
                    latitude = pv[1];
                    longitude = pv[2];
                    lat = Double.valueOf(latitude).doubleValue();
                    lng = Double.valueOf(longitude).doubleValue();
                    if(la != lat && lg != lng){
                        salvar.add(latitude_longitude);
                    }
                    if(la == lat && lg == lng){
                        ID_Temp = ID_Lat_Lng;
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void removemarkers_info(){
        String lstrNomeArq;
        File arq;
        String infos;
        int ID_Info;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Markers";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    ID_Info = Integer.parseInt(pv[0]);
                    if(ID_Info != ID_Temp){
                        salvar.add(infos);
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }


        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro aqui: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void removeinfo_obra(){
        String lstrNomeArq;
        File arq;
        String infos;
        int ID_Info;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    ID_Info = Integer.parseInt(pv[0]);
                    if(ID_Info != ID_Temp){
                        salvar.add(infos);
                    }
                }
                br.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
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

    public void renumera_ID_Lat_Lng(){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int num =1, temp = 0;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Lat_Lng";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while (br.readLine() != null) {
                    temp += 1;
                }
                br.close();
                BufferedReader br2 = new BufferedReader(new FileReader(arq));
                while ((inf_user = br2.readLine()) != null) {
                    String [] pv = inf_user.split(";");
                    if(num < temp + 1){
                        pv[0] = Integer.toString(num);
                        num += 1;
                    }
                    String aux = (pv[0] + ";" + pv[1] + ";" + pv[2]);
                    salvar.add(aux);
                }
                br2.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void renumera_ID_Info_Markers(){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int num =1, temp = 0;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Markers";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while (br.readLine() != null) {
                    temp += 1;
                }
                br.close();
                BufferedReader br2 = new BufferedReader(new FileReader(arq));
                while ((inf_user = br2.readLine()) != null) {
                    String [] pv = inf_user.split(";");
                    if(num < temp + 1){
                        pv[0] = Integer.toString(num);
                        num += 1;
                    }
                    String aux = (pv[0] + ";" + pv[1] + ";" + pv[2] + ";" + pv[3]);
                    salvar.add(aux);
                }
                br2.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void renumera_ID_Info_Obra(){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int num =1, temp = 0;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Obra";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while (br.readLine() != null) {
                    temp += 1;
                }
                br.close();
                BufferedReader br2 = new BufferedReader(new FileReader(arq));
                while ((inf_user = br2.readLine()) != null) {
                    String [] pv = inf_user.split(";");
                    if(num < temp + 1){
                        pv[0] = Integer.toString(num);
                        num += 1;
                    }
                    String aux = (pv[0] + ";" + pv[1] + ";" + pv[2] + ";" + pv[3] + ";" + pv[4]);
                    salvar.add(aux);
                }
                br2.close();
                FileWriter arq2 = new FileWriter(arq, true);
                arq2.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(arq));
                for(int i = 0; i < salvar.size(); i++){
                    bw.write(salvar.get(i));
                    bw.newLine();
                }
                bw.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    //----------------------------------------------------------------------------------------------------
//          Algoritmo de busca por nome de rua
private String downloadUrl(String strUrl) throws IOException {
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try{
        URL url = new URL(strUrl);


        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        iStream = urlConnection.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

        StringBuffer sb  = new StringBuffer();

        String line = "";
        while( ( line = br.readLine())  != null){
            sb.append(line);
        }

        data = sb.toString();

        br.close();

    }catch(Exception e){
        Log.d("Exception downloading", e.toString());
    }finally{
        iStream.close();
        urlConnection.disconnect();
    }

    return data;

}


    /** A class, to download Places from Geocoding webservice */
    private class DownloadTask extends AsyncTask<String, Integer, String>{

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){

            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();

            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    /** A class to parse the Geocoding Places in non-ui thread */
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){


            for(int i=0;i<list.size();i++){

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                lng = Double.parseDouble(hmPlace.get("lng"));

                laln = (Double.toString(lat) + ";" + Double.toString(lng) + " \n");

                // Getting name
                String name = hmPlace.get("formatted_address");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker
                markerOptions.title(name);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                // Placing a marker on the touched position
                if(adcmarker == 0) {
                    mMap.addMarker(markerOptions);
                    adcmarker = adcmarker + 1;
                } else {
                    Toast.makeText(getApplication(),"CEP ou endereço digitado incorreto, favor digitar corretamente o endereço",Toast.LENGTH_LONG).show();
                }

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                if (adcmarker == 1 || adcmarker > 1){
                    LBadd.setVisibility(View.VISIBLE);
                    LBcancel.setVisibility(View.VISIBLE);
                    LBinfo.setVisibility(View.VISIBLE);
                }

            }
        }
    }
//------------------------------------------------------------------------------------------------
}
