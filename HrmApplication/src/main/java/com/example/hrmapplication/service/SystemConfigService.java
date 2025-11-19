package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.SystemConfig;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public List<SystemConfig> findAll() {
        return systemConfigRepository.findAll();
    }

    public SystemConfig findById(Long id) {
        return systemConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cấu hình với ID: " + id));
    }

    public Optional<SystemConfig> findByKey(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey);
    }

    public String getConfigValue(String configKey) {
        return findByKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }

    public String getConfigValue(String configKey, String defaultValue) {
        return findByKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }

    public List<SystemConfig> findByType(String configType) {
        return systemConfigRepository.findByConfigType(configType);
    }

    public List<SystemConfig> findActiveConfigs() {
        return systemConfigRepository.findByIsActiveTrue();
    }

    public SystemConfig save(SystemConfig systemConfig) {
        return systemConfigRepository.save(systemConfig);
    }

    public SystemConfig saveOrUpdate(String configKey, String configValue, String configType, String description) {
        Optional<SystemConfig> existing = findByKey(configKey);
        if (existing.isPresent()) {
            SystemConfig config = existing.get();
            config.setConfigValue(configValue);
            config.setDescription(description);
            return save(config);
        } else {
            SystemConfig newConfig = new SystemConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setConfigType(configType);
            newConfig.setDescription(description);
            return save(newConfig);
        }
    }

    public SystemConfig update(Long id, SystemConfig systemConfig) {
        SystemConfig existing = findById(id);
        existing.setConfigValue(systemConfig.getConfigValue());
        existing.setDescription(systemConfig.getDescription());
        existing.setIsActive(systemConfig.getIsActive());
        return save(existing);
    }

    public void delete(Long id) {
        if (!systemConfigRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy cấu hình với ID: " + id);
        }
        systemConfigRepository.deleteById(id);
    }
}

