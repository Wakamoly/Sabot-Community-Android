package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewModel
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessagesViewModel
constructor(
        private val messagesRepository: MessagesRepository
        //@Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(messagesRepository) {

    private val _dataState: MutableLiveData<DataState<List<MessagesDataModel>>> = MutableLiveData()

    val dataState: LiveData<DataState<List<MessagesDataModel>>>
        get () = _dataState

    /*fun setStateEvent(mainStateEvent: MainStateEvent, dUsername: String, dUserID: Int){
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
                    // TODO: 10/31/20 Not implemented
                }
            }
        }
    }*/
}

sealed class MainStateEvent{
    object GetMessagesEvents: MainStateEvent()
    object None: MainStateEvent()
}