package com.example.frefalllib

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.frefalllib.db.FreeFallDao
import com.example.frefalllib.db.FreeFallDatabase
import com.example.frefalllib.db.FallObject
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author Tomislav Curis
 */

class FreeFallService: Service(), SensorEventListener, CoroutineScope {

    companion object {

        lateinit var onSensorChanged: OnSensorChanged
        lateinit var onFallsListFetch: OnFallsListFetch
        lateinit var dao: FreeFallDao
        private var fetchingDB = FetchingDB()

        fun startService(context: Context, onSensorChanged: OnSensorChanged, onFallsListFetch: OnFallsListFetch) {

            dao = FreeFallDatabase.getInstance(context).getFreeFallDao()

            this.onSensorChanged = onSensorChanged
            this.onFallsListFetch = onFallsListFetch

            context.startService(Intent(context, FreeFallService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, FreeFallService::class.java))
        }

        fun getAllFallObjects() {
            fetchingDB.getFalls()
        }
    }

    class FetchingDB: CoroutineScope {
        private val cancelJob = Job()
        override val coroutineContext: CoroutineContext
            get() = cancelJob + Dispatchers.Default

        fun getFalls() {
            launch {
                val result = dao.getFallObjects() as ArrayList<FallObject>
                withContext(Dispatchers.Main) {
                    onFallsListFetch.onFallsFetch(result)
                }
            }
        }
    }


    private lateinit var sensorManager: SensorManager
    var mSensor: Sensor? = null
    private var lastMovementFall: Long = 0
    private var movementStart: Long = 0
    private var ct = 2

    private val CHANNEL_ID = "ChannelFreeFallService"
    private val CHANNEL_ID_NOTIFICATIONS = "ChannelFreeFallServiceNotifications"

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (mSensor == null) {
            mSensor = if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            } else {
                null
            }
        }

        startForeground()

        mSensor?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }

        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder?  = null

    override fun onDestroy() {
        super.onDestroy()

        sensorManager.unregisterListener(this)
        stopSelf()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            movementStart = System.currentTimeMillis()

            val loX = event.values[0]
            val loY = event.values[1]
            val loZ = event.values[2]

            val loAccelerationReader = sqrt(
                loX.toDouble().pow(2.0)
                        + loY.toDouble().pow(2.0)
                        + loZ.toDouble().pow(2.0)
            )

            val precision = DecimalFormat("0.00")
            val ldAccRound = java.lang.Double.parseDouble(precision.format(loAccelerationReader))

            // precision/fall detection and more than 1000ms after last fall
            if (ldAccRound > 0.3 && ldAccRound < 1.2 && (movementStart - lastMovementFall) > 1000) {

                val timeStamp = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").format(Date(System.currentTimeMillis()))
                val duration = (System.currentTimeMillis() - movementStart).toString()

                lastMovementFall = System.currentTimeMillis()

                val fallObject = FallObject(timeStamp, duration)
                saveLastFall(fallObject)
                onSensorChanged.onFall(fallObject)

                showFallNotification(timeStamp, duration)

            }
        }
    }

    fun saveLastFall(fallObject: FallObject) {

        launch {
            withContext(Dispatchers.Default) {
                dao.saveFallObject(fallObject)
            }
        }
    }

    private fun startForeground(){

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "My Background Service"
            val chan = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan);

            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("FreeFallService")
                .setContentText("Foreground service is running")
                .setSmallIcon(R.drawable.ic_stat_name)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FreeFallService")
                .setContentText("Foreground service is running")
                .setSmallIcon(R.drawable.ic_stat_name)
                .build()
        }

        startForeground(1, notification)
    }

    fun showFallNotification(timeStamp: String, duration: String) {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "My Background Service2"
            val chan = NotificationChannel(CHANNEL_ID_NOTIFICATIONS, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(chan);

            val builder = Notification.Builder(this, CHANNEL_ID_NOTIFICATIONS)
                .setContentTitle("FreeFallService")
                .setContentText("Last fall $timeStamp with duration of $duration ms")
                .setSmallIcon(R.drawable.ic_stat_name)

            manager.notify(ct++, builder.build())

        }else{

            val builder = NotificationCompat.Builder(this, CHANNEL_ID_NOTIFICATIONS)
                .setContentTitle("FreeFallService")
                .setContentText("Last fall $timeStamp with duration of $duration ms")
                .setSmallIcon(R.drawable.ic_stat_name)

            manager.notify(ct++, builder.build())
        }
    }
}

