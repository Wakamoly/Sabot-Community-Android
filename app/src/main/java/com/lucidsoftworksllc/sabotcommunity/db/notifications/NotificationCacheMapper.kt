package com.lucidsoftworksllc.sabotcommunity.db.notifications

import com.lucidsoftworksllc.sabotcommunity.util.EntityMapper
import javax.inject.Inject

class NotificationCacheMapper
@Inject
constructor(): EntityMapper<NotificationCacheEntity, NotificationDataModel> {

    override fun mapFromEntity(entity: NotificationCacheEntity): NotificationDataModel {
        return NotificationDataModel(
                id = entity.id,
                user_to = entity.user_to,
                user_from = entity.user_from,
                message = entity.message,
                link = entity.link,
                datetime = entity.datetime,
                opened = entity.opened,
                viewed = entity.viewed,
                user_id = entity.user_id,
                profile_pic = entity.profile_pic,
                nickname = entity.nickname,
                verified = entity.verified,
                last_online = entity.last_online,
                type = entity.type,
                deleted = entity.deleted
        )
    }

    override fun mapToEntity(domainModel: NotificationDataModel): NotificationCacheEntity {
        return NotificationCacheEntity(
                id = domainModel.id,
                user_to = domainModel.user_to,
                user_from = domainModel.user_from,
                message = domainModel.message,
                link = domainModel.link,
                datetime = domainModel.datetime,
                opened = domainModel.opened,
                viewed = domainModel.viewed,
                user_id = domainModel.user_id,
                profile_pic = domainModel.profile_pic,
                nickname = domainModel.nickname,
                verified = domainModel.verified,
                last_online = domainModel.last_online,
                type = domainModel.type,
                deleted = domainModel.deleted
        )
    }

    fun mapFromEntityList(entities: List<NotificationCacheEntity>): List<NotificationDataModel>{
        return entities.map { mapFromEntity(it) }
    }


}