package com.kiwi.nodes;

import com.kiwi.features.nodes.controllers.NodesService;
import com.kiwi.features.nodes.controllers.NodesProgressService;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.exceptions.NodeLockedException;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import static com.kiwi.nodes.NodesTestFactory.*;

public class NodesServiceTests {

    private final NodesTestRepositoryInMemory nodeRepo = new NodesTestRepositoryInMemory();
    private final UserNodeStatusTestRepositoryInMemory statusRepo = new UserNodeStatusTestRepositoryInMemory();
    private final NodesProgressService progress = new NodesProgressService();

    private final NodesService service = new NodesService(nodeRepo, statusRepo, progress);

    @Test
    public void getExistingNode() {
        nodeRepo.save(persistenceNode(1L, 1, 100));

        var result = service.getNode(1L);
        assertEquals(1L, result.getId());
    }

    @Test(expected = NodeNotFoundException.class)
    public void getNonExistingNode() {
        service.getNode(99L);
    }

    @Test
    public void getNodesForUser() {
        nodeRepo.save(persistenceNode(1L, 1, 100));
        nodeRepo.save(persistenceNode(2L, 2, 200));

        var result = service.getNodesForUser(1L);
        assertEquals(2, result.size());
    }

    @Test
    public void unlockNodeSuccess() {
        int userId = 1L;

        nodeRepo.save(persistenceNode(1L, 1, 100));
        statusRepo.saveUserStatus(lockedStatus(userId, 1));

        var result = service.unlockNode(userId, 1L);

        assertEquals(NodeStatus.OPEN.name(), result.getStatus());
    }

    @Test(expected = NodeLockedException.class)
    public void unlockNodeFailsIfNotLocked() {
        int userId = 1L;

        nodeRepo.save(persistenceNode(1L, 1, 100));
        statusRepo.saveUserStatus(inaccessibleStatus(userId, 1));

        service.unlockNode(userId, 1L);
    }

    @Test(expected = NodeNotFoundException.class)
    public void unlockNodeNotFound() {
        service.unlockNode(1L, 99L);
    }

    @Test
    public void completeNodeSuccess() {
        int userId = 1;

        nodeRepo.save(persistenceNode(2, 2, 200));
        nodeRepo.saveUserStatus(openStatus(userId, 2));

        var result = service.completeNode(userId, 2L);

        assertEquals(NodeStatus.COMPLETED.name(), result.getStatus());
    }

    @Test(expected = NodeLockedException.class)
    public void completeLockedNodeFails() {
        int userId = 1;

        nodeRepo.save(persistenceNode(2, 2, 200));
        nodeRepo.saveUserStatus(lockedStatus(userId, 2));

        service.completeNode(userId, 2L);
    }

    @Test(expected = NodeNotFoundException.class)
    public void completeNonExistingNodeFails() {
        int userId = 1;
        service.completeNode(userId, 999L);
    }
}
