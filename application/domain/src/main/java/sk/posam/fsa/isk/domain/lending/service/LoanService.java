package sk.posam.fsa.isk.domain.lending.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.finance.FineFactory;
import sk.posam.fsa.isk.domain.finance.FineRepository;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.finance.service.FineService;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.lending.event.BookReturnedEvent;
import sk.posam.fsa.isk.domain.lending.event.LoanCreatedEvent;
import sk.posam.fsa.isk.domain.member.predicate.HasActiveMembershipPredicate;
import sk.posam.fsa.isk.domain.member.predicate.HasNoUnpaidFinesPredicate;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.member.access.MemberVisibilityResolver;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.Transactional;

import java.util.List;

public class LoanService implements LoanFacade{
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final LoanFactory loanFactory;
    private final FineRepository fineRepository;
    private final ReservationRepository reservationRepository;
    private final MemberVisibilityResolver memberVisibilityResolver;
    private final DomainEventPublisher eventPublisher;
    private final Money finesDailyRate;
    private final FineFactory fineFactory;

    public LoanService(BookRepository bookRepository,
                       MemberRepository memberRepository,
                       LoanRepository loanRepository,
                       LoanFactory loanFactory,
                       FineRepository fineRepository,
                       ReservationRepository reservationRepository,
                       MemberVisibilityResolver memberVisibilityResolver,
                       DomainEventPublisher eventPublisher,
                       Money finesDailyRate,
                       FineFactory fineFactory) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.loanFactory = loanFactory;
        this.fineRepository = fineRepository;
        this.reservationRepository = reservationRepository;
        this.memberVisibilityResolver = memberVisibilityResolver;
        this.eventPublisher = eventPublisher;
        this.finesDailyRate = finesDailyRate;
        this.fineFactory = fineFactory;
    }

    @Override
    @Transactional
    public void create(Member loanedTo, Book book, Member createdBy){
        require(HasActiveMembershipPredicate.INSTANCE.test(loanedTo.getMembership()),
                DomainException.Type.FORBIDDEN,
                "Čitateľ " + loanedTo.getId() + " nemá platné členstvo.");
        require(HasNoUnpaidFinesPredicate.INSTANCE.test(loanedTo),
                DomainException.Type.FORBIDDEN,
                "Čitateľ " + loanedTo.getId() + " má neuhradené pokuty.");
        require(book.isAvailable(),
                DomainException.Type.CONFLICT,
                "Kniha " + book.getIsbn() + " nie je momentálne dostupná.");

        List<Reservation> readyForPickup = reservationRepository.findReadyForPickupByBook(book).stream().toList();
        boolean borrowerHasReservation = readyForPickup.stream()
                .anyMatch(r -> r.getCreatedBy().equals(loanedTo));
        long otherReadyCount = readyForPickup.stream()
                .filter(r -> !r.getCreatedBy().equals(loanedTo))
                .count();
        require(borrowerHasReservation || book.getAvailableCopies() > otherReadyCount,
                DomainException.Type.CONFLICT,
                "Všetky dostupné kópie sú rezervované pre iných čitateľov.");

        book.borrowCopy();
        bookRepository.save(book);
        Loan loan = loanFactory.createLoan(loanedTo, book, createdBy);
        loanRepository.save(loan);
        eventPublisher.publish(new LoanCreatedEvent(loanedTo, book));
    }

    @Override
    @Transactional
    public void returnBook(Loan loan) {
        loan.returnBook();

        Book book = loan.getBook();
        book.returnCopy();
        bookRepository.save(book);

        fineRepository.findPendingByLoan(loan)
                        .ifPresent(fine -> {
                            fineFactory.updateFor(fine, loan, finesDailyRate);
                            fineRepository.save(fine);
                        });

        loanRepository.save(loan);
        eventPublisher.publish(new BookReturnedEvent(book));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByMember(Member member) {
        return loanRepository.findByMember(member).stream().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        return loanRepository.findAll().stream().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Loan find(long id) {
        return loanRepository.find(id)
                .orElseThrow(() -> new DomainException(
                        DomainException.Type.NOT_FOUND,
                        "Výpôžička s ID " + id + " neexistuje."
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findOverdue() {
        return loanRepository.findOverdueLoans().stream().toList();
    }

    @Override
    @Transactional
    public void renew(Loan loan, Member requestedBy) {
        loan.renew(requestedBy);
        loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void changeDueDate(long loanId, java.time.LocalDate newDueDate) {
        Loan loan = find(loanId);
        loan.changeDueDate(newDueDate);
        loanRepository.save(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findVisible(Member requestingMember, Long targetMemberId) {
        return memberVisibilityResolver.resolve(requestingMember, targetMemberId)
                .map(target -> loanRepository.findByMember(target).stream().toList())
                .orElseGet(() -> loanRepository.findAll().stream().toList());
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }
}
