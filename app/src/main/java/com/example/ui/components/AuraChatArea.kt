package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraChatArea(
    messages: List<ChatMessage>,
    currentModel: String,
    availableModels: List<String>,
    onSelectModel: (String) -> Unit,
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    isWebBrowsingActive: Boolean,
    onToggleWebBrowsing: () -> Unit,
    isVoiceRecording: Boolean,
    onToggleVoiceRecording: () -> Unit,
    attachedFiles: List<String>,
    onAttachFileClick: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    isGeneratingResponse: Boolean,
    searchQuery: String,
    isSearchOpen: Boolean,
    onToggleSearch: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onExportNotes: () -> Unit,
    onClearChat: () -> Unit,
    onSendDraft: (String) -> Unit,
    onEditDraft: (EmailDraftWidget) -> Unit,
    onDiscardDraft: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onJoinMeeting: (ScheduleEvent) -> Unit,
    isCompact: Boolean = false,
    onOpenDrawer: (() -> Unit)? = null,
    onToggleContextPanel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size, isGeneratingResponse) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val filteredMessages = remember(messages, searchQuery) {
        if (searchQuery.isBlank()) messages
        else messages.filter {
            it.content.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBgDark)
            .testTag("aura_main_chat_area")
    ) {
        // Chat Header with Model Selector & Actions
        ChatAreaHeader(
            currentModel = currentModel,
            availableModels = availableModels,
            onSelectModel = onSelectModel,
            isSearchOpen = isSearchOpen,
            onToggleSearch = onToggleSearch,
            onExportNotes = onExportNotes,
            onClearChat = onClearChat,
            isCompact = isCompact,
            onOpenDrawer = onOpenDrawer,
            onToggleContextPanel = onToggleContextPanel
        )

        // Search Bar when opened
        AnimatedVisibility(visible = isSearchOpen) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = AuraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AuraTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text("Filter conversation keywords...", color = AuraTextMuted, fontSize = 13.sp)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            cursorColor = AuraPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChanged("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = AuraTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Conversation Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(filteredMessages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    onSendDraft = onSendDraft,
                    onEditDraft = onEditDraft,
                    onDiscardDraft = onDiscardDraft,
                    onToggleTask = onToggleTask,
                    onJoinMeeting = onJoinMeeting
                )
            }

            if (isGeneratingResponse) {
                item {
                    GeneratingResponseIndicator(currentModel = currentModel)
                }
            }
        }

        // Quick Suggestion Chips Carousel
        QuickPromptChipsBar(
            onPromptClick = { prompt ->
                onInputTextChanged(prompt)
            }
        )

        // Multimodal Input Container
        ChatInputContainer(
            inputText = inputText,
            onInputTextChanged = onInputTextChanged,
            onSendMessage = onSendMessage,
            isWebBrowsingActive = isWebBrowsingActive,
            onToggleWebBrowsing = onToggleWebBrowsing,
            isVoiceRecording = isVoiceRecording,
            onToggleVoiceRecording = onToggleVoiceRecording,
            attachedFiles = attachedFiles,
            onAttachFileClick = onAttachFileClick,
            onRemoveAttachment = onRemoveAttachment
        )
    }
}

@Composable
private fun QuickPromptChipsBar(onPromptClick: (String) -> Unit) {
    val quickSuggestions = listOf(
        Pair("📅 Schedule", "Can you summarize my schedule for today?"),
        Pair("✉️ Draft Email", "Draft a reply to Marcus regarding the API spec review"),
        Pair("✅ Task Check", "Show my active tasks and top priorities"),
        Pair("⚡ Focus Session", "Set up a 50-minute deep work block"),
        Pair("🌐 Web Digest", "Perform a search for the latest tech updates")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        quickSuggestions.forEach { (label, prompt) ->
            Surface(
                onClick = { onPromptClick(prompt) },
                shape = RoundedCornerShape(16.dp),
                color = AuraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = AuraTextSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatAreaHeader(
    currentModel: String,
    availableModels: List<String>,
    onSelectModel: (String) -> Unit,
    isSearchOpen: Boolean,
    onToggleSearch: (Boolean) -> Unit,
    onExportNotes: () -> Unit,
    onClearChat: () -> Unit,
    isCompact: Boolean,
    onOpenDrawer: (() -> Unit)?,
    onToggleContextPanel: (() -> Unit)?
) {
    var modelMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_header"),
        color = AuraSurfaceDark,
        tonalElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mobile Drawer toggle if compact
            if (isCompact && onOpenDrawer != null) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("open_sidebar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Sidebar",
                        tint = AuraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Title & Model Selector
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Smart Orchestrator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = AuraTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AuraAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AuraAccent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(AuraAccent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "280ms",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = AuraAccent
                            )
                        }
                    }
                }

                // Model Selector Pill with dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { modelMenuExpanded = true }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentModel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = AuraSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Model",
                            tint = AuraSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = modelMenuExpanded,
                        onDismissRequest = { modelMenuExpanded = false },
                        modifier = Modifier.background(AuraSurfaceElevated)
                    ) {
                        availableModels.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = model,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (model == currentModel) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (model == currentModel) AuraPrimary else AuraTextPrimary
                                    )
                                },
                                onClick = {
                                    onSelectModel(model)
                                    modelMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Action Buttons: search, export, clear
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onToggleSearch(!isSearchOpen) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("action_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search in conversation",
                        tint = if (isSearchOpen) AuraPrimary else AuraTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onExportNotes,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("action_export")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Export notes",
                        tint = AuraTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("action_clear")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Clear conversation",
                        tint = AuraTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // If compact screen, toggle context panel button
                if (isCompact && onToggleContextPanel != null) {
                    IconButton(
                        onClick = onToggleContextPanel,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_context_panel")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = "Toggle Context Panel",
                            tint = AuraSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    onSendDraft: (String) -> Unit,
    onEditDraft: (EmailDraftWidget) -> Unit,
    onDiscardDraft: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onJoinMeeting: (ScheduleEvent) -> Unit
) {
    val isUser = message.sender == "user"
    val clipboardManager = LocalClipboardManager.current
    var hasLiked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("message_${message.id}"),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Message Header (Sender name + timestamp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AuraPrimary, AuraSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aura Assistant",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary
                )
            } else {
                Text(
                    text = "Alex Mercer",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = AuraTextMuted
            )
        }

        // Message Bubble with custom gradient styling
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) AuraPrimary else AuraSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) AuraPrimaryVariant else AuraBorderDark
            ),
            tonalElevation = if (isUser) 2.dp else 4.dp,
            modifier = Modifier.widthIn(max = 520.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 23.sp
                    ),
                    color = if (isUser) Color.White else AuraTextPrimary
                )

                // Quick actions on assistant messages
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy text",
                            tint = AuraTextMuted,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(message.content))
                                }
                        )

                        Icon(
                            imageVector = if (hasLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Helpful",
                            tint = if (hasLiked) AuraAccent else AuraTextMuted,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { hasLiked = !hasLiked }
                        )
                    }
                }
            }
        }

        // Widgets (if assistant message contains any)
        if (message.widgets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                message.widgets.forEach { widget ->
                    when (widget) {
                        is ScheduleWidget -> {
                            ScheduleWidgetCard(
                                widget = widget,
                                onJoinMeeting = onJoinMeeting
                            )
                        }
                        is EmailDraftWidget -> {
                            EmailDraftWidgetCard(
                                widget = widget,
                                onSend = { onSendDraft(widget.id) },
                                onEdit = { onEditDraft(widget) },
                                onDiscard = { onDiscardDraft(widget.id) }
                            )
                        }
                        is TaskWidget -> {
                            TaskWidgetCard(
                                widget = widget,
                                onToggleTask = onToggleTask
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneratingResponseIndicator(currentModel: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "generating")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderSubtle),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AuraPrimary.copy(alpha = alpha), AuraSecondary.copy(alpha = alpha))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$currentModel is synthesizing...",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = AuraSecondary.copy(alpha = alpha)
            )
        }
    }
}

@Composable
private fun ChatInputContainer(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    isWebBrowsingActive: Boolean,
    onToggleWebBrowsing: () -> Unit,
    isVoiceRecording: Boolean,
    onToggleVoiceRecording: () -> Unit,
    attachedFiles: List<String>,
    onAttachFileClick: () -> Unit,
    onRemoveAttachment: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_input_container"),
        color = AuraSurfaceDark,
        tonalElevation = 5.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Attached files chips row
            if (attachedFiles.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    attachedFiles.forEach { file ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AuraSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AttachFile,
                                    contentDescription = null,
                                    tint = AuraPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = file,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = AuraTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = AuraTextMuted,
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { onRemoveAttachment(file) }
                                )
                            }
                        }
                    }
                }
            }

            // Input Container Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AuraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isVoiceRecording) AuraRose.copy(alpha = 0.6f) else AuraBorderDark
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    // Main Text Field or Voice Waveform
                    if (isVoiceRecording) {
                        VoiceWaveformBar()
                    } else {
                        TextField(
                            value = inputText,
                            onValueChange = onInputTextChanged,
                            placeholder = {
                                Text(
                                    text = "Ask anything, schedule tasks, or drop files...",
                                    color = AuraTextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = AuraTextPrimary,
                                unfocusedTextColor = AuraTextPrimary,
                                cursorColor = AuraPrimary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSendMessage() }),
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chat_text_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Multimodal Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left Multimodal Toggles: Voice, Paperclip, Globe
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Voice input toggle with recording pulse
                            val pulseScale by animateFloatAsState(
                                targetValue = if (isVoiceRecording) 1.15f else 1f,
                                label = "mic_scale"
                            )
                            IconButton(
                                onClick = onToggleVoiceRecording,
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(
                                        if (isVoiceRecording) AuraRose.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                    .testTag("action_voice_input")
                            ) {
                                Icon(
                                    imageVector = if (isVoiceRecording) Icons.Default.Mic else Icons.Outlined.Mic,
                                    contentDescription = "Voice input",
                                    tint = if (isVoiceRecording) AuraRose else AuraTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // File attachment button
                            IconButton(
                                onClick = onAttachFileClick,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("action_file_attachment")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AttachFile,
                                    contentDescription = "Attach file",
                                    tint = AuraTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Web browsing toggle button
                            Surface(
                                onClick = onToggleWebBrowsing,
                                shape = RoundedCornerShape(10.dp),
                                color = if (isWebBrowsingActive) AuraAccent.copy(alpha = 0.12f) else Color.Transparent,
                                border = if (isWebBrowsingActive) androidx.compose.foundation.BorderStroke(1.dp, AuraAccent.copy(alpha = 0.4f)) else null,
                                modifier = Modifier.testTag("action_web_browsing_toggle")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Public,
                                        contentDescription = "Web browsing",
                                        tint = if (isWebBrowsingActive) AuraAccent else AuraTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isWebBrowsingActive) "Web: ON" else "Web: OFF",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isWebBrowsingActive) AuraAccent else AuraTextMuted
                                    )
                                }
                            }
                        }

                        // Send Button (icon: arrow-up, primary gradient pill)
                        IconButton(
                            onClick = onSendMessage,
                            enabled = inputText.isNotBlank() || attachedFiles.isNotEmpty(),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isNotBlank() || attachedFiles.isNotEmpty()) {
                                        Brush.linearGradient(listOf(AuraPrimary, AuraSecondary))
                                    } else {
                                        Brush.linearGradient(listOf(AuraSurfaceLight, AuraSurfaceLight))
                                    }
                                )
                                .testTag("send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send message",
                                tint = if (inputText.isNotBlank() || attachedFiles.isNotEmpty()) Color.White
                                else AuraTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceWaveformBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val h1 by infiniteTransition.animateFloat(initialValue = 8f, targetValue = 28f, animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "h1")
    val h2 by infiniteTransition.animateFloat(initialValue = 18f, targetValue = 10f, animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "h2")
    val h3 by infiniteTransition.animateFloat(initialValue = 12f, targetValue = 24f, animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse), label = "h3")
    val h4 by infiniteTransition.animateFloat(initialValue = 22f, targetValue = 6f, animationSpec = infiniteRepeatable(tween(320), RepeatMode.Reverse), label = "h4")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Listening...",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = AuraRose
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(4.dp).height(h1.dp).clip(RoundedCornerShape(2.dp)).background(AuraRose))
            Box(modifier = Modifier.width(4.dp).height(h2.dp).clip(RoundedCornerShape(2.dp)).background(AuraSecondary))
            Box(modifier = Modifier.width(4.dp).height(h3.dp).clip(RoundedCornerShape(2.dp)).background(AuraCyan))
            Box(modifier = Modifier.width(4.dp).height(h4.dp).clip(RoundedCornerShape(2.dp)).background(AuraAccent))
        }
    }
}
