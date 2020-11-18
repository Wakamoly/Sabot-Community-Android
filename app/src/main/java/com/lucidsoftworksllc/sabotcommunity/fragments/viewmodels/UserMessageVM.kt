package com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.UserMessageRepo
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.*
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


    private val _userMessages : MutableLiveData<DataState<UserMessageData>> = MutableLiveData()
    val userMessages: LiveData<DataState<UserMessageData>>
        get() = _userMessages

    fun getUserMessages(userTo: String, dUsername: String, dUserID: Int) = viewModelScope.launch {
        _userMessages.value = DataState.Loading
        if (repository.isUserMessagesRetrievable(userTo, dUsername)){
            _userMessages.value = repository.getUserMessagesDB(userTo, dUsername)
        }
        _userMessages.value = repository.getUserMessagesNET(userTo, dUsername, dUserID)
    }


    private val _newUserMessages : MutableLiveData<DataState<UserMessagesFromID>> = MutableLiveData()
    val newUserMessages: LiveData<DataState<UserMessagesFromID>>
        get() = _newUserMessages

    fun getNewUserMessages(userTo: String, dUsername: String, dUserID: Int, lastId: Int) = viewModelScope.launch {
        _newUserMessages.value = repository.getNewUserMessagesNET(userTo, dUsername, dUserID, lastId)
    }


    // TODO: 10/30/20 MERGE IMAGE UPLOAD WITH SEND
    private val _sentMessage : MutableLiveData<DataState<UserMessageData>> = MutableLiveData()
    val sentMessage: LiveData<DataState<UserMessageData>>
        get() = _sentMessage

    fun sendMessage(userTo: String, dUsername: String, dUserID: Int, message: String, image: Bitmap?) = viewModelScope.launch {
        _sentMessage.value = DataState.Loading
        _sentMessage.value = repository.sendMessage(userTo, dUsername, dUserID, message, image)
    }




}