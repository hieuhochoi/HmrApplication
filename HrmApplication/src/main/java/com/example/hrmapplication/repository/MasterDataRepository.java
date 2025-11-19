package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterDataRepository extends JpaRepository<MasterData, Long> {
    List<MasterData> findByDataType(String dataType);
    List<MasterData> findByDataTypeAndIsActiveTrue(String dataType);
    Optional<MasterData> findByDataTypeAndCode(String dataType, String code);
}

