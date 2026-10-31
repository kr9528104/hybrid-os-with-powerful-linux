package com.example.model

sealed interface AssistantWidget {
    val id: String
}

data class ScheduleEvent(
    val time: String,
    val title: String,
    val attendees: Int,
    val type: String // "video_call", "one_on_one", "presentation"
)

data class ScheduleWidget(
    override val id: String = "widget_schedule",
    val title: String = "Today's Key Events",
    val events: List<ScheduleEvent>
) : AssistantWidget

data class EmailDraftWidget(
    override val id: String = "widget_email",
    val recipient: String,
    val subject: String,
    val body: String,
    val quickActions: List<String> = listOf("Send via Gmail", "Edit Draft", "Discard"),
    val status: EmailStatus = EmailStatus.Draft
) : AssistantWidget

enum class EmailStatus {
    Draft,
    Sent,
    Discarded
}

data class TaskWidget(
    override val id: String = "widget_task",
    val title: String,
    val tasks: List<TaskItem>
) : AssistantWidget

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "assistant"
    val timestamp: String,
    val content: String,
    val widgets: List<AssistantWidget> = emptyList()
)

data class TaskItem(
    val id: String,
    val title: String,
    val category: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val priority: String = "Medium"
)

data class CalendarEventItem(
    val id: String,
    val time: String,
    val title: String,
    val duration: String,
    val attendees: Int,
    val type: String,
    val link: String = "https://meet.google.com/aur-sync-mtg"
)

data class VaultResource(
    val id: String,
    val name: String,
    val size: String,
    val updated: String,
    val type: String,
    val content: String
)

data class UserProfile(
    val name: String = "Alex Mercer",
    val role: String = "Product Designer & Linux Engineer",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80",
    val status: String = "online"
)

data class SystemMetrics(
    val title: String = "Assistant Status",
    val contextMemoryText: String = "24.5k / 128k tokens",
    val contextPercent: Int = 19,
    val latencyText: String = "45ms (Fast Turbo)",
    val latencyStatus: String = "optimal",
    val activeIntegrations: String = "Linux POSIX, GitHub OSS, Workspace"
)

data class SuggestedAutomation(
    val id: String,
    val label: String,
    val iconName: String,
    val prompt: String
)

data class SessionItem(
    val id: String,
    val label: String,
    val preview: String = ""
)

data class LinuxCommandOutput(
    val id: String,
    val command: String,
    val output: String,
    val exitCode: Int = 0,
    val timestamp: String
)

data class OpenSourceRepo(
    val id: String,
    val name: String,
    val owner: String,
    val description: String,
    val language: String,
    val stars: String,
    val license: String,
    val version: String,
    val url: String
)

data class LinuxSysTelemetry(
    val kernel: String = "Linux 6.8.0-45-generic aarch64",
    val uptime: String = "14d 6h 32m",
    val cpuPercent: Int = 14,
    val memoryUsedGb: Float = 4.2f,
    val memoryTotalGb: Float = 16.0f,
    val loadAverage: String = "0.42, 0.38, 0.31",
    val shell: String = "bash 5.2.21 / zsh 5.9"
)

data class LinuxProcessItem(
    val pid: Int,
    val user: String,
    val cpuPercent: Float,
    val memMb: Int,
    val command: String,
    val state: String = "Running" // "Running", "Sleeping", "System"
)

data class LinuxScriptTemplate(
    val id: String,
    val title: String,
    val filename: String,
    val description: String,
    val code: String
)

data class GarudaSnapshot(
    val id: String,
    val name: String,
    val timestamp: String,
    val type: String = "btrfs-subvolume",
    val size: String = "2.4 GB",
    val description: String
)

data class GarudaPackage(
    val id: String,
    val name: String,
    val version: String,
    val repo: String = "core", // "core", "extra", "chaotic-aur"
    val size: String,
    val description: String,
    val isInstalled: Boolean = true
)

data class GitBranch(
    val name: String,
    val isCurrent: Boolean = false,
    val aheadCount: Int = 0,
    val behindCount: Int = 0
)

data class GitFileDiff(
    val path: String,
    val status: String, // "staged", "modified", "untracked", "deleted"
    val additions: Int = 0,
    val deletions: Int = 0,
    val diffSnippet: String = ""
)

data class GitCommitItem(
    val hash: String,
    val message: String,
    val author: String,
    val timeAgo: String,
    val branch: String = "main"
)

data class GitRepoState(
    val branch: String = "feature/hyprland-ui",
    val remoteUrl: String = "https://github.com/aistudio/aura-linux-workstation.git",
    val availableBranches: List<GitBranch> = listOf(
        GitBranch("main", isCurrent = false, aheadCount = 0, behindCount = 0),
        GitBranch("feature/hyprland-ui", isCurrent = true, aheadCount = 2, behindCount = 0),
        GitBranch("hotfix/waybar-telemetry", isCurrent = false, aheadCount = 1, behindCount = 0)
    ),
    val stagedFiles: List<GitFileDiff> = emptyList(),
    val unstagedFiles: List<GitFileDiff> = emptyList(),
    val recentCommits: List<GitCommitItem> = emptyList()
)

data class WaybarMetrics(
    val activeWorkspace: String = "chat",
    val cpuPercent: Int = 14,
    val ramUsedGb: Float = 4.2f,
    val ramTotalGb: Float = 16.0f,
    val latencyMs: Int = 18,
    val activeWindowTitle: String = "hyprland: ~/workspace/aura-ai",
    val zenGovernor: String = "Performance",
    val timeText: String = "10:14"
)


