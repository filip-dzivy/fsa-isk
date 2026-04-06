package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.mapper.ReservationMapper;
import sk.posam.fsa.isk.rest.api.ReservationsApi;
import sk.posam.fsa.isk.rest.dto.CreateReservationRequestDto;
import sk.posam.fsa.isk.rest.dto.ReservationDto;
import sk.posam.fsa.isk.security.CurrentUserDetailService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReservationRestController implements ReservationsApi {
    private final ReservationFacade reservationFacade;
    private final ReservationMapper reservationMapper;
    private final MemberFacade memberFacade;
    private final CatalogFacade catalogFacade;
    private final CurrentUserDetailService currentUserDetailService;

    public ReservationRestController(ReservationFacade reservationFacade,
                                     ReservationMapper reservationMapper,
                                     MemberFacade memberFacade,
                                     CatalogFacade catalogFacade, CurrentUserDetailService currentUserDetailService) {
        this.reservationFacade = reservationFacade;
        this.reservationMapper = reservationMapper;
        this.memberFacade = memberFacade;
        this.catalogFacade = catalogFacade;
        this.currentUserDetailService = currentUserDetailService;
    }

   @Override
    public ResponseEntity<Void> cancelReservation(Long id) {
        Reservation reservation = reservationFacade.find(id);
        Member currentMember = currentUserDetailService.getFullCurrentMember();
        reservationFacade.cancel(reservation, currentMember);
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> createReservation(CreateReservationRequestDto dto) {
        Member member = memberFacade.find(dto.getMemberId());
        Book book = catalogFacade.find(new ISBN(dto.getIsbn()));
        reservationFacade.create(member, book);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<List<ReservationDto>> getAllReservations(Long memberId) {
        List<Reservation> reservations;

        if(currentUserDetailService.isPrivileged()){
            if(memberId != null) {
                Member member = memberFacade.find(memberId);
                reservations = reservationFacade.findByMember(member);
            } else {
                reservations = reservationFacade.findAll();
            }

        } else {
            // Member vidí iba svoje Reservacie
            Member currentMember = currentUserDetailService.getFullCurrentMember();
            reservations = reservationFacade.findByMember(currentMember);
        }

        return ResponseEntity.ok(reservationMapper.toDto(reservations));
    }
}
