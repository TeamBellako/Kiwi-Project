package com.kiwi.features.skills.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.goals.data.GoalDTO;
import com.kiwi.features.skills.data.EquipSkillDTO;
import com.kiwi.features.skills.data.SkillDTO;
import com.kiwi.features.users.controllers.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;
    private final UsersService usersService;

    public SkillController(SkillService skillService, UsersService usersService) {
        this.skillService = skillService;
        this.usersService = usersService;
    }

    // ============================================================================================
    // GET USER SKILLS
    // ============================================================================================

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllSkills(
            @AuthenticationPrincipal @NotNull UserDetails userDetails
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.getAllSkillsForUser(userId));
    }

    // ============================================================================================
    // GIVE / LEVEL UP SKILL
    // ============================================================================================

    @PostMapping("/{skillId}/give")
    public ResponseEntity<SkillDTO> giveSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long skillId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.giveSkillToUser(userId, skillId));
    }

    @PostMapping("/{skillId}/levelup")
    public ResponseEntity<SkillDTO> levelUpSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long skillId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.levelUpSkill(userId, skillId));
    }

    // ============================================================================================
    // COOLDOWN
    // ============================================================================================

    @PostMapping("/{skillId}/cooldown")
    public ResponseEntity<SkillDTO> putOnCooldown(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long skillId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.putSkillOnCooldown(userId, skillId));
    }

    @PostMapping("/{skillId}/ready")
    public ResponseEntity<SkillDTO> removeCooldown(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long skillId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.removeCooldown(userId, skillId));
    }

    // ============================================================================================
    // EQUIP
    // ============================================================================================

    @PostMapping("/{skillId}/equip")
    public ResponseEntity<SkillDTO> equipSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long skillId,
            @RequestBody EquipSkillDTO equipSkillDTO
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.equipSkill(userId, skillId, equipSkillDTO));
    }

    @PostMapping("/{skillId}/unequip")
    public ResponseEntity<SkillDTO> unequipSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long skillId
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(skillService.unequipSkill(userId, skillId));
    }


}
