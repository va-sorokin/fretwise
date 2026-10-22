package com.fretwise.app.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** 24dp stroke icons from the design; tint them with Icon's tint. */
object FretwiseIcons {
    val Tune = strokeIcon("Tune", "M4 6h10M4 12h4M12 12h8M4 18h12", circle(16, 6), circle(10, 12), circle(18, 18))
    val Back = strokeIcon("Back", "M19 12H5M12 19l-7-7 7-7")
    val Minus = strokeIcon("Minus", "M5 12h14")
    val Plus = strokeIcon("Plus", "M12 5v14M5 12h14")
    val Reset = strokeIcon("Reset", "M3 12a9 9 0 1 0 3-6.7L3 8", "M3 3v5h5")

    /** A radius-2 circle as two arcs, since path data has no circle command. */
    private fun circle(cx: Int, cy: Int) = "M${cx - 2} ${cy}a2 2 0 1 0 4 0a2 2 0 1 0 -4 0"

    private fun strokeIcon(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            paths.forEach { data ->
                addPath(
                    pathData = addPathNodes(data),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
}
