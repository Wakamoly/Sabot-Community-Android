package com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.UserMessageRepo
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.CurrentPublicsModel
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.DashboardAdModelItem
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UsersOnlineModel2
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewModel
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.launch

class UserMessageVM (
        private val repository: UserMessageRepo
) : BaseViewModel(repository) {

    private val _typedMessage : MutableLiveData<String> = MutableLiveData()
    val typedMessage: LiveData<String>
        get() = _typedMessage

    fun getTypedMessage(userTo: String) = viewModelScope.launch {
        _typedMessage.value = repository.getTypedMessage(userTo)
    }

    fun setTypedMessage(string: String, userTo: String) = viewModelScope.launch {
        repository.setTypedMessage(string, userTo)
    }

    private val _userInfo : MutableLiveData<DataState<MessageUserInfoEntity>> = MutableLiveData()
    val userInfo: LiveData<DataState<MessageUserInfoEntity>>
        get() = _userInfo

    fun getUserInfo(userTo: String, dUsername: String, dUserID: Int) = viewModelScope.launch {
        _userInfo.value = DataState.Loading
        if (repository.isUserInfoRetrievable(userTo)){
            _userInfo.value = repository.getUserInfoDB(userTo)
        }
        _userInfo.value = repository.getUserInfoNET(userTo, dUsername, dUserID)
    }




}