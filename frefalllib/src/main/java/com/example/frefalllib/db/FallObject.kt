package com.example.frefalllib.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @author Tomislav Curis
 */

@Entity(tableName = "fallObjects")
class FallObject(

    @PrimaryKey
    var timestamp: String = "",

    var fallDuration: String = ""

) : Serializable