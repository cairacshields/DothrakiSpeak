package com.example.dothrakispeak

import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var compositeDisposable: CompositeDisposable
    lateinit var editText: EditText
    lateinit var translate: Button
    lateinit var newText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compositeDisposable = CompositeDisposable()
        editText = this.findViewById(R.id.et)
        translate = this.findViewById(R.id.translate)
        newText = this.findViewById(R.id.new_text)


        //Upon button press, we check for value in EditText View and then call out data load method
        translate.setOnClickListener {
            val text = editText.text.toString()
            if (editText.text.isNotEmpty()) {
                loadData(text)
            } else {
                //Nothing typed in the edit text
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadData(query: String) {
        //Create the Retrofit instance...
        val requestInterface = Retrofit.Builder()
            //Set the API’s base URL//
            .baseUrl(BASE_URL)
            //Specify the converter factory to use for serialization and deserialization//
            .addConverterFactory(GsonConverterFactory.create())
            //Add a call adapter factory to support RxJava return types//
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            //Get a usable Retrofit object by calling .build()//
            .build().create(GetData::class.java)


        //Add all RxJava disposables to a CompositeDisposable//
        compositeDisposable.add(requestInterface.getData(query)
            //Send the Observable’s notifications to the main UI thread//
            .observeOn(AndroidSchedulers.mainThread())
            //Subscribe to the Observer away from the main UI thread//
            .subscribeOn(Schedulers.io())
            //Will pass results to the handleResponse method on main thread//
            .subscribe(this::handleResponse))

        Log.d("MAIN_ACTIVITY", "In Load Data")
    }

    private fun handleResponse(dText: DothrakiObject) {
        //Here is where we will receive our response data... Hopefully
        editText.text.clear()
        newText.visibility = View.VISIBLE
        newText.text = dText.contents.translated
        Log.d("MAIN_ACTIVITY", "In handle response ${dText.contents.translated}")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Clear all your disposables//
        compositeDisposable.clear()
    }

    companion object {
        const val BASE_URL = "https://api.funtranslations.com/translate/"
    }
}
