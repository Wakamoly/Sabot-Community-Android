package com.lucidsoftworksllc.sabotcommunity.db

import androidx.room.*

@Dao
interface PublicsDao {

    @Insert
    suspend fun addGame(game: PublicsEntity)

    @Query("SELECT * FROM publicsentity ORDER BY id DESC")
    suspend fun getAllGamesByIDDESC() : List<PublicsEntity>

    @Query("SELECT * FROM publicsentity ORDER BY followers DESC")
    suspend fun getAllGamesByFollowersDESC() : List<PublicsEntity>

    @Insert
    suspend fun addMultipleGames(vararg game: PublicsEntity)

    @Update
    suspend fun updateGame(game: PublicsEntity)

    @Delete
    suspend fun deleteGame(game: PublicsEntity)
}