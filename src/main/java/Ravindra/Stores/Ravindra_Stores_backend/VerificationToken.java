package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;

@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private Date expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType = TokenType.EMAIL_VERIFICATION; // Default value

    public VerificationToken() {
        super();
    }

    public VerificationToken(final String token, final User user, final TokenType tokenType) {
        super();

        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
    
    // Backward compatibility constructor for email verification
    public VerificationToken(final String token, final User user) {
        this(token, user, TokenType.EMAIL_VERIFICATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public TokenType getTokenType() {
        return tokenType;
    }
}
