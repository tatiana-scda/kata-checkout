package com.scda.kata_checkout.domain

data class Cart(val items: MutableMap<Item, Int>)
