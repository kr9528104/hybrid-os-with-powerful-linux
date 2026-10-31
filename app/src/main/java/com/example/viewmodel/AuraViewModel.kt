package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AuraUiState(
    val currentModule: String = "chat", // "chat", "tasks", "calendar", "knowledge", "terminal", "opensource"
    val currentModel: String = "Aura-Reason-v2.5",
    val availableModels: List<String> = listOf(
        "Aura-Reason-v2.5",
        "Aura-Vision-v1",
        "Aura-Fast-v3 (Turbo)",
        "Gemini 1.5 Pro"
    ),
    val activeSessionId: String = "session-active",
    val recentSessions: List<SessionItem> = listOf(
        SessionItem("session-1", "Q4 Financial Forecast Review", "Revenue model and OPEX analysis"),
        SessionItem("session-2", "TypeScript Refactoring Ideas", "Monorepo AST migration pipeline"),
        SessionItem("session-3", "Weekly Grocery & Meal Plan", "High-protein batch cooking list")
    ),
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isWebBrowsingActive: Boolean = true,
    val isVoiceRecording: Boolean = false,
    val attachedFiles: List<String> = emptyList(),
    val isGeneratingResponse: Boolean = false,
    val searchQuery: String = "",
    val isSearchOpen: Boolean = false,
    val userProfile: UserProfile = UserProfile(),
    val systemMetrics: SystemMetrics = SystemMetrics(),
    val linuxTelemetry: LinuxSysTelemetry = LinuxSysTelemetry(),
    val suggestedAutomations: List<SuggestedAutomation> = listOf(
        SuggestedAutomation(
            id = "auto-1",
            label = "Summarize inbox unreads",
            iconName = "mail",
            prompt = "Scan my priority inbox and summarize high-importance unread threads."
        ),
        SuggestedAutomation(
            id = "auto-2",
            label = "Prepare 1:1 talking points",
            iconName = "file-text",
            prompt = "Prepare talking points and open action items for my 1:1 with the Engineering Lead."
        ),
        SuggestedAutomation(
            id = "auto-3",
            label = "Generate weekly focus timer",
            iconName = "play",
            prompt = "Set up a 50-minute deep work block for design token standardization."
        )
    ),
    val pinnedResources: List<VaultResource> = listOf(
        VaultResource(
            id = "res-1",
            name = "design-token-specs.json",
            size = "14 KB",
            updated = "2h ago",
            type = "JSON",
            content = """{
  "theme": "Aura-Dark-Modern",
  "version": "2.4.0",
  "tokens": {
    "color": {
      "primary": "#6366f1",
      "secondary": "#8b5cf6",
      "background": "#0f172a",
      "surface": "#1e293b",
      "accent": "#22c55e"
    },
    "typography": {
      "fontFamily": "Inter, Roboto, sans-serif",
      "scale": "1.25"
    },
    "elevation": {
      "card": "2dp",
      "dialog": "8dp"
    }
  }
}"""
        ),
        VaultResource(
            id = "res-2",
            name = "roadmap-2026.pdf",
            size = "2.4 MB",
            updated = "Yesterday",
            type = "PDF",
            content = "Executive Roadmap 2026\n• Q1: Real-time autonomous orchestration\n• Q2: Multi-modal on-device reasoning engine\n• Q3: Cross-workspace unified semantic index\n• Q4: Federated spatial intelligence modules"
        )
    ),
    val tasks: List<TaskItem> = listOf(
        TaskItem("t1", "Review API Auth Spec for Marcus", "Engineering", "Today, 11:00 AM", false, "High"),
        TaskItem("t2", "Finalize Q4 forecast slides", "Finance", "Today, 03:00 PM", false, "High"),
        TaskItem("t3", "Schedule design retro & critique", "Design", "Tomorrow", false, "Medium")
    ),
    val calendarEvents: List<CalendarEventItem> = listOf(
        CalendarEventItem("e1", "11:00 AM", "Design System Sync", "45 mins", 4, "video_call"),
        CalendarEventItem("e2", "02:30 PM", "1:1 with Engineering Lead", "30 mins", 2, "one_on_one"),
        CalendarEventItem("e3", "04:00 PM", "Quarterly Planning Walkthrough", "60 mins", 12, "presentation"),
        CalendarEventItem("e4", "05:30 PM", "Async Wrap-up & Inbox Zero", "30 mins", 1, "solo")
    ),
    val terminalHistory: List<LinuxCommandOutput> = listOf(
        LinuxCommandOutput(
            id = "cmd-init-1",
            command = "neofetch",
            output = """        .-.        alex@aura-linux
       /v\       -------------
      // \\      OS: Aura Linux (Debian/Ubuntu Core) aarch64
     /(   )\     Host: Aura Virtual Environment
      ^^-^^      Kernel: 6.8.0-45-generic
                 Uptime: 14 days, 6 hours, 32 mins
                 Packages: 1420 (dpkg), 12 (flatpak)
                 Shell: bash 5.2.21
                 Terminal: Aura Developer Console v3.1
                 CPU: ARM Cortex-A78 (8) @ 2.84GHz
                 Memory: 4.2 GiB / 16.0 GiB (26%)""",
            exitCode = 0,
            timestamp = "10:12:04"
        ),
        LinuxCommandOutput(
            id = "cmd-init-2",
            command = "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'",
            output = """NAMES              STATUS          PORTS
aura-ollama-llm    Up 4 hours      0.0.0.0:11434->11434/tcp
aura-postgres-db   Up 14 days      0.0.0.0:5432->5432/tcp
aura-redis-cache   Up 14 days      0.0.0.0:6379->6379/tcp
aura-nginx-proxy   Up 14 days      0.0.0.0:80->80/tcp, 443->443/tcp""",
            exitCode = 0,
            timestamp = "10:13:10"
        )
    ),
    val openSourceRepos: List<OpenSourceRepo> = listOf(
        OpenSourceRepo(
            id = "repo-1",
            name = "linux",
            owner = "torvalds",
            description = "Linux kernel source tree by Linus Torvalds.",
            language = "C",
            stars = "182k",
            license = "GPL-2.0",
            version = "v6.8.0",
            url = "https://github.com/torvalds/linux"
        ),
        OpenSourceRepo(
            id = "repo-2",
            name = "compose-multiplatform",
            owner = "JetBrains",
            description = "Jetpack Compose for Desktop and Web, based on Google's modern toolkit.",
            language = "Kotlin",
            stars = "17.4k",
            license = "Apache-2.0",
            version = "v1.7.1",
            url = "https://github.com/JetBrains/compose-multiplatform"
        ),
        OpenSourceRepo(
            id = "repo-3",
            name = "kubernetes",
            owner = "kubernetes",
            description = "Production-Grade Container Scheduling and Management.",
            language = "Go",
            stars = "112k",
            license = "Apache-2.0",
            version = "v1.31.1",
            url = "https://github.com/kubernetes/kubernetes"
        ),
        OpenSourceRepo(
            id = "repo-4",
            name = "rust",
            owner = "rust-lang",
            description = "Empowering everyone to build reliable and efficient software.",
            language = "Rust",
            stars = "98k",
            license = "MIT / Apache-2.0",
            version = "1.82.0",
            url = "https://github.com/rust-lang/rust"
        ),
        OpenSourceRepo(
            id = "repo-5",
            name = "neovim",
            owner = "neovim",
            description = "Vim-fork focused on extensibility and usability.",
            language = "Lua / C",
            stars = "84k",
            license = "Apache-2.0",
            version = "v0.10.2",
            url = "https://github.com/neovim/neovim"
        ),
        OpenSourceRepo(
            id = "repo-6",
            name = "curl",
            owner = "curl",
            description = "A command line tool and library for transferring data with URLs.",
            language = "C",
            stars = "36k",
            license = "curl license",
            version = "8.10.1",
            url = "https://github.com/curl/curl"
        ),
        OpenSourceRepo(
            id = "repo-7",
            name = "ollama",
            owner = "ollama",
            description = "Get up and running with Llama 3, Mistral, Gemma and other large language models.",
            language = "Go",
            stars = "99k",
            license = "MIT",
            version = "v0.3.14",
            url = "https://github.com/ollama/ollama"
        )
    ),
    val linuxProcesses: List<LinuxProcessItem> = listOf(
        LinuxProcessItem(1, "root", 0.1f, 18, "systemd --system --deserialize", "System"),
        LinuxProcessItem(412, "systemd-network", 0.0f, 12, "systemd-networkd", "System"),
        LinuxProcessItem(890, "ollama", 8.4f, 2140, "ollama serve", "Running"),
        LinuxProcessItem(1024, "postgres", 1.8f, 320, "postgres: 16-alpine", "Running"),
        LinuxProcessItem(1140, "redis", 0.4f, 64, "redis-server *:6379", "Running"),
        LinuxProcessItem(1280, "nginx", 0.2f, 42, "nginx: master process", "Running"),
        LinuxProcessItem(2410, "alex", 2.2f, 180, "bash -i (Aura Terminal)", "Running"),
        LinuxProcessItem(3120, "alex", 1.1f, 96, "fastfetch --pipe", "Sleeping")
    ),
    val linuxScripts: List<LinuxScriptTemplate> = listOf(
        LinuxScriptTemplate(
            id = "script-1",
            title = "System Health & Diagnostic",
            filename = "healthcheck.sh",
            description = "Checks CPU load, RAM usage, storage threshold, and docker daemon status.",
            code = """#!/usr/bin/env bash
set -euo pipefail
echo "=== Aura System Health Check ==="
echo "Kernel: $(uname -r)"
echo "Uptime: $(uptime -p)"
echo "--- Memory Usage ---"
free -h
echo "--- Disk Space ---"
df -h /
echo "--- Docker Containers ---"
docker ps --format "table {{.Names}}\t{{.Status}}"
echo "Healthcheck complete." """
        ),
        LinuxScriptTemplate(
            id = "script-2",
            title = "Automated PostgreSQL Backup",
            filename = "backup_db.sh",
            description = "Exports compressed SQL dump with timestamp rotation.",
            code = """#!/usr/bin/env bash
BACKUP_DIR="/var/backups/aura"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
mkdir -p "${'$'}BACKUP_DIR"
pg_dump -U postgres -d aura_prod | gzip > "${'$'}BACKUP_DIR/db_${'$'}TIMESTAMP.sql.gz"
echo "Backup saved to ${'$'}BACKUP_DIR/db_${'$'}TIMESTAMP.sql.gz" """
        ),
        LinuxScriptTemplate(
            id = "script-3",
            title = "Docker Stack Zero-Downtime Reload",
            filename = "reload_stack.sh",
            description = "Gracefully pulls new images and reloads reverse proxy without dropping connections.",
            code = """#!/usr/bin/env bash
docker compose pull
docker compose up -d --remove-orphans
docker exec aura-nginx-proxy nginx -s reload
echo "Stack successfully reloaded." """
        )
    ),
    val garudaSnapshots: List<GarudaSnapshot> = listOf(
        GarudaSnapshot("snap-1", "@snapshot-2026-10-31-boot", "Today, 08:00 AM", "btrfs-subvolume", "1.8 GB", "Clean system cold boot"),
        GarudaSnapshot("snap-2", "@snapshot-pre-pacman-update", "Today, 09:30 AM", "btrfs-subvolume", "2.1 GB", "Automatic Snapper snapshot pre-system upgrade"),
        GarudaSnapshot("snap-3", "@snapshot-dragonized-theme", "Yesterday", "btrfs-subvolume", "2.4 GB", "Garuda Dragonized Sweet theme applied")
    ),
    val garudaPackages: List<GarudaPackage> = listOf(
        GarudaPackage("pkg-1", "linux-zen", "6.8.0.zen1-1", "core", "148 MB", "Tuned Linux kernel for maximum responsiveness & low-latency gaming", true),
        GarudaPackage("pkg-2", "garuda-dr460nized", "3.4.1-2", "chaotic-aur", "42 MB", "Dragonized Sweet KDE Plasma configuration and glowing assets", true),
        GarudaPackage("pkg-3", "snapper-gui", "0.7.2-1", "extra", "12 MB", "Graphical manager for BTRFS snapshots and automatic rollback", true),
        GarudaPackage("pkg-4", "ananicy-cpp", "1.1.1-1", "chaotic-aur", "4 MB", "Auto-renice daemon to prioritize desktop smoothness & input responsiveness", true),
        GarudaPackage("pkg-5", "alacritty-garuda", "0.13.2-1", "chaotic-aur", "8 MB", "GPU-accelerated terminal emulator with neon color scheme", true),
        GarudaPackage("pkg-6", "hyprland-git", "0.42.0.r12", "chaotic-aur", "28 MB", "Dynamic tiling Wayland compositor with fluid physics", false)
    ),
    val zenGovernor: String = "Performance",
    val zramEnabled: Boolean = true,
    val gitRepoState: GitRepoState = GitRepoState(
        branch = "feature/hyprland-ui",
        remoteUrl = "https://github.com/aistudio/aura-linux-workstation.git",
        availableBranches = listOf(
            GitBranch("main", isCurrent = false, aheadCount = 0, behindCount = 0),
            GitBranch("feature/hyprland-ui", isCurrent = true, aheadCount = 2, behindCount = 0),
            GitBranch("hotfix/waybar-telemetry", isCurrent = false, aheadCount = 1, behindCount = 0)
        ),
        stagedFiles = listOf(
            GitFileDiff(
                path = "app/src/main/java/com/example/ui/components/AuraWaybar.kt",
                status = "staged",
                additions = 184,
                deletions = 0,
                diffSnippet = "+ package com.example.ui.components\n+ @Composable fun AuraWaybar(...) {\n+   // Hyprland Waybar status bar implementation\n+ }"
            )
        ),
        unstagedFiles = listOf(
            GitFileDiff(
                path = "app/src/main/java/com/example/ui/components/AuraOpenSourceView.kt",
                status = "modified",
                additions = 95,
                deletions = 14,
                diffSnippet = "- // Generic card list\n+ // Dual tab: OSS Catalog + Interactive Git Workspace\n+ TabRow(selectedTabIndex = selectedTab) { ... }"
            ),
            GitFileDiff(
                path = "app/src/main/java/com/example/ui/theme/Color.kt",
                status = "modified",
                additions = 12,
                deletions = 2,
                diffSnippet = "+ val HyprlandBorderActive = Color(0xFF00F2FE)\n+ val HyprlandBorderInactive = Color(0xFF1F293D)"
            ),
            GitFileDiff(
                path = "res/values/strings.xml",
                status = "modified",
                additions = 4,
                deletions = 1,
                diffSnippet = "+ <string name=\"waybar_active_title\">Hyprland Workstation</string>"
            )
        ),
        recentCommits = listOf(
            GitCommitItem(
                hash = "a4b7f91",
                message = "feat(ui): implement Hyprland Waybar status panel & workspace pills",
                author = "alex@aura-station",
                timeAgo = "18 mins ago",
                branch = "feature/hyprland-ui"
            ),
            GitCommitItem(
                hash = "f1c082d",
                message = "perf(kernel): integrate Zen scheduler governor and sub-50ms dispatch",
                author = "alex@aura-station",
                timeAgo = "2 hours ago",
                branch = "feature/hyprland-ui"
            ),
            GitCommitItem(
                hash = "3e90aa4",
                message = "refactor(shell): POSIX command evaluator & BTRFS Snapper bindings",
                author = "alex@aura-station",
                timeAgo = "Yesterday",
                branch = "main"
            )
        )
    ),
    val gitCommitInput: String = "",
    val snackbarMessage: String? = null
)

class AuraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuraUiState())
    val uiState: StateFlow<AuraUiState> = _uiState.asStateFlow()

    init {
        loadInitialMessages()
    }

    private fun loadInitialMessages() {
        val initialMessages = listOf(
            ChatMessage(
                id = "msg-001",
                sender = "user",
                timestamp = "10:14 AM",
                content = "Can you summarize my schedule for today and draft a reply to Marcus regarding the API spec review?"
            ),
            ChatMessage(
                id = "msg-002",
                sender = "assistant",
                timestamp = "10:14 AM",
                content = "Good morning, Alex. Here is your daily overview and the requested draft.",
                widgets = listOf(
                    ScheduleWidget(
                        id = "widget-schedule-today",
                        title = "Today's Key Events",
                        events = listOf(
                            ScheduleEvent("11:00 AM", "Design System Sync", 4, "video_call"),
                            ScheduleEvent("02:30 PM", "1:1 with Engineering Lead", 2, "one_on_one"),
                            ScheduleEvent("04:00 PM", "Quarterly Planning Walkthrough", 12, "presentation")
                        )
                    ),
                    EmailDraftWidget(
                        id = "widget-draft-marcus",
                        recipient = "marcus@company.internal",
                        subject = "Re: API Specification Review & Feedback",
                        body = "Hi Marcus,\n\nI reviewed the updated endpoints for the auth workflow. Overall, the schemas look solid—just left two minor suggestions on token refresh cadence in the doc.\n\nLet me know if you want to touch base after our 11:00 AM sync.\n\nBest,\nAlex",
                        quickActions = listOf("Send via Gmail", "Edit Draft", "Discard"),
                        status = EmailStatus.Draft
                    )
                )
            )
        )
        _uiState.update { it.copy(messages = initialMessages) }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun toggleWebBrowsing() {
        _uiState.update { it.copy(isWebBrowsingActive = !it.isWebBrowsingActive) }
    }

    fun toggleVoiceRecording() {
        val currentlyRecording = _uiState.value.isVoiceRecording
        if (!currentlyRecording) {
            _uiState.update { it.copy(isVoiceRecording = true) }
        } else {
            // Stop voice recording and simulate transcription
            _uiState.update {
                it.copy(
                    isVoiceRecording = false,
                    inputText = "Schedule a 30-minute sync with the UI team on Wednesday at 2pm."
                )
            }
        }
    }

    fun attachFile(fileName: String) {
        _uiState.update { state ->
            if (!state.attachedFiles.contains(fileName)) {
                state.copy(attachedFiles = state.attachedFiles + fileName)
            } else state
        }
    }

    fun removeAttachedFile(fileName: String) {
        _uiState.update { state ->
            state.copy(attachedFiles = state.attachedFiles.filter { it != fileName })
        }
    }

    fun setModule(module: String) {
        _uiState.update { it.copy(currentModule = module) }
    }

    fun setModel(model: String) {
        _uiState.update {
            it.copy(
                currentModel = model,
                snackbarMessage = "Model switched to $model"
            )
        }
    }

    fun startNewConversation() {
        val welcomeMsg = ChatMessage(
            id = "msg-${UUID.randomUUID()}",
            sender = "assistant",
            timestamp = currentTimeString(),
            content = "Hello Alex! I am ready for your next session with ${uiState.value.currentModel}. What would you like to achieve right now?"
        )
        _uiState.update {
            it.copy(
                messages = listOf(welcomeMsg),
                activeSessionId = "session-new-${System.currentTimeMillis()}",
                currentModule = "chat",
                snackbarMessage = "New conversation started"
            )
        }
    }

    fun selectSession(session: SessionItem) {
        _uiState.update { state ->
            state.copy(
                activeSessionId = session.id,
                currentModule = "chat",
                snackbarMessage = "Loaded: ${session.label}"
            )
        }
    }

    fun clearChat() {
        _uiState.update {
            it.copy(
                messages = emptyList(),
                snackbarMessage = "Conversation cleared"
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearch(isOpen: Boolean) {
        _uiState.update {
            it.copy(
                isSearchOpen = isOpen,
                searchQuery = if (!isOpen) "" else it.searchQuery
            )
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun sendUserMessage() {
        val query = _uiState.value.inputText.trim()
        val attachments = _uiState.value.attachedFiles
        if (query.isEmpty() && attachments.isEmpty()) return

        val userMessageContent = buildString {
            append(query)
            if (attachments.isNotEmpty()) {
                append("\n[Attached: ${attachments.joinToString(", ")}]")
            }
        }

        val userMessage = ChatMessage(
            id = "msg-${UUID.randomUUID()}",
            sender = "user",
            timestamp = currentTimeString(),
            content = userMessageContent
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                inputText = "",
                attachedFiles = emptyList(),
                isGeneratingResponse = true
            )
        }

        viewModelScope.launch {
            delay(50) // Ultra-fast response
            val reply = generateAssistantReply(query)
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + reply,
                    isGeneratingResponse = false
                )
            }
        }
    }

    fun triggerAutomation(automation: SuggestedAutomation) {
        _uiState.update { it.copy(currentModule = "chat") }
        val userMessage = ChatMessage(
            id = "msg-${UUID.randomUUID()}",
            sender = "user",
            timestamp = currentTimeString(),
            content = automation.prompt
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isGeneratingResponse = true
            )
        }

        viewModelScope.launch {
            delay(50)
            val reply = when (automation.id) {
                "auto-1" -> ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = currentTimeString(),
                    content = "I parsed your Gmail and Slack inboxes. Found 3 priority threads requiring attention:\n\n1. **Marcus (Core API)**: Awaiting approval on rate-limiting tokens.\n2. **Elena (Design System)**: Shared Figma preview for multi-column dashboard tokens.\n3. **DevOps Alert**: Cloud staging cluster completed regression check successfully.",
                    widgets = listOf(
                        TaskWidget(
                            title = "Inbox Action Items",
                            tasks = listOf(
                                TaskItem("t-auto-1", "Reply to Elena regarding token contrast", "Design", "Today", false, "High"),
                                TaskItem("t-auto-2", "Sign off on staging deployment", "DevOps", "Today", false, "Medium")
                            )
                        )
                    )
                )
                "auto-2" -> ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = currentTimeString(),
                    content = "Here are curated discussion anchors for your 2:30 PM 1:1 with the Engineering Lead:\n\n• **Q4 Architecture Alignment**: Reviewing GraphQL vs REST auth caching.\n• **Design Token Sync**: Ensuring automated generation pipeline works with Kotlin Compose styles.\n• **Sprint Velocity**: 12 story points cleared ahead of schedule.",
                    widgets = listOf(
                        EmailDraftWidget(
                            id = "widget-1on1-agenda",
                            recipient = "eng-lead@company.internal",
                            subject = "Talking points for our 2:30 PM 1:1",
                            body = "Hey,\n\nHere are the 3 key topics I'd love to walk through today:\n1. GraphQL & REST auth caching\n2. Design token Compose automation\n3. Velocity & resource allocation\n\nSee you at 2:30!\nAlex",
                            quickActions = listOf("Send via Gmail", "Edit Draft", "Discard"),
                            status = EmailStatus.Draft
                        )
                    )
                )
                else -> ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = currentTimeString(),
                    content = "Deep work focus timer scheduled: 50 minutes deep work + 10 minutes restorative break. Notifications on Slack and Gmail have been muted until 11:00 AM.",
                    widgets = listOf(
                        ScheduleWidget(
                            id = "widget-focus-block",
                            title = "Scheduled Focus Session",
                            events = listOf(
                                ScheduleEvent("10:15 AM - 11:05 AM", "Deep Focus: Design Tokens & Jetpack Compose", 1, "solo")
                            )
                        )
                    )
                )
            }

            _uiState.update { state ->
                state.copy(
                    messages = state.messages + reply,
                    isGeneratingResponse = false
                )
            }
        }
    }

    private fun generateAssistantReply(query: String): ChatMessage {
        val lower = query.lowercase()
        val time = currentTimeString()

        return when {
            lower.contains("linux") || lower.contains("kernel") || lower.contains("terminal") || lower.contains("shell") -> {
                ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = time,
                    content = "Linux Kernel environment is active (`Linux 6.8.0-45-generic aarch64`). Standard POSIX shell utilities, package management, and open-source Docker orchestration tools are accessible in the **Linux Terminal** module."
                )
            }
            lower.contains("task") || lower.contains("todo") -> {
                ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = time,
                    content = "I've analyzed your current task backlog. Here are the most pressing milestones for today:",
                    widgets = listOf(
                        TaskWidget(
                            title = "Active Sprint Tasks",
                            tasks = _uiState.value.tasks
                        )
                    )
                )
            }
            lower.contains("schedule") || lower.contains("meeting") || lower.contains("calendar") -> {
                ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = time,
                    content = "Here is your synchronized itinerary from Google Calendar:",
                    widgets = listOf(
                        ScheduleWidget(
                            title = "Synchronized Schedule",
                            events = _uiState.value.calendarEvents.map {
                                ScheduleEvent(it.time, it.title, it.attendees, it.type)
                            }
                        )
                    )
                )
            }
            lower.contains("email") || lower.contains("draft") || lower.contains("reply") -> {
                ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = time,
                    content = "I composed a refined email draft according to your instructions:",
                    widgets = listOf(
                        EmailDraftWidget(
                            id = "widget-${UUID.randomUUID()}",
                            recipient = "collaborator@company.internal",
                            subject = "Follow-up: Project Architecture & Execution Plan",
                            body = "Hi team,\n\nFollowing up on our discussions, we have consolidated the design specs and data models. The dark modern theme tokens and three-column orchestration pipeline are now locked in.\n\nLet me know if any further tweaks are needed before Friday.\n\nWarm regards,\nAlex",
                            quickActions = listOf("Send via Gmail", "Edit Draft", "Discard"),
                            status = EmailStatus.Draft
                        )
                    )
                )
            }
            else -> {
                ChatMessage(
                    id = "msg-${UUID.randomUUID()}",
                    sender = "assistant",
                    timestamp = time,
                    content = "Understood. Using ${_uiState.value.currentModel} with web search ${_uiState.value.isWebBrowsingActive.let { if (it) "enabled" else "disabled" }}, I have analyzed your request: \"$query\". All contextual vaults, Linux POSIX tools, and open-source repositories are synced and ready for immediate deployment."
                )
            }
        }
    }

    fun executeLinuxCommand(rawCommand: String) {
        val cmd = rawCommand.trim()
        if (cmd.isEmpty()) return

        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val output = evaluateLinuxCommand(cmd)

        val newRecord = LinuxCommandOutput(
            id = "cmd-${UUID.randomUUID()}",
            command = cmd,
            output = output,
            exitCode = 0,
            timestamp = time
        )

        _uiState.update { state ->
            state.copy(terminalHistory = state.terminalHistory + newRecord)
        }
    }

    private fun evaluateLinuxCommand(cmd: String): String {
        val lower = cmd.lowercase()
        return when {
            lower == "uname -a" -> "Linux aura-station 6.8.0-45-generic #45-Ubuntu SMP PREEMPT_DYNAMIC aarch64 GNU/Linux"
            lower == "uptime" -> " 10:14:22 up 14 days,  6:32,  2 users,  load average: 0.42, 0.38, 0.31"
            lower == "free -h" || lower == "free -m" -> """              total        used        free      shared  buff/cache   available
Mem:           15Gi       4.1Gi       8.2Gi       210Mi       2.7Gi        10Gi
Swap:         4.0Gi          0B       4.0Gi"""
            lower == "df -h" -> """Filesystem      Size  Used Avail Use% Mounted on
/dev/nvme0n1p2  512G   64G  422G  13% /
tmpfs           7.8G     0  7.8G   0% /dev/shm
/dev/nvme0n1p1  512M  6.1M  506M   2% /boot/efi"""
            lower.startsWith("docker ps") -> """NAMES              IMAGE                 STATUS          PORTS
aura-ollama-llm    ollama/ollama:latest  Up 4 hours      0.0.0.0:11434->11434/tcp
aura-postgres-db   postgres:16-alpine    Up 14 days      0.0.0.0:5432->5432/tcp
aura-redis-cache   redis:7-alpine        Up 14 days      0.0.0.0:6379->6379/tcp
aura-nginx-proxy   nginx:alpine          Up 14 days      0.0.0.0:80->80/tcp"""
            lower.startsWith("git status") -> """On branch main
Your branch is up to date with 'origin/main'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
	modified:   app/src/main/java/com/example/ui/AuraMainScreen.kt

nothing added to commit but untracked files present"""
            lower == "neofetch" || lower == "fastfetch" -> """        .-.        alex@aura-linux
       /v\       -------------
      // \\      OS: Aura Linux (Debian/Ubuntu Core) aarch64
     /(   )\     Host: Aura Virtual Environment
      ^^-^^      Kernel: 6.8.0-45-generic
                 Uptime: 14 days, 6 hours, 32 mins
                 Packages: 1420 (dpkg), 12 (flatpak)
                 Shell: bash 5.2.21
                 Terminal: Aura Developer Console v3.1
                 CPU: ARM Cortex-A78 (8) @ 2.84GHz
                 Memory: 4.2 GiB / 16.0 GiB (26%)"""
            lower.startsWith("ls") -> "bin  boot  dev  etc  home  lib  opt  proc  root  run  sbin  sys  tmp  usr  var"
            lower == "pwd" -> "/home/alex/workspace/aura-ai"
            lower == "whoami" -> "alex"
            lower.startsWith("curl") -> """HTTP/2 200 
server: GitHub.com
date: Sat, 31 Oct 2026 15:30:00 GMT
content-type: application/json; charset=utf-8
cache-control: public, max-age=60, s-maxage=60
x-github-media-type: github.v3; format=json
x-ratelimit-limit: 5000
x-ratelimit-remaining: 4992"""
            lower == "clear" -> ""
            else -> "aura-exec: executed '$cmd' with exit status 0 (stdout flushed)."
        }
    }

    fun clearTerminal() {
        _uiState.update { it.copy(terminalHistory = emptyList()) }
    }

    fun sendDraft(widgetId: String) {
        updateWidgetStatus(widgetId, EmailStatus.Sent)
        _uiState.update { it.copy(snackbarMessage = "Email sent successfully via Gmail!") }
    }

    fun discardDraft(widgetId: String) {
        updateWidgetStatus(widgetId, EmailStatus.Discarded)
        _uiState.update { it.copy(snackbarMessage = "Email draft discarded") }
    }

    fun updateDraftContent(widgetId: String, recipient: String, subject: String, body: String) {
        _uiState.update { state ->
            val updatedMessages = state.messages.map { msg ->
                val updatedWidgets = msg.widgets.map { widget ->
                    if (widget is EmailDraftWidget && widget.id == widgetId) {
                        widget.copy(recipient = recipient, subject = subject, body = body)
                    } else widget
                }
                msg.copy(widgets = updatedWidgets)
            }
            state.copy(
                messages = updatedMessages,
                snackbarMessage = "Draft updated successfully"
            )
        }
    }

    private fun updateWidgetStatus(widgetId: String, status: EmailStatus) {
        _uiState.update { state ->
            val updatedMessages = state.messages.map { msg ->
                val updatedWidgets = msg.widgets.map { widget ->
                    if (widget is EmailDraftWidget && widget.id == widgetId) {
                        widget.copy(status = status)
                    } else widget
                }
                msg.copy(widgets = updatedWidgets)
            }
            state.copy(messages = updatedMessages)
        }
    }

    fun toggleTask(taskId: String) {
        _uiState.update { state ->
            val updated = state.tasks.map {
                if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
            }
            state.copy(tasks = updated)
        }
    }

    fun addTask(title: String, category: String, dueDate: String, priority: String) {
        val newTask = TaskItem(
            id = "t-${UUID.randomUUID()}",
            title = title,
            category = category.ifEmpty { "General" },
            dueDate = dueDate.ifEmpty { "Today" },
            isCompleted = false,
            priority = priority
        )
        _uiState.update { state ->
            state.copy(
                tasks = listOf(newTask) + state.tasks,
                snackbarMessage = "Task created"
            )
        }
    }

    fun deleteTask(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.filter { it.id != taskId },
                snackbarMessage = "Task removed"
            )
        }
    }

    fun terminateProcess(pid: Int) {
        _uiState.update { state ->
            val updated = state.linuxProcesses.filter { it.pid != pid }
            state.copy(
                linuxProcesses = updated,
                snackbarMessage = "SIGTERM sent to PID $pid"
            )
        }
    }
    fun createSnapshot(name: String, desc: String) {
        val newSnap = GarudaSnapshot(
            id = "snap-${UUID.randomUUID()}",
            name = if (name.startsWith("@")) name else "@snapshot-$name",
            timestamp = "Just now",
            type = "btrfs-subvolume",
            size = "1.9 GB",
            description = desc.ifEmpty { "Manual user snapshot created via Garuda Hub" }
        )
        _uiState.update { state ->
            state.copy(
                garudaSnapshots = listOf(newSnap) + state.garudaSnapshots,
                snackbarMessage = "BTRFS Snapshot created: ${newSnap.name}"
            )
        }
    }

    fun deleteSnapshot(id: String) {
        _uiState.update { state ->
            state.copy(
                garudaSnapshots = state.garudaSnapshots.filter { it.id != id },
                snackbarMessage = "Snapshot pruned from subvolume"
            )
        }
    }

    fun togglePackageInstall(id: String) {
        _uiState.update { state ->
            val updated = state.garudaPackages.map { pkg ->
                if (pkg.id == id) {
                    val newInstalled = !pkg.isInstalled
                    pkg.copy(isInstalled = newInstalled)
                } else pkg
            }
            val target = state.garudaPackages.find { it.id == id }
            val action = if (target?.isInstalled == true) "uninstalled" else "installed"
            state.copy(
                garudaPackages = updated,
                snackbarMessage = "Package ${target?.name} $action via Pacman"
            )
        }
    }

    fun setZenGovernor(gov: String) {
        _uiState.update {
            it.copy(
                zenGovernor = gov,
                snackbarMessage = "Zen CPU Governor set to $gov"
            )
        }
    }

    fun toggleZram() {
        _uiState.update {
            it.copy(
                zramEnabled = !it.zramEnabled,
                snackbarMessage = if (!it.zramEnabled) "ZRAM Swap (zstd) enabled" else "ZRAM Swap disabled"
            )
        }
    }

    fun onGitCommitInputChanged(text: String) {
        _uiState.update { it.copy(gitCommitInput = text) }
    }

    fun stageFile(file: GitFileDiff) {
        _uiState.update { state ->
            val unstaged = state.gitRepoState.unstagedFiles.filter { it.path != file.path }
            val staged = state.gitRepoState.stagedFiles + file.copy(status = "staged")
            state.copy(
                gitRepoState = state.gitRepoState.copy(
                    stagedFiles = staged,
                    unstagedFiles = unstaged
                ),
                snackbarMessage = "Staged: ${file.path.substringAfterLast('/')}"
            )
        }
    }

    fun unstageFile(file: GitFileDiff) {
        _uiState.update { state ->
            val staged = state.gitRepoState.stagedFiles.filter { it.path != file.path }
            val unstaged = state.gitRepoState.unstagedFiles + file.copy(status = "modified")
            state.copy(
                gitRepoState = state.gitRepoState.copy(
                    stagedFiles = staged,
                    unstagedFiles = unstaged
                ),
                snackbarMessage = "Unstaged: ${file.path.substringAfterLast('/')}"
            )
        }
    }

    fun gitDiscardFile(file: GitFileDiff) {
        _uiState.update { state ->
            val unstaged = state.gitRepoState.unstagedFiles.filter { it.path != file.path }
            state.copy(
                gitRepoState = state.gitRepoState.copy(unstagedFiles = unstaged),
                snackbarMessage = "Discarded changes in ${file.path.substringAfterLast('/')}"
            )
        }
    }

    fun commitGitChanges() {
        val msg = _uiState.value.gitCommitInput.trim()
        if (msg.isEmpty()) {
            _uiState.update { it.copy(snackbarMessage = "Please enter a commit message") }
            return
        }
        val staged = _uiState.value.gitRepoState.stagedFiles
        if (staged.isEmpty()) {
            _uiState.update { it.copy(snackbarMessage = "Nothing staged to commit (use [+] to stage files)") }
            return
        }

        val shortHash = UUID.randomUUID().toString().substring(0, 7)
        val newCommit = GitCommitItem(
            hash = shortHash,
            message = msg,
            author = "alex@aura-station",
            timeAgo = "Just now",
            branch = _uiState.value.gitRepoState.branch
        )

        _uiState.update { state ->
            val updatedBranches = state.gitRepoState.availableBranches.map {
                if (it.name == state.gitRepoState.branch) {
                    it.copy(aheadCount = it.aheadCount + 1)
                } else it
            }
            state.copy(
                gitRepoState = state.gitRepoState.copy(
                    stagedFiles = emptyList(),
                    recentCommits = listOf(newCommit) + state.gitRepoState.recentCommits,
                    availableBranches = updatedBranches
                ),
                gitCommitInput = "",
                snackbarMessage = "[$shortHash] $msg committed to ${state.gitRepoState.branch}"
            )
        }
    }

    fun switchBranch(branchName: String) {
        _uiState.update { state ->
            val updatedBranches = state.gitRepoState.availableBranches.map {
                it.copy(isCurrent = it.name == branchName)
            }
            state.copy(
                gitRepoState = state.gitRepoState.copy(
                    branch = branchName,
                    availableBranches = updatedBranches
                ),
                snackbarMessage = "Switched to branch '$branchName'"
            )
        }
    }

    fun gitPull() {
        viewModelScope.launch {
            _uiState.update { it.copy(snackbarMessage = "Fetching from origin/main...") }
            delay(40)
            _uiState.update { state ->
                val updatedBranches = state.gitRepoState.availableBranches.map {
                    if (it.name == state.gitRepoState.branch) it.copy(aheadCount = 0, behindCount = 0)
                    else it
                }
                state.copy(
                    gitRepoState = state.gitRepoState.copy(availableBranches = updatedBranches),
                    snackbarMessage = "Already up to date with remote"
                )
            }
        }
    }

    private fun currentTimeString(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
}

