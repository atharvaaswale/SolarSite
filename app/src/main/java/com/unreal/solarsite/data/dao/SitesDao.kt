package com.unreal.solarsite.data.dao


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SitesDao(
    @SerialName("count")
    val count: Int,
    @SerialName("data")
    val `data`: List<Data>,
    @SerialName("success")
    val success: Boolean
) {
    @Serializable
    data class Data(
        @SerialName("boundary_geojson")
        val boundaryGeojson: BoundaryGeojson,
        @SerialName("client_name")
        val clientName: String,
        @SerialName("created_at")
        val createdAt: String,
        @SerialName("id")
        val id: String,
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double,
        @SerialName("name")
        val name: String,
        @SerialName("primary_image_url")
        val primaryImageUrl: String,
        @SerialName("site_assessments")
        val siteAssessments: SiteAssessments,
        @SerialName("status")
        val status: String,
        @SerialName("updated_at")
        val updatedAt: String
    ) {
        @Serializable
        data class BoundaryGeojson(
            @SerialName("coordinates")
            val coordinates: List<List<List<Double>>>,
            @SerialName("type")
            val type: String
        )

        @Serializable
        data class SiteAssessments(
            @SerialName("azimuth_degrees")
            val azimuthDegrees: Int,
            @SerialName("calculated_area_sqm")
            val calculatedAreaSqm: Double,
            @SerialName("capacity_kwp")
            val capacityKwp: Int,
            @SerialName("id")
            val id: String,
            @SerialName("notes")
            val notes: String,
            @SerialName("roof_type")
            val roofType: String,
            @SerialName("shading_condition")
            val shadingCondition: String,
            @SerialName("site_id")
            val siteId: String,
            @SerialName("tilt_degrees")
            val tiltDegrees: Int
        )
    }
}