package sk.isk.domain.lending.service;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.lending.Loan;
import sk.isk.domain.membership.Member;

import java.util.List;

public interface LoanFacade {

    public void create(Member loanedTo, Book book, Member createdBy);

    public List<Loan> findByMember(Member member);

    public List<Loan> findOverdue();

    public void renew(Loan loan);
}
