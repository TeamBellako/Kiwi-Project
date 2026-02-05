package com.bellako.kiwi.features.nodes.model

import com.bellako.kiwi.features.nodes.data.NodesDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface INodesAPI {
    @GET("api/nodes/{mapId}")
    suspend fun getNodesForMapId(
        @Path("mapId") mapId: Int,
    ): List<NodesDTO>

    @POST("api/nodes/{nodeId}/unlock")
    suspend fun unlockNode(
        @Path("nodeId") nodeId: Long,
    ): NodesDTO

    @POST("api/nodes/{nodeId}/complete")
    suspend fun completeNode(
        @Path("nodeId") nodeId: Long,
    ): List<NodesDTO>
}
