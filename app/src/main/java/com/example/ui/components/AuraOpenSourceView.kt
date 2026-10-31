package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GarudaPackage
import com.example.model.GitFileDiff
import com.example.model.GitRepoState
import com.example.model.OpenSourceRepo
import com.example.ui.theme.*

@Composable
fun AuraOpenSourceView(
    repos: List<OpenSourceRepo>,
    packages: List<GarudaPackage>,
    gitRepoState: GitRepoState,
    gitCommitInput: String,
    onGitCommitInputChanged: (String) -> Unit,
    onStageFile: (GitFileDiff) -> Unit,
    onUnstageFile: (GitFileDiff) -> Unit,
    onDiscardFile: (GitFileDiff) -> Unit,
    onCommitChanges: () -> Unit,
    onSwitchBranch: (String) -> Unit,
    onGitPull: () -> Unit,
    onTogglePackage: (String) -> Unit,
    onRepoClick: (OpenSourceRepo) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: OSS & Package Store, 1: Git Workstation
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val clipboardManager = LocalClipboardManager.current
    var copiedText by remember { mutableStateOf<String?>(null) }
    var expandedDiffFile by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HyprlandBgObsidian)
            .padding(14.dp)
            .testTag("aura_opensource_workstation")
    ) {
        // Workstation Header with Hyprland Sharp Aesthetic
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "❖ OPEN-SOURCE & GIT WORKSTATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GarudaTextWhite
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Linux package repository, Chaotic-AUR binaries, and distributed Git tree inspector",
                    style = MaterialTheme.typography.bodySmall,
                    color = GarudaTextSecondary
                )
            }

            // Status Pill
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = HyprlandPaneSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderActive)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(WaybarGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYNCED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = WaybarGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sharp Hyprland Navigation Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HyprlandPaneBg, RoundedCornerShape(4.dp))
                .border(1.dp, HyprlandBorderInactive, RoundedCornerShape(4.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                "📦 Package Store & Chaotic-AUR",
                "🌿 Git Repository Workspace (${gitRepoState.stagedFiles.size + gitRepoState.unstagedFiles.size} changes)"
            )

            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Surface(
                    onClick = { selectedTab = index },
                    shape = RoundedCornerShape(4.dp),
                    color = if (isSelected) HyprlandPaneSurface else Color.Transparent,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderActive) else null,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("oss_tab_$index")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) WaybarCyan else GarudaTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // TAB 0: PACKAGE STORE & CHAOTIC-AUR
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search 140,000+ packages, Chaotic-AUR, and Linux kernels...",
                        color = GarudaTextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = WaybarCyan)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = GarudaTextMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = HyprlandPaneBg,
                    unfocusedContainerColor = HyprlandPaneBg,
                    focusedBorderColor = HyprlandBorderActive,
                    unfocusedBorderColor = HyprlandBorderInactive,
                    focusedTextColor = GarudaTextWhite,
                    unfocusedTextColor = GarudaTextWhite
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val categories = listOf("All", "Core", "Chaotic-AUR", "Flatpak", "Trending OSS")
                categories.forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(4.dp),
                        color = if (isCatSelected) HyprlandPaneSurface else HyprlandPaneBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCatSelected) WaybarCyan else HyprlandBorderInactive
                        ),
                        modifier = Modifier.testTag("filter_cat_$cat")
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (isCatSelected) WaybarCyan else GarudaTextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Package & Repository List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Chaotic-AUR & System Packages
                val filteredPkgs = packages.filter { pkg ->
                    (searchQuery.isBlank() || pkg.name.contains(searchQuery, ignoreCase = true) || pkg.description.contains(searchQuery, ignoreCase = true)) &&
                    (selectedCategory == "All" ||
                     (selectedCategory == "Chaotic-AUR" && pkg.repo == "chaotic-aur") ||
                     (selectedCategory == "Core" && pkg.repo == "core") ||
                     (selectedCategory == "Flatpak" && pkg.repo == "flatpak"))
                }

                if (filteredPkgs.isNotEmpty() && selectedCategory != "Trending OSS") {
                    item {
                        Text(
                            text = "PACMAN & CHAOTIC-AUR BINARIES (${filteredPkgs.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = WaybarCyan
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(filteredPkgs, key = { it.id }) { pkg ->
                        PackageItemRow(
                            pkg = pkg,
                            onToggleInstall = { onTogglePackage(pkg.id) },
                            onCopyCli = {
                                val cmd = if (pkg.repo == "chaotic-aur") "garuda-update && pacman -S ${pkg.name}" else "pacman -S ${pkg.name}"
                                clipboardManager.setText(AnnotatedString(cmd))
                                copiedText = pkg.id
                            },
                            isCopied = copiedText == pkg.id
                        )
                    }
                }

                // Section 2: Foundational Open-Source Git Repositories
                val filteredRepos = repos.filter { repo ->
                    (searchQuery.isBlank() || repo.name.contains(searchQuery, ignoreCase = true) || repo.language.contains(searchQuery, ignoreCase = true) || repo.description.contains(searchQuery, ignoreCase = true)) &&
                    (selectedCategory == "All" || selectedCategory == "Trending OSS")
                }

                if (filteredRepos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "OFFICIAL UPSTREAM GIT REPOSITORIES (${filteredRepos.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = WaybarMagenta
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(filteredRepos, key = { it.id }) { repo ->
                        OpenSourceRepoSharpCard(
                            repo = repo,
                            isCopied = copiedText == repo.id,
                            onCopyClone = {
                                clipboardManager.setText(AnnotatedString("git clone ${repo.url}.git"))
                                copiedText = repo.id
                            },
                            onClick = { onRepoClick(repo) }
                        )
                    }
                }
            }

        } else {
            // TAB 1: GIT REPOSITORY WORKSPACE
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Git Branch & Remote Header Toolbar
                item {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = HyprlandPaneBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ForkRight,
                                    contentDescription = null,
                                    tint = WaybarCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BRANCH:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = GarudaTextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                // Branch Selector Pills
                                gitRepoState.availableBranches.forEach { br ->
                                    val isCurrent = br.name == gitRepoState.branch
                                    Surface(
                                        onClick = { onSwitchBranch(br.name) },
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isCurrent) WaybarPillActive else Color.Transparent,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isCurrent) WaybarCyan else HyprlandBorderInactive
                                        ),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isCurrent) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .background(WaybarCyan, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = br.name,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isCurrent) WaybarCyan else GarudaTextSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Git Pull / Sync Button
                                Button(
                                    onClick = onGitPull,
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HyprlandPaneSurface,
                                        contentColor = WaybarGreen
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WaybarGreen.copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("git pull", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "origin: ${gitRepoState.remoteUrl}",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = GarudaTextMuted
                            )
                        }
                    }
                }

                // Section: Staged Changes
                item {
                    Text(
                        text = "STAGED CHANGES (${gitRepoState.stagedFiles.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = WaybarGreen
                        )
                    )
                }

                if (gitRepoState.stagedFiles.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = HyprlandPaneBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No changes staged. Stage modified files below to commit.",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = GarudaTextMuted,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(gitRepoState.stagedFiles, key = { "staged_${it.path}" }) { file ->
                        GitFileRow(
                            file = file,
                            isStaged = true,
                            isDiffExpanded = expandedDiffFile == "staged_${file.path}",
                            onToggleDiff = {
                                expandedDiffFile = if (expandedDiffFile == "staged_${file.path}") null else "staged_${file.path}"
                            },
                            onAction = { onUnstageFile(file) }
                        )
                    }
                }

                // Section: Unstaged / Working Tree Changes
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "UNSTAGED WORKING TREE (${gitRepoState.unstagedFiles.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = GarudaElectricOrange
                        )
                    )
                }

                if (gitRepoState.unstagedFiles.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = HyprlandPaneBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Working tree clean. Nothing to stage.",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = GarudaTextMuted,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(gitRepoState.unstagedFiles, key = { "unstaged_${it.path}" }) { file ->
                        GitFileRow(
                            file = file,
                            isStaged = false,
                            isDiffExpanded = expandedDiffFile == "unstaged_${file.path}",
                            onToggleDiff = {
                                expandedDiffFile = if (expandedDiffFile == "unstaged_${file.path}") null else "unstaged_${file.path}"
                            },
                            onAction = { onStageFile(file) },
                            onDiscard = { onDiscardFile(file) }
                        )
                    }
                }

                // Commit Input Box
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = HyprlandPaneBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderActive),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "COMMIT CHANGES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = WaybarCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = gitCommitInput,
                                onValueChange = onGitCommitInputChanged,
                                placeholder = {
                                    Text(
                                        "feat(ui): message for branch ${gitRepoState.branch}...",
                                        color = GarudaTextMuted,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                },
                                shape = RoundedCornerShape(4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = HyprlandPaneSurface,
                                    unfocusedContainerColor = HyprlandPaneSurface,
                                    focusedBorderColor = WaybarCyan,
                                    unfocusedBorderColor = HyprlandBorderInactive,
                                    focusedTextColor = GarudaTextWhite,
                                    unfocusedTextColor = GarudaTextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = onCommitChanges,
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WaybarCyan,
                                        contentColor = HyprlandBgObsidian
                                    ),
                                    modifier = Modifier.testTag("git_commit_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "git commit -m",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Recent Commits History
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "RECENT COMMITS LOG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = WaybarMagenta
                        )
                    )
                }

                items(gitRepoState.recentCommits, key = { it.hash }) { commit ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = HyprlandPaneBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = WaybarPillActive,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WaybarCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = commit.hash,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = WaybarCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = commit.branch,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = WaybarMagenta
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = commit.timeAgo,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = GarudaTextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = commit.message,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = GarudaTextWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Author: ${commit.author}",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = GarudaTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PackageItemRow(
    pkg: GarudaPackage,
    onToggleInstall: () -> Unit,
    onCopyCli: () -> Unit,
    isCopied: Boolean
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = HyprlandPaneBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (pkg.isInstalled) HyprlandBorderActive.copy(alpha = 0.5f) else HyprlandBorderInactive
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pkg.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GarudaTextWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = HyprlandPaneSurface
                    ) {
                        Text(
                            text = pkg.repo,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = if (pkg.repo == "chaotic-aur") WaybarMagenta else WaybarCyan,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pkg.version,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = GarudaTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = pkg.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GarudaTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Copy command
            IconButton(
                onClick = onCopyCli,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Outlined.ContentCopy,
                    contentDescription = "Copy command",
                    tint = if (isCopied) WaybarGreen else GarudaTextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Install / Remove Button
            Button(
                onClick = onToggleInstall,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pkg.isInstalled) HyprlandPaneSurface else WaybarCyan,
                    contentColor = if (pkg.isInstalled) WaybarGreen else HyprlandBgObsidian
                ),
                border = if (pkg.isInstalled) androidx.compose.foundation.BorderStroke(1.dp, WaybarGreen.copy(alpha = 0.5f)) else null,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(
                    text = if (pkg.isInstalled) "Installed" else "Install",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun GitFileRow(
    file: GitFileDiff,
    isStaged: Boolean,
    isDiffExpanded: Boolean,
    onToggleDiff: () -> Unit,
    onAction: () -> Unit,
    onDiscard: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = HyprlandPaneBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isStaged) WaybarGreen.copy(alpha = 0.5f) else HyprlandBorderInactive
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleDiff,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isDiffExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Diff",
                        tint = WaybarCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = file.path,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    color = GarudaTextWhite,
                    modifier = Modifier.weight(1f)
                )

                // Additions / Deletions pills
                Text(
                    text = "+${file.additions}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = WaybarGreen
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "-${file.deletions}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = GarudaElectricOrange
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Discard button (if unstaged)
                if (onDiscard != null) {
                    IconButton(
                        onClick = onDiscard,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Restore,
                            contentDescription = "Discard",
                            tint = GarudaTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Stage / Unstage Action Button
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStaged) HyprlandPaneSurface else WaybarCyan.copy(alpha = 0.2f),
                        contentColor = if (isStaged) GarudaTextSecondary else WaybarCyan
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isStaged) HyprlandBorderInactive else WaybarCyan.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text(
                        text = if (isStaged) "Unstage" else "+ Stage",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Expandable syntax-highlighted diff preview
            AnimatedVisibility(visible = isDiffExpanded) {
                Surface(
                    color = HyprlandBgObsidian,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "DIFF PREVIEW: ${file.path}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = WaybarCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        file.diffSnippet.lines().forEach { line ->
                            val lineCol = when {
                                line.startsWith("+") -> WaybarGreen
                                line.startsWith("-") -> GarudaElectricOrange
                                else -> GarudaTextMuted
                            }
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = lineCol
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OpenSourceRepoSharpCard(
    repo: OpenSourceRepo,
    isCopied: Boolean,
    onCopyClone: () -> Unit,
    onClick: () -> Unit
) {
    val langColor = when (repo.language.lowercase()) {
        "c" -> Color(0xFF555555)
        "kotlin" -> Color(0xFFA97BFF)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFDEA584)
        "lua / c" -> Color(0xFF000080)
        else -> WaybarCyan
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        color = HyprlandPaneBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, HyprlandBorderInactive),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FolderSpecial,
                    contentDescription = null,
                    tint = WaybarCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${repo.owner} / ",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = GarudaTextMuted
                )
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GarudaTextWhite,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = HyprlandPaneSurface
                ) {
                    Text(
                        text = repo.version,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = WaybarCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = repo.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
                color = GarudaTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(langColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = repo.language,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = GarudaTextWhite
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = AuraAmber,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = repo.stars,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = GarudaTextMuted
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = repo.license,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = GarudaTextMuted,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onCopyClone,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCopied) WaybarGreen else HyprlandPaneSurface,
                        contentColor = if (isCopied) HyprlandBgObsidian else GarudaTextWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCopied) WaybarGreen else HyprlandBorderInactive),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCopied) "Cloned" else "Clone",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
