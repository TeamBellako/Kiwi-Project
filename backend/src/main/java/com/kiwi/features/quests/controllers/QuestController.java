package com.kiwi.features.quests.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.quests.data.QuestDTO;
import com.kiwi.features.quests.data.SubquestDTO;
import com.kiwi.features.localization.LocalizedDataService;
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
        private final LocalizedDataService localizedDataService;

        public QuestController(QuestService questService, UsersService usersService, LocalizedDataService localizedDataService) {
        this.questService = questService;
        this.usersService = usersService;
                this.localizedDataService = localizedDataService;
    }

    // ============================================================================================
    // GET USER QUESTS
    // ============================================================================================

    @GetMapping("/active")
    public ResponseEntity<List<QuestDTO>> getActiveQuests(
            @AuthenticationPrincipal @NotNull UserDetails userDetails
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        List<QuestDTO> quests = localizedDataService.localizeQuests(
                questService.getActiveQuestsForUser(userId));
        return ResponseEntity.ok(quests);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<QuestDTO>> getCompletedQuests(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        List<QuestDTO> quests = localizedDataService.localizeQuests(
                questService.getCompletedQuestsForUser(userId));
        return ResponseEntity.ok(quests);
    }

    // ============================================================================================
    // GET QUEST FOR USER
    // ============================================================================================

    @GetMapping("/{questId}")
    public ResponseEntity<QuestDTO> getQuest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int questId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        QuestDTO quest = localizedDataService.localizeQuests(
                List.of(questService.getQuestForUser(userId, questId))).get(0);
        return ResponseEntity.ok(quest);
    }

    // ============================================================================================
    // GET SUBQUEST FOR USER
    // ============================================================================================

    @GetMapping("/subquests/{subquestId}")
    public ResponseEntity<SubquestDTO> getSubquest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int subquestId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        SubquestDTO subquest = localizedDataService.localizeSubquest(
                questService.getSubquestForUser(userId, subquestId));

        return ResponseEntity.ok(subquest);
    }

    // ============================================================================================
    // GIVE QUEST
    // ============================================================================================

    @PostMapping("/{questId}/give")
    public ResponseEntity<QuestDTO> giveQuest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int questId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        QuestDTO quest = localizedDataService.localizeQuests(
                List.of(questService.giveQuestToUser(userId, questId))).get(0);
        return ResponseEntity.ok(quest);
    }

    // ============================================================================================
    // COMPLETE / FAIL SUBQUEST
    // ============================================================================================

    @PostMapping("/subquests/{subquestId}/complete")
    public ResponseEntity<QuestDTO> completeSubquest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int subquestId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        QuestDTO result = localizedDataService.localizeQuests(
                List.of(questService.completeSubquest(userId, subquestId))).get(0);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subquests/{subquestId}/fail")
    public ResponseEntity<QuestDTO> failSubquest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int subquestId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        QuestDTO result = localizedDataService.localizeQuests(
                List.of(questService.failSubquest(userId, subquestId))).get(0);
        return ResponseEntity.ok(result);
    }
}
