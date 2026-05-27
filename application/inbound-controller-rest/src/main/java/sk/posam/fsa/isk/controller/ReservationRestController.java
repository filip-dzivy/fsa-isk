package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;
import sk.posam.fsa.isk.domain.shared.Transactional;
import sk.posam.fsa.isk.mapper.ReservationMapper;
import sk.posam.fsa.isk.rest.api.ReservationsApi;
import sk.posam.fsa.isk.rest.dto.CreateReservationRequestDto;
import sk.posam.fsa.isk.rest.dto.ReservationDto;
import sk.posam.fsa.isk.security.CurrentUserDetailService;

import java.util.List;

@RestController
public class ReservationRestController implements ReservationsApi {
    private final ReservationFacade reservationFacade;
    private final ReservationMapper reservationMapper;
    private final CatalogFacade catalogFacade;
    private final CurrentUserDetailService currentUserDetailService;

    public ReservationRestController(ReservationFacade reservationFacade,
                                     ReservationMapper reservationMapper,
                                     CatalogFacade catalogFacade,
                                     CurrentUserDetailService currentUserDetailService) {
        this.reservationFacade = reservationFacade;
        this.reservationMapper = reservationMapper;
        this.catalogFacade = catalogFacade;
        this.currentUserDetailService = currentUserDetailService;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> cancelReservation(Long id) {
        Member currentMember = currentUserDetailService.getFullCurrentMember();
        reservationFacade.cancel(reservationFacade.find(id), currentMember);
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> createReservation(CreateReservationRequestDto dto) {
        Member requestingMember = currentUserDetailService.getFullCurrentMember();
        Book book = catalogFacade.find(new ISBN(dto.getIsbn()));
        reservationFacade.create(requestingMember, dto.getMemberId(), book);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReservationDto>> getAllReservations(Long memberId) {
        Member requestingMember = currentUserDetailService.getFullCurrentMember();
        return ResponseEntity.ok(reservationMapper.toDto(reservationFacade.findVisible(requestingMember, memberId)));
    }
}
