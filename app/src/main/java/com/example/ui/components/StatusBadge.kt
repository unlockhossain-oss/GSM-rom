package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusChecking
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusReceived
import com.example.ui.theme.StatusWaitingParts

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        "Received" -> Pair(StatusReceived.copy(alpha = 0.12f), StatusReceived)
        "Checking" -> Pair(StatusChecking.copy(alpha = 0.12f), StatusChecking)
        "Processing" -> Pair(StatusProcessing.copy(alpha = 0.12f), StatusProcessing)
        "Waiting for Parts" -> Pair(StatusWaitingParts.copy(alpha = 0.12f), StatusWaitingParts)
        "Completed" -> Pair(StatusCompleted.copy(alpha = 0.12f), StatusCompleted)
        "Delivered" -> Pair(StatusDelivered.copy(alpha = 0.12f), StatusDelivered)
        "Cancelled" -> Pair(StatusCancelled.copy(alpha = 0.12f), StatusCancelled)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = textColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

