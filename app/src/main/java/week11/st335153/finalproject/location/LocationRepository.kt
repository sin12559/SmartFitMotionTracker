package week11.st335153.finalproject.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val time: Long
)

class LocationRepository(context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(
        context.applicationContext
    )

    /**
     * Stream of location updates every [intervalMs] ms.
     * Caller must ensure location permission is granted before using this.
     */
    @SuppressLint("MissingPermission")
    fun observeLocation(intervalMs: Long = 10_000L): Flow<LocationData> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMs
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location = result.lastLocation ?: return
                trySend(
                    LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        time = location.time
                    )
                )
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }

    /**
     * One-shot fetch of last known location.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): LocationData? =
        suspendCancellableCoroutine { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location == null) {
                        cont.resume(null)
                    } else {
                        cont.resume(
                            LocationData(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                time = location.time
                            )
                        )
                    }
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
}
