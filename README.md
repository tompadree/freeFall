# FreeFall
FreeFall is a library for detecting free fall of the android device. It is based on the accelerometer sensor inside mobile phones.
https://developer.android.com/guide/topics/sensors/sensors_motion#sensors-motion-accel

Project also contain sample app for testing the library.

# Usage
```kotlin 
        FreeFallService.startService(context,
            object : OnSensorChanged {
                override fun onFall(fallObject: FallObject) {
                    // code when free fall is detected
                }
            },
            object : OnFallsListFetch {
                override fun onFallsFetch(fallsList: ArrayList<FallObject>) {
                    // handling call for fetching all events from the db
                }
            })
