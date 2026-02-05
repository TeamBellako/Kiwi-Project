package com.kiwi.nodes;

import com.kiwi.features.nodes.controllers.NodesService;
import com.kiwi.features.nodes.controllers.NodesProgressService;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.exceptions.NodeInaccessibleException;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.kiwi.nodes.NodesTestFactory.*;

public class NodesServiceTests {

    private final NodesTestRepositoryInMemory nodeRepo =
            new NodesTestRepositoryInMemory();

    private final UserNodeStatusTestRepositoryInMemory statusRepo =
            new UserNodeStatusTestRepositoryInMemory();

    private final NodesProgressService progress =
            new NodesProgressService();

    private final NodesService service =
            new NodesService(nodeRepo, statusRepo, progress);

    private final Long userId = 1L;

    @Test
    public void getNodesForUser() {

        var node1 =  persistenceNode( 1L , 0);
        var node2 = persistenceNode( 2L, 0);
        var node3 = persistenceNode( 3L, 0);

        nodeRepo.saveAndFlush(node1);
        nodeRepo.saveAndFlush(node2);
        nodeRepo.saveAndFlush(node3);

        statusRepo.saveUserStatus(openStatus(userId, node1.getId()));
        statusRepo.saveUserStatus(lockedStatus(userId, node2.getId()));

        var result = service.getNodesForUser(userId);
        assertEquals(2, result.size());
    }

    @Test
    public void lockNode_success() {
        var node =  nodeRepo.saveAndFlush(persistenceNode( 1L, 0));
        var result = service.lockNode(userId, node.getId());

        assertEquals(NodeStatus.LOCKED.name(), result.getStatus());
    }

    @Test
    public void unlockNode_success() {
        var node =  nodeRepo.saveAndFlush(persistenceNode( 1L, 0));
        statusRepo.saveUserStatus(lockedStatus(userId, node.getId()));

        var result = service.unlockNode(userId, node.getId());
        assertEquals(NodeStatus.OPEN.name(), result.getStatus());
    }

    @Test(expected = NodeInaccessibleException.class)
    public void unlockNode_failsIfNoStatus() {
        var node =  nodeRepo.saveAndFlush(persistenceNode( 1L, 0));
        service.unlockNode(userId, node.getId());
    }

    @Test(expected = NodeNotFoundException.class)
    public void unlockNode_notFound() {
        service.unlockNode(userId, 99L);
    }

    @Test
    public void completeNode_success() {
        var node =  nodeRepo.saveAndFlush(persistenceNode( 2L, 0));
        statusRepo.saveUserStatus(openStatus(userId, node.getId()));

        var result = service.completeNode(userId, node.getId());

        assertNotNull(result);
        assertEquals(1, result.size()); // no edges, just the completed node
    }

    @Test(expected = IllegalStateException.class)
    public void completeNode_lockedFails() {
        var node =  nodeRepo.saveAndFlush(persistenceNode( 2L, 0));
        statusRepo.saveUserStatus(lockedStatus(userId, node.getId()));

        service.completeNode(userId, node.getId());
    }

    @Test(expected = NodeNotFoundException.class)
    public void completeNode_nonExistingFails() {
        service.completeNode(userId, 999L);
    }
}
