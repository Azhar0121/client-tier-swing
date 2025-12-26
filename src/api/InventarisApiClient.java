package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.reflect.TypeToken;

import model.Inventaris;

public class InventarisApiClient {

        private static final String BASE_URL = "http://localhost/realtime-application-tier-php/public/inventaris";

        private final HttpClient client = HttpClient.newHttpClient();
        private final Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();

        public List<Inventaris> findAll() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL))
                                .GET()
                                .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ApiResponse<List<Inventaris>> apiResp = gson.fromJson(response.body(),
                                new TypeToken<ApiResponse<List<Inventaris>>>() {
                                }.getType());

                if (!apiResp.success)
                        throw new Exception(apiResp.message);

                return apiResp.data;
        }

        public void create(Inventaris i) throws Exception {
                var requestBody = new HashMap<String, Object>();
                requestBody.put("nama_barang", i.getNamaBarang());
                requestBody.put("kategori", i.getKategori());
                requestBody.put("kondisi", i.getKondisi());
                requestBody.put("jumlah", i.getJumlah());

                String json = gson.toJson(requestBody);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                handleResponse(response);
        }

        public void update(Inventaris i) throws Exception {
                var requestBody = new HashMap<String, Object>();
                requestBody.put("nama_barang", i.getNamaBarang());
                requestBody.put("kategori", i.getKategori());
                requestBody.put("kondisi", i.getKondisi());
                requestBody.put("jumlah", i.getJumlah());

                String json = gson.toJson(requestBody);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/" + i.getId()))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(json))
                                .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                handleResponse(response);
        }

        public void delete(int id) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/" + id))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                handleResponse(response);
        }

        private static class ApiResponse<T> {
                boolean success;
                T data;
                String message;
        }

        private void handleResponse(HttpResponse<String> response) throws Exception {
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
                }

                try {
                        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);
                        if (apiResp == null)
                                throw new Exception(response.body());
                        if (!apiResp.success)
                                throw new Exception(apiResp.message);
                } catch (com.google.gson.JsonSyntaxException ex) {
                        // Jika response bukan JSON yang diharapkan, munculkan body mentah untuk
                        // debugging
                        throw new Exception(response.body());
                }
        }
}
