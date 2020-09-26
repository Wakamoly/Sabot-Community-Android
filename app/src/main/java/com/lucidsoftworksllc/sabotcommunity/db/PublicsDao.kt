package com.lucidsoftworksllc.sabotcommunity.db

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface PublicsDao {

    @Insert
    suspend fun addGame(game: PublicsEntity)

    /*@Query("SELECT * FROM publicsentity ORDER BY :sortBy ASC LIMIT :start, :limit")
    suspend fun getAllGamesByASC(start: Int, limit: Int) : List<PublicsEntity>*/

    /*@Query("SELECT * FROM publicsentity ORDER BY title ASC LIMIT :start, :limit")
    suspend fun getAllGamesByASCTitle(start: Int, limit: Int) : List<PublicsEntity>

    @Query("SELECT * FROM publicsentity ORDER BY id ASC LIMIT :start, :limit")
    suspend fun getAllGamesByASCID(start: Int, limit: Int) : List<PublicsEntity>



    *//*@Query("SELECT * FROM publicsentity ORDER BY :sortBy DESC LIMIT :start, :limit")
    suspend fun getAllGamesDESC(start: Int, limit: Int, sortBy: String) : List<PublicsEntity>*//*

    @Query("SELECT * FROM publicsentity ORDER BY followers DESC LIMIT :start, :limit")
    suspend fun getAllGamesDESCFollowers(start: Int, limit: Int) : List<PublicsEntity>

    @Query("SELECT * FROM publicsentity ORDER BY id DESC LIMIT :start, :limit")
    suspend fun getAllGamesDESCID(start: Int, limit: Int) : List<PublicsEntity>

    @Query("SELECT * FROM publicsentity ORDER BY postcount DESC LIMIT :start, :limit")
    suspend fun getAllGamesDESCPostCount(start: Int, limit: Int) : List<PublicsEntity>

    @Query("SELECT * FROM publicsentity ORDER BY numratings DESC LIMIT :start, :limit")
    suspend fun getAllGamesDESCNumRatings(start: Int, limit: Int) : List<PublicsEntity>



    @Query("SELECT * FROM publicsentity WHERE platforms LIKE '%'+:platform+'%' ORDER BY :sortBy ASC LIMIT :start, :limit")
    suspend fun getAllGamesByASCPlatform(start: Int, limit: Int, sortBy: String, platform: String) : List<PublicsEntity>

    @Query("SELECT * FROM publicsentity WHERE platforms LIKE '%'+:platform+'%' ORDER BY :sortBy DESC LIMIT :start, :limit")
    suspend fun getAllGamesDESCPlatform(start: Int, limit: Int, sortBy: String, platform: String) : List<PublicsEntity>*/


    @RawQuery
    suspend fun getGamesRaw(query: SimpleSQLiteQuery) : List<PublicsEntity>
    @RawQuery
    suspend fun getNumGamesFilterRaw(query: SimpleSQLiteQuery) : Int



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