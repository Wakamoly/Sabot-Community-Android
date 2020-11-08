package com.lucidsoftworksllc.sabotcommunity.others.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.ConvosRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.DashboardRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.ProfileRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.UserMessageRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.ConvosVM
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.DashboardVM
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.ProfileVM
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.UserMessageVM

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
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}