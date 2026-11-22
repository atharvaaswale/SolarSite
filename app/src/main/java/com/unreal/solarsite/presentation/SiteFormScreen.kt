package com.unreal.solarsite.presentation.siteform

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unreal.solarsite.ui.theme.textRed
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

private val SurfaceInputBg = Color(0xFFF1F5F9)
private val ActiveRust = Color(0xFF8B3A00)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)

@Composable
fun SiteFormScreen(
    siteId: String?,
    onBackClick: () -> Unit,
    onOpenBoundaryMap: () -> Unit,
    viewModel: SiteFormViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(siteId) {
        viewModel.initForm(siteId)
    }

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            onBackClick()
        }
    }

    SiteFormContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onNameChange = viewModel::onNameChange,
        onClientNameChange = viewModel::onClientNameChange,
        onStatusChange = viewModel::onStatusChange,
        onRoofTypeChange = viewModel::onRoofTypeChange,
        onShadingChange = viewModel::onShadingChange,
        onTiltChange = viewModel::onTiltChange,
        onAzimuthChange = viewModel::onAzimuthChange,
        onCapacityChange = viewModel::onCapacityChange,
        onNotesChange = viewModel::onNotesChange,
        onOpenBoundaryMap = onOpenBoundaryMap,
        onSaveClick = viewModel::saveSite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteFormContent(
    uiState: SiteFormUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onClientNameChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onRoofTypeChange: (String) -> Unit,
    onShadingChange: (String) -> Unit,
    onTiltChange: (String) -> Unit,
    onAzimuthChange: (String) -> Unit,
    onCapacityChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onOpenBoundaryMap: () -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Site" else "Add Site",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Button(
                    onClick = {},//onSaveClick,
                    enabled = !uiState.isSaving && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = ActiveRust),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .height(54.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = if (uiState.isEditMode) "Update Site" else "Save Site",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ActiveRust)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // 1. Site Name
            SoftTextField(
                label = "Site Name",
                required = true,
                value = uiState.name,
                onValueChange = onNameChange,
                placeholder = "e.g. Axis Industrial Roof",
                leadingIcon = Icons.Default.Business
            )

            // 2. Client Name
            SoftTextField(
                label = "Client Name",
                required = true,
                value = uiState.clientName,
                onValueChange = onClientNameChange,
                placeholder = "e.g. Axis Logistics Ltd",
                leadingIcon = Icons.Default.Person
            )

            // 3. Survey Status (Chips)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Survey Status")
                val statuses = listOf(
                    "DRAFT" to "Draft",
                    "READY_FOR_REVIEW" to "Ready for Review",
                    "SYNCED" to "Synced"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statuses.forEach { (key, label) ->
                        val selected = uiState.status == key
                        FilterChip(
                            selected = selected,
                            onClick = { onStatusChange(key) },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ActiveRust,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceInputBg,
                                labelColor = TextPrimary
                            ),
                            border = null,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // 4. Primary Roof Profile (2x2 Card Grid)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Primary Roof Profile")
                val roofOptions = listOf(
                    Triple("FLAT_CONCRETE", "Flat Concrete", Icons.Default.Apartment),
                    Triple("METAL_SHEET", "Metal Sheet", Icons.Default.Roofing),
                    Triple("TILE", "Tile", Icons.Default.GridView),
                    Triple("OTHER", "Other", Icons.Default.Layers)
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    roofOptions.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { (key, title, icon) ->
                                val selected = uiState.roofType == key
                                RoofProfileCard(
                                    title = title,
                                    icon = icon,
                                    selected = selected,
                                    onClick = { onRoofTypeChange(key) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Shading Condition (Compact Segmented Control)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Shading Condition")
                val shadings = listOf("NONE" to "None", "LOW" to "Low", "MEDIUM" to "Medium", "HIGH" to "High")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceInputBg)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    shadings.forEach { (key, label) ->
                        val selected = uiState.shadingCondition == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ActiveRust else Color.Transparent)
                                .clickable { onShadingChange(key) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            // 6. Tilt & Azimuth Side-by-Side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SoftTextField(
                    label = "Tilt Angle (°)",
                    value = uiState.tiltDegrees,
                    onValueChange = onTiltChange,
                    placeholder = "10",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                SoftTextField(
                    label = "Azimuth (°)",
                    value = uiState.azimuthDegrees,
                    onValueChange = onAzimuthChange,
                    placeholder = "180",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            // 7. Target Capacity (kWp)
            SoftTextField(
                label = "Estimated Capacity (kWp)",
                value = uiState.capacityKwp,
                onValueChange = onCapacityChange,
                placeholder = "e.g. 550.0",
                keyboardType = KeyboardType.Decimal
            )

            // 8. Roof Polygon & Calculated Area Trigger Card (Read-only)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val area = uiState.calculatedAreaSqm
                val isMapped = area != null && area > 0.0

                FieldLabel("Roof Boundary & Usable Area")
                Surface(
                    onClick = onOpenBoundaryMap,
                    color = if (isMapped) ActiveRust.copy(alpha = 0.08f) else SurfaceInputBg,
                    border = if (isMapped) BorderStroke(1.dp, ActiveRust.copy(alpha = 0.35f)) else null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (uiState.calculatedAreaSqm != null) ActiveRust.copy(alpha = 0.12f)
                                    else Color.White
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = ActiveRust
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val area = uiState.calculatedAreaSqm
                            Text(
                                text = if (area != null && area > 0.0) {
                                    String.format(Locale.US, "%.2f m²", area)
                                } else {
                                    "Draw Roof Boundary on Map"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (area != null && area > 0.0) {
                                    "${uiState.polygonPointCount} vertices mapped • Tap to adjust"
                                } else {
                                    "Tap to drop pins and auto-calculate area"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }

            // 9. Survey Notes
            SoftTextField(
                label = "Survey Notes",
                value = uiState.notes,
                onValueChange = onNotesChange,
                placeholder = "Obstructions, parapet height, inverter location...",
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String, required: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        if (required) {
            Text(
                text = "Required",
                style = MaterialTheme.typography.labelSmall,
                color = textRed//TextSecondary

            )
        }
    }
}

@Composable
private fun SoftTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel(text = label, required = required)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.7f)) },
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = TextSecondary) }
            },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceInputBg,
                unfocusedContainerColor = SurfaceInputBg,
                disabledContainerColor = SurfaceInputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = ActiveRust
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RoofProfileCard(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) ActiveRust else SurfaceInputBg,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) Color.White.copy(alpha = 0.16f) else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) Color.White else TextPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SiteFormPreview() {
    SiteFormContent(
        uiState = SiteFormUiState(
            name = "(e.g. Axis Industrial Roof)",
            clientName = "(Axis Logistics Ltd)",
            calculatedAreaSqm = 420.5,
            polygonPointCount = 4
        ),
        onBackClick = {},
        onNameChange = {},
        onClientNameChange = {},
        onStatusChange = {},
        onRoofTypeChange = {},
        onShadingChange = {},
        onTiltChange = {},
        onAzimuthChange = {},
        onCapacityChange = {},
        onNotesChange = {},
        onOpenBoundaryMap = {},
        onSaveClick = {}
    )
}