package com.example.anabada

interface BoardRepository {
    fun getBoardData() : String
}

class BoardRepositoryImpl() : BoardRepository {
    override fun getBoardData(): String {
        return "Hello Board Repo"
    }
}