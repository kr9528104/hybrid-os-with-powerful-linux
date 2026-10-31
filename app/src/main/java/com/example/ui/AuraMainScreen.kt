package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EmailDraftWidget
import com.example.model.ScheduleEvent
import com.example.model.VaultResource
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AuraViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraMainScreen(
    viewModel: AuraViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // Dialog states
    var editingDraft by remember { mutableStateOf<EmailDraftWidget?>(null) }
    var viewingResource by remember { mutableStateOf<VaultResource?>(null) }
    var showAttachmentDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Mobile / Tablet overlay panel states
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showMobileContextSheet by remember { mutableStateOf(false) }

    // Snackbar effect
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    // BackHandler for secondary modules
    BackHandler(enabled = uiState.currentModule != "chat" || drawerState.isOpen || showMobileContextSheet) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (showMobileContextSheet) {
            showMobileContextSheet = false
        } else {
            viewModel.setModule("chat")
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBgDark)
            .drawBehind {
                // Top-right electric magenta glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GarudaDragonPink.copy(alpha = 0.10f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.08f),
                        radius = size.width * 0.65f
                    )
                )
                // Center-left mystic violet ambient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GarudaDragonViolet.copy(alpha = 0.07f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.45f),
                        radius = size.width * 0.55f
                    )
                )
                // Bottom-right electric cyan pulse
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GarudaDragonCyan.copy(alpha = 0.08f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.85f),
                        radius = size.width * 0.6f
                    )
                )
            }
    ) {
        val screenWidth = maxWidth
        val isMobile = screenWidth <= 640.dp
        val isTablet = screenWidth in 641.dp..1024.dp
        val isDesktop = screenWidth > 1024.dp

        if (isMobile) {
            // Mobile layout: Drawer for Sidebar + Main Content + Bottom Sheet for Context Panel
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = AuraSurfaceDark,
                        modifier = Modifier.width(280.dp)
                    ) {
                        AuraSidebar(
                            currentModule = uiState.currentModule,
                            onSelectModule = { module ->
                                viewModel.setModule(module)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onNewConversation = {
                                viewModel.startNewConversation()
                                coroutineScope.launch { drawerState.close() }
                            },
                            recentSessions = uiState.recentSessions,
                            activeSessionId = uiState.activeSessionId,
                            onSelectSession = { session ->
                                viewModel.selectSession(session)
                                coroutineScope.launch { drawerState.close() }
                            },
                            userProfile = uiState.userProfile,
                            onOpenSettings = {
                                showSettingsDialog = true
                                coroutineScope.launch { drawerState.close() }
                            },
                            taskCount = uiState.tasks.count { !it.isCompleted }
                        )
                    }
                }
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        AuraWaybar(
                            currentModule = uiState.currentModule,
                            onSelectModule = { viewModel.setModule(it) },
                            linuxTelemetry = uiState.linuxTelemetry,
                            zenGovernor = uiState.zenGovernor,
                            gitBranch = uiState.gitRepoState.branch
                        )
                    },
                    bottomBar = {
                        // Mobile Navigation Bar for quick module switching
                        NavigationBar(
                            containerColor = AuraSurfaceDark,
                            tonalElevation = 6.dp,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            NavigationBarItem(
                                selected = uiState.currentModule == "chat",
                                onClick = { viewModel.setModule("chat") },
                                icon = { Icon(Icons.Outlined.Forum, contentDescription = "Chat") },
                                label = { Text("Chat") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AuraPrimary,
                                    selectedTextColor = AuraPrimary,
                                    unselectedIconColor = AuraTextMuted,
                                    unselectedTextColor = AuraTextMuted,
                                    indicatorColor = AuraPrimary.copy(alpha = 0.15f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentModule == "tasks",
                                onClick = { viewModel.setModule("tasks") },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            val pending = uiState.tasks.count { !it.isCompleted }
                                            if (pending > 0) {
                                                Badge(containerColor = AuraPrimary) { Text(pending.toString()) }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Outlined.CheckCircle, contentDescription = "Tasks")
                                    }
                                },
                                label = { Text("Tasks") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AuraPrimary,
                                    selectedTextColor = AuraPrimary,
                                    unselectedIconColor = AuraTextMuted,
                                    unselectedTextColor = AuraTextMuted,
                                    indicatorColor = AuraPrimary.copy(alpha = 0.15f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentModule == "calendar",
                                onClick = { viewModel.setModule("calendar") },
                                icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = "Schedule") },
                                label = { Text("Schedule") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AuraPrimary,
                                    selectedTextColor = AuraPrimary,
                                    unselectedIconColor = AuraTextMuted,
                                    unselectedTextColor = AuraTextMuted,
                                    indicatorColor = AuraPrimary.copy(alpha = 0.15f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentModule == "terminal",
                                onClick = { viewModel.setModule("terminal") },
                                icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal") },
                                label = { Text("Terminal") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AuraAccent,
                                    selectedTextColor = AuraAccent,
                                    unselectedIconColor = AuraTextMuted,
                                    unselectedTextColor = AuraTextMuted,
                                    indicatorColor = AuraAccent.copy(alpha = 0.15f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentModule == "opensource",
                                onClick = { viewModel.setModule("opensource") },
                                icon = { Icon(Icons.Outlined.Code, contentDescription = "OSS") },
                                label = { Text("OSS") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AuraSecondary,
                                    selectedTextColor = AuraSecondary,
                                    unselectedIconColor = AuraTextMuted,
                                    unselectedTextColor = AuraTextMuted,
                                    indicatorColor = AuraSecondary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ActiveModuleContent(
                            currentModule = uiState.currentModule,
                            viewModel = viewModel,
                            uiState = uiState,
                            isCompact = true,
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                            onToggleContextPanel = { showMobileContextSheet = true },
                            onEditDraft = { editingDraft = it },
                            onViewResource = { viewingResource = it },
                            onAttachFileClick = { showAttachmentDialog = true },
                            onExportNotes = { showExportDialog = true },
                            onJoinMeeting = { event ->
                                Toast.makeText(context, "Launching meeting: ${event.title}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Mobile Context Panel Bottom Sheet
            if (showMobileContextSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showMobileContextSheet = false },
                    containerColor = AuraSurfaceDark,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    AuraContextPanel(
                        systemMetrics = uiState.systemMetrics,
                        suggestedAutomations = uiState.suggestedAutomations,
                        pinnedResources = uiState.pinnedResources,
                        onSelectAutomation = { auto ->
                            viewModel.triggerAutomation(auto)
                            showMobileContextSheet = false
                        },
                        onViewResource = { res ->
                            viewingResource = res
                            showMobileContextSheet = false
                        },
                        onAddResource = {
                            viewModel.showSnackbar("Resource index updated")
                        },
                        linuxTelemetry = uiState.linuxTelemetry,
                        gitRepoState = uiState.gitRepoState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        } else if (isTablet) {
            // Tablet layout: 2 columns (Sidebar + Center Content), with Context Panel toggleable
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    AuraWaybar(
                        currentModule = uiState.currentModule,
                        onSelectModule = { viewModel.setModule(it) },
                        linuxTelemetry = uiState.linuxTelemetry,
                        zenGovernor = uiState.zenGovernor,
                        gitBranch = uiState.gitRepoState.branch
                    )
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Left Sidebar
                    AuraSidebar(
                        currentModule = uiState.currentModule,
                        onSelectModule = { viewModel.setModule(it) },
                        onNewConversation = { viewModel.startNewConversation() },
                        recentSessions = uiState.recentSessions,
                        activeSessionId = uiState.activeSessionId,
                        onSelectSession = { viewModel.selectSession(it) },
                        userProfile = uiState.userProfile,
                        onOpenSettings = { showSettingsDialog = true },
                        taskCount = uiState.tasks.count { !it.isCompleted },
                        modifier = Modifier.width(240.dp)
                    )

                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(AuraBorderDark)
                    )

                    // Main Active Module Content
                    Box(modifier = Modifier.weight(1f)) {
                        ActiveModuleContent(
                            currentModule = uiState.currentModule,
                            viewModel = viewModel,
                            uiState = uiState,
                            isCompact = true,
                            onOpenDrawer = null,
                            onToggleContextPanel = { showMobileContextSheet = true },
                            onEditDraft = { editingDraft = it },
                            onViewResource = { viewingResource = it },
                            onAttachFileClick = { showAttachmentDialog = true },
                            onExportNotes = { showExportDialog = true },
                            onJoinMeeting = { event ->
                                Toast.makeText(context, "Launching meeting: ${event.title}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            if (showMobileContextSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showMobileContextSheet = false },
                    containerColor = AuraSurfaceDark
                ) {
                    AuraContextPanel(
                        systemMetrics = uiState.systemMetrics,
                        suggestedAutomations = uiState.suggestedAutomations,
                        pinnedResources = uiState.pinnedResources,
                        onSelectAutomation = { auto ->
                            viewModel.triggerAutomation(auto)
                            showMobileContextSheet = false
                        },
                        onViewResource = { res ->
                            viewingResource = res
                            showMobileContextSheet = false
                        },
                        onAddResource = {
                            viewModel.showSnackbar("Resource index updated")
                        },
                        linuxTelemetry = uiState.linuxTelemetry,
                        gitRepoState = uiState.gitRepoState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        } else {
            // Desktop / Wide layout: Full Three-Column Dashboard!
            // Left: Sidebar (260dp) | Center: Main Chat / Module (weighted) | Right: Context Panel (320dp)
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    AuraWaybar(
                        currentModule = uiState.currentModule,
                        onSelectModule = { viewModel.setModule(it) },
                        linuxTelemetry = uiState.linuxTelemetry,
                        zenGovernor = uiState.zenGovernor,
                        gitBranch = uiState.gitRepoState.branch
                    )
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag("three_column_dashboard_root")
                ) {
                    // Column 1: Sidebar
                    AuraSidebar(
                        currentModule = uiState.currentModule,
                        onSelectModule = { viewModel.setModule(it) },
                        onNewConversation = { viewModel.startNewConversation() },
                        recentSessions = uiState.recentSessions,
                        activeSessionId = uiState.activeSessionId,
                        onSelectSession = { viewModel.selectSession(it) },
                        userProfile = uiState.userProfile,
                        onOpenSettings = { showSettingsDialog = true },
                        taskCount = uiState.tasks.count { !it.isCompleted },
                        modifier = Modifier.width(260.dp)
                    )

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(AuraBorderDark)
                    )

                    // Column 2: Main Content Area
                    Box(modifier = Modifier.weight(1f)) {
                        ActiveModuleContent(
                            currentModule = uiState.currentModule,
                            viewModel = viewModel,
                            uiState = uiState,
                            isCompact = false,
                            onOpenDrawer = null,
                            onToggleContextPanel = null,
                            onEditDraft = { editingDraft = it },
                            onViewResource = { viewingResource = it },
                            onAttachFileClick = { showAttachmentDialog = true },
                            onExportNotes = { showExportDialog = true },
                            onJoinMeeting = { event ->
                                Toast.makeText(context, "Launching meeting: ${event.title}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(AuraBorderDark)
                    )

                    // Column 3: Context Panel
                    AuraContextPanel(
                        systemMetrics = uiState.systemMetrics,
                        suggestedAutomations = uiState.suggestedAutomations,
                        pinnedResources = uiState.pinnedResources,
                        onSelectAutomation = { viewModel.triggerAutomation(it) },
                        onViewResource = { viewingResource = it },
                        onAddResource = {
                            viewModel.showSnackbar("Resource index updated")
                        },
                        linuxTelemetry = uiState.linuxTelemetry,
                        gitRepoState = uiState.gitRepoState,
                        modifier = Modifier.width(320.dp)
                    )
                }
            }
        }
    }

    // Dialogs
    editingDraft?.let { draft ->
        EditDraftDialog(
            draft = draft,
            onDismiss = { editingDraft = null },
            onSave = { recipient, subject, body ->
                viewModel.updateDraftContent(draft.id, recipient, subject, body)
                editingDraft = null
            }
        )
    }

    viewingResource?.let { res ->
        ResourcePreviewDialog(
            resource = res,
            onDismiss = { viewingResource = null }
        )
    }

    if (showAttachmentDialog) {
        FileAttachmentDialog(
            onDismiss = { showAttachmentDialog = false },
            onFileSelected = { file ->
                viewModel.attachFile(file)
                showAttachmentDialog = false
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            userProfile = uiState.userProfile,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showExportDialog) {
        val notesContent = buildString {
            appendLine("# Aura AI - Conversation Export")
            appendLine("Session: ${uiState.activeSessionId}")
            appendLine("Model: ${uiState.currentModel}")
            appendLine("----------------------------------------")
            uiState.messages.forEach { msg ->
                appendLine("[${msg.timestamp}] ${if (msg.sender == "user") "Alex" else "Aura"}:")
                appendLine(msg.content)
                appendLine()
            }
        }
        ExportNotesDialog(
            notesContent = notesContent,
            onDismiss = { showExportDialog = false },
            onCopy = {
                clipboardManager.setText(AnnotatedString(notesContent))
                viewModel.showSnackbar("Copied notes to clipboard!")
                showExportDialog = false
            }
        )
    }
}

@Composable
private fun ActiveModuleContent(
    currentModule: String,
    viewModel: AuraViewModel,
    uiState: com.example.viewmodel.AuraUiState,
    isCompact: Boolean,
    onOpenDrawer: (() -> Unit)?,
    onToggleContextPanel: (() -> Unit)?,
    onEditDraft: (EmailDraftWidget) -> Unit,
    onViewResource: (VaultResource) -> Unit,
    onAttachFileClick: () -> Unit,
    onExportNotes: () -> Unit,
    onJoinMeeting: (ScheduleEvent) -> Unit
) {
    when (currentModule) {
        "tasks" -> {
            AuraTaskView(
                tasks = uiState.tasks,
                onToggleTask = { viewModel.toggleTask(it) },
                onAddTask = { title, cat, due, priority ->
                    viewModel.addTask(title, cat, due, priority)
                },
                onDeleteTask = { viewModel.deleteTask(it) }
            )
        }
        "calendar" -> {
            AuraCalendarView(
                events = uiState.calendarEvents,
                onJoinMeeting = { link ->
                    onJoinMeeting(
                        ScheduleEvent("Now", "Design System Sync", 4, "video_call")
                    )
                }
            )
        }
        "knowledge" -> {
            AuraVaultView(
                resources = uiState.pinnedResources,
                onViewResource = onViewResource
            )
        }
        "terminal" -> {
            AuraTerminalView(
                history = uiState.terminalHistory,
                processes = uiState.linuxProcesses,
                scripts = uiState.linuxScripts,
                onExecuteCommand = { viewModel.executeLinuxCommand(it) },
                onTerminateProcess = { viewModel.terminateProcess(it) },
                onClear = { viewModel.clearTerminal() }
            )
        }
        "opensource" -> {
            AuraOpenSourceView(
                repos = uiState.openSourceRepos,
                packages = uiState.garudaPackages,
                gitRepoState = uiState.gitRepoState,
                gitCommitInput = uiState.gitCommitInput,
                onGitCommitInputChanged = { viewModel.onGitCommitInputChanged(it) },
                onStageFile = { viewModel.stageFile(it) },
                onUnstageFile = { viewModel.unstageFile(it) },
                onDiscardFile = { viewModel.gitDiscardFile(it) },
                onCommitChanges = { viewModel.commitGitChanges() },
                onSwitchBranch = { viewModel.switchBranch(it) },
                onGitPull = { viewModel.gitPull() },
                onTogglePackage = { viewModel.togglePackageInstall(it) },
                onRepoClick = { repo ->
                    viewModel.showSnackbar("Inspecting ${repo.name} (${repo.license})")
                }
            )
        }
        "garuda" -> {
            AuraGarudaView(
                snapshots = uiState.garudaSnapshots,
                packages = uiState.garudaPackages,
                zenGovernor = uiState.zenGovernor,
                zramEnabled = uiState.zramEnabled,
                onCreateSnapshot = { name, desc -> viewModel.createSnapshot(name, desc) },
                onDeleteSnapshot = { viewModel.deleteSnapshot(it) },
                onTogglePackage = { viewModel.togglePackageInstall(it) },
                onSetGovernor = { viewModel.setZenGovernor(it) },
                onToggleZram = { viewModel.toggleZram() }
            )
        }
        else -> {
            // Main Chat Area
            AuraChatArea(
                messages = uiState.messages,
                currentModel = uiState.currentModel,
                availableModels = uiState.availableModels,
                onSelectModel = { viewModel.setModel(it) },
                inputText = uiState.inputText,
                onInputTextChanged = { viewModel.onInputTextChanged(it) },
                onSendMessage = { viewModel.sendUserMessage() },
                isWebBrowsingActive = uiState.isWebBrowsingActive,
                onToggleWebBrowsing = { viewModel.toggleWebBrowsing() },
                isVoiceRecording = uiState.isVoiceRecording,
                onToggleVoiceRecording = { viewModel.toggleVoiceRecording() },
                attachedFiles = uiState.attachedFiles,
                onAttachFileClick = onAttachFileClick,
                onRemoveAttachment = { viewModel.removeAttachedFile(it) },
                isGeneratingResponse = uiState.isGeneratingResponse,
                searchQuery = uiState.searchQuery,
                isSearchOpen = uiState.isSearchOpen,
                onToggleSearch = { viewModel.toggleSearch(it) },
                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                onExportNotes = onExportNotes,
                onClearChat = { viewModel.clearChat() },
                onSendDraft = { viewModel.sendDraft(it) },
                onEditDraft = onEditDraft,
                onDiscardDraft = { viewModel.discardDraft(it) },
                onToggleTask = { viewModel.toggleTask(it) },
                onJoinMeeting = onJoinMeeting,
                isCompact = isCompact,
                onOpenDrawer = onOpenDrawer,
                onToggleContextPanel = onToggleContextPanel
            )
        }
    }
}
