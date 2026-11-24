package com.unreal.solarsite.data.dao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SitesDao(
    @SerialName("count")
    val count: Int = 0,
    @SerialName("data")
    val `data`: List<Data> = emptyList(),
    @SerialName("success")
    val success: Boolean = false
) {
    @Serializable
    data class Data(
        @SerialName("boundary_geojson")
        val boundaryGeojson: BoundaryGeojson? = null,
        @SerialName("client_name")
        val clientName: String = "",
        @SerialName("created_at")
        val createdAt: String = "",
        @SerialName("id")
        val id: String = "",
        @SerialName("latitude")
        val latitude: Double? = null,
        @SerialName("longitude")
        val longitude: Double? = null,
        @SerialName("name")
        val name: String = "",
        @SerialName("primary_image_url")
        val primaryImageUrl: String? = null,
        @SerialName("site_assessments")
        val siteAssessments: SiteAssessments? = null,
        @SerialName("status")
        val status: String = "DRAFT",
        @SerialName("updated_at")
        val updatedAt: String = ""
    ) {
        @Serializable
        data class BoundaryGeojson(
            @SerialName("coordinates")
            val coordinates: List<List<List<Double>>> = emptyList(),
            @SerialName("type")
            val type: String = ""
        )

        @Serializable
        data class SiteAssessments(
            @SerialName("azimuth_degrees")
            val azimuthDegrees: Int? = null,
            @SerialName("calculated_area_sqm")
            val calculatedAreaSqm: Double? = null,
            @SerialName("capacity_kwp")
            val capacityKwp: Double? = null,
            @SerialName("id")
            val id: String = "",
            @SerialName("notes")
            val notes: String? = null,
            @SerialName("roof_type")
            val roofType: String? = null,
            @SerialName("shading_condition")
            val shadingCondition: String? = null,
            @SerialName("site_id")
            val siteId: String = "",
            @SerialName("tilt_degrees")
            val tiltDegrees: Int? = null
        )
    }
}