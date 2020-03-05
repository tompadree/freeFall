package com.example.frefalllib

import com.example.frefalllib.db.FallObject

/**
 * @author Tomislav Curis
 */

interface OnSensorChanged {

    fun onFall(fallObject: FallObject)

}

interface OnFallsListFetch {

    fun onFallsFetch(fallsList: ArrayList<FallObject>)

}