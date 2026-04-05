package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.rest.dto.FineDto;
import sk.posam.fsa.isk.rest.dto.FineStatusDto;

@Component
public class FineMapper {

    private final MoneyMapper moneyMapper;

    public FineMapper(MoneyMapper moneyMapper) {
        this.moneyMapper = moneyMapper;
    }

    public FineDto toDto(Fine entity) {
        if (entity == null) {
            return null;
        }

        FineDto dto = new FineDto();
        dto.setAmount(moneyMapper.toDto(entity.getAmount()));
        dto.setReason(entity.getReason());
        dto.setStatus(FineStatusDto.valueOf(entity.getStatus().name()));
        return dto;
    }
}
