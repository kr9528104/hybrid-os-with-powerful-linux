package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LinuxSysTelemetry
import com.example.model.SuggestedAutomation
import com.example.model.SystemMetrics
import com.example.model.VaultResource
import com.example.ui.theme.*

import com.example.model.GitRepoState

@Composable
fun AuraContextPanel(
    systemMetrics: SystemMetrics,
    suggestedAutomations: List<SuggestedAutomation>,
    pinnedResources: List<VaultResource>,
    onSelectAutomation: (SuggestedAutomation) -> Unit,
    onViewResource: (VaultResource) -> Unit,
    onAddResource: () -> Unit,
    modifier: Modifier = Modifier,
    linuxTelemetry: LinuxSysTelemetry = LinuxSysTelemetry(),
    gitRepoState: GitRepoState = GitRepoState()
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .border(1.dp, HyprlandBorderInactive)
            .testTag("aura_context_panel"),
        color = HyprlandPaneBg,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Panel Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(WaybarCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Analytics,
                        contentDescription = null,
                        tint = WaybarCyan,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HYPR TELEMETRY & CONTEXT",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = AuraTextPrimary
                )
            }

            // Git Workstation Branch Inspector Card
            GitStatusInspectorCard(gitRepoState = gitRepoState)

            // Widget 1: System Metrics ("Assistant Status")
            SystemMetricsCard(metrics = systemMetrics)

            // Linux Kernel Telemetry
            LinuxTelemetryCard(telemetry = linuxTelemetry)

            // Widget 2: Suggested Automations ("Suggested Automations")
            SuggestedAutomationsCard(
                automations = suggestedAutomations,
                onSelectAutomation = onSelectAutomation
            )

            // Widget 3: Pinned Artifacts ("Pinned Resources")
            PinnedResourcesCard(
                resources = pinnedResources,
                onViewResource = onViewResource,
                onAddResource = onAddResource
            )
        }
    }
}

@Composable
private fun GitStatusInspectorCard(gitRepoState: GitRepoState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("git_inspector_card"),
        shape = RoundedCornerShape(4.dp),
        color = HyprlandPaneSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderActive)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ForkRight,
                    contentDescription = null,
                    tint = WaybarCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GIT WORKTREE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = WaybarCyan,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = WaybarPillActive,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaybarCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = gitRepoState.branch,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = WaybarCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Staged files:",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = GarudaTextSecondary
                )
                Text(
                    text = "${gitRepoState.stagedFiles.size} files",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (gitRepoState.stagedFiles.isNotEmpty()) WaybarGreen else GarudaTextMuted
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Working tree:",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = GarudaTextSecondary
                )
                Text(
                    text = "${gitRepoState.unstagedFiles.size} modified",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (gitRepoState.unstagedFiles.isNotEmpty()) GarudaElectricOrange else WaybarGreen
                )
            }

            val headCommit = gitRepoState.recentCommits.firstOrNull()
            if (headCommit != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HyprlandBgObsidian, RoundedCornerShape(3.dp))
                        .padding(6.dp)
                ) {
                    Text(
                        text = "HEAD: ${headCommit.hash} ${headCommit.message}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = GarudaTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemMetricsCard(metrics: SystemMetrics) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("system_metrics_card"),
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metrics.title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Optimal Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AuraAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraAccent.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AuraAccent)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = metrics.latencyStatus.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = AuraAccent
                        )
                    }
                }
            }

            // Metric 1: Context Memory
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Context Memory",
                        style = MaterialTheme.typography.bodySmall,
                        color = AuraTextSecondary
                    )
                    Text(
                        text = metrics.contextMemoryText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = AuraTextPrimary
                    )
                }

                LinearProgressIndicator(
                    progress = { metrics.contextPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AuraPrimary,
                    trackColor = AuraSurfaceLight
                )
            }

            // Metric 2: Response Latency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Response Latency",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AuraSurfaceDark
                ) {
                    Text(
                        text = metrics.latencyText,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AuraAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Metric 3: Active Integrations
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Active Integrations",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IntegrationChip(name = "Workspace", color = AuraPrimary)
                    IntegrationChip(name = "GitHub", color = AuraSecondary)
                    IntegrationChip(name = "Slack", color = AuraCyan)
                }
            }
        }
    }
}

@Composable
private fun IntegrationChip(name: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = AuraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = AuraTextPrimary
            )
        }
    }
}

@Composable
private fun SuggestedAutomationsCard(
    automations: List<SuggestedAutomation>,
    onSelectAutomation: (SuggestedAutomation) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("suggested_automations_card"),
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Suggested Automations",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = AuraTextPrimary
            )

            automations.forEach { auto ->
                val icon = when (auto.iconName) {
                    "mail" -> Icons.Outlined.Mail
                    "file-text" -> Icons.Outlined.Description
                    else -> Icons.Outlined.PlayArrow
                }
                val iconColor = when (auto.iconName) {
                    "mail" -> AuraCyan
                    "file-text" -> AuraSecondary
                    else -> AuraAccent
                }

                Surface(
                    onClick = { onSelectAutomation(auto) },
                    shape = RoundedCornerShape(10.dp),
                    color = AuraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = auto.label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AuraTextPrimary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Run",
                            tint = AuraTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinnedResourcesCard(
    resources: List<VaultResource>,
    onViewResource: (VaultResource) -> Unit,
    onAddResource: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pinned_resources_card"),
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pinned Resources",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onAddResource,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Pin New Resource",
                        tint = AuraTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            resources.forEach { file ->
                val (fileIcon, fileColor) = if (file.type == "JSON") {
                    Pair(Icons.Outlined.Code, AuraAmber)
                } else {
                    Pair(Icons.Outlined.PictureAsPdf, AuraRose)
                }

                Surface(
                    onClick = { onViewResource(file) },
                    shape = RoundedCornerShape(10.dp),
                    color = AuraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(fileColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = fileIcon,
                                contentDescription = null,
                                tint = fileColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AuraTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${file.size} • ${file.updated}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AuraTextMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = "View",
                            tint = AuraTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinuxTelemetryCard(telemetry: LinuxSysTelemetry) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("linux_telemetry_card"),
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = AuraAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Linux Host Telemetry",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AuraSurfaceDark
                ) {
                    Text(
                        text = "POSIX",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = AuraAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Kernel Version
            Text(
                text = telemetry.kernel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                color = AuraCyan
            )

            // CPU Load
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CPU Load", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                Text("${telemetry.cpuPercent}% (Active)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = AuraTextPrimary)
            }
            LinearProgressIndicator(
                progress = { telemetry.cpuPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = AuraAccent,
                trackColor = AuraSurfaceLight
            )

            // RAM
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Memory", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                Text("${telemetry.memoryUsedGb}G / ${telemetry.memoryTotalGb}G", style = MaterialTheme.typography.bodySmall, color = AuraTextPrimary)
            }

            // Load Avg & Uptime
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Uptime", style = MaterialTheme.typography.labelSmall, color = AuraTextMuted)
                Text(telemetry.uptime, style = MaterialTheme.typography.labelSmall, color = AuraTextMuted)
            }
        }
    }
}
