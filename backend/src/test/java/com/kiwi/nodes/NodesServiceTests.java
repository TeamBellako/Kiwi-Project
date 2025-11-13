package com.kiwi.nodes;

import com.kiwi.features.nodes.controllers.NodesService;
import com.kiwi.features.nodes.controllers.NodesProgressService;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.kiwi.nodes.NodesTestFactory.*;

public class NodesServiceTests {

    private final NodesTestRepositoryInMemory nodeRepo = new NodesTestRepositoryInMemory();
    private final UserNodeStatusTestRepositoryInMemory statusRepo = new UserNodeStatusTestRepositoryInMemory();
    private final NodesProgressService progress = new NodesProgressService();

    private final NodesService service = new NodesService(nodeRepo, statusRepo, progress);

    private final int userId = 1;

    @Test
    public void getNodesForUser_returnsAllNodes() {
        nodeRepo.saveAndFlush(persistenceNode(1, 1, 100, 0, 0));
        nodeRepo.saveAndFlush(persistenceNode(2, 2, 200, 0, 0));

        var result = service.getNodesForUser(userId);
        assertEquals(2, result.size());
    }

    @Test
    public void unlockNode_success() {
        nodeRepo.saveAndFlush(persistenceNode(1, 1, 100, 0, 0));
        statusRepo.saveUserStatus(lockedStatus(userId, 1));

        var result = service.unlockNode(userId, 1);
        assertEquals(NodeStatus.OPEN.name(), result.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void unlockNode_failsIfNotLocked() {
        nodeRepo.saveAndFlush(persistenceNode(1, 1, 100, 0, 0));
        statusRepo.saveUserStatus(inaccessibleStatus(userId, 1));

        service.unlockNode(userId, 1);
    }

    @Test(expected = NodeNotFoundException.class)
    public void unlockNode_notFound() {
        service.unlockNode(userId, 99);
    }

    @Test
    public void completeNode_success() {
        nodeRepo.saveAndFlush(persistenceNode(2, 2, 200, 0, 0));
        statusRepo.saveUserStatus(openStatus(userId, 2));

        var result = service.completeNode(userId, 2);
        assertEquals(NodeStatus.COMPLETED.name(), result.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void completeNode_lockedFails() {
        nodeRepo.saveAndFlush(persistenceNode(2, 2, 200, 0, 0));
        statusRepo.saveUserStatus(lockedStatus(userId, 2));

        service.completeNode(userId, 2);
    }

    @Test(expected = NodeNotFoundException.class)
    public void completeNode_nonExistingFails() {
        service.completeNode(userId, 999);
    }
}
