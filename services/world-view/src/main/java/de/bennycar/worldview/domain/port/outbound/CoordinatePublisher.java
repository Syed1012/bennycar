package de.bennycar.worldview.domain.port.outbound;

import de.bennycar.worldview.domain.model.Coordinate;
import de.bennycar.worldview.domain.model.JourneyState;

/**
 * Outbound port for publishing coordinate updates to the frontend.
 * This is a secondary port implemented by infrastructure adapters.
 */
public interface CoordinatePublisher {

    /**
     * Publish the current coordinate update for a journey.
     *
     * @param journeyId The journey identifier
     * @param coordinate The current coordinate
     * @param journeyState The full journey state for additional context
     */
    void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState);

    /**
     * Publish a journey started event.
     *
     * @param journeyState The initial journey state
     */
    void publishJourneyStarted(JourneyState journeyState);

    /**
     * Publish a journey completed event.
     *
     * @param journeyState The final journey state
     */
    void publishJourneyCompleted(JourneyState journeyState);
}