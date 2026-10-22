package com.fretwise.app

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fretwise.app.ui.FretwiseApp
import com.fretwise.app.ui.theme.FretwiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The UI is always dark, so keep light system bar icons regardless of the system theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        // Hands are on the guitar, not the phone: don't let the screen dim or lock
        // while the app is in front. The flag only applies while this window is
        // visible, so the normal timeout comes back as soon as the app is backgrounded.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            FretwiseTheme {
                FretwiseApp()
            }
        }
    }
}
