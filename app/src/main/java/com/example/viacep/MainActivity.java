package com.example.viacep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.viacep.R;
import com.example.viacep.CEP;
import com.example.viacep.Mascara;
import com.example.viacep.RestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = "https://viacep.com.br/ws/";

    private Retrofit retrofitCEP;
    private Button btnConsultarCEP;
    private TextInputEditText txtCEP, txtLogradouro, txtComplemento, txtBairro, txtUF, txtLocalidade;
    private TextInputLayout layCEP;
    private ProgressBar progressBarCEP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layCEP = findViewById(R.id.txtinplayCEP);
        txtCEP = findViewById(R.id.txtinpedtCEP);
        txtLogradouro = findViewById(R.id.txtinpedtLogradouro);
        txtComplemento = findViewById(R.id.txtinpedtComplemento);
        txtBairro = findViewById(R.id.txtinpedtBairro);
        txtUF = findViewById(R.id.txtinpedtUF);
        txtLocalidade = findViewById(R.id.txtinpedtLocalidade);
        btnConsultarCEP = findViewById(R.id.btnConsultarCEP);
        progressBarCEP = findViewById(R.id.progressBarCEP);

        //configurando como invisível
        progressBarCEP.setVisibility(View.GONE);

        //Aplicando a máscara para CEP
        txtCEP.addTextChangedListener(Mascara.insert(Mascara.MASCARA_CEP, txtCEP));

        //configura os recursos do retrofit
        retrofitCEP = new Retrofit.Builder()
                .baseUrl(URL)                                       //endereço do webservice
                .addConverterFactory(GsonConverterFactory.create()) //conversor
                .build();

        btnConsultarCEP.setOnClickListener(this);
    }

    private Boolean validarCampos() {
        // Inicializa a variável 'status' como verdadeira (true) assumindo que os campos estão válidos
        Boolean status = true;

        // Obtém o valor do CEP do EditText 'txtCEP' e remove espaços extras no início e no final
        String cep = txtCEP.getText().toString().trim();

        // Verifica se o campo do CEP está vazio
        if (cep.isEmpty()) {
            // Se o campo estiver vazio, define uma mensagem de erro no EditText 'txtCEP'
            // indicando que é necessário digitar um CEP válido
            txtCEP.setError("Digite um CEP válido.");
            // Define 'status' como falso (false) para indicar que os campos não estão válidos
            status = false;
        }

        // Verifica se o comprimento do CEP é maior que 1 e menor que 10 (espera-se um CEP com 8 dígitos)
        if ((cep.length() > 1) && (cep.length() < 10)) {
            // Se o comprimento do CEP não estiver no intervalo válido, define uma mensagem de erro
            // no EditText 'txtCEP' indicando que o CEP deve possuir 8 dígitos
            txtCEP.setError("O CEP deve possuir 8 dígitos");
            // Define 'status' como falso (false) para indicar que os campos não estão válidos
            status = false;
        }

        // Retorna o valor de 'status', que indica se os campos são válidos ou não
        return status;
    }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnConsultarCEP) {
                if (validarCampos()) {
                    esconderTeclado();
                    consultarCEP();
                }
            }
        }

        private void esconderTeclado() {
            // Cria uma instância de InputMethodManager para gerenciar o teclado virtual
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            // Esconde o teclado virtual da tela atual, se estiver visível
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    private void consultarCEP() {
        String sCep = txtCEP.getText().toString().trim();

        //removendo o ponto e o traço do padrão CEP
        sCep = sCep.replaceAll("[.-]+", "");

        //instanciando a interface
        RestService restService = retrofitCEP.create(RestService.class);

        //passando os dados para consulta
        Call<CEP> call = restService.consultarCEP(sCep);

        //exibindo a progressbar
        progressBarCEP.setVisibility(View.VISIBLE);

        //colocando a requisição na fila para execução
        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if (response.isSuccessful()) {
                    CEP cep = response.body();
                    txtLogradouro.setText(cep.getLogradouro());
                    txtComplemento.setText(cep.getComplemento());
                    txtBairro.setText(cep.getBairro());
                    txtUF.setText(cep.getUf());
                    txtLocalidade.setText(cep.getLocalidade());
                    Toast.makeText(getApplicationContext(), "CEP consultado com sucesso", Toast.LENGTH_LONG).show();

                    //escondendo a progressbar
                    progressBarCEP.setVisibility(View.GONE);

                    //TODO desabilitar escrita nos campos com preenchimento automático
                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro ao tentar consultar o CEP. Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();

                //escondendo a progressbar
                progressBarCEP.setVisibility(View.GONE);
            }
        });
    }

}