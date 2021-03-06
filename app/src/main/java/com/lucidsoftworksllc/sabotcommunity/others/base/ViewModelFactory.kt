package com.lucidsoftworksllc.sabotcommunity.others.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.*
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.*

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(ProfileVM::class.java) -> ProfileVM(repository as ProfileRepo) as T
            modelClass.isAssignableFrom(DashboardVM::class.java) -> DashboardVM(repository as DashboardRepo) as T
            modelClass.isAssignableFrom(UserMessageVM::class.java) -> UserMessageVM(repository as UserMessageRepo) as T
            modelClass.isAssignableFrom(ConvosVM::class.java) -> ConvosVM(repository as ConvosRepo) as T
            modelClass.isAssignableFrom(NotificationsVM::class.java) -> NotificationsVM(repository as NotificationsRepo) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}