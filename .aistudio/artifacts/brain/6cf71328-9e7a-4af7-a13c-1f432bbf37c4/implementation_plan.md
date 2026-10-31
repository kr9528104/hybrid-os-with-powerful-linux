# Hyprland-Style Modern Linux Workstation & OSS Hub Upgrade

A modern, high-efficiency desktop interface upgrade for Aura AI inspired by the sleek Hyprland Wayland compositor aesthetic, featuring sharp geometric borders, a Waybar-style top status panel, a responsive three-column tiling layout, and an integrated Open-Source Package Store with Git Workspace controls.

### User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed the following architectural and visual specifications in Phase 1:
> - **Visual Aesthetic**: Hyprland Tiling style featuring sharp geometric borders (2dp–4dp border radii, high-contrast neon borders), clean dark surfaces (`#0B0E14`), and a minimalist Waybar-style top status bar.
> - **Layout Structure**: Three-column workstation layout (Collapsible Waybar/Workspace Sidebar 260dp, Active Tiling Stage in the center, and System Inspector / Telemetry on the right 320dp).
> - **Feature Focus**: Open-source package store and Git repository workspace taking center stage, alongside the interactive terminal and AI orchestrator.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms Aura AI into an ultra-modern Linux workstation interface inspired by Hyprland and Waybar. It delivers a sharp, clean tiling desktop experience with active workspace badges (`[1: chat]`, `[2: oss]`, `[3: term]`, `[4: tasks]`, `[5: git]`), an Open-Source Package Store (Pacman/AUR & Flatpak explorer), a Git Repository staging console, and a live Waybar system monitor.
- **Target Audience / Persona**: Linux enthusiasts, developers, and system architects who value the minimalist, ultra-fast, and keyboard-centric aesthetic of modern Wayland tiling window managers (Hyprland / Sway).
- **Key Value**: Replaces rounded generic cards with sleek, sharp geometric tiling panes, instant sub-50ms responsiveness, real-time memory and CPU indicators in the Waybar panel, and seamless navigation across developer workflows.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Tiling Desktop Navigation**: User views the top Waybar status bar showing active workspaces (`[1]`, `[2]`, `[3]`, `[4]`, `[5]`), active window title, and live CPU/RAM/Battery status pills. Tapping a workspace instantly tiles into that module.
  2. **Open-Source Store & Git Workspace**: The center workspace features a searchable package and repository catalog with one-click `git clone`, commit history viewer, license compliance badges, and package installation controls.
  3. **Fast Tiling Terminal & Orchestrator**: The terminal and AI chat operate as snappy tiled panes with sharp borders, glowing active-window outline indicators, and instant stdout flushing.
  4. **Right Inspector Panel**: Displays Git status (`branch: main`, modified files), active system resources, and pinned architectural specs.

- **Visual Identity & Theme (Hyprland / Waybar Aesthetic)**:
  - **Aesthetic Direction**: Hyprland / Wayland Tiling minimal brutalist dark mode with sharp geometry, crisp 4dp rounded corners, micro-borders, and high-visibility status indicators.
  - **Color Palette**:
    - Background: `#0B0E14` (Deep Wayland Obsidian)
    - Surface Pane: `#121722` (Tiled Window Surface)
    - Active Pane Border: `#00F2FE` (Electric Cyan Neon Accent)
    - Inactive Pane Border: `#1F293D` (Subtle Slate Border)
    - Waybar Pill: `#182030` with `#00FF87` (Emerald Live Beacons)
    - Text Primary: `#F8FAFC` (Clean High-Contrast White)
    - Text Secondary: `#94A3B8` (Muted Slate)
  - **Typography**: Clean monospace headings (`Roboto Mono` / `Monospace`) paired with crisp sans-serif body typography for high legibility and information density.
  - **Component Styling**:
    - **Top Waybar**: Fixed-height status bar displaying workspace pills `[1] [2] [3]`, active window title, clock, network ping, and battery/CPU chips.
    - **Tiled Window Panes**: Outer borders with active window highlights (cyan/magenta glowing edge), minimal padding, and tabbed split views.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Hyprland Sharp Geometry vs. Rounded Cards**
  - *Chosen Approach*: Transition from generic large rounded cards (16dp–20dp) to crisp, sharp 4dp–6dp geometric panes with 1.5dp glowing borders.
  - *Why*: Delivers an authentic tiling window manager look and increases content density for code and terminal workflows.
  - *Alternatives Considered*: Standard GNOME Adwaita large pill shapes (passed over based on user preference for Hyprland).

- **Decision 2: Open-Source Package Store & Git Center Stage**
  - *Chosen Approach*: Elevate the Open Source & Git repository manager into a dedicated, primary workspace with searchable package repositories (Arch/AUR/Flatpak) and Git commit/branch inspectors.
  - *Why*: Aligns with the user's explicit choice for open-source tools to take center stage.

- **Decision 3: Seamless Three-Column Architecture with Mobile Adaptation**
  - *Chosen Approach*: Maintain a true three-column workstation on desktop and tablet (>640dp), while condensing on mobile into the top Waybar with workspace selector pills and quick pane toggles.
  - *Why*: Preserves full power-user productivity on large screens without compromising touch usability on mobile.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                   Top Waybar Status Bar (Hyprland Header)              │
│  [1:chat] [2:oss] [3:term] [4:tasks] │ Active: aura-git │ ⚡ 280ms 14% │
└────────────────────────────────────────────────────────────────────────┘
┌───────────────────┬──────────────────────────────────┬─────────────────┐
│  Left Workspace   │     Center Tiling Active Pane    │  Right Context  │
│  Navigation Rail  │                                  │  Inspector Bar  │
│  (260dp)          │   ┌──────────────────────────┐   │  (320dp)        │
│                   │   │ Open Source Hub / Git    │   │                 │
│  • Workspaces     │   │ Repository Stage         │   │  • Git Branch   │
│  • Recent Trees   │   │                          │   │  • System Load  │
│  • Profile Card   │   │ - Search Packages & AUR  │   │  • Pinned Specs │
│                   │   │ - Git Clone & Commits    │   │                 │
│                   │   └──────────────────────────┘   │                 │
└───────────────────┴──────────────────────────────────┴─────────────────┘
```

- **Data Models**:
  - `WorkspaceItem`: Id, index, label, icon, isActive
  - `GitRepoStatus`: Current branch, uncommitted count, last commit message, remote URL
  - `OpenSourceRepo`: Enhanced with package manager flags (Pacman, AUR, Flatpak) and clone metrics
  - `WaybarMetrics`: CPU, RAM, battery level, network latency, active workspace label

- **State Management**:
  - `AuraViewModel` coordinates instant workspace switching via `StateFlow<AuraUiState>`.
  - Recomposition optimized with stable keys and sub-50ms execution latency.
