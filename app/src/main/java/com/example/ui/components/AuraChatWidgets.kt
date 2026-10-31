package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun ScheduleWidgetCard(
    widget: ScheduleWidget,
    onJoinMeeting: (ScheduleEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_card_${widget.id}"),
        shape = RoundedCornerShape(16.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(
                colors = listOf(AuraPrimary.copy(alpha = 0.5f), AuraBorderSubtle)
            )
        ),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AuraPrimary, AuraSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = widget.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = AuraTextPrimary
                    )
                    Text(
                        text = "3 sessions synchronized today",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AuraPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AuraAccent)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Live Sync",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            color = AuraAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Events List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                widget.events.forEachIndexed { index, event ->
                    ScheduleEventItem(
                        event = event,
                        isFirst = index == 0,
                        onJoinClick = { onJoinMeeting(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleEventItem(
    event: ScheduleEvent,
    onJoinClick: () -> Unit,
    isFirst: Boolean = false
) {
    val (typeLabel, typeColor, typeIcon) = when (event.type) {
        "video_call" -> Triple("Google Meet", AuraCyan, Icons.Outlined.Videocam)
        "one_on_one" -> Triple("1:1 In-Person", AuraSecondary, Icons.Outlined.Person)
        else -> Triple("Keynote Deck", AuraAmber, Icons.Outlined.CoPresent)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isFirst) AuraCyan.copy(alpha = 0.4f) else AuraBorderSubtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Column
            Column(
                modifier = Modifier.width(78.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = event.time,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = if (isFirst) AuraCyan else AuraPrimary
                )
                Text(
                    text = "${event.attendees} attendees",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AuraTextMuted
                )
            }

            // Divider vertical line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(34.dp)
                    .background(
                        if (isFirst) AuraCyan.copy(alpha = 0.6f) else AuraBorderDark
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Event Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AuraTextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        tint = typeColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = typeColor
                    )
                    if (isFirst) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AuraAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Next Up",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = AuraAccent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Action: Join Call button
            if (event.type == "video_call") {
                Button(
                    onClick = onJoinClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuraCyan,
                        contentColor = AuraBgDark
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Join",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun EmailDraftWidgetCard(
    widget: EmailDraftWidget,
    onSend: () -> Unit,
    onEdit: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedToClipboard by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("email_draft_card_${widget.id}"),
        shape = RoundedCornerShape(16.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (widget.status == EmailStatus.Sent) AuraAccent.copy(alpha = 0.6f)
            else AuraSecondary.copy(alpha = 0.35f)
        ),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (widget.status == EmailStatus.Sent) AuraAccent.copy(alpha = 0.2f)
                            else AuraSecondary.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (widget.status == EmailStatus.Sent) Icons.Default.CheckCircle else Icons.Outlined.ForwardToInbox,
                        contentDescription = null,
                        tint = if (widget.status == EmailStatus.Sent) AuraAccent else AuraSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Smart Email Draft",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AuraTextPrimary
                    )
                    Text(
                        text = "Aura Synthesized Reply",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraTextMuted
                    )
                }

                val statusColor = when (widget.status) {
                    EmailStatus.Draft -> AuraAmber
                    EmailStatus.Sent -> AuraAccent
                    EmailStatus.Discarded -> AuraRose
                }
                val statusText = when (widget.status) {
                    EmailStatus.Draft -> "Ready to Send"
                    EmailStatus.Sent -> "Sent via Gmail"
                    EmailStatus.Discarded -> "Discarded"
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metadata card: To & Subject
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AuraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "To:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AuraTextMuted,
                            modifier = Modifier.width(55.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AuraPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = widget.recipient,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = AuraCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AuraBorderSubtle)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Subject:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AuraTextMuted,
                            modifier = Modifier.width(55.dp)
                        )
                        Text(
                            text = widget.subject,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = AuraTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Email Body Container
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AuraBgDark.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = widget.body,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 21.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = AuraTextPrimary.copy(alpha = 0.95f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(widget.body))
                                copiedToClipboard = true
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Icon(
                                imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                tint = if (copiedToClipboard) AuraAccent else AuraTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (copiedToClipboard) "Copied" else "Copy Body",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (copiedToClipboard) AuraAccent else AuraTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions: "Send via Gmail", "Edit Draft", "Discard"
            if (widget.status == EmailStatus.Draft) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Send via Gmail
                    Button(
                        onClick = onSend,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(40.dp)
                            .testTag("action_send_email"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send via Gmail",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // Edit Draft
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("action_edit_email"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AuraTextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = AuraSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Draft",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = AuraTextPrimary
                        )
                    }

                    // Discard
                    IconButton(
                        onClick = onDiscard,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_discard_email")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Discard Draft",
                            tint = AuraRose.copy(alpha = 0.85f),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskWidgetCard(
    widget: TaskWidget,
    onToggleTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_widget_card"),
        shape = RoundedCornerShape(16.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(listOf(AuraAccent.copy(alpha = 0.4f), AuraBorderSubtle))
        ),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuraAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = AuraAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = widget.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AuraSurfaceDark
                ) {
                    Text(
                        text = "${widget.tasks.count { it.isCompleted }}/${widget.tasks.size} done",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            widget.tasks.forEach { task ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AuraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { onToggleTask(task.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { onToggleTask(task.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AuraAccent,
                                uncheckedColor = AuraTextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (task.isCompleted) AuraTextMuted else AuraTextPrimary
                            )
                            Text(
                                text = "${task.category} • ${task.dueDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AuraTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
