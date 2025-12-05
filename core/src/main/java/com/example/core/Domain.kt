package com.example.core

import java.util.UUID

interface Product {
    val id: String
    val name: String
    val price: Double
    val weight: Int
    fun getDetails(): String
}

abstract class Validator {
    protected fun validateName(name: String) {
        require(name.isNotBlank()) { "Название не может быть пустым" }
    }
    protected fun validatePrice(price: Double) {
        require(price >= 0) { "Цена не может быть отрицательной" }
    }
    protected fun validateWeight(weight: Int) {
        require(weight > 0) { "Вес должен быть положительным" }
    }
}

enum class CoffeeType { INSTANT, GROUND, BEANS }
enum class TeaType { BLACK, GREEN }

private fun CoffeeType.ru() = when (this) {
    CoffeeType.INSTANT -> "растворимый"
    CoffeeType.GROUND  -> "молотый"
    CoffeeType.BEANS   -> "в зёрнах"
}
private fun TeaType.ru() = when (this) {
    TeaType.BLACK -> "чёрный"
    TeaType.GREEN -> "зелёный"
}

data class Coffee(
    override val id: String,
    override val name: String,
    override val price: Double,
    override val weight: Int,
    val type: CoffeeType
) : Validator(), Product {
    init {
        validateName(name); validatePrice(price); validateWeight(weight)
    }
    override fun getDetails(): String =
        "Кофе: $name (${type.ru()}), цена: $price руб, вес: ${weight}г"
}

data class Tea(
    override val id: String,
    override val name: String,
    override val price: Double,
    override val weight: Int,
    val type: TeaType
) : Validator(), Product {
    init {
        validateName(name); validatePrice(price); validateWeight(weight)
    }
    override fun getDetails(): String =
        "Чай: $name (${type.ru()}), цена: $price руб, вес: ${weight}г"
}

interface ProductRepository {
    fun add(product: Product)
    fun getAll(): List<Product>
    fun getById(id: String): Product?
    fun update(product: Product): Boolean
    fun delete(id: String): Boolean
}

class InMemoryProductRepository : ProductRepository {
    private val items = linkedMapOf<String, Product>()

    override fun add(product: Product) {
        items[product.id] = product
    }

    override fun getAll(): List<Product> = items.values.toList()

    override fun getById(id: String): Product? = items[id]

    override fun update(product: Product): Boolean {
        if (!items.containsKey(product.id)) return false
        items[product.id] = product
        return true
    }

    override fun delete(id: String): Boolean = items.remove(id) != null
}

interface ProductService {
    fun createCoffee(name: String, price: Double, weightGrams: Int, type: CoffeeType): Product
    fun createTea(name: String, price: Double, weightGrams: Int, type: TeaType): Product

    fun getAll(): List<Product>
    fun getById(id: String): Product?
    fun getLighterThan(maxWeightGrams: Int): List<Product>

    fun updateCoffee(id: String, name: String, price: Double, weightGrams: Int, type: CoffeeType): Boolean
    fun updateTea(id: String, name: String, price: Double, weightGrams: Int, type: TeaType): Boolean

    fun delete(id: String): Boolean
}

class DefaultProductService(private val repo: ProductRepository) : ProductService {

    override fun createCoffee(name: String, price: Double, weightGrams: Int, type: CoffeeType): Product {
        val p = Coffee(id = UUID.randomUUID().toString(), name = name, price = price, weight = weightGrams, type = type)
        repo.add(p)
        return p
    }

    override fun createTea(name: String, price: Double, weightGrams: Int, type: TeaType): Product {
        val p = Tea(id = UUID.randomUUID().toString(), name = name, price = price, weight = weightGrams, type = type)
        repo.add(p)
        return p
    }

    override fun getAll(): List<Product> = repo.getAll()

    override fun getById(id: String): Product? = repo.getById(id)

    override fun getLighterThan(maxWeightGrams: Int): List<Product> =
        repo.getAll().filter { it.weight < maxWeightGrams }

    override fun updateCoffee(id: String, name: String, price: Double, weightGrams: Int, type: CoffeeType): Boolean {
        val updated = Coffee(id = id, name = name, price = price, weight = weightGrams, type = type)
        return repo.update(updated)
    }

    override fun updateTea(id: String, name: String, price: Double, weightGrams: Int, type: TeaType): Boolean {
        val updated = Tea(id = id, name = name, price = price, weight = weightGrams, type = type)
        return repo.update(updated)
    }

    override fun delete(id: String): Boolean = repo.delete(id)
}
