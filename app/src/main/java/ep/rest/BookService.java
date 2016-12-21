package ep.rest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class BookService {
    interface RestApi {
        String URL = "http://192.168.34.117/netbeans/mvc-rest/api/";

        @GET("books")
        Call<List<Book>> getAll();

        @GET("books/{id}")
        Call<Book> get(@Path("id") int id);

        @FormUrlEncoded
        @POST("books")
        Call<Void> insert(@Field("author") String author,
                          @Field("title") String title,
                          @Field("price") double price,
                          @Field("year") int year,
                          @Field("description") String description);

        @FormUrlEncoded
        @PUT("books/{id}")
        Call<Void> update(@Path("id") int id,
                          @Field("author") String author,
                          @Field("title") String title,
                          @Field("price") double price,
                          @Field("year") int year,
                          @Field("description") String description);
    }

    private static RestApi instance;

    public static synchronized RestApi getInstance() {
        if (instance == null) {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RestApi.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(RestApi.class);
        }

        return instance;
    }
}
