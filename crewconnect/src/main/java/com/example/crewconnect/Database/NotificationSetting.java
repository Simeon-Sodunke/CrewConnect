package com.example.crewconnect.Database;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NotificationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationSettingID;

    private boolean enabled;
    private String method;

    @ManyToOne
    private Employee user;
}
