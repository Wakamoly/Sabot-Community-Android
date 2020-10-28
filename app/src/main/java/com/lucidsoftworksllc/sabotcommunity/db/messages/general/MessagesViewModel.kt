package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessagesViewModel
@ViewModelInject
constructor(
        private val messagesRepository: MessagesRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _dataState: MutableLiveData<DataState<List<MessagesDataModel>>> = MutableLiveData()

    val dataState: LiveData<DataState<List<MessagesDataModel>>>
        get () = _dataState

    fun setStateEvent(mainStateEvent: MainStateEvent, dUsername: String, dUserID: String){
        viewModelScope.launch {
            when(mainStateEvent){
                is MainStateEvent.GetMessagesEvents -> {
                    messagesRepository.getMessages(dUsername, dUserID)
                            .onEach { dataState ->
                                _dataState.value = dataState
                            }
                            .launchIn(viewModelScope)
                }
                is MainStateEvent.None -> {
                    //who cares
                }
            }
        }
    }
}

sealed class MainStateEvent{
    object GetMessagesEvents: MainStateEvent()
    object None: MainStateEvent()
}