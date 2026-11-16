package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;
    private static final int MAX_TICKETS = 25;
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }


    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        validatePurchase(accountId, ticketTypeRequests);
        int totalAmount = calculateAmount(ticketTypeRequests);
        int totalSeats = calculateSeats(ticketTypeRequests);
        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeats);
    }

    private int calculateSeats(TicketTypeRequest[] ticketTypeRequests) {
        int totalSeats = 0;
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            switch (ticketTypeRequest.getTicketType()) {
                case ADULT, CHILD -> totalSeats += ticketTypeRequest.getNoOfTickets();
                // INFANT: no seat, they sit on an adult's lap
            }
        }
        return totalSeats;
    }

    private int calculateAmount(TicketTypeRequest[] ticketTypeRequests) {
        int totalAmount = 0;
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            switch (ticketTypeRequest.getTicketType()) {
                case ADULT -> totalAmount += ticketTypeRequest.getNoOfTickets() * ADULT_PRICE;
                case CHILD -> totalAmount += ticketTypeRequest.getNoOfTickets() * CHILD_PRICE;
                // INFANT: free
            }
        }
        return totalAmount;
    }

    private void validatePurchase(Long accountId, TicketTypeRequest[] ticketTypeRequests) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be greater than zero");
        }
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("At least one ticket request is required");
        }
        int adults = 0;
        int children = 0;
        int infants = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null || request.getTicketType() == null) {
                throw new InvalidPurchaseException("Ticket request and ticket type must not be null");
            }
            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Number of tickets must be greater than zero");
            }
            switch (request.getTicketType()) {
                case ADULT -> adults += request.getNoOfTickets();
                case CHILD -> children += request.getNoOfTickets();
                case INFANT -> infants += request.getNoOfTickets();
            }

        }
        int totalTickets = adults + children + infants;

        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + " tickets at a time");
        }

        if (adults == 0) {
            throw new InvalidPurchaseException("Child and Infant tickets require at least one Adult ticket");
        }

        if (infants > adults) {
            throw new InvalidPurchaseException("Each Infant must sit on an Adult's lap: infants cannot exceed adults");
        }
    }

}
