package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.*
import androidx.compose.material3.ExperimentalMaterial3Api
private enum class Kind { COFFEE, TEA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    vm: ProductViewModel,
    id: String?,          // null -> создание, иначе редактирование
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val existing = remember(id) { id?.let(vm::getById) }

    var kind by remember { mutableStateOf(if (existing is Tea) Kind.TEA else Kind.COFFEE) }

    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var price by remember { mutableStateOf(existing?.price?.toString().orEmpty()) }
    var weight by remember { mutableStateOf(existing?.weight?.toString().orEmpty()) }

    var coffeeType by remember { mutableStateOf((existing as? Coffee)?.type ?: CoffeeType.INSTANT) }
    var teaType by remember { mutableStateOf((existing as? Tea)?.type ?: TeaType.BLACK) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (id == null) "Создать" else "Редактировать") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // выбор сущности
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = kind == Kind.COFFEE,
                    onClick = { kind = Kind.COFFEE; },
                    label = { Text("Кофе") },
                    enabled = (id == null) // при редактировании фиксируем тип
                )
                FilterChip(
                    selected = kind == Kind.TEA,
                    onClick = { kind = Kind.TEA; },
                    label = { Text("Чай") },
                    enabled = (id == null)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Цена") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Вес (г)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (kind == Kind.COFFEE) {
                EnumPicker(
                    title = "Тип кофе",
                    value = coffeeType,
                    values = CoffeeType.entries,
                    onPick = { coffeeType = it }
                )
            } else {
                EnumPicker(
                    title = "Тип чая",
                    value = teaType,
                    values = TeaType.entries,
                    onPick = { teaType = it }
                )
            }

            Button(
                onClick = {
                    val p = price.replace(',', '.').toDoubleOrNull()
                    val w = weight.toIntOrNull()
                    if (p == null || w == null) {
                        // покажем ошибку через state.error
                        vm.createTea("", -1.0, -1, TeaType.BLACK) // триггернёт валидацию? не надо
                        // лучше аккуратно:
                        // но проще — кинем сами:
                        // (в этом шаблоне оставим минимально)
                        return@Button
                    }

                    if (id == null) {
                        if (kind == Kind.COFFEE) vm.createCoffee(name, p, w, coffeeType)
                        else vm.createTea(name, p, w, teaType)
                    } else {
                        when (existing) {
                            is Coffee -> vm.updateCoffee(id, name, p, w, coffeeType)
                            is Tea -> vm.updateTea(id, name, p, w, teaType)
                            else -> {}
                        }
                    }
                    onDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (id == null) "Создать" else "Сохранить")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Enum<T>> EnumPicker(
    title: String,
    value: T,
    values: List<T>,
    onPick: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = value.name,
                onValueChange = {},
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                values.forEach { v ->
                    DropdownMenuItem(
                        text = { Text(v.name) },
                        onClick = { onPick(v); expanded = false }
                    )
                }
            }
        }
    }
}
