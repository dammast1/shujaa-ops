package com.shujaa.ops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shujaa.ops.ui.AppShell
import com.shujaa.ops.ui.theme.ShujaaOpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShujaaOpsTheme {
                AppShell()
            }
        }
    }
}
