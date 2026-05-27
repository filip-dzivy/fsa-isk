package sk.posam.fsa.isk.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import sk.posam.fsa.isk.domain.shared.Transactional;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
    Wiring pre domenove Transactional anotacie
 */
@Aspect
@Component
public class DomainTransactionalAspect {

    private final PlatformTransactionManager txManager;

    public DomainTransactionalAspect(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Around(
            "@annotation(sk.posam.fsa.isk.domain.shared.Transactional) " +
            "|| @within(sk.posam.fsa.isk.domain.shared.Transactional)"
    )
    public Object wrap(ProceedingJoinPoint pjp) throws Throwable {
        Transactional ann = resolveAnnotation(pjp);
        TransactionTemplate template = new TransactionTemplate(txManager);
        if (ann != null) {
            template.setReadOnly(ann.readOnly());
        }

        AtomicReference<Throwable> thrown = new AtomicReference<>();
        Object result = template.execute(status -> {
            try {
                return pjp.proceed();
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                thrown.set(e);
                status.setRollbackOnly();
                return null;
            }
        });
        if (thrown.get() != null) throw thrown.get();
        return result;
    }

    private Transactional resolveAnnotation(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Transactional ann = AnnotationUtils.findAnnotation(method, Transactional.class);
        if (ann != null) return ann;
        return AnnotationUtils.findAnnotation(method.getDeclaringClass(), Transactional.class);
    }
}