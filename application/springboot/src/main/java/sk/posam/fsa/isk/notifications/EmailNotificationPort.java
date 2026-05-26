package sk.posam.fsa.isk.notifications;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.NotificationPort;

import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnProperty(name = "notifications.email.enabled", havingValue = "true")
public class EmailNotificationPort implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationPort.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String fromAddress;

    public EmailNotificationPort(JavaMailSender mailSender,
                                 SpringTemplateEngine templateEngine,
                                 @Value("${notifications.email.from:no-reply@isk.sk}") String fromAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromAddress = fromAddress;
    }

    @Async
    @Override
    public void notifyReservationReady(Member member, Book book) {
        Context ctx = new Context();
        ctx.setVariable("firstName", member.getFirstName());
        ctx.setVariable("bookTitle", book.getTitle());
        ctx.setVariable("bookAuthor", book.getAuthor());
        send(member.getEmail().toString(),
                "Vaša rezervácia je pripravená na vyzdvihnutie",
                "reservation-ready", ctx);
    }

    @Async
    @Override
    public void notifyMembershipExpiringSoon(Member member, int daysLeft) {
        Context ctx = new Context();
        ctx.setVariable("firstName", member.getFirstName());
        ctx.setVariable("daysLeft", daysLeft);
        send(member.getEmail().toString(),
                "Vaše členstvo končí o " + daysLeft + " dní",
                "membership-expiring", ctx);
    }

    @Async
    @Override
    public void notifyLoanDueSoon(Member member, Loan loan, int daysLeft) {
        Context ctx = new Context();
        ctx.setVariable("firstName", member.getFirstName());
        ctx.setVariable("bookTitle", loan.getBook().getTitle());
        ctx.setVariable("dueDate", loan.getDueDate());
        ctx.setVariable("daysLeft", daysLeft);
        send(member.getEmail().toString(),
                "Termín vrátenia knihy sa blíži",
                "loan-due", ctx);
    }

    private void send(String to, String subject, String template, Context ctx) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(templateEngine.process("email/" + template, ctx), true);
            mailSender.send(message);
            log.info("Sent {} email to {}", template, to);
        } catch (MessagingException | RuntimeException e) {
            log.error("Failed to send {} email to {}: {}", template, to, e.getMessage(), e);
        }
    }
}
