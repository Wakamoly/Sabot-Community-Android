package com.lucidsoftworksllc.sabotcommunity.db.notifications

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotificationViewModel
@ViewModelInject
constructor(
        private val notiRepository: NotificationRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _dataState: MutableLiveData<DataState<List<NotificationDataModel>>> = MutableLiveData()

    val dataState: LiveData<DataState<List<NotificationDataModel>>>
        get () = _dataState

    fun setStateEvent(mainStateEvent: MainStateEvent, mContext: Context){
        viewModelScope.launch {
            when(mainStateEvent){
                is MainStateEvent.GetNotiEvents -> {
                    notiRepository.getNotification(mContext)
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
    object GetNotiEvents: MainStateEvent()
    object None: MainStateEvent()
}