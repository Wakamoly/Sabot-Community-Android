package com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.DashboardRepo
import com.lucidsoftworksllc.sabotcommunity.models.CurrentPublicsPOJO
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.CurrentPublicsModel
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.DashboardAdModelItem
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UsersOnlineModel2
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewModel
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.launch

class DashboardVM (
        private val repository: DashboardRepo
) : BaseViewModel(repository) {

    private val _ads : MutableLiveData<DataState<List<DashboardAdModelItem>>> = MutableLiveData()
    val ads: LiveData<DataState<List<DashboardAdModelItem>>>
        get() = _ads

    fun getAds(dUsername: String) = viewModelScope.launch {
        _ads.value = DataState.Loading
        _ads.value = repository.getAds(dUsername)
    }



    private val _feed : MutableLiveData<DataState<List<ProfilenewsRecycler>>> = MutableLiveData()
    val feed: LiveData<DataState<List<ProfilenewsRecycler>>>
        get() = _feed

    fun getDashboardFeed(page: Int, items: Int, dUsername: String, dUserid: Int, method: String) = viewModelScope.launch {
        _feed.value = DataState.Loading
        _feed.value = repository.getFeed(page, items, dUsername, dUserid, method)
    }



    private val _publics : MutableLiveData<DataState<List<CurrentPublicsModel>>> = MutableLiveData()
    val publics: LiveData<DataState<List<CurrentPublicsModel>>>
        get() = _publics

    fun getCurrentPublics(dUsername: String, filter: String) = viewModelScope.launch {
        _publics.value = DataState.Loading
        _publics.value = repository.getCurrentPublics(dUsername, filter)
    }



    private val _numOnline : MutableLiveData<DataState<UsersOnlineModel2>> = MutableLiveData()
    val numOnline: LiveData<DataState<UsersOnlineModel2>>
        get() = _numOnline

    fun getNumOnline(dUsername: String) = viewModelScope.launch {
        _numOnline.value = DataState.Loading
        _numOnline.value = repository.getNumOnline(dUsername)
    }



    fun setAdViewed(adID: Int, method: String, dUserID: Int, dUsername: String) = viewModelScope.launch {
        repository.setAdViewed(adID, method, dUserID, dUsername)
    }



}