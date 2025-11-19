package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.MasterData;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.MasterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MasterDataService {

    @Autowired
    private MasterDataRepository masterDataRepository;

    public List<MasterData> findAll() {
        return masterDataRepository.findAll();
    }

    public MasterData findById(Long id) {
        return masterDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dữ liệu với ID: " + id));
    }

    public List<MasterData> findByDataType(String dataType) {
        return masterDataRepository.findByDataType(dataType);
    }

    public List<MasterData> findActiveByDataType(String dataType) {
        return masterDataRepository.findByDataTypeAndIsActiveTrue(dataType);
    }

    public MasterData findByDataTypeAndCode(String dataType, String code) {
        return masterDataRepository.findByDataTypeAndCode(dataType, code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dữ liệu với type: " + dataType + " và code: " + code));
    }

    public MasterData save(MasterData masterData) {
        return masterDataRepository.save(masterData);
    }

    public MasterData update(Long id, MasterData masterData) {
        MasterData existing = findById(id);
        existing.setName(masterData.getName());
        existing.setDescription(masterData.getDescription());
        existing.setMetadata(masterData.getMetadata());
        existing.setIsActive(masterData.getIsActive());
        existing.setDisplayOrder(masterData.getDisplayOrder());
        return save(existing);
    }

    public void delete(Long id) {
        if (!masterDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy dữ liệu với ID: " + id);
        }
        masterDataRepository.deleteById(id);
    }

    public void deactivate(Long id) {
        MasterData masterData = findById(id);
        masterData.setIsActive(false);
        save(masterData);
    }
}

