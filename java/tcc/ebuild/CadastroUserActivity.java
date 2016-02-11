package tcc.ebuild;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * Created by Gabriel Duarte Momberg on 14/12/2015.
 */

public class CadastroUserActivity extends AppCompatActivity {

    Button salva, cancela;
    EditText nome, email, login, senha, resenha;
    String info;
    int numID_Info_User, ID_Info_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_user);

        cadastro_info();
    }

    private void cadastro_info(){

        nome = (EditText) findViewById(R.id.nome);
        email = (EditText) findViewById(R.id.email);
        login = (EditText) findViewById(R.id.login);
        senha = (EditText) findViewById(R.id.senha);
        resenha = (EditText) findViewById(R.id.resenha);

        salva = (Button) findViewById(R.id.Bsalvarcadastro);
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
                    countNum_Info_Users();
                    if(numID_Info_User == 0){
                        ID_Info_User = 1;
                    }
                    if(numID_Info_User > 1 || numID_Info_User == 1){
                        ID_Info_User = numID_Info_User + 1;
                    }
                    info = (Integer.toString(ID_Info_User) + ";" + nome.getText().toString() + ";" + email.getText().toString() + ";" + login.getText().toString() + ";" + senha.getText().toString() + "\n");
                    click_Salvar();
                    finish();
                }
            }
        });

        cancela = (Button) findViewById(R.id.Bcancelacadastro);
        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            lstrNomeArq = "Info_Users";
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                dados = info.getBytes();
                bos = new BufferedOutputStream(new FileOutputStream(arq, true));
                bos.write(dados);
                bos.flush();
                bos.close();
                Toast.makeText(getApplicationContext(), "Arquivo atualizado", Toast.LENGTH_LONG).show();
            }
            if(!arq.exists()) {
                dados = info.getBytes();
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

    public void countNum_Info_Users(){
        String lstrNomeArq;
        File arq;
        String id;
        int num;
        lstrNomeArq = "Info_Users";
        try {
            arq = new File(Environment.getExternalStorageDirectory(), lstrNomeArq);
            if(arq.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(arq));
                while ((id = br.readLine()) != null) {
                    String [] pv = id.split(";");
                    num = Integer.parseInt(pv[0]);
                    numID_Info_User = num;
                }
                br.close();
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Erro : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
