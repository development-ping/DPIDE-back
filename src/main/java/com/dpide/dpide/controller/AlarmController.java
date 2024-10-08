package com.dpide.dpide.controller;

import com.dpide.dpide.dto.AlarmDto;
import com.dpide.dpide.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/alarm")
@RestController
public class AlarmController {
    private final AlarmService alarmService;

    @PostMapping
    public ResponseEntity<Void> makeInviteAlarm(@RequestHeader("Authorization") String token,
                                              @RequestBody AlarmDto.InviteReq req) {
        log.info("CALL: AlarmController.makeInviteAlarm");
        alarmService.makeInviteAlarm(req, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<AlarmDto.AlarmRes> getAlarms(@RequestHeader("Authorization") String token) {
        log.info("CALL: AlarmController.getAlarms");
        return ResponseEntity.ok(alarmService.getAlarms(token));
    }

    @PutMapping("/{alarmId}/deny")
    public ResponseEntity<Void> denyInvite(@PathVariable Long alarmId) {
        log.info("CALL: AlarmController.denyInvite");
        alarmService.readAlarm(alarmId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{alarmId}/accept")
    public ResponseEntity<Void> acceptInvite(@PathVariable Long alarmId) {
        log.info("CALL: AlarmController.acceptInvite");
        alarmService.acceptAlarm(alarmId);
        return ResponseEntity.ok().build();
    }
}
