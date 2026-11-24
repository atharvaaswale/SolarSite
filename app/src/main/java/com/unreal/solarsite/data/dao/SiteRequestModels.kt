package com.unreal.solarsite.data.dao

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class UpsertSiteRequest(
    @SerializedName("name") val name: String,
    @SerializedName("client_name") val clientName: String,
    @SerializedName("status") val status: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("boundary_geojson") val boundaryGeoJson: JsonElement?,
    @SerializedName("assessment") val assessment: AssessmentRequest
)

data class AssessmentRequest(
    @SerializedName("roof_type") val roofType: String,
    @SerializedName("tilt_degrees") val tiltDegrees: Int,
    @SerializedName("azimuth_degrees") val azimuthDegrees: Int,
    @SerializedName("shading_condition") val shadingCondition: String,
    @SerializedName("capacity_kwp") val capacityKwp: Double?,
    @SerializedName("calculated_area_sqm") val calculatedAreaSqm: Double?,
    @SerializedName("notes") val notes: String?
)
@Serializable
data class SingleSiteResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: SitesDao.Data? = null
)

data class BasicApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)