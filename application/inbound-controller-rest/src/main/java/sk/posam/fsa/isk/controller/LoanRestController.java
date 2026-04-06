package sk.posam.fsa.isk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.service.LoanFacade;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.mapper.LoanMapper;
import sk.posam.fsa.isk.rest.api.LoansApi;
import sk.posam.fsa.isk.rest.dto.CreateLoanRequestDto;
import sk.posam.fsa.isk.rest.dto.LoanDto;

import java.util.List;

@RestController
public class LoanRestController implements LoansApi {

    private final LoanFacade loanFacade;
    private final LoanMapper loanMapper;
    private final MemberFacade memberFacade;
    private final CatalogFacade catalogFacade;

    public LoanRestController(LoanFacade loanFacade,
                              LoanMapper loanMapper,
                              MemberFacade memberFacade,
                              CatalogFacade catalogFacade) {
        this.loanFacade = loanFacade;
        this.loanMapper = loanMapper;
        this.memberFacade = memberFacade;
        this.catalogFacade = catalogFacade;
    }


    @Override
    @Transactional
    public ResponseEntity<Void> createLoan(CreateLoanRequestDto createLoanRequestDto) {
        Member loanedTo = memberFacade.find(createLoanRequestDto.getMemberId());
        Book book = catalogFacade.find(new ISBN(createLoanRequestDto.getIsbn()));
        Member createdBy = memberFacade.find(createLoanRequestDto.getCreatedById());
        loanFacade.create(loanedTo, book, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<LoanDto>> getAllLoans(Long memberId) {
        List<Loan> loans;
        if(memberId != null) {
            Member member = memberFacade.find(memberId);
            loans = loanFacade.findByMember(member);
        } else {
            loans = loanFacade.findAll();
        }
        return ResponseEntity.ok(loanMapper.toDto(loans));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<LoanDto>> getOverdueLoans() {
        return ResponseEntity.ok(loanMapper.toDto(loanFacade.findOverdue()));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> renewLoan(Long id) {
        Loan loan = loanFacade.find(id);
        loanFacade.renew(loan);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> returnLoan(Long id) {
        Loan loan = loanFacade.find(id);
        loanFacade.returnBook(loan);
        return ResponseEntity.ok().build();
    }
}
