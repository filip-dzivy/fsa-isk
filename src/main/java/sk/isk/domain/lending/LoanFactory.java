package sk.isk.domain.lending;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.membership.Member;

import java.time.LocalDate;

public class LoanFactory {

    public LoanFactory() {}

    public Loan createLoan(Member loanedTo, Book book, Member createdBy){
        Loan loan = new Loan(loanedTo, book, createdBy);
        loan.validateForCreation();
        return loan;
    }

    public Loan createLoan(Member loanedTo, Book book, Member createdBy, LocalDate loanDate, int durationDays) {
        Loan loan = new Loan(loanedTo, book, createdBy, loanDate, durationDays);
        loan.validateForCreation();
        return loan;
    }
}
