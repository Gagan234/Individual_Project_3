package com.example.mazegame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

@Composable
fun ParentDashboard(username: String, progressData: List<Int>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val chartWidth = size.width
        val chartHeight = size.height

        val maxProgress = progressData.maxOrNull()?.toFloat() ?: 1f
        val barWidth = chartWidth / (progressData.size * 2f)

        progressData.forEachIndexed { index, progress ->
            val barHeight = (progress / maxProgress) * chartHeight

            val startX = index * 2 * barWidth
            val topY = chartHeight - barHeight

            drawRect(
                color = Color.Blue,
                topLeft = androidx.compose.ui.geometry.Offset(startX, topY),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }


        translate(left = 0f, top = chartHeight) {
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(chartWidth, 0f),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
