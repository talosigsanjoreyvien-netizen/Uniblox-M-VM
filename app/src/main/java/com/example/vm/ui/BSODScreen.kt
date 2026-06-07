package com.example.vm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BSODScreen(errorCode: String, onRestart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0078D7)) // True BSOD Blue
            .clickable { onRestart() }
            .padding(48.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = ":(",
                color = Color.White,
                fontSize = 120.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your PC ran into a problem and needs to restart. We're just collecting some error info, and then we'll restart for you.",
                color = Color.White,
                fontSize = 24.sp,
                lineHeight = 32.sp
            )
            
            Spacer(modifier = Modifier.height(36.dp))
            
            Text(
                text = "0% complete",
                color = Color.White,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // QR code placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White)
                )
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column {
                    Text(
                        text = "For more information about this issue and possible fixes, visit https://www.windows.com/stopcode",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "If you call a support person, give them this info:",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Stop code: $errorCode",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Text(
            text = "Tap to restart",
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}
