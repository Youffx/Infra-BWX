package com.infrabwx.app.data.model

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("image") val imageBase64: String,
    @SerializedName("category") val category: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("kecamatan") val kecamatan: String
)

data class ReportResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class RankingItem(
    @SerializedName("kecamatan") val kecamatan: String,
    @SerializedName("jumlah") val jumlah: Int
)

data class RankingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<RankingItem>
)

data class ReportLocationItem(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("kecamatan") val kecamatan: String,
    @SerializedName("category") val category: String
)

data class ReportLocationsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<ReportLocationItem>
)
