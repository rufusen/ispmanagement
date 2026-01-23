package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.TicketCreateDto;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.Ticket;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for ticket business logic.
 */
public class TicketService {

  private final UnitOfWork unitOfWork;

  public TicketService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Opens a new ticket for a subscriber.
   *
   * @param dto ticket creation data
   * @return created ticket
   * @throws IllegalArgumentException if subscriber not found
   */
  public Ticket openTicket(TicketCreateDto dto) {
    // Validate subscriber exists
    Subscriber subscriber = unitOfWork.subscribers().findById(dto.getSubscriberId())
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + dto.getSubscriberId()));

    // Create ticket
    Ticket ticket = new Ticket(
        UUID.randomUUID(),
        LocalDateTime.now(),
        Ticket.Status.OPEN,
        dto.getDescription()
    );

    unitOfWork.tickets().save(ticket);

    // Add ticket to subscriber's ticket list
    if (subscriber.getTickets() == null) {
      subscriber.setTickets(new java.util.ArrayList<>());
    }
    subscriber.getTickets().add(ticket);
    unitOfWork.subscribers().save(subscriber);

    return ticket;
  }

  /**
   * Closes a ticket.
   *
   * @param ticketId ticket ID
   * @return updated ticket
   * @throws IllegalArgumentException if ticket not found
   */
  public Ticket closeTicket(UUID ticketId) {
    Ticket ticket = unitOfWork.tickets().findById(ticketId)
        .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

    ticket.setStatus(Ticket.Status.CLOSED);
    unitOfWork.tickets().save(ticket);
    return ticket;
  }

  /**
   * Updates ticket status to IN_PROGRESS.
   *
   * @param ticketId ticket ID
   * @return updated ticket
   * @throws IllegalArgumentException if ticket not found
   */
  public Ticket startTicket(UUID ticketId) {
    Ticket ticket = unitOfWork.tickets().findById(ticketId)
        .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

    ticket.setStatus(Ticket.Status.IN_PROGRESS);
    unitOfWork.tickets().save(ticket);
    return ticket;
  }

  /**
   * Gets all tickets for a subscriber.
   *
   * @param subscriberId subscriber ID
   * @return list of tickets
   */
  public List<Ticket> getTicketsBySubscriber(UUID subscriberId) {
    Subscriber subscriber = unitOfWork.subscribers().findById(subscriberId)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + subscriberId));

    return subscriber.getTickets() != null ? subscriber.getTickets() : List.of();
  }

  /**
   * Gets tickets by status.
   *
   * @param status ticket status
   * @return list of tickets
   */
  public List<Ticket> getTicketsByStatus(Ticket.Status status) {
    return unitOfWork.tickets().filterByStatus(status);
  }

  /**
   * Gets all open tickets.
   *
   * @return list of open tickets
   */
  public List<Ticket> getOpenTickets() {
    return getTicketsByStatus(Ticket.Status.OPEN);
  }

  /**
   * Finds ticket by ID.
   *
   * @param id ticket ID
   * @return ticket or null if not found
   */
  public Ticket findById(UUID id) {
    return unitOfWork.tickets().findById(id).orElse(null);
  }
}
