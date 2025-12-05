package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.Product
import androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    vm: ProductViewModel,
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Магазин: чай & кофе") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Лёгкие")
                        Switch(
                            checked = state.showOnlyLight,
                            onCheckedChange = { vm.setOnlyLight(it) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Text("+") }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {

            state.error?.let { msg ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(msg, color = MaterialTheme.colorScheme.onErrorContainer)
                        TextButton(onClick = vm::clearError) { Text("OK") }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (state.items.isEmpty()) {
                Text("Список пуст.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.items, key = { it.id }) { p ->
                        ProductRow(
                            p = p,
                            onOpen = { onOpen(p.id) },
                            onEdit = { onEdit(p.id) },
                            onDelete = { vm.delete(p.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    p: Product,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .clickable { onOpen() }
                .padding(12.dp)
        ) {
            Text(p.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(p.getDetails(), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Изменить") }
                OutlinedButton(onClick = onDelete) { Text("Удалить") }
            }
        }
    }
}
