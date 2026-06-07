package com.example.vm

import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class WinApp(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val type: AppType
)

enum class AppType {
    EXPLORER, TERMINAL, SETTINGS
}

enum class WinVersion(val label: String, val color: Color, val accent: Color) {
    WIN7("Windows 7", Color(0xFF004275), Color(0xFF72AEE6)),
    WIN10("Windows 10", Color(0xFF0078D7), Color(0xFF00ADEF)),
    WIN11("Windows 11", Color(0xFF005A9E), Color(0xFF4CC2FF))
}

data class VMStatus(
    val isSetup: Boolean = true,
    val isBooting: Boolean = false,
    val isBsod: Boolean = false,
    val bsodErrorCode: String = "0x0000005C",
    val systemTime: String = "10:18 AM",
    val version: WinVersion = WinVersion.WIN11,
    val runningExe: String? = null
)

class VMViewModel : ViewModel() {

    private val _status = MutableStateFlow(VMStatus())
    val status: StateFlow<VMStatus> = _status.asStateFlow()

    private val _openWindows = mutableStateListOf<AppType>()
    val openWindows: List<AppType> = _openWindows

    // Drive Mapping
    val dDrive: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val gDrive: File = File("/storage")

    private val _dFiles = MutableStateFlow<List<File>>(emptyList())
    val dFiles: StateFlow<List<File>> = _dFiles.asStateFlow()

    private val _gFiles = MutableStateFlow<List<File>>(emptyList())
    val gFiles: StateFlow<List<File>> = _gFiles.asStateFlow()

    init {
        scanD()
        scanG()
    }

    fun scanD() {
        _dFiles.value = dDrive.listFiles()?.toList() ?: emptyList()
    }

    fun scanG() {
        _gFiles.value = gDrive.listFiles()?.toList() ?: emptyList()
    }

    fun selectVersion(version: WinVersion) {
        _status.value = _status.value.copy(
            version = version,
            isSetup = false,
            isBooting = true
        )
    }

    fun runExecutable(file: File) {
        if (file.name.lowercase().endsWith(".exe")) {
            _status.value = _status.value.copy(runningExe = file.name)
            if (!_openWindows.contains(AppType.TERMINAL)) {
                _openWindows.add(AppType.TERMINAL)
            }
        }
    }

    fun closeExecutable() {
        _status.value = _status.value.copy(runningExe = null)
    }

    fun toggleApp(type: AppType) {
        if (_openWindows.contains(type)) {
            _openWindows.remove(type)
        } else {
            _openWindows.add(type)
        }
    }

    fun triggerBsod(error: String = "CRITICAL_PROCESS_DIED") {
        _status.value = _status.value.copy(isBsod = true, bsodErrorCode = error)
    }

    fun reboot() {
        _status.value = VMStatus(isSetup = true, isBooting = false)
        _openWindows.clear()
    }

    fun finishBooting() {
        _status.value = _status.value.copy(isBooting = false)
    }
}
