package com.lucidsoftworksllc.sabotcommunity.db.publics

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface PublicsDao {

    @Insert
    suspend fun addGame(game: PublicsEntity)


    @RawQuery
    suspend fun getGamesRaw(query: SimpleSQLiteQuery) : List<PublicsEntity>
    @RawQuery
    suspend fun getNumGamesFilterRaw(query: SimpleSQLiteQuery) : Int


    @Query("UPDATE publicsentity SET followed = 'yes' WHERE id = :id")
    suspend fun followGame(id: Int)

    @Query("UPDATE publicsentity SET followed = 'no' WHERE id = :id")
    suspend fun unfollowGame(id: Int)



    @Insert
    suspend fun addMultipleGames(vararg game: PublicsEntity)

    @Update
    suspend fun updateGame(game: PublicsEntity)

    @Delete
    suspend fun deleteGame(game: PublicsEntity)

    @Query("SELECT EXISTS(SELECT id FROM publicsentity WHERE id = :id)")
    suspend fun isRowIsExist(id : Int) : Boolean

    @Query("SELECT COUNT(id) FROM publicsentity WHERE platforms LIKE '%'+:filter+'%'")
    suspend fun numRowsFilter(filter: String) : Int

    @Query("SELECT COUNT(id) FROM publicsentity WHERE active = 'yes'")
    suspend fun numRowsAll() : Int
}