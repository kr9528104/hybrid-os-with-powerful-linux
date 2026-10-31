package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ArchLayer(
    val level: Int,
    val title: String,
    val subtitle: String,
    val color: Color,
    val components: List<String>,
    val highlights: String
)

data class ArchFileNode(
    val path: String,
    val category: String, // "Kernel", "Containers", "AOSP Framework", "Auditing"
    val description: String,
    val language: String
)

@Composable
fun AuraOsArchitectureSection(
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf("Layers") } // "Layers", "Files", "Init (C++)", "Build & Flash", "Security Matrix"
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var torProxyActive by remember { mutableStateOf(true) }
    var ksppHardeningActive by remember { mutableStateOf(true) }
    var packetSnifferActive by remember { mutableStateOf(false) }
    var zenMuqssActive by remember { mutableStateOf(true) }
    var showAsciiDiagram by remember { mutableStateOf(false) }

    val layers = remember {
        listOf(
            ArchLayer(
                level = 5,
                title = "User Interface & Compositor",
                subtitle = "Garuda / Hyprland Wayland Compositor + Waydroid Integration",
                color = GarudaDragonCyan,
                components = listOf(
                    "Wayland EGL Compositor with Kawase Dual-Pass Shader Blur",
                    "Hyprland Tiling Window Engine (Mobile touch gestures + keyboard hotkeys)",
                    "Waydroid AOSP Container Bridge (Zero-overhead Shared Memory Surface)",
                    "Waybar Status Panel & Live Hardware Telemetry Daemon"
                ),
                highlights = "Renders smooth 120Hz gesture animations, adaptive neon themes, and floating or auto-tiled Android application windows on both handheld and desktop external displays."
            ),
            ArchLayer(
                level = 4,
                title = "Base System Framework (AOSP 17)",
                subtitle = "Android Runtime (ART), HAL, and Native Security APIs",
                color = GarudaDragonPink,
                components = listOf(
                    "ART (Android Runtime with PGO & Cloud Profile JIT)",
                    "Binder IPC High-Performance Bus with Namespace Passthrough",
                    "Custom HAL (`android.hardware.audit@1.0`) for Raw Network & Memory Access",
                    "Native Penetration Service APIs exposed to authorized audit tools"
                ),
                highlights = "Preserves 100% Android app ecosystem compatibility while injecting system-level auditing services and high-speed IPC hooks."
            ),
            ArchLayer(
                level = 3,
                title = "Security & Micro-Virtualization Layer",
                subtitle = "Qubes/Tails Zero-Trust Isolated Domains & Tor Routing",
                color = GarudaNeonGreen,
                components = listOf(
                    "Lightweight KVM Micro-VMs & Namespaces (`CLONE_NEWNS | CLONE_NEWPID | CLONE_NEWNET`)",
                    "Per-Application Ephemeral Overlays (Read-only rootfs + volatile tmpfs)",
                    "System-Wide Transparent Tor Routing via nftables redirection (Port 9040)",
                    "Cgroups v2 Strict Memory / CPU Quotas & I/O Isolation"
                ),
                highlights = "Every untrusted Android application runs in a hermetic disposable sandbox. Outgoing traffic is forcefully routed through the Tor onion network with zero IP leaks."
            ),
            ArchLayer(
                level = 2,
                title = "Hybrid Hardened + Zen Linux Kernel",
                subtitle = "Ultra-Low Latency Responsiveness + KSPP Exploitation Defense",
                color = GarudaDragonPurple,
                components = listOf(
                    "Zen Patches: MuQSS / EEVDF CPU Scheduler, BBRv3 TCP, 1000Hz Tick Rate",
                    "KSPP Hardened Flags: `slab_nomerge`, `init_on_alloc=1`, Clang SafeStack, Kernel CFI",
                    "eBPF TC/XDP Packet Filter Hooks for Kernel-Level Sniffing & Injection",
                    "BTRFS Rootfs with Transparent ZSTD Compression & Instant Subvolume Snapshots"
                ),
                highlights = "Combines competitive esports-grade desktop responsiveness with aerospace-grade kernel self-protection against zero-day buffer overflows and memory corruption."
            ),
            ArchLayer(
                level = 1,
                title = "Target Hardware Platforms",
                subtitle = "Mobile ARM64 & Workstation x86_64 Architectures",
                color = GarudaDragonOrange,
                components = listOf(
                    "ARM64: Qualcomm Snapdragon 8 Gen 2/3, Google Tensor, MediaTek Dimensity",
                    "x86_64: AMD Ryzen Embedded, Intel Core Ultra (Touch Laptops / Minis)",
                    "Universal Direct Rendering Manager (DRM / KMS) with Vulkan / OpenGL ES 3.2",
                    "Hardware Security Module (Titan M2 / StrongBox / TPM 2.0 Keymaster)"
                ),
                highlights = "Unified HAL driver baseline supporting both modern flagship Android handhelds and convertible x86 touch workstations."
            )
        )
    }

    val fileNodes = remember {
        listOf(
            ArchFileNode("kernel/zen-hardened/arch/arm64/configs/zen_hardened_defconfig", "Kernel", "Hardened KSPP & Zen latency kernel compile flags", "Kconfig"),
            ArchFileNode("kernel/zen-hardened/drivers/net/audit_ebpf_tap.c", "Kernel", "Kernel-level eBPF XDP hook for zero-copy packet interception", "C"),
            ArchFileNode("kernel/zen-hardened/kernel/sched/muqss.c", "Kernel", "MuQSS / EEVDF ultra-low latency desktop CPU task scheduler", "C"),
            ArchFileNode("system/containers/daemon/microcontainer_mgr.cpp", "Containers", "C++ micro-virtualization container spawner and namespace controller", "C++"),
            ArchFileNode("system/containers/configs/seccomp_app_strict.json", "Containers", "Zero-trust seccomp system call whitelist for unprivileged apps", "JSON"),
            ArchFileNode("system/netd/server/TorFirewallController.cpp", "Containers", "Leak-proof nftables packet redirection daemon for Tor transparent proxy", "C++"),
            ArchFileNode("frameworks/base/core/java/android/os/AuditManager.java", "AOSP Framework", "Public AOSP Java API for security auditing, sniffing, and memory probing", "Java"),
            ArchFileNode("frameworks/base/services/core/java/com/android/server/AuditService.java", "AOSP Framework", "System server daemon managing container telemetry and audit driver access", "Java"),
            ArchFileNode("hardware/interfaces/audit/1.0/default/AuditHal.cpp", "AOSP Framework", "HIDL / AIDL HAL bridge connecting userspace audit APIs to kernel eBPF", "C++"),
            ArchFileNode("external/wayland-garuda/compositor/hypr_mobile_wm.cpp", "AOSP Framework", "120Hz gesture-driven Wayland compositor with dual-pass Kawase blur", "C++"),
            ArchFileNode("external/kali-suite/metasploit-framework/Android.bp", "Auditing", "Soong blueprint compiling native Kali tools for ARM64 AOSP rootfs", "Android.bp"),
            ArchFileNode("external/kali-suite/wireshark-tshark/Android.bp", "Auditing", "Headless Wireshark / TShark packet analyzer integrated into system image", "Android.bp"),
            ArchFileNode("build/make/target/product/aosp_hybrid_arm64.mk", "AOSP Framework", "Top-level AOSP product makefile incorporating hybrid Zen packages", "Makefile")
        )
    }

    val asciiDiagram = remember {
        """
+-------------------------------------------------------------------------+
|                  HYBRID OS RUNTIME ARCHITECTURE BLUEPRINT                |
+-------------------------------------------------------------------------+
| [Layer 5: GARUDA / HYPRLAND COMPOSITOR]                                 |
|  - Wayland Display Server        - Kawase Dual-Pass Blur Shaders        |
|  - 120Hz Touch Gesture Engine    - Hyprland Auto-Tiling / Float Panes   |
|  - Waybar Hardware Telemetry     - Waydroid Shared Memory Surface       |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------+------------------------------------+
| [Layer 4: AOSP 17 FRAMEWORK & CORE RUNTIME]                             |
|  - Android Runtime (ART) with PGO  - Binder High-Speed IPC Subsystem    |
|  - AuditManager System Service     - Hardware Abstraction Layer (HAL)   |
|  - Dynamic Window Manager Hook     - Package Manager & App Sandboxes    |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------+------------------------------------+
| [Layer 3: SECURITY, PRIVACY & CONTAINERIZATION LAYER]                  |
|  - Qubes-style Lightweight Micro-Containers (CLONE_NEWNS, NEWPID)       |
|  - Ephemeral Rootfs Overlays       - Cgroups v2 Memory/CPU Throttling   |
|  - Strict Seccomp System Call Filter - nftables Leak-Proof Tor Proxy    |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------+------------------------------------+
| [Layer 2: HYBRID HARDENED + ZEN LINUX KERNEL]                           |
|  - Zen Latency: MuQSS / EEVDF, BBRv3 TCP, 1000Hz Timer Tick            |
|  - KSPP Hardening: init_on_alloc, slab_nomerge, Clang CFI, SafeStack    |
|  - eBPF XDP/TC Packet Sniffer      - Btrfs ZSTD Compressed Subvolumes   |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------+------------------------------------+
| [Layer 1: TARGET HARDWARE PLATFORM]                                     |
|  - ARM64 (Qualcomm Snapdragon, Google Tensor, MediaTek Dimensity)       |
|  - x86_64 (AMD Ryzen Embedded, Intel Core Ultra)                       |
|  - Hardware Security Module (Titan M2 / StrongBox / TPM 2.0 Keymaster)  |
+-------------------------------------------------------------------------+
        """.trimIndent()
    }

    val initCppCode = remember {
        """
// init.microcontainers.cpp - Hybrid OS Application Sandbox Daemon
#define _GNU_SOURCE
#include <sched.h>
#include <sys/mount.h>
#include <sys/syscall.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <iostream>
#include <string>

static int pivot_root(const char *new_root, const char *put_old) {
    return syscall(SYS_pivot_root, new_root, put_old);
}

bool SpawnAppMicroContainer(const std::string& app_pkg_id, bool route_via_tor) {
    std::cout << "[HYBRID-INIT] Spawning isolated domain for: " << app_pkg_id << std::endl;

    // 1. Unshare all security-critical namespaces (Qubes isolation model)
    if (unshare(CLONE_NEWNS | CLONE_NEWPID | CLONE_NEWIPC | CLONE_NEWNET | CLONE_NEWUTS) != 0) {
        perror("unshare failed");
        return false;
    }

    // 2. Prevent mount propagation back to the host filesystem
    mount(NULL, "/", NULL, MS_REC | MS_PRIVATE, NULL);

    // 3. Assemble Ephemeral Rootfs Overlay (Read-Only AOSP System + Volatile tmpfs)
    std::string root_dir = "/mnt/containers/" + app_pkg_id;
    mkdir(root_dir.c_str(), 0755);
    mount("none", root_dir.c_str(), "tmpfs", MS_NODEV | MS_NOSUID, "size=512M");

    std::string ro_system = root_dir + "/system";
    mkdir(ro_system.c_str(), 0755);
    mount("/system", ro_system.c_str(), "bind", MS_BIND | MS_RDONLY | MS_REC, NULL);

    // 4. Pivot into container root
    std::string old_root = root_dir + "/old_root";
    mkdir(old_root.c_str(), 0755);
    if (pivot_root(root_dir.c_str(), old_root.c_str()) != 0) {
        perror("pivot_root failed");
        return false;
    }
    umount2("/old_root", MNT_DETACH);

    // 5. Apply Linux-Hardened KSPP Protections
    int fd = open("/proc/sys/kernel/dmesg_restrict", O_WRONLY);
    if (fd >= 0) { write(fd, "1", 1); close(fd); }

    // 6. Network Tor Routing via nftables (Tails anonymity standard)
    if (route_via_tor) {
        system("nft add rule nat PREROUTING ip protocol tcp redirect to :9040");
        std::cout << "[HYBRID-NET] Transparent Tor proxy enabled on 127.0.0.1:9040" << std::endl;
    }

    std::cout << "[HYBRID-SEC] Domain active with zero persistent leakage." << std::endl;
    return true;
}
        """.trimIndent()
    }

    val buildSteps = remember {
        listOf(
            "Phase 1: Environment & Toolchain Setup" to listOf(
                "Export LLVM toolchain: `export LLVM=1 LLVM_IAS=1 CC=clang`",
                "Install Ninja & Soong build dependencies: `sudo apt install ninja-build python3-protobuf libbpf-dev`",
                "Initialize AOSP repo with hybrid manifest: `repo init -u https://github.com/aosp-hybrid/manifest -b android-17.0`",
                "Sync repos in parallel: `repo sync -c -j$(nproc) --no-clone-bundle`"
            ),
            "Phase 2: Hybrid Zen + Hardened Kernel Compilation" to listOf(
                "Apply Zen patchset and Linux-Hardened KSPP patches to kernel source tree",
                "Load unified config: `make ARCH=arm64 zen_hardened_defconfig`",
                "Compile kernel image and eBPF drivers: `make ARCH=arm64 CC=clang LLVM=1 -j$(nproc) Image.gz-dtb`",
                "Verify zero CFI/SafeStack compilation warnings before packing `boot.img`"
            ),
            "Phase 3: AOSP 17 & Waydroid ROM Compilation" to listOf(
                "Source AOSP environment: `source build/envsetup.sh`",
                "Target device lunch spec: `lunch aosp_hybrid_arm64-userdebug`",
                "Build system images via Soong & Ninja: `m -j$(nproc) systemimage vendorimage bootimage`",
                "Integrate Wayland compositor packages: `m -j$(nproc) hypr_mobile_wm waybar_telemetry`"
            ),
            "Phase 4: Flashing to Target Mobile Device" to listOf(
                "Reboot phone to bootloader: `adb reboot bootloader`",
                "Flash boot partition with hybrid kernel: `fastboot flash boot out/target/product/hybrid_arm64/boot.img`",
                "Flash super partition: `fastboot flash super out/target/product/hybrid_arm64/super.img`",
                "Wipe userdata for fresh Btrfs subvolume layout: `fastboot -w`",
                "Reboot into unified Hybrid OS: `fastboot reboot`"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_os_architecture_section")
    ) {
        // Sub-tabs row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Layers" to Icons.Default.Layers,
                "Files" to Icons.Default.Folder,
                "Init (C++)" to Icons.Default.Code,
                "Build & Flash" to Icons.Default.Build,
                "Security Matrix" to Icons.Default.Security
            ).forEach { (tab, icon) ->
                val isSelected = activeSubTab == tab
                Surface(
                    onClick = { activeSubTab = tab },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) GarudaDragonCyan.copy(alpha = 0.2f) else GarudaSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) GarudaDragonCyan else GarudaBorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab,
                            tint = if (isSelected) GarudaDragonCyan else GarudaTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) GarudaDragonCyan else GarudaTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Tab Content
        when (activeSubTab) {
            "Layers" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "5-Layer Hybrid OS Architecture",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GarudaTextWhite
                                    )
                                    Text(
                                        text = "Hardware -> Zen Kernel -> Qubes Isolation -> AOSP 17 -> Wayland UI",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GarudaDragonCyan
                                    )
                                }
                                OutlinedButton(
                                    onClick = { showAsciiDiagram = !showAsciiDiagram },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (showAsciiDiagram) GarudaDragonPink else GarudaDragonCyan
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(if (showAsciiDiagram) "Hide ASCII" else "View ASCII", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    if (showAsciiDiagram) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = GarudaBgTerminal,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GarudaDragonCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = asciiDiagram,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    ),
                                    color = GarudaNeonGreen,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .horizontalScroll(rememberScrollState())
                                )
                            }
                        }
                    }

                    items(layers) { layer ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GarudaSurfaceGlass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, layer.color.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = layer.color.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, layer.color.copy(alpha = 0.6f))
                                    ) {
                                        Text(
                                            text = "L${layer.level}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = layer.color,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = layer.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GarudaTextWhite
                                        )
                                        Text(
                                            text = layer.subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = layer.color
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = GarudaBorderSubtle)
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    layer.components.forEach { comp ->
                                        Row(verticalAlignment = Alignment.Top) {
                                            Text(
                                                text = "•",
                                                color = layer.color,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                            Text(
                                                text = comp,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = GarudaTextSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = layer.highlights,
                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                    color = GarudaTextMuted
                                )
                            }
                        }
                    }
                }
            }

            "Files" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Category filter chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Kernel", "Containers", "AOSP Framework", "Auditing").forEach { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GarudaDragonCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = GarudaDragonCyan,
                                    labelColor = GarudaTextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredNodes = fileNodes.filter {
                        selectedCategoryFilter == "All" || it.category == selectedCategoryFilter
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredNodes) { node ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GarudaSurfaceCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.InsertDriveFile,
                                        contentDescription = null,
                                        tint = if (node.category == "Kernel") GarudaDragonPurple
                                        else if (node.category == "Containers") GarudaNeonGreen
                                        else if (node.category == "Auditing") GarudaDragonPink
                                        else GarudaDragonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = node.path,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = GarudaTextWhite
                                        )
                                        Text(
                                            text = node.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GarudaTextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GarudaSurfaceGlass,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle)
                                    ) {
                                        Text(
                                            text = node.language,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = GarudaTextMuted,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Init (C++)" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "system/containers/init.microcontainers.cpp",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = GarudaDragonCyan
                            )
                            Text(
                                text = "Hermetic Application Sandboxing & Tor Redirection",
                                style = MaterialTheme.typography.labelSmall,
                                color = GarudaTextMuted
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GarudaNeonGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "C++20 / Linux 6.x",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GarudaNeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GarudaBgTerminal,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GarudaDragonCyan.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            item {
                                Text(
                                    text = initCppCode,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.5.sp,
                                        lineHeight = 15.sp
                                    ),
                                    color = GarudaTextWhite
                                )
                            }
                        }
                    }
                }
            }

            "Build & Flash" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(buildSteps) { (phaseTitle, commands) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceGlass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = phaseTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GarudaDragonCyan
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    commands.forEachIndexed { idx, cmd ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = GarudaDragonCyan.copy(alpha = 0.2f),
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${idx + 1}",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = GarudaDragonCyan
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = cmd,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp
                                                ),
                                                color = GarudaTextWhite
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Security Matrix" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Active Kernel & Isolation Controls",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GarudaTextWhite
                                )
                                Text(
                                    text = "Runtime telemetry and security toggles for the hybrid kernel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GarudaTextMuted
                                )
                            }
                        }
                    }

                    item {
                        // Tor Transparent Routing Switch
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceGlass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VpnLock, contentDescription = null, tint = GarudaNeonGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Global Tor Proxy (Port 9040)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GarudaTextWhite)
                                    Text("Forcefully routes all app TCP traffic through onion circuits with zero DNS leaks", style = MaterialTheme.typography.labelSmall, color = GarudaTextMuted)
                                }
                                Switch(
                                    checked = torProxyActive,
                                    onCheckedChange = { torProxyActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = GarudaNeonGreen, checkedTrackColor = GarudaNeonGreen.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }

                    item {
                        // KSPP Hardening Switch
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceGlass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = GarudaDragonPink, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("KSPP Memory Poisoning", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GarudaTextWhite)
                                    Text("init_on_alloc=1, slab_nomerge, and strict Clang forward-edge CFI", style = MaterialTheme.typography.labelSmall, color = GarudaTextMuted)
                                }
                                Switch(
                                    checked = ksppHardeningActive,
                                    onCheckedChange = { ksppHardeningActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = GarudaDragonPink, checkedTrackColor = GarudaDragonPink.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }

                    item {
                        // Packet Sniffing HAL Hook
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceGlass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = GarudaDragonCyan, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Native eBPF Sniffer Hook", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GarudaTextWhite)
                                    Text("Kernel raw socket promiscuous capture driver for Kali/Parrot tool suite", style = MaterialTheme.typography.labelSmall, color = GarudaTextMuted)
                                }
                                Switch(
                                    checked = packetSnifferActive,
                                    onCheckedChange = { packetSnifferActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = GarudaDragonCyan, checkedTrackColor = GarudaDragonCyan.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }

                    item {
                        // Zen MuQSS Scheduler
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GarudaSurfaceGlass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GarudaBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = GarudaDragonOrange, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Zen MuQSS / EEVDF Scheduler", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GarudaTextWhite)
                                    Text("1000Hz tick rate with auto-renicing for zero input latency", style = MaterialTheme.typography.labelSmall, color = GarudaTextMuted)
                                }
                                Switch(
                                    checked = zenMuqssActive,
                                    onCheckedChange = { zenMuqssActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = GarudaDragonOrange, checkedTrackColor = GarudaDragonOrange.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
