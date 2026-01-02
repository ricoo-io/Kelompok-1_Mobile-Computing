package com.example.kelompok_1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kelompok_1.data.model.Category

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipSelector(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category.id == selectedCategoryId
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(text = category.name)
                },
                leadingIcon = {
                    Icon(
                        imageVector = getCategoryIcon(category.icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(category.color).copy(alpha = 0.2f),
                    selectedLabelColor = Color(category.color),
                    selectedLeadingIconColor = Color(category.color)
                )
            )
        }
    }
}
