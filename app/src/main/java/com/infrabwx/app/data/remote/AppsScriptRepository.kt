package com.infrabwx.app.data.remote

import com.infrabwx.app.data.model.RankingItem
import com.infrabwx.app.data.model.RankingResponse
import com.infrabwx.app.data.model.ReportRequest
import com.infrabwx.app.data.model.ReportResponse

class AppsScriptRepository {

    private val api = RetrofitClient.apiService

    suspend fun submitReport(
        imageBase64: String,
        category: String,
        latitude: Double,
        longitude: Double,
        kecamatan: String
    ): Result<ReportResponse> {
        return try {
            val request = ReportRequest(
                imageBase64 = imageBase64,
                category = category,
                latitude = latitude,
                longitude = longitude,
                kecamatan = kecamatan
            )
            val response = api.submitReport(request)
            if (response.status == "success") {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRanking(category: String): Result<List<RankingItem>> {
        return try {
            val response = api.getRanking(category = category)
            if (response.status == "success") {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.status))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
