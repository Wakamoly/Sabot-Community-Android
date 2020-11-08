package com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MainStateEvent
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDataModel
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.ConvosRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.UserMessageRepo
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessageData
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessagesFromID
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewModel
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ConvosVM (
        private val repository: ConvosRepo
) : BaseViewModel(repository) {

    private val _messageUnits : MutableLiveData<DataState<List<MessagesCacheEntity>>> = MutableLiveData()
    val messageUnits: LiveData<DataState<List<MessagesCacheEntity>>>
        get() = _messageUnits

    fun getMessageUnits(dUsername: String, dUserID: Int){
        viewModelScope.launch {
            repository.getMessages(dUsername, dUserID)
                    .onEach { dataState ->
                        _messageUnits.value = dataState
                    }
                    .launchIn(viewModelScope)

        }
    }

}