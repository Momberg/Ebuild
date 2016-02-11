package tcc.ebuild;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * Created by Gabriel Duarte Momberg on 14/12/2015.
 */

public class FormularioActivity extends AppCompatActivity {

    RadioButton int_ext;
    RadioGroup GrupoRadio;
    Button salvar, cancelar;
    String[] interno, externo;
    String item_selecionado, infos;
    EditText NomeObra, DataInicObra;
    ListView lista;
    int item = 0, numID_Info_Obra = 0, ID_Info_Obra = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        interno = new String[] {"Verificar a necessidade","Elaboração dos estudos técnicos preliminares","Licença ambiental prévia","Elaboração do projeto básico","Elaboração do projeto executivo"};
        externo = new String[] {"Publicação do edital","Licitação","Contrataçao e designação do fiscal da obra","Pagamento seguindo o cronograma físico-financeiro e ordem cronológica","Recebimento da obra","Devolução de garantia","Registros finais"};
        info_obras();
    }

    public void info_obras() {


        NomeObra = (EditText) findViewById(R.id.nome);
        DataInicObra = (EditText) findViewById(R.id.data);


        GrupoRadio = (RadioGroup) findViewById(R.id.radio_Ext_Int);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, interno);
        lista = (ListView) findViewById(R.id.list_Int_Ext);
        lista.setAdapter(adapter);
        int temId = GrupoRadio.getCheckedRadioButtonId();
        int_ext = (RadioButton) findViewById(temId);
        lista.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                item_selecionado = (String) arg0.getAdapter().getItem(arg2);
                item = 1;
                Toast.makeText(getBaseContext(), item_selecionado, Toast.LENGTH_SHORT).show();
            }
        });


        GrupoRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = GrupoRadio.getCheckedRadioButtonId();
                int_ext = (RadioButton) findViewById(selectedId);
                if ((int_ext = (RadioButton) findViewById(selectedId)) != null) {
                    if (int_ext.getText().equals("Interna")) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, interno);
                        lista = (ListView) findViewById(R.id.list_Int_Ext);
                        lista.setAdapter(adapter);
                    }
                    if (int_ext.getText().equals("Externa")) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, externo);
                        lista = (ListView) findViewById(R.id.list_Int_Ext);
                        lista.setAdapter(adapter);
                    }
                    lista.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            item_selecionado = (String) arg0.getAdapter().getItem(arg2);
                            item = 1;
                            Toast.makeText(getBaseContext(), item_selecionado, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if ((int_ext = (RadioButton) findViewById(selectedId)) == null) {
                    Toast.makeText(getBaseContext(), "Selecione um checkbox", Toast.LENGTH_SHORT).show();
                }
            }
        });



        countNum_Info_Obra();
        if(numID_Info_Obra == 0){
            ID_Info_Obra = 1;
        }
        if(numID_Info_Obra > 1 || numID_Info_Obra == 1){
            ID_Info_Obra = numID_Info_Obra + 1;
        }

        salvar = (Button) findViewById(R.id.Bsalvar);
        salvar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!NomeObra.toString().equals("") || !DataInicObra.toString().equals("")) && (item == 1)){
                    infos = (Integer.toString(ID_Info_Obra)+ ";" + NomeObra.getText().toString() + ";" + DataInicObra.getText().toString() + ";" + int_ext.getText().toString() + ";" + item_selecionado + "\n");
                    click_Salvar();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelar = (Button) findViewById(R.id.Bcancela);
        cancelar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void click_Salvar() {
        String lstrNomeArq;
        File arq;
        byte[] dados;
        FileOutputStream fos;
        BufferedOutputStream bos;
        try {
            lstrNomeArq = "Info_Obra";
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                dados = infos.getBytes();
                bos = new BufferedOutputStream(new FileOutputStream(arq, true));
                bos.write(dados);
                bos.flush();
                bos.close();
                Toast.makeText(getApplicationContext(), "Arquivo atualizado", Toast.LENGTH_LONG).show();
            }
            if(!arq.exists()) {
                dados = infos.getBytes();
                fos = new FileOutputStream(arq);
                fos.write(dados);
                fos.flush();
                fos.close();
                Toast.makeText(getApplicationContext(), "Arquivo gerado", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void countNum_Info_Obra(){
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
                    numID_Info_Obra = num;
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
