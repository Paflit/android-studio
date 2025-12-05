package com.example.console

import com.example.core.*

private const val THRESHOLD = 150

object ConsoleUI {

    fun run(service: ProductService) {
        seedDemo(service)

        while (true) {
            println(
                """
                Магазин: чай & кофе
                1) Создать кофе
                2) Создать чай
                3) Показать все товары
                4) Показать товары весом < $THRESHOLD г
                5) Просмотр товара (detail)
                6) Обновить товар (update)
                7) Удалить товар (delete)
                0) Выход
                """.trimIndent()
            )

            when (readlnPrompt("Ваш выбор").trim()) {
                "1" -> createCoffeeFlow(service)
                "2" -> createTeaFlow(service)
                "3" -> printProducts(service.getAll())
                "4" -> printProducts(service.getLighterThan(THRESHOLD))
                "5" -> detailFlow(service)
                "6" -> updateFlow(service)
                "7" -> deleteFlow(service)
                "0" -> return
                else -> println("Неизвестная команда.")
            }
        }
    }

    private fun createCoffeeFlow(service: ProductService) {
        val name = readlnPrompt("Название")
        val price = readDouble("Цена")
        val weight = readInt("Вес (г)")
        val type = readCoffeeType()
        try {
            val p = service.createCoffee(name, price, weight, type)
            println("Кофе добавлен. id=${p.id}")
        } catch (e: IllegalArgumentException) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun createTeaFlow(service: ProductService) {
        val name = readlnPrompt("Название")
        val price = readDouble("Цена")
        val weight = readInt("Вес (г)")
        val type = readTeaType()
        try {
            val p = service.createTea(name, price, weight, type)
            println("Чай добавлен. id=${p.id}")
        } catch (e: IllegalArgumentException) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun detailFlow(service: ProductService) {
        val id = readlnPrompt("Введите id")
        val p = service.getById(id)
        if (p == null) println("Не найдено.")
        else println(p.getDetails())
    }

    private fun updateFlow(service: ProductService) {
        val id = readlnPrompt("Введите id")
        val old = service.getById(id) ?: run { println("Не найдено."); return }

        val name = readlnPrompt("Новое название (enter = оставить '${old.name}')").ifBlank { old.name }
        val price = readDoubleOrBlank("Новая цена (enter = оставить ${old.price})") ?: old.price
        val weight = readIntOrBlank("Новый вес (г) (enter = оставить ${old.weight})") ?: old.weight

        try {
            val ok = when (old) {
                is Coffee -> service.updateCoffee(id, name, price, weight, readCoffeeTypeOrKeep(old.type))
                is Tea -> service.updateTea(id, name, price, weight, readTeaTypeOrKeep(old.type))
                else -> false
            }
            println(if (ok) "Обновлено." else "Не удалось обновить.")
        } catch (e: IllegalArgumentException) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun deleteFlow(service: ProductService) {
        val id = readlnPrompt("Введите id")
        val ok = service.delete(id)
        println(if (ok) "Удалено." else "Не найдено.")
    }

    private fun printProducts(items: List<Product>) {
        if (items.isEmpty()) {
            println("Список пуст.")
            return
        }
        println("—".repeat(40))
        items.forEachIndexed { i, p -> println("${i + 1}. [${p.id}] ${p.getDetails()}") }
        println("—".repeat(40))
    }

    private fun readlnPrompt(label: String): String = run { print("$label: "); readln() }

    private fun readInt(label: String): Int {
        while (true) {
            val s = readlnPrompt(label)
            s.toIntOrNull()?.let { return it }
            println("Введите целое число.")
        }
    }

    private fun readIntOrBlank(label: String): Int? {
        while (true) {
            val s = readlnPrompt(label)
            if (s.isBlank()) return null
            s.toIntOrNull()?.let { return it }
            println("Введите целое число.")
        }
    }

    private fun readDouble(label: String): Double {
        while (true) {
            val s = readlnPrompt(label).replace(',', '.')
            s.toDoubleOrNull()?.let { return it }
            println("Введите число, например 199.90")
        }
    }

    private fun readDoubleOrBlank(label: String): Double? {
        while (true) {
            val s = readlnPrompt(label).replace(',', '.')
            if (s.isBlank()) return null
            s.toDoubleOrNull()?.let { return it }
            println("Введите число, например 199.90")
        }
    }

    private fun readCoffeeType(): CoffeeType {
        val options = CoffeeType.entries.joinToString { it.name }
        while (true) {
            val s = readlnPrompt("Тип кофе [$options]").trim().uppercase()
            CoffeeType.entries.firstOrNull { it.name == s }?.let { return it }
            println("Допустимые значения: $options")
        }
    }

    private fun readTeaType(): TeaType {
        val options = TeaType.entries.joinToString { it.name }
        while (true) {
            val s = readlnPrompt("Тип чая [$options]").trim().uppercase()
            TeaType.entries.firstOrNull { it.name == s }?.let { return it }
            println("Допустимые значения: $options")
        }
    }

    private fun readCoffeeTypeOrKeep(current: CoffeeType): CoffeeType {
        val options = CoffeeType.entries.joinToString { it.name }
        while (true) {
            val s = readlnPrompt("Тип кофе [$options] (enter = оставить ${current.name})").trim()
            if (s.isBlank()) return current
            val up = s.uppercase()
            CoffeeType.entries.firstOrNull { it.name == up }?.let { return it }
            println("Допустимые значения: $options")
        }
    }

    private fun readTeaTypeOrKeep(current: TeaType): TeaType {
        val options = TeaType.entries.joinToString { it.name }
        while (true) {
            val s = readlnPrompt("Тип чая [$options] (enter = оставить ${current.name})").trim()
            if (s.isBlank()) return current
            val up = s.uppercase()
            TeaType.entries.firstOrNull { it.name == up }?.let { return it }
            println("Допустимые значения: $options")
        }
    }

    private fun seedDemo(service: ProductService) {
        service.createCoffee("Arabica Premium", 799.0, 250, CoffeeType.BEANS)
        service.createCoffee("Monarch", 199.0, 100, CoffeeType.INSTANT)
        service.createTea("Assam Strong", 199.0, 90, TeaType.BLACK)
        service.createTea("Lipton", 249.0, 180, TeaType.GREEN)
    }
}

fun main() {
    val repo: ProductRepository = InMemoryProductRepository()
    val service: ProductService = DefaultProductService(repo)
    ConsoleUI.run(service)
}
