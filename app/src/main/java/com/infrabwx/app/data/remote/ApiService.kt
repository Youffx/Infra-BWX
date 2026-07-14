package com.infrabwx.app.data.remote

import com.infrabwx.app.data.model.RankingResponse
import com.infrabwx.app.data.model.ReportLocationsResponse
import com.infrabwx.app.data.model.ReportRequest
import com.infrabwx.app.data.model.ReportResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("exec")
    suspend fun submitReport(@Body request: ReportRequest): ReportResponse

    @GET("exec")
    suspend fun getRanking(
        @Query("action") action: String = "getRanking",
        @Query("category") category: String
    ): RankingResponse

    @GET("exec")
    suspend fun getReportLocations(
        @Query("action") action: String = "getLocations"
    ): ReportLocationsResponse
}
