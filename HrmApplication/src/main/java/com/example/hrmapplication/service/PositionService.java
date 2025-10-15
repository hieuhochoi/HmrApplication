package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Position;
import com.example.hrmapplication.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));
    }

    public Position save(Position position) {
        return positionRepository.save(position);
    }

    public void delete(Long id) {
        positionRepository.deleteById(id);
    }
}
