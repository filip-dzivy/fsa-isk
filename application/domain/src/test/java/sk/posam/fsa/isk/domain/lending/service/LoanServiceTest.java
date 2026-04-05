package sk.posam.fsa.isk.domain.lending.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.FineFactory;
import sk.posam.fsa.isk.domain.finance.FineRepository;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.lending.LoanStatus;
import sk.posam.fsa.isk.domain.lending.event.BookReturnedEvent;
import sk.posam.fsa.isk.domain.lending.service.LoanService;
import sk.posam.fsa.isk.domain.member.*;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanFactory loanFactory;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private FineRepository fineRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private FineFactory fineFactory;

    private LoanService service;

    private Member loanedTo;
    private Member createdBy;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
        loanedTo = new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        createdBy = new Member(2L, new Email("jan.horak@example.sk"), "Jan", "Horak", MemberRole.LIBRARIAN);
        loanedTo.assignMembership(Membership.createNew());

        service = new LoanService(bookRepository, memberRepository, loanRepository,
                loanFactory, fineRepository, eventPublisher,
                Money.of(0.50, "EUR"), fineFactory);
    }

    // -------------------------------------------------------------------------
    // UC02 — Vytvorenie výpožičky
    // -------------------------------------------------------------------------

    @Test
    void createLoan_happyPath() {
        Loan loan = new Loan(loanedTo, book, createdBy);
        when(loanFactory.createLoan(loanedTo, book, createdBy)).thenReturn(loan);

        service.create(loanedTo, book, createdBy);

        verify(loanRepository).save(loan);
        assertEquals(2, book.getAvailableCopies());
    }

    @Test
    void createdLoan_expiredMembership_throws() {
        loanedTo.assignMembership(new Membership(LocalDate.now().minusDays(1)));

        assertThrows(DomainException.class,
                () -> service.create(loanedTo, book, createdBy));

        verify(loanRepository, never()).save(any());
    }

    @Test
    void createLoan_unpaidFine_throws() {
        Loan overdueLoan = new LoanFactory().createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(20), 14);
        Fine fine = new Fine(Money.of(0.50, "EUR"), "Oneskorené vrátenie", overdueLoan);
        loanedTo.addFine(fine);

        assertThrows(DomainException.class,
                () -> service.create(loanedTo, book, createdBy));

        verify(loanRepository, never()).save(any());
    }

    @Test
    void createLoan_bookNotAvailable_throws() {
        Book unavailableBook = new Book(new ISBN("9780306406157"), "Clean Code",
                "Robert C. Martin", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 1);
        unavailableBook.borrowCopy();

        assertThrows(DomainException.class,
                () -> service.create(loanedTo, unavailableBook, createdBy));

        verify(loanRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // UC03 — Vrátenie knihy
    // -------------------------------------------------------------------------

    @Test
    void returnBook_onTime_noFine() {
        book.borrowCopy();
        Loan loan = new Loan(loanedTo, book, createdBy);
        when(fineRepository.findPendingByLoan(loan)).thenReturn(Optional.empty());

        service.returnBook(loan);

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertEquals(LocalDate.now(), loan.getReturnDate());
        assertTrue(loanedTo.getFines().isEmpty());
        assertEquals(3, book.getAvailableCopies());
        verify(loanRepository).save(loan);
        verify(bookRepository).save(book);
        verify(fineRepository, never()).save(any());
        verify(eventPublisher).publish(any(BookReturnedEvent.class));
    }

    @Test
    void returnBook_overdue_fineUpdated() {
        book.borrowCopy();
        Loan loan = new LoanFactory().createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(20), 14);

        Fine existingFine = new Fine(Money.of(1.00, "EUR"), "Oneskorené vrátenie", loan);
        when(fineRepository.findPendingByLoan(loan)).thenReturn(Optional.of(existingFine));

        service.returnBook(loan);

        verify(fineFactory).updateFor(existingFine, loan, Money.of(0.50, "EUR"));
        verify(fineRepository).save(existingFine);
        verify(eventPublisher).publish(any(BookReturnedEvent.class));
    }

    // -------------------------------------------------------------------------
    // UC08 — Predĺženie výpožičky
    // -------------------------------------------------------------------------

    @Test
    void renew_happyPath() {
        Loan loan = new Loan(loanedTo, book, createdBy);
        LocalDate originalDue = loan.getDueDate();

        service.renew(loan);

        assertEquals(originalDue.plusDays(14), loan.getDueDate());
        assertEquals(1, loan.getRenewalCount());
        verify(loanRepository).save(loan);
    }

    @Test
    void renew_maxRenewalsReached_throws() {
        Loan loan = new Loan(loanedTo, book, createdBy);
        service.renew(loan);

        assertThrows(DomainException.class, () -> service.renew(loan));
        verify(loanRepository, times(1)).save(loan);
    }

    // -------------------------------------------------------------------------
    // RQ06 — Sledovanie výpožičiek
    // -------------------------------------------------------------------------

    @Test
    void findByMember_returnsOnlyMembersLoans() {
        Loan loan = new Loan(loanedTo, book, createdBy);
        when(loanRepository.findByMember(loanedTo)).thenReturn(List.of(loan));

        List<Loan> result = service.findByMember(loanedTo);

        assertEquals(1, result.size());
        assertEquals(loanedTo, result.get(0).getLoanedTo());
    }

    @Test
    void findOverdue_returnsOnlyOverdueLoans() {
        Loan overdue = new LoanFactory().createLoan(loanedTo, book, createdBy,
                LocalDate.now().minusDays(20), 14);
        when(loanRepository.findOverdueLoans()).thenReturn(List.of(overdue));

        List<Loan> result = service.findOverdue();

        assertEquals(1, result.size());
    }
}