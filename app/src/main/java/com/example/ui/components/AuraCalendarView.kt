package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEventItem
import com.example.ui.theme.*

@Composable
fun AuraCalendarView(
    events: List<CalendarEventItem>,
    onJoinMeeting: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBgDark)
            .padding(16.dp)
            .testTag("aura_calendar_view")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Schedule & Meetings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary
                )
                Text(
                    text = "Synced with Google Workspace Calendar",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraCyan
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AuraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
            ) {
                Text(
                    text = "Today, Oct 31",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AuraTextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(events, key = { it.id }) { item ->
                CalendarCard(item = item, onJoin = { onJoinMeeting(item.link) })
            }
        }
    }
}

@Composable
private fun CalendarCard(item: CalendarEventItem, onJoin: () -> Unit) {
    val (icon, color) = when (item.type) {
        "video_call" -> Pair(Icons.Outlined.Videocam, AuraCyan)
        "one_on_one" -> Pair(Icons.Outlined.Person, AuraSecondary)
        "presentation" -> Pair(Icons.Outlined.Slideshow, AuraAmber)
        else -> Pair(Icons.Outlined.Event, AuraPrimary)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AuraTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.time} (${item.duration}) • ${item.attendees} attendees",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraTextMuted
                )
            }

            if (item.type == "video_call") {
                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = AuraCyan),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Join Meet",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AuraBgDark
                    )
                }
            }
        }
    }
}
