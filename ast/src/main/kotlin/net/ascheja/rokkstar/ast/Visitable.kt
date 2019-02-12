package net.ascheja.rokkstar.ast

interface Visitable {
    fun <T> accept(v: Visitor<out T>): T
}