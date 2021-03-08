package com.myapplication.postexpandedlist;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    String BASE_URL = "https://jsonplaceholder.typicode.com/posts/";
    String COMMENTS_URL = "https://jsonplaceholder.typicode.com/comments/";

    @GET(".")
    Call<List<Post>> getPosts();

    @GET(".")
    Call<List<Comment>> getComments();

}
