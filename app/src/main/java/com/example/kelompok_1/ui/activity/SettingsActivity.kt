package com.example.kelompok_1.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kelompok_1.ExpenseTrackerApplication
import com.example.kelompok_1.ui.components.BottomNavBar
import com.example.kelompok_1.ui.theme.ExpenseTrackerTheme
import com.example.kelompok_1.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overridePendingTransition(0, 0)
        
        val repository = (application as ExpenseTrackerApplication).repository
        
        setContent {
            val context = LocalContext.current
            val isDarkMode by ThemePreferences.isDarkMode(context).collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            
            ExpenseTrackerTheme(darkTheme = isDarkMode) {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onToggleTheme = {
                        scope.launch {
                            ThemePreferences.setDarkMode(context, !isDarkMode)
                        }
                    },
                    onClearData = {
                        scope.launch {
                            repository.clearAllData()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onClearData: () -> Unit
) {
    var showFirstConfirmDialog by remember { mutableStateOf(false) }
    var showSecondConfirmDialog by remember { mutableStateOf(false) }
    var confirmText by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavBar(currentRoute = "settings")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Mode Gelap",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isDarkMode) "Aktif" else "Nonaktif",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme() }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                onClick = { showFirstConfirmDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Column {
                        Text(
                            text = "Hapus Semua Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Menghapus semua data transaksi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "PanDana v1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showFirstConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFirstConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Hapus Semua Data?") },
            text = {
                Text("Apakah Anda yakin ingin menghapus semua data? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFirstConfirmDialog = false
                        showSecondConfirmDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFirstConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showSecondConfirmDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSecondConfirmDialog = false
                confirmText = ""
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Konfirmasi Akhir") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Ketik \"HAPUS\" untuk mengonfirmasi penghapusan semua data.")
                    OutlinedTextField(
                        value = confirmText,
                        onValueChange = { confirmText = it.uppercase() },
                        label = { Text("Ketik HAPUS") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearData()
                        showSecondConfirmDialog = false
                        confirmText = ""
                        showSuccessMessage = true
                    },
                    enabled = confirmText == "HAPUS",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showSecondConfirmDialog = false
                    confirmText = ""
                }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showSuccessMessage) {
        AlertDialog(
            onDismissRequest = { showSuccessMessage = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Berhasil") },
            text = { Text("Semua data telah dihapus.") },
            confirmButton = {
                TextButton(onClick = { showSuccessMessage = false }) {
                    Text("OK")
                }
            }
        )
    }
}
