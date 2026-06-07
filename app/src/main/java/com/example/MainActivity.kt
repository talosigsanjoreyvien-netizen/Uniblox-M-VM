package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vm.VMViewModel
import com.example.vm.ui.BSODScreen
import com.example.vm.ui.DesktopScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: VMViewModel = viewModel()
                val status by viewModel.status.collectAsState()

                Crossfade(targetState = status, label = "ScreenTransition") { currentStatus ->
                    when {
                        currentStatus.isBsod -> {
                            BSODScreen(
                                errorCode = currentStatus.bsodErrorCode,
                                onRestart = { viewModel.reboot() }
                            )
                        }
                        currentStatus.isSetup -> {
                            SetupScreen(onVersionSelected = { viewModel.selectVersion(it) })
                        }
                        currentStatus.isBooting -> {
                            BootScreen(onFinished = { viewModel.finishBooting() })
                        }
                        else -> {
                            DesktopScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetupScreen(onVersionSelected: (com.example.vm.WinVersion) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "WinVM Setup",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select your Windows environment",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            com.example.vm.WinVersion.entries.forEach { version ->
                VersionCard(version = version, onClick = { onVersionSelected(version) })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VersionCard(version: com.example.vm.WinVersion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(version.color, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Window,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = version.label,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Optimized with Box64 & Dynarec",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BootScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds boot
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WinVM",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}
