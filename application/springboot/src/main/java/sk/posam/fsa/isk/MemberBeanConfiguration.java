package sk.posam.fsa.isk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;
import sk.posam.fsa.isk.domain.member.service.MemberService;

@Configuration
public class MemberBeanConfiguration {

    @Bean
    public MemberFacade memberFacade(MemberRepository memberRepository) {
        return new MemberService(memberRepository);
    }
}
