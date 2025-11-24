package com.kiwi.features.quests.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.quests.data.QuestDTO;
import com.kiwi.features.quests.data.SubquestResultDTO;
import com.kiwi.features.users.controllers.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
public class QuestController {

    private final QuestService questService;
    private final UsersService usersService;

    public QuestController(QuestService questService, UsersService usersService) {
        this.questService = questService;
        this.usersService = usersService;
    }

    // ============================================================================================
    // GET USER QUESTS
    // ============================================================================================

    @GetMapping("/active")
    public ResponseEntity<List<QuestDTO>> getActiveQuests(
            @AuthenticationPrincipal @NotNull UserDetails userDetails
    ) {
        int userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        List<QuestDTO> quests = questService.getActiveQuestsForUser(userId);
        return ResponseEntity.ok(quests);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<QuestDTO>> getCompletedQuests(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        int userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        List<QuestDTO> quests = questService.getCompletedQuestsForUser(userId);
        return ResponseEntity.ok(quests);
    }

    // ============================================================================================
    // GIVE QUEST
    // ============================================================================================

    @PostMapping("/{questId}/give")
    public ResponseEntity<QuestDTO> giveQuest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int questId
    ) {
        int userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        QuestDTO quest = questService.giveQuestToUser(userId, questId);
        return ResponseEntity.ok(quest);
    }

    // ============================================================================================
    // COMPLETE / FAIL SUBQUEST
    // ============================================================================================

    @PostMapping("/subquests/{subquestId}/complete")
    public ResponseEntity<SubquestResultDTO> completeSubquest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int subquestId
    ) {
        int userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        SubquestResultDTO result = questService.completeSubquest(userId, subquestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subquests/{subquestId}/fail")
    public ResponseEntity<SubquestResultDTO> failSubquest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int subquestId
    ) {
        int userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        SubquestResultDTO result = questService.failSubquest(userId, subquestId);
        return ResponseEntity.ok(result);
    }
}
