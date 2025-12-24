package de.bennycar.vehicle.client;

import de.bennycar.api.user.contract.UserServiceContract;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Feign client for User Service communication.
 * Uses the UserServiceContract interface for type-safe inter-service communication.
 *
 * <p>Configuration:
 * - name: Logical name for the client
 * - url: User service base URL (can be configured via properties)
 * - path: Not needed as contract already defines @RequestMapping
 */
@FeignClient(
    name = "user-service",
    url = "${services.user-service.url:http://localhost:8081}"
)
public interface UserServiceClient extends UserServiceContract {
    // All methods are inherited from UserServiceContract
    // No need to redefine endpoints - they're already defined in the contract
}

