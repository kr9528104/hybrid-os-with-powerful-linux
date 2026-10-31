package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LinuxSysTelemetry
import com.example.ui.theme.*

data class WaybarWorkspace(
    val id: String,
    val index: Int,
    val label: String,
    val icon: ImageVector
)

val WAYBAR_WORKSPACES = listOf(
    WaybarWorkspace("chat", 1, "chat", Icons.Outlined.Forum),
    WaybarWorkspace("opensource", 2, "oss & git", Icons.Outlined.Code),
    WaybarWorkspace("terminal", 3, "term", Icons.Default.Terminal),
    WaybarWorkspace("tasks", 4, "tasks", Icons.Outlined.CheckCircle),
    WaybarWorkspace("garuda", 5, "garuda", Icons.Outlined.Speed),
    WaybarWorkspace("calendar", 6, "cal", Icons.Outlined.CalendarToday),
    WaybarWorkspace("knowledge", 7, "vault", Icons.Outlined.FolderSpecial)
)

@Composable
fun AuraWaybar(
    currentModule: String,
    onSelectModule: (String) -> Unit,
    linuxTelemetry: LinuxSysTelemetry,
    zenGovernor: String,
    gitBranch: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val activeWindowTitle = when (currentModule) {
        "opensource" -> "hyprland: oss-hub [branch: $gitBranch]"
        "terminal" -> "hyprland: alacritty [6.8.0-zen: bash]"
        "garuda" -> "hyprland: garuda-control [btrfs/zen: $zenGovernor]"
        "tasks" -> "hyprland: task-board [active]"
        "calendar" -> "hyprland: cal-agenda [sync-ok]"
        "knowledge" -> "hyprland: semantic-vault [json/pdf]"
        else -> "hyprland: aura-ai [low-latency: 18ms]"
    }

    Surface(
        color = WaybarBg,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .border(
                width = 1.dp,
                color = WaybarBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .testTag("aura_waybar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Hyprland / Arch Logo Indicator + Workspace Badges
            Row(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .horizontalScroll(scrollState),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Wayland Compositor Pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WaybarPillActive,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaybarCyan.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "❖ HYPR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = WaybarCyan
                        )
                    }
                }

                // Workspace Pills [1:chat] [2:oss] [3:term] ...
                WAYBAR_WORKSPACES.forEach { ws ->
                    val isActive = currentModule == ws.id
                    val borderColor = if (isActive) WaybarCyan else WaybarBorder
                    val pillBg = if (isActive) WaybarPillActive else WaybarPillBg
                    val textColor = if (isActive) WaybarCyan else GarudaTextSecondary

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = pillBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .clickable { onSelectModule(ws.id) }
                            .testTag("waybar_ws_${ws.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "[${ws.index}: ${ws.label}]",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Center: Active Window Title (Sharp Monospace Hyprland style)
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = HyprlandPaneSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = activeWindowTitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = GarudaTextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Telemetry & Hardware Status Bar Pills
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // CPU load pill
                WaybarStatusPill(
                    label = "CPU ${linuxTelemetry.cpuPercent}%",
                    accentColor = WaybarCyan
                )

                // RAM pill
                WaybarStatusPill(
                    label = "RAM ${String.format("%.1f", linuxTelemetry.memoryUsedGb)}G",
                    accentColor = WaybarGreen
                )

                // Zen governor pill
                WaybarStatusPill(
                    label = "⚡ $zenGovernor",
                    accentColor = WaybarMagenta
                )

                // Sub-50ms ping
                WaybarStatusPill(
                    label = "18ms",
                    accentColor = WaybarGreen
                )
            }
        }
    }
}

@Composable
private fun WaybarStatusPill(
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = WaybarPillBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WaybarBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(accentColor, RoundedCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = GarudaTextWhite
            )
        }
    }
}
