package com.example.myapplication.di

import com.example.core.DefaultProductService
import com.example.core.InMemoryProductRepository
import com.example.core.ProductService

object AppContainer {
    private val repo = InMemoryProductRepository()
    val service: ProductService = DefaultProductService(repo)
}
