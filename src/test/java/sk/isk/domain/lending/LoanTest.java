package sk.isk.domain.lending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.membership.Member;

public class LoanTest {

    private Member loanedTo;
    private Member createdBy;
    private ISBN isbn;
    private LoanFactory factory;

    @BeforeEach
    void setUp(){
        loanedTo = new Member();
        isbn = new ISBN("9780306406157");
        factory = new LoanFactory();
    }

    @Test
    void newLoanIsActiveWithCorrectDueDate(){
    }
}
