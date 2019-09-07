package com.example.dothrakispeak

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GetData {
    //This interface is used to define our Retrofit base methods
        //Describe the request type and the relative URL//
    @GET("dothraki.json")
    fun getData(@Query("text") englishText: String) : Observable<DothrakiObject>
}