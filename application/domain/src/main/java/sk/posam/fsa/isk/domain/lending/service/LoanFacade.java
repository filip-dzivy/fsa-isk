package sk.posam.fsa.isk.domain.lending.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.member.Member;

import java.util.List;

public interface LoanFacade {

    public void create(Member loanedTo, Book book, Member createdBy);

    public List<Loan> findByMember(Member member);

    public List<Loan> findOverdue();

    public void renew(Loan loan);

    public void returnBook(Loan loan);

    public Loan find(long id);

    public List<Loan> findAll();
}
