package greencity.security.service;

import greencity.entity.User;
import java.time.LocalDateTime;

/**
 * Service that does email verification.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
public interface VerifyEmailService {
    /**
     * Saves email verification token for the given user.
     *
     * @param user {@link User}.
     */
    void saveEmailVerificationTokenForUser(User user);

    /**
     * Verifies email by token.
     *
     * @param token {@link String} - token that confirms the user is the owner of his/her email.
     */
    void verifyByToken(String token);

    /**
     * Checks whether a user is not late with email verification.
     *
     * @return {@code boolean}
     */
    boolean isNotExpired(LocalDateTime emailExpiredDate);

    /**
     * Deletes email verification tokens that are expired.
     */
    void deleteAllUsersThatDidNotVerifyEmail();
}
