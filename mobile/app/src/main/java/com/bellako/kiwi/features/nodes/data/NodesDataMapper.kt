package com.bellako.kiwi.features.nodes.data

import android.os.Build
import androidx.annotation.RequiresApi

object NodesDataMapper {
    fun toDomain(dto: NodesDTO): NodesDomain =
        NodesDomain(
            id = dto.id,
            order = dto.order,
            status = NodeStatus.valueOf(dto.status),
            price = dto.price,
            posX = dto.posX,
            posY = dto.posY,
        )

    fun toDTO(domain: NodesDomain): NodesDTO =
        NodesDTO(
            id = domain.id,
            order = domain.order,
            status = domain.status.name,
            price = domain.price,
            posX = domain.posX,
            posY = domain.posY,
        )
}
