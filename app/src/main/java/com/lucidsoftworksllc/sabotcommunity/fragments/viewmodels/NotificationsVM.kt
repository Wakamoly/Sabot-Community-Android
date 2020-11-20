package com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationCacheEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.NotificationsRepo
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewModel
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.launch

class NotificationsVM (
        private val repository: NotificationsRepo
) : BaseViewModel(repository) {

    private val _currentNotifications : MutableLiveData<DataState<List<NotificationCacheEntity>>> = MutableLiveData()
    val currentNotifications: LiveData<DataState<List<NotificationCacheEntity>>>
        get() = _currentNotifications

    fun getCurrentNotifications(dUsername: String, dUserID: Int, currentPage: Int, size: Int) = viewModelScope.launch {
        _currentNotifications.value = DataState.Loading
        if (repository.isNotificationsRetrievable()){
            _currentNotifications.value = repository.getNotificationsDB(currentPage, size)
        }else{
            _currentNotifications.value = repository.getNotificationsNetwork(dUserID, dUsername, currentPage, size, 0)
        }
    }

    private val _newNotifications : MutableLiveData<DataState<List<NotificationCacheEntity>>> = MutableLiveData()
    val newNotifications: LiveData<DataState<List<NotificationCacheEntity>>>
        get() = _newNotifications

    fun getNewNotifications(dUsername: String, dUserID: Int, currentPage: Int, size: Int, last_id: Int) = viewModelScope.launch {
        _newNotifications.value = repository.getNotificationsNetwork(dUserID, dUsername, currentPage, size, last_id)
    }

    fun setAllOpened(dUsername: String) = viewModelScope.launch {
        repository.setAllNotificationsOpened(dUsername)
    }


}