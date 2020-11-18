package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import com.lucidsoftworksllc.sabotcommunity.util.EntityMapper
import javax.inject.Inject

class MessagesCacheMapper : EntityMapper<MessagesCacheEntity, MessagesDataModel> {

    override fun mapFromEntity(entity: MessagesCacheEntity): MessagesDataModel {
        return MessagesDataModel(
                id = entity.id,
                sent_by = entity.sent_by,
                body_split = entity.body_split,
                time_message = entity.time_message,
                latest_profile_pic = entity.latest_profile_pic,
                profile_pic = entity.profile_pic,
                nickname = entity.nickname,
                verified = entity.verified,
                last_online = entity.last_online,
                viewed = entity.viewed,
                message_id = entity.message_id,
                user_from = entity.user_from,
                type = entity.type,
                group_id = entity.group_id,
                loaded = entity.loaded
        )
    }

    override fun mapToEntity(domainModel: MessagesDataModel): MessagesCacheEntity {
        return MessagesCacheEntity(
                id = domainModel.id,
                sent_by = domainModel.sent_by,
                body_split = domainModel.body_split,
                time_message = domainModel.time_message,
                latest_profile_pic = domainModel.latest_profile_pic,
                profile_pic = domainModel.profile_pic,
                nickname = domainModel.nickname,
                verified = domainModel.verified,
                last_online = domainModel.last_online,
                viewed = domainModel.viewed,
                message_id = domainModel.message_id,
                user_from = domainModel.user_from,
                type = domainModel.type,
                group_id = domainModel.group_id,
                loaded = domainModel.loaded
        )
    }

    fun mapFromEntityList(entities: List<MessagesCacheEntity>): List<MessagesDataModel>{
        return entities.map { mapFromEntity(it) }
    }


}