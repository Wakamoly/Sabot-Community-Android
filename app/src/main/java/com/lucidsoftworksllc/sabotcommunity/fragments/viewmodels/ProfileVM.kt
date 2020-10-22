package com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.ProfileRepo
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.ProfileTopModel
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewModel
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.launch

class ProfileVM (
        private val repository: ProfileRepo
) : BaseViewModel(repository) {

    private val _profileTop : MutableLiveData<DataState<ProfileTopModel>> = MutableLiveData()
    val profileTop: LiveData<DataState<ProfileTopModel>>
        get() = _profileTop

    fun getProfileTop(dUserID: Int, profileUserId: Int, dUsername: String) = viewModelScope.launch {
        _profileTop.value = DataState.Loading
        _profileTop.value = repository.getProfileTop(dUserID, profileUserId, dUsername)
    }



    private val _profileNews : MutableLiveData<DataState<List<ProfilenewsRecycler>>> = MutableLiveData()
    val profileNews: LiveData<DataState<List<ProfilenewsRecycler>>>
        get() = _profileNews

    fun getProfileNews(dUserID: Int, profileUserId: Int, dUsername: String) = viewModelScope.launch {
        _profileNews.value = DataState.Loading
        _profileNews.value = repository.getProfileNews(dUserID, profileUserId, dUsername)
    }



    /*fun setAdViewed(adID: Int, method: String, dUserID: Int, dUsername: String) = viewModelScope.launch {
        repository.setAdViewed(adID, method, dUserID, dUsername)
    }*/



}