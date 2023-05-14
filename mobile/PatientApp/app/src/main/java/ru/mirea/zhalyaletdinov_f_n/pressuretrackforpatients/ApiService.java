package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/create_patient/")
    Call<List<User>> getUsers();
}
//    @GET("users/")
//    Call<List<User>> getUsers();
//
//    @GET("users/{id}/")
//    Call<User> getUserById(@Path("id") int id);
//
//    @POST("users/")
//    Call<User> createUser(@Body User user);
//
//    @PUT("users/{id}/")
//    Call<User> updateUser(@Path("id") int id, @Body User user);
//
//    @DELETE("users/{id}/")
//    Call<Void> deleteUser(@Path("id") int id);
