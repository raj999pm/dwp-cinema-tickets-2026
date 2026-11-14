# DWP Cinema Tickets

## Objective

This solution implements the TicketService according to the requirements provided in the coding exercise.

---

## Ticket Types

| Ticket Type | Price | Seat Required |
|-------------|---------|-------------|
| ADULT | £25 | Yes |
| CHILD | £15 | Yes |
| INFANT | £0 | No |

---

## Assumptions

- Account ID must be greater than 0.
- At least one Adult ticket must be purchased.
- Child tickets cannot be purchased without an Adult ticket.
- Infant tickets cannot be purchased without an Adult ticket.
- The total number of tickets purchased cannot exceed 25.
- Infants do not require a seat.
- The number of Infant tickets cannot exceed the number of Adult tickets.
- Payment and seat reservation services are trusted external dependencies as stated in the exercise requirements.

---

## Valid Use Cases

### Example 1

Input:

- 1 Adult

Expected:

- Payment = £25
- Seats Reserved = 1

### Example 2

Input:

- 2 Adult
- 3 Child

Expected:

- Payment = £95
- Seats Reserved = 5

### Example 3

Input:

- 2 Adult
- 3 Child
- 1 Infant

Expected:

- Payment = £95
- Seats Reserved = 5

---

## Invalid Use Cases

### Example 1

Input:

- Account ID = 0

Result:

- InvalidPurchaseException

### Example 2

Input:

- 2 Child

Result:

- InvalidPurchaseException

### Example 3

Input:

- 2 Infant

Result:

- InvalidPurchaseException

### Example 4

Input:

- 20 Adult
- 6 Child

Result:

- InvalidPurchaseException

### Example 5

Input:

- 1 Adult
- 2 Infant

Result:

- InvalidPurchaseException

---

## Design Decisions

- Validation is performed before payment processing.
- Only Adult and Child tickets contribute to the payment amount.
- Only Adult and Child tickets reserve seats.
- Infant tickets are free and do not reserve seats.
- Invalid requests result in an InvalidPurchaseException.
- TicketServiceImpl is responsible for orchestrating validation, payment and seat reservation.

---

## Running Tests

```bash
mvn test
```