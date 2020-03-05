package com.example.freefall.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.freefall.R
import com.example.freefall.app.adapter.FreeFallListAdapter
import kotlinx.android.synthetic.main.activity_main.*

import com.example.frefalllib.FreeFallService
import com.example.frefalllib.OnFallsListFetch
import com.example.frefalllib.OnSensorChanged
import com.example.frefalllib.db.FallObject


/**
 * @author Tomislav Curis
 */

class MainActivity : AppCompatActivity(), OnSensorChanged, OnFallsListFetch, View.OnClickListener {


    private lateinit var freeFallListAdapter: FreeFallListAdapter
    private var fallList = ArrayList<FallObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            verifyServicePermissions(this, 1234)
        else
            startService()

        setView()

    }

    private fun verifyServicePermissions(activity: Activity, requestCode: Int): Boolean {
        // Check if we have service permission
        val permission: Int = ActivityCompat.checkSelfPermission(activity, Manifest.permission.FOREGROUND_SERVICE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.FOREGROUND_SERVICE), requestCode)
            return false;
        } else
            startService()

        return true
    }


    private fun  startService() {
        FreeFallService.startService(this, this, this)
    }

    private fun stopService() {
        FreeFallService.stopService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFall(fallObject: FallObject) {

        Toast.makeText(this, "FALL", Toast.LENGTH_SHORT).show()

        fallList.add(fallObject)
        val newList = ArrayList<FallObject>()
        newList.addAll(fallList)
        freeFallListAdapter.submitList(newList)

    }

    override fun onFallsFetch(fallsList: ArrayList<FallObject>) {
        freeFallListAdapter.submitList(fallsList)
    }

    override fun onClick(v: View) {
        if(v.id == fallsCompleteButton.id){
            FreeFallService.getAllFallObjects()
        }

        if(v.id == fallsCurrentButton.id){
            val newList = ArrayList<FallObject>()
            newList.addAll(fallList)
            freeFallListAdapter.submitList(newList)
        }
    }

    private fun setView() {

        fallsCompleteButton.setOnClickListener(this)
        fallsCurrentButton.setOnClickListener(this)

        freeFallListAdapter = FreeFallListAdapter()
        with(fallsRv) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = freeFallListAdapter
        }

    }

}


