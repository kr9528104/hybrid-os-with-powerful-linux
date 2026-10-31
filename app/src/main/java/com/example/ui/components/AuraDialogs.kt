package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmailDraftWidget
import com.example.model.UserProfile
import com.example.model.VaultResource
import com.example.ui.theme.*

@Composable
fun EditDraftDialog(
    draft: EmailDraftWidget,
    onDismiss: () -> Unit,
    onSave: (recipient: String, subject: String, body: String) -> Unit
) {
    var recipient by remember { mutableStateOf(draft.recipient) }
    var subject by remember { mutableStateOf(draft.subject) }
    var body by remember { mutableStateOf(draft.body) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraSurfaceElevated,
        title = {
            Text("Edit Email Draft", color = AuraTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("To") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary,
                        focusedBorderColor = AuraPrimary,
                        unfocusedBorderColor = AuraBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary,
                        focusedBorderColor = AuraPrimary,
                        unfocusedBorderColor = AuraBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Message Body") },
                    minLines = 6,
                    maxLines = 10,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary,
                        focusedBorderColor = AuraPrimary,
                        unfocusedBorderColor = AuraBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(recipient, subject, body) },
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AuraTextSecondary)
            }
        }
    )
}

@Composable
fun ResourcePreviewDialog(
    resource: VaultResource,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraSurfaceElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (resource.type == "JSON") Icons.Outlined.Code else Icons.Outlined.PictureAsPdf,
                    contentDescription = null,
                    tint = if (resource.type == "JSON") AuraAmber else AuraRose,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(resource.name, color = AuraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AuraBgDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = resource.content,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        ),
                        color = AuraTextPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun FileAttachmentDialog(
    onDismiss: () -> Unit,
    onFileSelected: (String) -> Unit
) {
    val sampleFiles = listOf(
        Pair("design-token-specs.json", "JSON Config • 14 KB"),
        Pair("roadmap-2026.pdf", "Executive Doc • 2.4 MB"),
        Pair("user-flow-v3.png", "UI Mockup • 840 KB"),
        Pair("customer-survey.csv", "Telemetry Data • 320 KB"),
        Pair("api-auth-spec.pdf", "Security Protocol • 1.1 MB")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraSurfaceElevated,
        title = {
            Text("Attach File to Context", color = AuraTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Supported types: .pdf, .png, .jpg, .csv, .json",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraTextMuted
                )

                sampleFiles.forEach { (fileName, fileDesc) ->
                    Surface(
                        onClick = { onFileSelected(fileName) },
                        shape = RoundedCornerShape(8.dp),
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
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = null,
                                tint = AuraPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fileName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = AuraTextPrimary
                                )
                                Text(
                                    text = fileDesc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AuraTextMuted
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = AuraTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AuraTextSecondary)
            }
        }
    )
}

@Composable
fun SettingsDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraSurfaceElevated,
        title = {
            Text("Aura Assistant Settings", color = AuraTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AuraPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AM",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AuraPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(userProfile.name, fontWeight = FontWeight.Bold, color = AuraTextPrimary)
                        Text(userProfile.role, style = MaterialTheme.typography.bodySmall, color = AuraTextMuted)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AuraAccent))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Online • Connected to Workspace", fontSize = 11.sp, color = AuraAccent)
                        }
                    }
                }

                HorizontalDivider(color = AuraBorderDark)

                // Engine details
                Text("Reasoning Engine", fontWeight = FontWeight.SemiBold, color = AuraTextPrimary)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AuraSurfaceDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Active Engine: Aura-Reason-v2.5 (128k Context)", style = MaterialTheme.typography.bodySmall, color = AuraCyan)
                        Text("Web Retrieval: Grounded Search enabled", style = MaterialTheme.typography.labelSmall, color = AuraTextMuted)
                        Text("Cache Policy: Encrypted Zero-Retention", style = MaterialTheme.typography.labelSmall, color = AuraTextMuted)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)) {
                Text("Done")
            }
        }
    )
}

@Composable
fun ExportNotesDialog(
    notesContent: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraSurfaceElevated,
        title = {
            Text("Export Conversation & Notes", color = AuraTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Conversation summarized and formatted in Markdown:",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AuraBgDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Text(
                        text = notesContent,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = AuraTextPrimary,
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCopy,
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy & Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AuraTextSecondary)
            }
        }
    )
}
