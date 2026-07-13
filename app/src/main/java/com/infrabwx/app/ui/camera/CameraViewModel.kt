package com.infrabwx.app.ui.camera

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.infrabwx.app.data.remote.AppsScriptRepository
import com.infrabwx.app.util.ImageUtils
import com.infrabwx.app.util.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CameraUiState(
    val isCaptured: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val kecamatan: String = "",
    val isInBanyuwangi: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val submitError: String? = null,
    val isLocationLoading: Boolean = false
)

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppsScriptRepository()
    private val _state = MutableStateFlow(CameraUiState())
    val state: StateFlow<CameraUiState> = _state.asStateFlow()

    fun onImageCaptured(bitmap: Bitmap) {
        _state.value = _state.value.copy(
            isCaptured = false,
            isLocationLoading = true
        )
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val locationTask = LocationUtils.getLastLocation(getApplication())
                try {
                    val location = com.google.android.gms.tasks.Tasks.await(locationTask)
                    if (location != null) {
                        val kecamatan = LocationUtils.getKecamatanFromLocation(
                            getApplication(),
                            location.latitude,
                            location.longitude
                        )
                        val isInBanyuwangi = LocationUtils.isInBanyuwangi(
                            location.latitude,
                            location.longitude
                        )
                        Triple(location.latitude, location.longitude, kecamatan to isInBanyuwangi)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (result != null) {
                val lat = result.first
                val lng = result.second
                val locationInfo = result.third
                val kecName = locationInfo.first
                val inBanyuwangi = locationInfo.second
                _state.value = _state.value.copy(
                    isCaptured = true,
                    capturedBitmap = bitmap,
                    latitude = lat,
                    longitude = lng,
                    kecamatan = kecName,
                    isInBanyuwangi = inBanyuwangi,
                    isLocationLoading = false
                )
            } else {
                _state.value = _state.value.copy(
                    isCaptured = true,
                    capturedBitmap = bitmap,
                    isLocationLoading = false
                )
            }
        }
    }

    fun submitReport(categoryId: String) {
        val currentState = _state.value
        val bitmap = currentState.capturedBitmap ?: return

        _state.value = currentState.copy(isSubmitting = true, submitError = null)

        viewModelScope.launch {
            val watermarked = withContext(Dispatchers.Default) {
                ImageUtils.drawWatermark(
                    bitmap = bitmap,
                    category = categoryId,
                    latitude = currentState.latitude,
                    longitude = currentState.longitude,
                    kecamatan = currentState.kecamatan
                )
            }
            val base64 = ImageUtils.bitmapToBase64(watermarked)

            val result = repository.submitReport(
                imageBase64 = base64,
                category = categoryId,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                kecamatan = currentState.kecamatan
            )

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        submitSuccess = true
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        submitError = error.message ?: "Gagal mengirim laporan"
                    )
                }
            )
        }
    }

    fun resetCapture() {
        _state.value = CameraUiState()
    }

    fun clearError() {
        _state.value = _state.value.copy(submitError = null, submitSuccess = false)
    }
}
