package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.SessionItem
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun AuraSidebar(
    currentModule: String,
    onSelectModule: (String) -> Unit,
    onNewConversation: () -> Unit,
    recentSessions: List<SessionItem>,
    activeSessionId: String,
    onSelectSession: (SessionItem) -> Unit,
    userProfile: UserProfile,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    taskCount: Int = 3
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp)
            .border(1.dp, HyprlandBorderInactive)
            .testTag("aura_sidebar"),
        color = HyprlandPaneBg,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            // Brand Header: logo_icon: "sparkles", title: "Aura AI", badge: "Pro"
            BrandHeader()

            Spacer(modifier = Modifier.height(18.dp))

            // Action Button: "New Conversation", icon "plus", style "primary_pill"
            NewConversationButton(onClick = onNewConversation)

            Spacer(modifier = Modifier.height(18.dp))

            // Scrollable Menu Sections
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Core Modules section
                SectionTitle(title = "CORE MODULES")

                Spacer(modifier = Modifier.height(6.dp))

                NavigationMenuItem(
                    id = "chat",
                    label = "Chat & Reasoning",
                    icon = Icons.Outlined.Forum,
                    isActive = currentModule == "chat",
                    badgeText = null,
                    onClick = { onSelectModule("chat") }
                )

                NavigationMenuItem(
                    id = "tasks",
                    label = "Task Manager",
                    icon = Icons.Outlined.CheckCircle,
                    isActive = currentModule == "tasks",
                    badgeText = if (taskCount > 0) taskCount.toString() else null,
                    badgeColor = AuraPrimary,
                    onClick = { onSelectModule("tasks") }
                )

                NavigationMenuItem(
                    id = "calendar",
                    label = "Schedule & Meetings",
                    icon = Icons.Outlined.CalendarToday,
                    isActive = currentModule == "calendar",
                    badgeText = null,
                    onClick = { onSelectModule("calendar") }
                )

                NavigationMenuItem(
                    id = "knowledge",
                    label = "Personal Vault",
                    icon = Icons.Outlined.Storage,
                    isActive = currentModule == "knowledge",
                    badgeText = null,
                    onClick = { onSelectModule("knowledge") }
                )

                NavigationMenuItem(
                    id = "terminal",
                    label = "Linux Terminal",
                    icon = Icons.Default.Terminal,
                    isActive = currentModule == "terminal",
                    badgeText = "BASH",
                    badgeColor = AuraAccent,
                    onClick = { onSelectModule("terminal") }
                )

                NavigationMenuItem(
                    id = "opensource",
                    label = "Open Source Hub",
                    icon = Icons.Outlined.Code,
                    isActive = currentModule == "opensource",
                    badgeText = "OSS",
                    badgeColor = AuraSecondary,
                    onClick = { onSelectModule("opensource") }
                )

                NavigationMenuItem(
                    id = "garuda",
                    label = "Garuda Linux Hub",
                    icon = Icons.Default.AutoAwesome,
                    isActive = currentModule == "garuda",
                    badgeText = "ZEN",
                    badgeColor = GarudaDragonPink,
                    onClick = { onSelectModule("garuda") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Recent Sessions section
                SectionTitle(title = "RECENT SESSIONS")

                Spacer(modifier = Modifier.height(6.dp))

                recentSessions.forEach { session ->
                    RecentSessionItem(
                        session = session,
                        isActive = activeSessionId == session.id && currentModule == "chat",
                        onClick = { onSelectSession(session) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User Profile Card at bottom
            UserProfileCard(
                user = userProfile,
                onSettingsClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("brand_header"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo icon: Sparkles in glowing container
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(WaybarCyan, GarudaDragonViolet)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Aura AI Logo",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Aura AI",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = AuraTextPrimary
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Pro badge
        Surface(
            shape = RoundedCornerShape(3.dp),
            color = WaybarCyan.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, WaybarCyan.copy(alpha = 0.5f))
        ) {
            Text(
                text = "HYPR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = WaybarCyan,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun NewConversationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .testTag("new_conversation_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WaybarCyan.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(WaybarCyan.copy(alpha = 0.25f), GarudaDragonViolet.copy(alpha = 0.35f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Conversation",
                    tint = WaybarCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "New Workstation Tab",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GarudaTextWhite
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp
        ),
        color = AuraTextMuted,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
    )
}

@Composable
private fun NavigationMenuItem(
    id: String,
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    badgeText: String?,
    badgeColor: Color = AuraPrimary,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) HyprlandPaneSurface else Color.Transparent
    val contentColor = if (isActive) WaybarCyan else AuraTextSecondary
    val borderColor = if (isActive) HyprlandBorderActive else Color.Transparent

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag("nav_item_$id"),
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isActive) AuraTextPrimary else AuraTextSecondary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSessionItem(
    session: SessionItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag("recent_${session.id}"),
        shape = RoundedCornerShape(4.dp),
        color = if (isActive) HyprlandPaneSurface else Color.Transparent,
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderActive.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = if (isActive) WaybarCyan else AuraTextMuted,
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = session.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                ),
                color = if (isActive) AuraTextPrimary else AuraTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun UserProfileCard(
    user: UserProfile,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_profile_card"),
        shape = RoundedCornerShape(4.dp),
        color = HyprlandPaneSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online status dot
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AuraPrimary.copy(alpha = 0.2f))
                )

                // Online indicator dot
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(AuraAccent)
                        .border(1.5.dp, AuraSurfaceDark, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AuraTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp
                    ),
                    color = AuraTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = AuraTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
