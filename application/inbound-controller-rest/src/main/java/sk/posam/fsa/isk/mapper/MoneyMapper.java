package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.membership.Money;
import sk.posam.fsa.isk.rest.dto.MoneyDto;

import java.math.BigDecimal;

@Component
public class MoneyMapper {

    public MoneyDto toDto(Money entity) {
        if (entity == null) {
            return null;
        }

        MoneyDto dto = new MoneyDto();
        dto.setAmount(entity.getAmount().doubleValue());
        dto.setCurrency(entity.getCurrency());
        return dto;
    }

    public Money toMoney(MoneyDto dto) {
        if (dto == null) {
            return null;
        }

        return Money.of(dto.getAmount(), dto.getCurrency());
    }
}
