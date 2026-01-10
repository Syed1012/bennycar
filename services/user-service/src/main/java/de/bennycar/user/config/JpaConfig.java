package de.bennycar.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration enabling auditing support.
 * This allows automatic population of @CreatedDate and @LastModifiedDate fields.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}