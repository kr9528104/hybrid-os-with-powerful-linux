package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LinuxCommandOutput
import com.example.model.LinuxProcessItem
import com.example.model.LinuxScriptTemplate
import com.example.ui.theme.*

@Composable
fun AuraTerminalView(
    history: List<LinuxCommandOutput>,
    processes: List<LinuxProcessItem>,
    scripts: List<LinuxScriptTemplate>,
    onExecuteCommand: (String) -> Unit,
    onTerminateProcess: (Int) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Console") } // "Console", "Processes", "Scripts"
    var commandInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(history.size) {
        if (history.isNotEmpty() && selectedTab == "Console") {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    val quickCommands = listOf(
        "neofetch",
        "docker ps",
        "free -h",
        "df -h",
        "uname -a",
        "uptime",
        "git status",
        "curl https://api.github.com"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090E1A))
            .testTag("aura_terminal_view")
    ) {
        // Terminal Window Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF111728),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Window dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = AuraAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "alex@aura-linux: ~ (bash 5.2.21)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AuraTextPrimary
                        )
                    }

                    // Mode switch tabs
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Console", "Processes", "Scripts").forEach { tab ->
                            val isSelected = selectedTab == tab
                            Surface(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) AuraPrimary.copy(alpha = 0.25f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, AuraPrimary.copy(alpha = 0.6f)) else null
                            ) {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) AuraPrimary else AuraTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sub-view rendering based on active tab
        when (selectedTab) {
            "Processes" -> {
                LinuxProcessManagerView(
                    processes = processes,
                    onTerminate = onTerminateProcess
                )
            }
            "Scripts" -> {
                LinuxScriptsView(
                    scripts = scripts,
                    onRunScript = { script ->
                        onExecuteCommand("bash ${script.filename}")
                        selectedTab = "Console"
                    }
                )
            }
            else -> {
                // Console view
                Column(modifier = Modifier.weight(1f)) {
                    // Quick Preset Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickCommands.forEach { cmd ->
                            Surface(
                                onClick = {
                                    onExecuteCommand(cmd)
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF141C2E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF23304A))
                            ) {
                                Text(
                                    text = "$ $cmd",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = AuraCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Log output
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(history, key = { it.id }) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0D1424))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "alex@aura-linux:~$ ",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = AuraAccent
                                    )
                                    Text(
                                        text = item.command,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = item.timestamp,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        ),
                                        color = AuraTextMuted
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "Copy output",
                                        tint = AuraTextMuted,
                                        modifier = Modifier
                                            .size(13.dp)
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString(item.output))
                                            }
                                    )
                                }

                                if (item.output.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.output,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        ),
                                        color = Color(0xFFCAD5E2)
                                    )
                                }
                            }
                        }
                    }

                    // Terminal Input Prompt
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F1626),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "alex@aura-linux:~$ ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AuraAccent
                            )

                            TextField(
                                value = commandInput,
                                onValueChange = { commandInput = it },
                                placeholder = {
                                    Text(
                                        "bash command (fast efficiency execution)...",
                                        color = AuraTextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = AuraAccent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    if (commandInput.isNotBlank()) {
                                        onExecuteCommand(commandInput)
                                        commandInput = ""
                                    }
                                }),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    if (commandInput.isNotBlank()) {
                                        onExecuteCommand(commandInput)
                                        commandInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AuraPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Execute",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinuxProcessManagerView(
    processes: List<LinuxProcessItem>,
    onTerminate: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Linux Process Table (htop view)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = AuraTextPrimary
            )
            Text(
                text = "${processes.size} Tasks Active",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = AuraAccent
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Process Table Header
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF131A2C),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PID", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = AuraTextMuted, modifier = Modifier.width(55.dp))
                Text("USER", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = AuraTextMuted, modifier = Modifier.width(65.dp))
                Text("CPU%", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = AuraTextMuted, modifier = Modifier.width(55.dp))
                Text("MEM", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = AuraTextMuted, modifier = Modifier.width(65.dp))
                Text("COMMAND", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = AuraTextMuted, modifier = Modifier.weight(1f))
                Text("ACTION", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = AuraTextMuted)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(processes, key = { it.pid }) { proc ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F1626),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2438)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proc.pid.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold),
                            color = AuraCyan,
                            modifier = Modifier.width(55.dp)
                        )
                        Text(
                            text = proc.user,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = AuraTextPrimary,
                            modifier = Modifier.width(65.dp)
                        )
                        Text(
                            text = "${proc.cpuPercent}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = if (proc.cpuPercent > 5f) AuraAmber else AuraTextSecondary,
                            modifier = Modifier.width(55.dp)
                        )
                        Text(
                            text = "${proc.memMb}M",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = AuraTextSecondary,
                            modifier = Modifier.width(65.dp)
                        )
                        Text(
                            text = proc.command,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            color = AuraTextPrimary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )

                        if (proc.state != "System") {
                            TextButton(
                                onClick = { onTerminate(proc.pid) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = "Kill",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = AuraRose)
                                )
                            }
                        } else {
                            Text(
                                text = "SYS",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = AuraTextMuted),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinuxScriptsView(
    scripts: List<LinuxScriptTemplate>,
    onRunScript: (LinuxScriptTemplate) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Linux Automation & Shell Scripts Library",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = AuraTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(scripts, key = { it.id }) { script ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F1626),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = AuraSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = script.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = AuraTextPrimary
                            )
                            Text(
                                text = script.filename,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = AuraCyan
                            )
                        }

                        Button(
                            onClick = { onRunScript(script) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = script.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = AuraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF070B14),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = script.code,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            color = Color(0xFFB0C4DE),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
