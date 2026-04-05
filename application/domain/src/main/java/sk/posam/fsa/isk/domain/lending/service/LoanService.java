package sk.posam.fsa.isk.domain.lending.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.member.Fine;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.predicate.HasActiveMembershipPredicate;
import sk.posam.fsa.isk.domain.member.predicate.HasNoUnpaidFinesPredicate;
import sk.posam.fsa.isk.domain.reservation.service.ReservationService;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.List;

public class LoanService implements LoanFacade{
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final LoanFactory loanFactory;
    private final FineService fineService;
    private final ReservationService reservationService;

    public LoanService(BookRepository bookRepository,
                       MemberRepository memberRepository,
                       LoanRepository loanRepository,
                       LoanFactory loanFactory,
                       FineService fineService,
                       ReservationService reservationService) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.loanFactory = loanFactory;
        this.fineService = fineService;
        this.reservationService = reservationService;
    }

    @Override
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

        book.borrowCopy();
        bookRepository.save(book);
        Loan loan = loanFactory.createLoan(loanedTo, book, createdBy);
        loanRepository.save(loan);
    }

    @Override
    public void returnBook(Loan loan) {
        loan.returnBook();

        Book book = loan.getBook();
        book.returnCopy();
        bookRepository.save(book);

        if(loan.daysOverdue() > 0) {
            Fine fine = fineService.calculate(loan);
            Member member = loan.getLoanedTo();
            member.addFine(fine);
            memberRepository.save(member);
        }

        loanRepository.save(loan);
        reservationService.notifyNextInQueue(book);
    }

    @Override
    public List<Loan> findByMember(Member member) {
        return loanRepository.findByMember(member).stream().toList();
    }

    @Override
    public List<Loan> findOverdue() {
        return loanRepository.findOverdueLoans().stream().toList();
    }

    @Override
    public void renew(Loan loan) {
        loan.renew();
        loanRepository.save(loan);
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }
}
