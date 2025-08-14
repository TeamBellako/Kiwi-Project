package com.kiwi.features.personality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user/personality")
public class PersonalityController {

    private final PersonalityService personalityService;

    @Autowired
    public PersonalityController(PersonalityService personalityService) {
        this.personalityService = personalityService;
    }

    @GetMapping("")
    public ResponseEntity<?> getPersonality(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(personalityService.getPersonality(userDetails.getUsername()).toDTO());
    }

    @PostMapping("/realName")
    public ResponseEntity<?> updateRealName(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserNameDTO userNameDTO) {
        return ResponseEntity.ok(personalityService.updateRealName(userDetails.getUsername(), userNameDTO));
    }

    @PostMapping("/knightName")
    public ResponseEntity<?> updateKnightName(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserNameDTO userNameDTO) {
        return ResponseEntity.ok(personalityService.updateKnightName(userDetails.getUsername(), userNameDTO));
    }

    @PostMapping("/build")
    public ResponseEntity<?> updateBuild(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BuildDTO buildDTO) {
        return ResponseEntity.ok(personalityService.updateBuild(userDetails.getUsername(), buildDTO));
    }

    @PostMapping("/apps")
    public ResponseEntity<?> updateBuild(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AppsDTO appsDTO) {
        return ResponseEntity.ok(personalityService.updateApps(userDetails.getUsername(), appsDTO));
    }

}
