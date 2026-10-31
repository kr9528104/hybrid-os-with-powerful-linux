package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Garuda Linux "Dragonized" & Sweet-Dark Aesthetic Colors
val GarudaDragonCyan = Color(0xFF00F2FE)
val GarudaDragonPink = Color(0xFFF857A6)
val GarudaDragonViolet = Color(0xFF7F00FF)
val GarudaDragonPurple = GarudaDragonViolet
val GarudaNeonGreen = Color(0xFF00FF87)
val GarudaElectricOrange = Color(0xFFFF5E3A)
val GarudaDragonOrange = GarudaElectricOrange

// Deep Space Obsidian & Glass Backgrounds
val GarudaBgObsidian = Color(0xFF090C12)
val GarudaBgTerminal = Color(0xFF070A0F)
val GarudaSurfaceGlass = Color(0xFF111622)
val GarudaSurfaceElevated = Color(0xFF181F30)
val GarudaSurfaceCard = Color(0xFF1E2638)
val GarudaBorderNeon = Color(0xFF2A3650)
val GarudaBorderSubtle = Color(0xFF1E273A)

// Typography colors
val GarudaTextWhite = Color(0xFFF8FAFC)
val GarudaTextSecondary = Color(0xFFA0AEC0)
val GarudaTextMuted = Color(0xFF6B7A90)

// Hyprland Tiling & Waybar Status Bar Palette
val HyprlandBgObsidian = Color(0xFF0B0E14)
val HyprlandPaneBg = Color(0xFF111722)
val HyprlandPaneSurface = Color(0xFF161E2C)
val HyprlandBorderActive = Color(0xFF00F2FE)
val HyprlandBorderInactive = Color(0xFF1E283A)
val HyprlandBorderHighlight = Color(0xFF7F00FF)
val WaybarBg = Color(0xFF0D121B)
val WaybarPillBg = Color(0xFF141C2A)
val WaybarPillActive = Color(0xFF1D283C)
val WaybarBorder = Color(0xFF222C3E)
val WaybarGreen = Color(0xFF00FF87)
val WaybarCyan = Color(0xFF00F2FE)
val WaybarMagenta = Color(0xFFF857A6)

// Backward compatible Aura mappings with boosted Garuda neon vibrancy
val AuraPrimary = GarudaDragonCyan
val AuraPrimaryVariant = GarudaDragonViolet
val AuraSecondary = GarudaDragonPink
val AuraAccent = GarudaNeonGreen
val AuraAccentGlow = Color(0x3300FF87)

val AuraBgDark = GarudaBgObsidian
val AuraSurfaceDark = GarudaSurfaceGlass
val AuraSurfaceLight = GarudaSurfaceCard
val AuraSurfaceElevated = GarudaSurfaceElevated
val AuraBorderDark = GarudaBorderNeon
val AuraBorderSubtle = GarudaBorderSubtle

val AuraTextPrimary = GarudaTextWhite
val AuraTextSecondary = GarudaTextSecondary
val AuraTextMuted = GarudaTextMuted

val AuraCyan = GarudaDragonCyan
val AuraAmber = Color(0xFFFFB800)
val AuraRose = GarudaDragonPink

// Signature Garuda Dragonized Neon Gradient Brushes
val GarudaDragonGradient = Brush.linearGradient(
    colors = listOf(GarudaDragonCyan, GarudaDragonViolet, GarudaDragonPink)
)

val GarudaCardBorderGradient = Brush.linearGradient(
    colors = listOf(
        GarudaDragonCyan.copy(alpha = 0.5f),
        GarudaDragonViolet.copy(alpha = 0.3f),
        GarudaDragonPink.copy(alpha = 0.4f)
    )
)

val GarudaNeonPillGradient = Brush.horizontalGradient(
    colors = listOf(GarudaDragonCyan, GarudaDragonViolet)
)
