package sk.posam.fsa.isk.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.rest.dto.LoanDto;
import sk.posam.fsa.isk.rest.dto.LoanStatusDto;

@Component
public class LoanMapper {

    private final MemberMapper memberMapper;
    private final BookMapper bookMapper;

    public LoanMapper(MemberMapper memberMapper, BookMapper bookMapper) {
        this.memberMapper = memberMapper;
        this.bookMapper = bookMapper;
    }

    public LoanDto toDto(Loan entity) {
        if (entity == null) {
            return null;
        }

        LoanDto dto = new LoanDto();
        dto.setId(entity.getId());
        dto.setLoanedTo(memberMapper.toDto(entity.getLoanedTo()));
        dto.setBook(bookMapper.toDto(entity.getBook()));
        dto.setCreatedBy(memberMapper.toDto(entity.getCreatedBy()));
        dto.setLoanDate(entity.getLoanDate());
        dto.setDueDate(entity.getDueDate());
        dto.setReturnDate(entity.getReturnDate());
        dto.setRenewalCount(entity.getRenewalCount());
        dto.setStatus(LoanStatusDto.valueOf(entity.getStatus().name()));
        return dto;
    }
}
