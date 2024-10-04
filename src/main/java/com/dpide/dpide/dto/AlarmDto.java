package com.dpide.dpide.dto;

import lombok.*;

import java.util.List;

public class AlarmDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InviteReq {
        private Long projectId;
        private String email;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmInfo {
        private Long id;
        private String senderName;
        private String projectName;
        private boolean isRead;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmRes {
        private List<AlarmInfo> alarmInfoList;
    }
}
