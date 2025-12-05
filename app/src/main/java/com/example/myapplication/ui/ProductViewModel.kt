package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import com.example.myapplication.di.AppContainer
import com.example.core.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ProductUiState(
    val items: List<Product> = emptyList(),
    val error: String? = null,
    val threshold: Int = 150,
    val showOnlyLight: Boolean = false
)

class ProductViewModel(
    private val service: ProductService = AppContainer.service
) : ViewModel() {

    private val _state = MutableStateFlow(ProductUiState())
    val state: StateFlow<ProductUiState> = _state

    init {
        seedDemo()
        refresh()
    }

    fun refresh() {
        val s = _state.value
        val items = if (s.showOnlyLight) service.getLighterThan(s.threshold) else service.getAll()
        _state.update { it.copy(items = items, error = null) }
    }

    fun setOnlyLight(enabled: Boolean) {
        _state.update { it.copy(showOnlyLight = enabled) }
        refresh()
    }

    fun getById(id: String): Product? = service.getById(id)

    fun createCoffee(name: String, price: Double, weight: Int, type: CoffeeType) = runCatching {
        service.createCoffee(name, price, weight, type)
    }.onFailure { e -> _state.update { it.copy(error = e.message) } }
        .onSuccess { refresh() }

    fun createTea(name: String, price: Double, weight: Int, type: TeaType) = runCatching {
        service.createTea(name, price, weight, type)
    }.onFailure { e -> _state.update { it.copy(error = e.message) } }
        .onSuccess { refresh() }

    fun updateCoffee(id: String, name: String, price: Double, weight: Int, type: CoffeeType) = runCatching {
        service.updateCoffee(id, name, price, weight, type)
    }.onFailure { e -> _state.update { it.copy(error = e.message) } }
        .onSuccess { refresh() }

    fun updateTea(id: String, name: String, price: Double, weight: Int, type: TeaType) = runCatching {
        service.updateTea(id, name, price, weight, type)
    }.onFailure { e -> _state.update { it.copy(error = e.message) } }
        .onSuccess { refresh() }

    fun delete(id: String) {
        service.delete(id)
        refresh()
    }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun seedDemo() {
        service.createCoffee("Arabica Premium", 799.0, 250, CoffeeType.BEANS)
        service.createCoffee("Monarch", 199.0, 100, CoffeeType.INSTANT)
        service.createTea("Assam Strong", 199.0, 90, TeaType.BLACK)
        service.createTea("Lipton", 249.0, 180, TeaType.GREEN)
    }
}
