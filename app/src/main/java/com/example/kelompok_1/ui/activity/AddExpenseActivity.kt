package com.example.kelompok_1.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kelompok_1.ExpenseTrackerApplication
import com.example.kelompok_1.ui.components.*
import com.example.kelompok_1.ui.theme.*
import com.example.kelompok_1.ui.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.*

class AddExpenseActivity : ComponentActivity() {
    companion object {
        const val EXTRA_EXPENSE_ID = "expense_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overridePendingTransition(0, 0)
        
        val repository = (application as ExpenseTrackerApplication).repository
        val expenseId = intent.getLongExtra(EXTRA_EXPENSE_ID, -1L).takeIf { it != -1L }
        
        setContent {
            val isDarkMode by ThemePreferences.isDarkMode(this).collectAsState(initial = false)
            
            ExpenseTrackerTheme(darkTheme = isDarkMode) {
                val viewModel: ExpenseViewModel = viewModel(
                    factory = ExpenseViewModel.Factory(repository)
                )
                AddExpenseScreen(
                    viewModel = viewModel,
                    expenseId = expenseId,
                    onSaved = { finish() },
                    onDeleted = { finish() },
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    expenseId: Long? = null,
    onSaved: () -> Unit,
    onDeleted: () -> Unit = {},
    onBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val context = LocalContext.current
    val isEditMode = expenseId != null
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(expenseId) {
        expenseId?.let { viewModel.loadExpense(it) }
    }

    LaunchedEffect(formState.isSaved) {
        if (formState.isSaved) {
            val message = if (isEditMode) "Transaksi berhasil diupdate" else "Transaksi berhasil disimpan"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onSaved()
        }
    }
    
    LaunchedEffect(formState.isDeleted) {
        if (formState.isDeleted) {
            Toast.makeText(context, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
            onDeleted()
        }
    }

    LaunchedEffect(formState.error) {
        formState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when {
                        isEditMode && formState.isIncome -> "Edit Pemasukan"
                        isEditMode && !formState.isIncome -> "Edit Pengeluaran"
                        formState.isIncome -> "Tambah Pemasukan"
                        else -> "Tambah Pengeluaran"
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(false to "Pengeluaran", true to "Pemasukan").forEach { (isIncome, label) ->
                    val isSelected = formState.isIncome == isIncome
                    TextButton(
                        onClick = { viewModel.setTransactionType(isIncome) },
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) {
                                    if (isIncome) SecondaryGreen else MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                RoundedCornerShape(8.dp)
                            ),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = if (formState.isIncome) "Jumlah Pemasukan" else "Jumlah Pengeluaran",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rp",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = formState.amount.ifEmpty { "" }.let { 
                                if (it.isNotEmpty()) {
                                    NumberFormat.getNumberInstance(Locale("id", "ID"))
                                        .format(it.toLongOrNull() ?: 0)
                                } else ""
                            },
                            onValueChange = { input ->
                                val cleaned = input.replace(".", "").replace(",", "")
                                viewModel.updateAmount(cleaned)
                            },
                            placeholder = { 
                                Text(
                                    text = "0",
                                    fontSize = 32.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryChipSelector(
                        categories = categories,
                        selectedCategoryId = formState.selectedCategoryId,
                        onCategorySelected = { viewModel.selectCategory(it.id) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    ExpenseDatePicker(
                        selectedDate = formState.date,
                        onDateSelected = { viewModel.updateDate(it) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Catatan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        placeholder = { 
                            Text(
                                text = "Tulis deskripsi pengeluaran (Opsional)...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveExpense() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !formState.isLoading
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (isEditMode) "Simpan Perubahan" else "Simpan Transaksi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi") },
            text = { 
                Text("Apakah Anda yakin ingin menghapus transaksi ini? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
