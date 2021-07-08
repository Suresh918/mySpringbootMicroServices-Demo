package com.example.mirai.libraries.notification.settings.repository;

import com.example.mirai.libraries.notification.settings.model.Settings;

import org.springframework.data.repository.CrudRepository;

public interface SettingsRepository extends CrudRepository<Settings, String> {
}
