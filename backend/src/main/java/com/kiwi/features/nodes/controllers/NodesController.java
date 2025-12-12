package com.kiwi.features.nodes.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.nodes.data.NodesDTO;
import com.kiwi.features.users.controllers.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nodes")
public class NodesController {

    private final NodesService nodesService;
    private final UsersService usersService;

    public NodesController(NodesService nodesService, UsersService usersService) {
        this.nodesService = nodesService;
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<List<NodesDTO>> getNodesForUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        List<NodesDTO> nodes = nodesService.getNodesForUser(userId);
        return ResponseEntity.ok(nodes);
    }

    @PostMapping("/{nodeId}/lock-next")
    public ResponseEntity<List<NodesDTO>> markNextNodesAsLocked(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int nodeId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        List<NodesDTO> nodes = nodesService.markNextNodesAsLocked(userId, nodeId);
        return ResponseEntity.ok(nodes);
    }

    @PostMapping("/{nodeId}/unlock")
    public ResponseEntity<NodesDTO> unlockNode(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int nodeId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        var node = nodesService.unlockNode(userId, nodeId);
        return ResponseEntity.ok(node);
    }

    @PostMapping("/{nodeId}/complete")
    public ResponseEntity<NodesDTO> completeNode(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int nodeId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        var node = nodesService.completeNode(userId, nodeId);
        return ResponseEntity.ok(node);
    }
}
