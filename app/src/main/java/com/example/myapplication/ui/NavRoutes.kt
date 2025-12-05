package com.example.myapplication.ui

object Routes {
    const val LIST = "list"
    const val DETAILS = "details/{id}"
    const val EDIT = "edit?id={id}" // id может быть пустым => создание
    fun details(id: String) = "details/$id"
    fun edit(id: String?) = if (id == null) "edit?id=" else "edit?id=$id"
}