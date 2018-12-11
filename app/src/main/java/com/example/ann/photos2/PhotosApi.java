package com.example.ann.photos2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhotosApi {
    @GET("photos_public.gne?tagmode=any&per_page=20&format=json&nojsoncallback=1")
    Call<Photos> getPhotos(@Query("tags") String tags);
}
