package com.example.viacep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.example.viacep.CEP;

public interface RestService {
    @GET("{cep}/json/")
    Call<CEP> consultarCEP(@Path("cep") String cep);

}
