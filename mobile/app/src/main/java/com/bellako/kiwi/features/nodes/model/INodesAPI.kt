package com.bellako.kiwi.features.nodes.model

import com.bellako.kiwi.features.nodes.data.NodesDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface INodesAPI {
    @GET("api/nodes")
    suspend fun getNodesForUser(): List<NodesDTO>

    @POST("api/nodes/{nodeId}/unlock")
    suspend fun unlockNode(
        @Path("nodeId") nodeId: Int,
    ): NodesDTO

    @POST("api/nodes/{nodeId}/complete")
    suspend fun completeNode(
        @Path("nodeId") nodeId: Int,
    ): NodesDTO

    @POST("api/nodes/{nodeId}/lock-next")
    suspend fun markNextNodesAsLocked(
        @Path("nodeId") nodeId: Int,
    ): List<NodesDTO>
}
