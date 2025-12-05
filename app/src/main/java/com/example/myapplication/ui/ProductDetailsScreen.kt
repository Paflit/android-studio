package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    vm: ProductViewModel,
    id: String,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val p = vm.getById(id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } },
                actions = { TextButton(onClick = onEdit) { Text("Редактировать") } }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            if (p == null) {
                Text("Товар не найден.")
            } else {
                Text(p.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(p.getDetails())
                Spacer(Modifier.height(8.dp))
                Text("id: ${p.id}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
