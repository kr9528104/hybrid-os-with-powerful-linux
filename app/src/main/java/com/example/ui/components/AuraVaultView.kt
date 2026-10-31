package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VaultResource
import com.example.ui.theme.*

@Composable
fun AuraVaultView(
    resources: List<VaultResource>,
    onViewResource: (VaultResource) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(resources, searchQuery) {
        if (searchQuery.isBlank()) resources
        else resources.filter { it.name.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBgDark)
            .padding(16.dp)
            .testTag("aura_vault_view")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Personal Vault",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AuraTextPrimary
                )
                Text(
                    text = "Semantic knowledge index & pinned artifacts",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AuraSecondary.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraSecondary.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "${resources.size} Artifacts",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AuraSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search vault artifacts...", color = AuraTextMuted) },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = AuraTextMuted)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AuraSurfaceDark,
                unfocusedContainerColor = AuraSurfaceDark,
                focusedBorderColor = AuraPrimary,
                unfocusedBorderColor = AuraBorderDark,
                focusedTextColor = AuraTextPrimary,
                unfocusedTextColor = AuraTextPrimary
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filtered, key = { it.id }) { item ->
                VaultCard(resource = item, onClick = { onViewResource(item) })
            }
        }
    }
}

@Composable
private fun VaultCard(resource: VaultResource, onClick: () -> Unit) {
    val (icon, color) = if (resource.type == "JSON") {
        Pair(Icons.Outlined.Code, AuraAmber)
    } else {
        Pair(Icons.Outlined.PictureAsPdf, AuraRose)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = AuraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AuraTextPrimary
                )
                Text(
                    text = "${resource.type} • ${resource.size} • Updated ${resource.updated}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraTextMuted
                )
            }

            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = "Inspect",
                tint = AuraTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
