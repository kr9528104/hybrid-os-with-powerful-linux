package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GarudaPackage
import com.example.model.GarudaSnapshot
import com.example.ui.theme.*

@Composable
fun AuraGarudaView(
    snapshots: List<GarudaSnapshot>,
    packages: List<GarudaPackage>,
    zenGovernor: String,
    zramEnabled: Boolean,
    onCreateSnapshot: (String, String) -> Unit,
    onDeleteSnapshot: (String) -> Unit,
    onTogglePackage: (String) -> Unit,
    onSetGovernor: (String) -> Unit,
    onToggleZram: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Snapshots") } // "Snapshots", "Packages", "Zen Performance"
    var showCreateSnapshotDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GarudaBgObsidian)
            .padding(16.dp)
            .testTag("aura_garuda_view")
    ) {
        // Hero Header with Dragonized Insignia
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = GarudaSurfaceGlass,
            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaCardBorderGradient)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GarudaDragonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Garuda Dragon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Garuda Linux Hub",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = GarudaTextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GarudaDragonPink.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaDragonPink.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "dr460nized",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = GarudaDragonPink,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Zen Kernel • BTRFS Snapper • Chaotic-AUR • ZRAM",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = GarudaDragonCyan
                    )
                }

                // Turbo Status
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GarudaNeonGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GarudaNeonGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GarudaNeonGreen))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "0ms Latency",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = GarudaNeonGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Snapshots", "Packages", "Zen Performance", "OS Architecture").forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    onClick = { selectedTab = tab },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) GarudaSurfaceElevated else GarudaSurfaceGlass,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) GarudaDragonCyan.copy(alpha = 0.6f) else GarudaBorderSubtle
                    )
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = if (isSelected) GarudaDragonCyan else GarudaTextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content Area based on tab
        when (selectedTab) {
            "Snapshots" -> {
                GarudaSnapshotsSection(
                    snapshots = snapshots,
                    onCreateClick = { showCreateSnapshotDialog = true },
                    onDelete = onDeleteSnapshot
                )
            }
            "Packages" -> {
                GarudaPackagesSection(
                    packages = packages,
                    onToggle = onTogglePackage
                )
            }
            "Zen Performance" -> {
                GarudaZenPerformanceSection(
                    governor = zenGovernor,
                    zramEnabled = zramEnabled,
                    onSetGovernor = onSetGovernor,
                    onToggleZram = onToggleZram
                )
            }
            else -> {
                AuraOsArchitectureSection(
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (showCreateSnapshotDialog) {
            CreateSnapshotDialog(
                onDismiss = { showCreateSnapshotDialog = false },
                onConfirm = { name, desc ->
                    onCreateSnapshot(name, desc)
                    showCreateSnapshotDialog = false
                }
            )
        }
    }
}

@Composable
private fun GarudaSnapshotsSection(
    snapshots: List<GarudaSnapshot>,
    onCreateClick: () -> Unit,
    onDelete: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BTRFS Snapper Subvolumes",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = GarudaTextWhite
            )

            Button(
                onClick = onCreateClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GarudaDragonCyan),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = GarudaBgObsidian, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "New Snapshot",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GarudaBgObsidian)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(snapshots, key = { it.id }) { snap ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GarudaSurfaceGlass,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GarudaDragonViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                tint = GarudaDragonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = snap.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = GarudaTextWhite
                            )
                            Text(
                                text = "${snap.description} • ${snap.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GarudaTextSecondary
                            )
                            Text(
                                text = snap.timestamp,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = GarudaTextMuted
                            )
                        }

                        IconButton(
                            onClick = { onDelete(snap.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Prune",
                                tint = GarudaDragonPink.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GarudaPackagesSection(
    packages: List<GarudaPackage>,
    onToggle: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Pacman & Chaotic-AUR Package Store",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = GarudaTextWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(packages, key = { it.id }) { pkg ->
            val repoColor = when (pkg.repo) {
                "chaotic-aur" -> GarudaDragonPink
                "extra" -> GarudaDragonCyan
                else -> GarudaNeonGreen
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GarudaSurfaceGlass,
                border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pkg.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GarudaTextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = repoColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = pkg.repo,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = repoColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Text(
                            text = pkg.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = GarudaTextSecondary
                        )

                        Text(
                            text = "v${pkg.version} • ${pkg.size}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GarudaTextMuted
                        )
                    }

                    Button(
                        onClick = { onToggle(pkg.id) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (pkg.isInstalled) GarudaSurfaceCard else GarudaDragonCyan,
                            contentColor = if (pkg.isInstalled) GarudaTextSecondary else GarudaBgObsidian
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = if (pkg.isInstalled) "Installed" else "Install",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GarudaZenPerformanceSection(
    governor: String,
    zramEnabled: Boolean,
    onSetGovernor: (String) -> Unit,
    onToggleZram: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // CPU Governor card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = GarudaSurfaceGlass,
            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Linux Zen CPU Governor",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = GarudaTextWhite
                )
                Text(
                    text = "Adjust CPU scheduling frequency scaling profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = GarudaTextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Performance", "Balanced", "Powersave").forEach { gov ->
                        val isSelected = governor == gov
                        val govColor = if (gov == "Performance") GarudaDragonCyan else if (gov == "Balanced") GarudaNeonGreen else GarudaTextSecondary

                        Surface(
                            onClick = { onSetGovernor(gov) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) govColor.copy(alpha = 0.2f) else GarudaSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) govColor else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = gov,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) govColor else GarudaTextSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // ZRAM & Ananicy card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = GarudaSurfaceGlass,
            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ZRAM Swap (zstd compression)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GarudaTextWhite)
                        Text("Compresses RAM pages in-place, eliminating storage I/O paging latency", style = MaterialTheme.typography.labelSmall, color = GarudaTextMuted)
                    }
                    Switch(
                        checked = zramEnabled,
                        onCheckedChange = { onToggleZram() },
                        colors = SwitchDefaults.colors(checkedThumbColor = GarudaNeonGreen, checkedTrackColor = GarudaNeonGreen.copy(alpha = 0.3f))
                    )
                }

                HorizontalDivider(color = GarudaBorderSubtle)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = GarudaDragonPink, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Ananicy-Cpp Priority Daemon", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GarudaTextWhite)
                        Text("Active: Auto-renicing foreground apps to nice -5 for zero input latency", style = MaterialTheme.typography.labelSmall, color = GarudaNeonGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateSnapshotDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GarudaSurfaceElevated,
        title = { Text("Create BTRFS Snapshot", color = GarudaTextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Snapshot Name (e.g. pre-build)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GarudaTextWhite,
                        unfocusedTextColor = GarudaTextWhite,
                        focusedBorderColor = GarudaDragonCyan,
                        unfocusedBorderColor = GarudaBorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GarudaTextWhite,
                        unfocusedTextColor = GarudaTextWhite,
                        focusedBorderColor = GarudaDragonCyan,
                        unfocusedBorderColor = GarudaBorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = GarudaDragonCyan)
            ) {
                Text("Create", color = GarudaBgObsidian, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = GarudaTextSecondary) }
        }
    )
}
