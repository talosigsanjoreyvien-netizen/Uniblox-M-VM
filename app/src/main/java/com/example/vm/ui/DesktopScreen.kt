package com.example.vm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vm.AppType
import com.example.vm.VMViewModel
import java.io.File

@Composable
fun DesktopScreen(viewModel: VMViewModel) {
    val status by viewModel.status.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(status.version.color)
    ) {
        // Desktop Icons
        DesktopIcons(
            onAppClick = { viewModel.toggleApp(it) }
        )

        // Windows (Simplified)
        Box(modifier = Modifier.fillMaxSize()) {
            viewModel.openWindows.forEach { appType ->
                WindowFrame(
                    title = "${appType.name} (${status.version.label})",
                    accentColor = status.version.accent,
                    onClose = { viewModel.toggleApp(appType) }
                ) {
                    AppContent(appType, viewModel)
                }
            }
        }

        // Taskbar
        Taskbar(
            systemTime = status.systemTime,
            accentColor = status.version.accent,
            onStartClick = { /* Show Start Menu */ },
            openWindows = viewModel.openWindows
        )
    }
}

@Composable
fun DesktopIcons(onAppClick: (AppType) -> Unit) {
    val icons = listOf(
        IconData("This PC", Icons.Default.Laptop, AppType.EXPLORER),
        IconData("Terminal", Icons.Default.Terminal, AppType.TERMINAL),
        IconData("Settings", Icons.Default.Settings, AppType.SETTINGS)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .padding(16.dp)
            .width(100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(count = icons.size) { index ->
            val icon = icons[index]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onAppClick(icon.appType) }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = icon.image,
                    contentDescription = icon.label,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = icon.label,
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class IconData(val label: String, val image: ImageVector, val appType: AppType)

@Composable
fun BoxScope.Taskbar(
    systemTime: String,
    accentColor: Color,
    onStartClick: () -> Unit,
    openWindows: List<AppType>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .align(Alignment.BottomCenter),
        color = Color.Black.copy(alpha = 0.8f),
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start Button
            IconButton(onClick = onStartClick) {
                Icon(
                    Icons.Default.Apps,
                    contentDescription = "Start",
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Open Apps Icons
            Row(modifier = Modifier.weight(1f)) {
                openWindows.forEach { app ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = when(app) {
                                AppType.EXPLORER -> Icons.Default.Folder
                                AppType.TERMINAL -> Icons.Default.Terminal
                                AppType.SETTINGS -> Icons.Default.Settings
                            },
                            contentDescription = app.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // System Tray
            Text(
                text = systemTime,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun BoxScope.WindowFrame(
    title: String,
    accentColor: Color,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    // In a real app, we'd make this draggable. For now, it's a centered modal-like box.
    Card(
        modifier = Modifier
            .fillMaxSize(0.8f)
            .padding(16.dp)
            .align(Alignment.Center),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Content
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun AppContent(appType: AppType, viewModel: VMViewModel) {
    when (appType) {
        AppType.EXPLORER -> ExplorerContent(viewModel)
        AppType.TERMINAL -> TerminalContent(viewModel)
        AppType.SETTINGS -> SettingsContent(viewModel)
    }
}

@Composable
fun ExplorerContent(viewModel: VMViewModel) {
    val dFiles by viewModel.dFiles.collectAsState()
    val gFiles by viewModel.gFiles.collectAsState()
    var currentDrive by remember { mutableStateOf("D:") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { currentDrive = "D:" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentDrive == "D:") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("D: Downloads")
            }
            Button(
                onClick = { currentDrive = "G:" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentDrive == "G:") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("G: USB/Storage")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val files = if (currentDrive == "D:") dFiles else gFiles

        if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No files found in $currentDrive", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(files) { file ->
                    FileListItem(file) {
                        viewModel.runExecutable(file)
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            "System: Box64 Virtualization Active • Read-only access to Android storage.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun FileListItem(file: File, onClick: () -> Unit) {
    val isExe = file.name.lowercase().endsWith(".exe")
    ListItem(
        headlineContent = { 
            Text(
                file.name, 
                maxLines = 1,
                color = if (isExe) MaterialTheme.colorScheme.primary else Color.Unspecified,
                fontWeight = if (isExe) FontWeight.Bold else FontWeight.Normal
            ) 
        },
        supportingContent = { 
            val size = if (file.isDirectory) "Folder" else "${file.length() / 1024} KB"
            Text(size) 
        },
        leadingContent = {
            Icon(
                imageVector = when {
                    file.isDirectory -> Icons.Default.Folder
                    isExe -> Icons.Default.PlayArrow
                    else -> Icons.Default.Description
                },
                contentDescription = null,
                tint = when {
                    file.isDirectory -> Color(0xFFFFCC33)
                    isExe -> Color(0xFF4CAF50)
                    else -> Color.Gray
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

@Composable
fun TerminalContent(viewModel: VMViewModel) {
    val status by viewModel.status.collectAsState()
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("WinVM x64 Terminal v1.0", color = Color.Green, fontSize = 12.sp)
            
            if (status.runningExe != null) {
                Text("C:\\> box64 ${status.runningExe}", color = Color.White, fontSize = 12.sp)
                Text("Box64 v0.2.6 with Dynarec enabled", color = Color.LightGray, fontSize = 12.sp)
                Text("Initializing BDOS compatibility layer...", color = Color.Cyan, fontSize = 12.sp)
                Text("Launching x64 process: ${status.runningExe}...", color = Color.Green, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.closeExecutable() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Terminate Process", color = Color.White)
                }
            } else {
                Text("C:\\>", color = Color.White, fontSize = 12.sp)
                Text("Box64 idle...", color = Color.LightGray, fontSize = 12.sp)
            }
            
            Text("_", color = Color.White, fontSize = 12.sp) // Cursor
        }
    }
}

@Composable
fun ConverterContent() {
    Column {
        Text("Windows App Converter", style = MaterialTheme.typography.headlineSmall)
        Text("Convert x86/x64 Windows Apps to Lightweight code.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Simulate conversion */ }) {
            Text("Select .exe to Convert")
        }
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
    }
}

@Composable
fun SettingsContent(viewModel: VMViewModel) {
    Column {
        Text("System Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.triggerBsod() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Test BSOD (System Crash)")
        }
    }
}
