package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.PositionRequest;
import com.example.hrmapplication.dto.PositionResponse;
import com.example.hrmapplication.entity.Position;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public Position toEntity(PositionRequest request) {
        if (request == null) {
            return null;
        }
        Position position = new Position();
        position.setId(request.getId());
        applyCommonFields(position, request);
        return position;
    }

    public PositionResponse toResponse(Position position) {
        if (position == null) {
            return null;
        }
        return PositionResponse.builder()
                .id(position.getId())
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .baseSalary(position.getBaseSalary())
                .description(position.getDescription())
                .build();
    }

    public PositionRequest toRequest(Position position) {
        if (position == null) {
            return null;
        }
        PositionRequest request = new PositionRequest();
        request.setId(position.getId());
        request.setPositionCode(position.getPositionCode());
        request.setPositionName(position.getPositionName());
        request.setBaseSalary(position.getBaseSalary());
        request.setDescription(position.getDescription());
        return request;
    }

    public void updateEntity(Position entity, PositionRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Position target, PositionRequest request) {
        target.setPositionCode(request.getPositionCode());
        target.setPositionName(request.getPositionName());
        target.setBaseSalary(request.getBaseSalary());
        target.setDescription(request.getDescription());
    }
}

