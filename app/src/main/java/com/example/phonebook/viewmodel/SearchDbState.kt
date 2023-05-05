package com.example.phonebook.viewmodel

/**
 * 0: default
 * 1: Mobile
 * 2: Family
 * 3: Home
 * 4: Office
 * 5: Other
 **/
data class SearchDbState(
    val search_by_tag: String = "Default"
)