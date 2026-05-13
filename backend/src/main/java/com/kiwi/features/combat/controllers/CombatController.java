package com.kiwi.features.combat.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.combat.data.dto.CombatDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.users.controllers.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/combat")
public class CombatController {

    private final CombatFacadeService combatFacadeService;
    private final UsersService usersService;

    public CombatController(
            CombatFacadeService combatFacadeService,
            UsersService usersService
    ) {
        this.combatFacadeService = combatFacadeService;
        this.usersService = usersService;
    }

    // ============================================================================================
    // START OR RESUME COMBAT
    // ============================================================================================

    @PostMapping("/start/{combatConfigId}")
    public ResponseEntity<CombatDTO> startCombat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long combatConfigId
    ) {

        Long userId = usersService.getUserByEmail(
                        new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(
                combatFacadeService.startOrResumeCombat(userId, combatConfigId)
        );
    }

    // ============================================================================================
    // GET ACTIVE COMBAT
    // ============================================================================================

    @GetMapping("/active")
    public ResponseEntity<CombatDTO> getActiveCombat(
            @AuthenticationPrincipal @NotNull UserDetails userDetails
    ) {

        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return combatFacadeService.getActiveCombat(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // ============================================================================================
    // EXECUTE TURN
    // ============================================================================================

    @PostMapping("/{combatId}/skill/{skillId}")
    public ResponseEntity<CombatTurnResultDTO> executeTurn(
            @AuthenticationPrincipal @NotNull UserDetails userDetails,
            @PathVariable Long combatId,
            @PathVariable Long skillId
    ) {

        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(
                combatFacadeService.executeTurn(userId, combatId, skillId)
        );
    }

    // ============================================================================================
    // TIME OUT
    // ============================================================================================

    @PostMapping("/{combatId}/timeout")
    public ResponseEntity<CombatTurnResultDTO> timeOutCombat(
            @AuthenticationPrincipal @NotNull UserDetails userDetails,
            @PathVariable Long combatId
    ) {

        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(
                combatFacadeService.timeOut(userId, combatId)
        );
    }

    // ============================================================================================
    // TIME OUT
    // ============================================================================================

    @PostMapping("/{combatId}/abandon")
    public ResponseEntity<CombatTurnResultDTO> abandonCombat(
            @AuthenticationPrincipal @NotNull UserDetails userDetails,
            @PathVariable Long combatId
    ) {

        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(
                combatFacadeService.abandon(userId, combatId)
        );
    }

    // ============================================================================================
    // MARK BARK FIRED
    // ============================================================================================

    @PostMapping("/{combatId}/barks/{triggerId}/fired")
    public ResponseEntity<Void> markBarkFired(
            @AuthenticationPrincipal @NotNull UserDetails userDetails,
            @PathVariable Long combatId,
            @PathVariable Long triggerId
    ) {

        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        combatFacadeService.markBarkFired(userId, combatId, triggerId);

        return ResponseEntity.noContent().build();
    }

}