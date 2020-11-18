package com.lucidsoftworksllc.sabotcommunity.others.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel (
    private val repository: BaseRepository
): ViewModel(){

    //suspend fun logout(api: UserApi) = repository.logout(api)

}
