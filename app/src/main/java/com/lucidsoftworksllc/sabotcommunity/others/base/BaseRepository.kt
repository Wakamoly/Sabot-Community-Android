package com.lucidsoftworksllc.sabotcommunity.others.base

import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
            apiCall: suspend () -> T
    ) : DataState<T>{
        return withContext(Dispatchers.IO){
            try {
                DataState.Success(apiCall.invoke())
            }catch(throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        DataState.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        DataState.Failure(true, null, null)
                    }
                }
            }
        }
    }

    /*suspend fun logout(api: UserApi) = safeApiCall {
        api.logout()
    }*/
}