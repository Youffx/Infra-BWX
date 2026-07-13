package com.infrabwx.app.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.Locale

object LocationUtils {

    private const val BANYUWANGI_MIN_LAT = -8.789
    private const val BANYUWANGI_MAX_LAT = -8.000
    private const val BANYUWANGI_MIN_LNG = 113.800
    private const val BANYUWANGI_MAX_LNG = 114.800

    fun isInBanyuwangi(lat: Double, lng: Double): Boolean {
        return lat in BANYUWANGI_MIN_LAT..BANYUWANGI_MAX_LAT &&
                lng in BANYUWANGI_MIN_LNG..BANYUWANGI_MAX_LNG
    }

    fun getLastLocation(context: Context): Task<android.location.Location> {
        val fusedClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        return fusedClient.lastLocation
    }

    fun getKecamatanFromLocation(context: Context, lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale("id", "ID"))
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val adminArea = address.adminArea ?: ""
                val subAdminArea = address.subAdminArea ?: ""
                val locality = address.locality ?: ""
                val subLocality = address.subLocality ?: ""

                subLocality.takeIf { it.isNotEmpty() }
                    ?: locality.takeIf { it.isNotEmpty() }
                    ?: subAdminArea.takeIf { it.isNotEmpty() }
                    ?: adminArea
            } else {
                "Tidak diketahui"
            }
        } catch (e: Exception) {
            "Tidak diketahui"
        }
    }
}
