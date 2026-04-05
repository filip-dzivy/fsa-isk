package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.rest.dto.ReservationDto;
import sk.posam.fsa.isk.rest.dto.ReservationStatusDto;

import java.util.List;

@Component
public class ReservationMapper {

    private final MemberMapper memberMapper;
    private final BookMapper bookMapper;

    public ReservationMapper(MemberMapper memberMapper, BookMapper bookMapper) {
        this.memberMapper = memberMapper;
        this.bookMapper = bookMapper;
    }

    public ReservationDto toDto(Reservation entity) {
        if (entity == null) {
            return null;
        }

        ReservationDto dto = new ReservationDto();
        dto.setId(entity.getId());
        dto.setCreatedBy(memberMapper.toDtoWithoutFines(entity.getCreatedBy()));
        dto.setBook(bookMapper.toDto(entity.getBook()));
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setStatus(ReservationStatusDto.valueOf(entity.getStatus().name()));
        dto.setPositionInQueue(entity.getPositionInQueue());
        return dto;
    }

    public List<ReservationDto> toDto(List<Reservation> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}
