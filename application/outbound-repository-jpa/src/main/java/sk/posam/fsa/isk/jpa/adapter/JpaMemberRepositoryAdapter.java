package sk.posam.fsa.isk.jpa.adapter;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.jpa.springrepo.MemberSpringDataRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaMemberRepositoryAdapter implements MemberRepository {

    private final MemberSpringDataRepository memberSpringDataRepository;
    private final EntityManager entityManager;

    public JpaMemberRepositoryAdapter(MemberSpringDataRepository memberSpringDataRepository,
                                      EntityManager entityManager) {
        this.memberSpringDataRepository = memberSpringDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Member> find(long id) {
        return memberSpringDataRepository.findById(id);
    }

    @Override
    public Optional<Member> findWithFines(long id) {
        return memberSpringDataRepository.findWithFinesById(id);
    }

    @Override
    public Optional<Member> find(Email email) {
        return memberSpringDataRepository.findByEmail(email);
    }

    @Override
    public Collection<Member> findAll() {return memberSpringDataRepository.findAll();}

    @Override
    public Collection<Member> findAllWithFines() {
        return memberSpringDataRepository.findAllWithFines();
    }

    @Override
    @Transactional
    public Member save(Member member) {
        if (member.getId() == 0L) {
            entityManager.persist(member);
            return member;
        }
        return entityManager.merge(member);
    }

    @Override
    public void delete(long id) {
        memberSpringDataRepository.deleteById(id);
    }
}