package br.com.gyselle.conversordemoedas.conection;

import com.google.gson.Gson;
import br.com.gyselle.conversordemoedas.models.Conversor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConversorMoedaApi {

    public Conversor buscaConversor(String baseCode, String targetCode, double valor){
        String BASE_URL = "https://v6.exchangerate-api.com/v6/";
        String API_KEY = "6de5d4586ccc0c838124159b";

        URI endereco = URI.create(BASE_URL + API_KEY + "/pair/" + baseCode + "/" + targetCode + "/" + valor);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(endereco)
                .build();

        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), Conversor.class);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível obter a cotação. Verifique os dados informados e tente novamente!");
        }
    }
}