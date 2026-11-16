package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class TicketServiceImplTest {

    private static final long ACCOUNT_ID = 1L;
    private static final TicketTypeRequest TWO_ADULTS =
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
    private static final TicketTypeRequest THREE_CHILDREN =
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
    private static final TicketTypeRequest ONE_INFANT =
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

    private SeatReservationService seatReservationService;
    private TicketPaymentService ticketPaymentService;
    private TicketService ticketService;

    @BeforeEach
    void setup() {
        ticketPaymentService = Mockito.mock(TicketPaymentService.class);
        seatReservationService = Mockito.mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void shouldPurchaseTicketsSuccessfully() {
        ticketService.purchaseTickets(ACCOUNT_ID, TWO_ADULTS, THREE_CHILDREN, ONE_INFANT);
        verify(ticketPaymentService)
                .makePayment(1L, 95);

        verify(seatReservationService)
                .reserveSeat(1L, 5);
    }

    @Test
    void shouldThrowExceptionWhenAccountIdIsInvalid() {
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        0L,
                        TWO_ADULTS
                )
        );
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void shouldThrowExceptionWhenChildTicketPurchasedWithoutAdult() {
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        ACCOUNT_ID,
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.CHILD,
                                2)
                )
        );
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void shouldThrowExceptionWhenMoreThan25TicketsPurchased() {

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        ACCOUNT_ID,
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.ADULT,
                                20),
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.CHILD,
                                6)
                )
        );
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void shouldThrowExceptionWhenInfantsExceedAdults() {

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        ACCOUNT_ID,
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.ADULT,
                                1),
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.INFANT,
                                2)
                )
        );
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void shouldThrowExceptionWhenInfantTicketPurchasedWithoutAdult() {

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        ACCOUNT_ID,
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.INFANT,
                                1)
                )
        );
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }
}
