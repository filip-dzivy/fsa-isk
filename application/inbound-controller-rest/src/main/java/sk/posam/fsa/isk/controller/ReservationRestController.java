package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;
import sk.posam.fsa.isk.mapper.ReservationMapper;
import sk.posam.fsa.isk.rest.api.ReservationsApi;
import sk.posam.fsa.isk.rest.dto.CreateReservationRequestDto;
import sk.posam.fsa.isk.rest.dto.ReservationDto;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReservationRestController implements ReservationsApi {
    private final ReservationFacade reservationFacade;
    private final ReservationMapper reservationMapper;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    public ReservationRestController(ReservationFacade reservationFacade,
                                     ReservationMapper reservationMapper,
                                     MemberRepository memberRepository,
                                     BookRepository bookRepository,
                                     ReservationRepository reservationRepository) {
        this.reservationFacade = reservationFacade;
        this.reservationMapper = reservationMapper;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ResponseEntity<Void> cancelReservation(Long id) {
        Reservation reservation = reservationRepository.find(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservationFacade.cancel(reservation);
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> createReservation(CreateReservationRequestDto dto) {
        Member member = memberRepository.find(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Book book = bookRepository.find(new ISBN(dto.getIsbn()))
                .orElseThrow(() -> new RuntimeException("Book not found"));

        reservationFacade.create(member, book);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<List<ReservationDto>> getAllReservations(Long memberId) {
        List<Reservation> reservations;
        if(memberId != null){
            Member member = memberRepository.find(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));
            reservations = reservationFacade.findByMember(member);
        } else {
          reservations = new ArrayList<>(reservationRepository.findAll());
        }
        return ResponseEntity.ok(reservationMapper.toDto(reservations));
    }
}
