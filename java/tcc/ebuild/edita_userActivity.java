package tcc.ebuild;

import android.app.Dialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by Gabriel Duarte Momberg on 14/12/2015.
 */

public class edita_userActivity extends AppCompatActivity {

    String item_selecionado, concatena = "", recadastro;
    EditText nome, email, login, senha, resenha;
    String camp1, camp2, camp3, camp4;
    ArrayList<String> info_lista = new ArrayList();
    int ID_Lista = 0, selecionado = 0;
    ListView lista;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_user);
        cadastro_info();
    }

    private void seleciona_item_lista(){
        adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, info_lista);
        lista = (ListView) findViewById(R.id.lista_edita_user);
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
        imprime_lista();
        seleciona_item_lista();
    }

    private void cadastro_info(){
        imprime_lista();
        seleciona_item_lista();
        renumera_ID();
        Button remove = (Button) findViewById(R.id.Bremove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selecionado == 1) {
                    remove_info_user(ID_Lista);
                    renumera_ID();
                    update_lista();
                    selecionado = 0;
                } else {
                    Toast.makeText(getApplication(), "Selecione um usuário", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button edita = (Button) findViewById(R.id.Bedita);
        edita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selecionado == 1) {
                    pega_info_edita(ID_Lista);
                    recadastro_info();
                    update_lista();
                    selecionado = 0;
                } else {
                    Toast.makeText(getApplication(), "Selecione um usuário", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancela = (Button) findViewById(R.id.Bcancela);
        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void imprime_lista(){
        String lstrNomeArq;
        File arq;
        String infos;
        lstrNomeArq = "Info_Users";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((infos = br.readLine()) != null) {
                    String [] pv = infos.split(";");
                    concatena = ("Nome: " + pv[1] + "\n" + "Email: " + pv[2] + "\n" + "Login: " + pv[3]);
                    info_lista.add(concatena);
                }
                br.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro no imprime lista de: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void recadastro_info(){
        final Dialog menuDialog = new Dialog(this);
        menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog.setContentView(R.layout.dialog_edita_user);
        menuDialog.setCanceledOnTouchOutside(false);
        menuDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        nome = (EditText) menuDialog.findViewById(R.id.nome);
        email = (EditText) menuDialog.findViewById(R.id.email);
        login = (EditText) menuDialog.findViewById(R.id.login);
        senha = (EditText) menuDialog.findViewById(R.id.senha);
        resenha = (EditText) menuDialog.findViewById(R.id.resenha);

        nome.setText(camp1);
        email.setText(camp2);
        login.setText(camp3);
        senha.setText(camp4);

        Button salva = (Button) menuDialog.findViewById(R.id.Bsalvarcadastro);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome.getText().toString().equals("") || email.getText().toString().equals("") || login.getText().toString().equals("") || senha.getText().toString().equals("") || resenha.getText().toString().equals("")) {
                    Toast.makeText(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    if(!senha.getText().toString().equals(resenha.getText().toString())){
                        Toast.makeText(getBaseContext(), "Senha não correspondente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!nome.getText().toString().equals("") || !email.getText().toString().equals("") || !login.getText().toString().equals("") || !senha.getText().toString().equals("") || !resenha.getText().toString().equals("")) {
                    recadastro = (nome.getText().toString() + ";" + email.getText().toString() + ";" + login.getText().toString() + ";" + senha.getText().toString());
                }
                reescreve_info(ID_Lista);
                renumera_ID();
                update_lista();
                menuDialog.dismiss();
            }
        });

        Button cancela = (Button) menuDialog.findViewById(R.id.Bcancelacadastro);
        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
            }
        });
        menuDialog.show();
    }

    public void remove_info_user(int ID){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Users";
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

    public void renumera_ID(){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int num =1, temp = 0;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Users";
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

    private void pega_info_edita(int ID){
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        lstrNomeArq = "Info_Users";
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
                        camp3 = pv[3];
                        camp4 = pv[4];
                    }
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void reescreve_info(int ID) {
        String lstrNomeArq;
        File arq;
        String inf_user;
        int ID_Info_User;
        ArrayList<String> salvar = new ArrayList();
        lstrNomeArq = "Info_Users";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if (arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((inf_user = br.readLine()) != null) {
                    String[] pv = inf_user.split(";");
                    String [] temp = recadastro.split(";");
                    ID_Info_User = Integer.parseInt(pv[0]);
                    if (ID == ID_Info_User) {
                        String aux = (pv[0] + ";" + temp[0] + ";" + temp[1] + ";" + temp[2] + ";" + temp[3]);
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
            Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
