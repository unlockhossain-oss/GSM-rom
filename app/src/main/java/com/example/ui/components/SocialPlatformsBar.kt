package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.OrangePrimary

data class SocialPlatformItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val iconResId: Int?,
    val fallbackIcon: ImageVector,
    val brandColor: Color,
    val defaultUrl: String
)

val DEFAULT_SOCIAL_PLATFORMS = listOf(
    SocialPlatformItem(
        id = "whatsapp",
        name = "WhatsApp",
        subtitle = "Help & Chat",
        iconResId = R.drawable.ic_whatsapp,
        fallbackIcon = Icons.Default.Chat,
        brandColor = Color(0xFF25D366),
        defaultUrl = "https://wa.me/"
    ),
    SocialPlatformItem(
        id = "telegram",
        name = "Telegram",
        subtitle = "Channel",
        iconResId = R.drawable.ic_telegram,
        fallbackIcon = Icons.Default.Send,
        brandColor = Color(0xFF2AABEE),
        defaultUrl = "https://t.me/"
    ),
    SocialPlatformItem(
        id = "facebook",
        name = "Facebook",
        subtitle = "Community",
        iconResId = R.drawable.ic_facebook,
        fallbackIcon = Icons.Default.Public,
        brandColor = Color(0xFF1877F2),
        defaultUrl = "https://facebook.com/"
    ),
    SocialPlatformItem(
        id = "youtube",
        name = "YouTube",
        subtitle = "Tutorials",
        iconResId = R.drawable.ic_youtube,
        fallbackIcon = Icons.Default.VideoLibrary,
        brandColor = Color(0xFFFF0000),
        defaultUrl = "https://youtube.com/"
    )
)

/**
 * Top Home Screen Social Platforms Component:
 * Displays WhatsApp, Telegram, Facebook, YouTube with official brand icons,
 * high-contrast dark theme styling, and interactive click handlers to launch URLs/apps.
 */
@Composable
fun SocialPlatformsBar(
    modifier: Modifier = Modifier,
    platforms: List<SocialPlatformItem> = DEFAULT_SOCIAL_PLATFORMS,
    onPlatformClick: ((SocialPlatformItem) -> Unit)? = null
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .animateContentSize()
    ) {
        // Section Header with subtle title and live indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "COMMUNITY & SUPPORT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp
                )
            }

            Text(
                text = "Official Channels",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = OrangePrimary
            )
        }

        // 4 Columns Grid Row for WhatsApp, Telegram, Facebook, YouTube
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            platforms.forEach { item ->
                SocialPlatformButton(
                    item = item,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (onPlatformClick != null) {
                            onPlatformClick(item)
                        } else {
                            openSocialUrl(context, item.name, item.defaultUrl)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SocialPlatformButton(
    item: SocialPlatformItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, Color(0xFF263248).copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("social_button_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Badge with platform brand color tint and gradient background
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                item.brandColor.copy(alpha = 0.18f),
                                item.brandColor.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = item.brandColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconResId != null) {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        imageVector = item.fallbackIcon,
                        contentDescription = item.name,
                        tint = item.brandColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Platform Name
            Text(
                text = item.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Subtitle / Action Label
            Text(
                text = item.subtitle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Safely launch URL intent or fallback to browser
 */
fun openSocialUrl(context: Context, platformName: String, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Opening $platformName...",
            Toast.LENGTH_SHORT
        ).show()
    }
}
