package com.backend.proj.serviceImpl;

import com.backend.proj.Services.EventsService;
import com.backend.proj.dtos.CancelEventDto;
import com.backend.proj.dtos.CreateEventsDto;
import com.backend.proj.dtos.PostponeEventDto;
import com.backend.proj.dtos.UpdateEventDto;
import com.backend.proj.entities.Events;
import com.backend.proj.entities.Leaders;
import com.backend.proj.enums.EEvent;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.ServiceException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.EventRepository;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.EventsResponse;
import com.backend.proj.response.NotFoundResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.Formatter;
import com.backend.proj.utils.GetLoggedUser;
import com.backend.proj.utils.Validators.Validator;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Builder
@RequiredArgsConstructor
public class EventServiceImpl implements EventsService {

    private final GetLoggedUser getLoggedUser;
    private final EventRepository eventRepository;
    private final LeaderRepository leaderRepository;

    @Override
    public ApiResponse<Object> createAEvent(CreateEventsDto dto) throws Exception {
        try {
            // this is to get logged user
            UserResponse user = getLoggedUser.getLoggedUser();
            // Validate the input DTO
            validateInput(dto);

            // Convert DTO to entity
            Events eventEntity = convertDtoToEntity(dto, user);

            // Save the event to the repository
            Events savedEvent = eventRepository.save(eventEntity);

            if (savedEvent != null) {
                EventsResponse response = new EventsResponse();
                response.setMessage("Announcement sent successfully");
                response.setEvents(savedEvent);
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            } else {
                throw new ServiceException("Failed to send announcement!");
            }
        } catch (ServiceException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Internal server error... " + e);
        }
    }

    private void validateInput(CreateEventsDto dto) {
        if (dto.getCategory() == null || dto.getLocation() == null || dto.getEndDateTime() == null ||
                dto.getEventName() == null || dto.getOrganizationLevel() == null ||
                dto.getDescriptions() == null ||
                dto.getStartDateTime() == null) {
            throw new BadRequestException("Please provide all required details for your  announcement!");
        } else {
            // validate the dates and time
            Validator.ValidateEventTime(Formatter.getStartLocalDateTime(dto.getStartDateTime()),
                    Formatter.getEndLocalDateTime(dto.getEndDateTime()));

            Validator.validateTimeInterval(Formatter.getStartLocalDateTime(dto.getStartDateTime()),
                    Formatter.getEndLocalDateTime(dto.getEndDateTime()));
        }
    }

    private Events convertDtoToEntity(CreateEventsDto dto, UserResponse user) {

        // Implement logic to convert DTO to Entity
        Events events = new Events();
        events.setOrganizationLevel(dto.getOrganizationLevel());
        events.setEventName(dto.getEventName());
        events.setDescriptions(dto.getDescriptions());
        events.setStartDateTime(Formatter.getStartLocalDateTime(dto.getStartDateTime()));
        events.setLocation(dto.getLocation());
        events.setEndDateTime(Formatter.getEndLocalDateTime(dto.getEndDateTime()));
        events.setCategory(dto.getCategory());
        events.setOwner(user.getNationalId());
        events.setStatus(EEvent.PENDING);

        return events;
    }

    // the logic to update the event
    @Override
    public ApiResponse<Object> updateMyEvent(UpdateEventDto dto, UUID id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (id == null) {
                throw new BadRequestException("Event id is required!");
            }

            // get the events by user and id
            Events[] events = eventRepository.findAllByOwner(user.getNationalId());

            if (events.length == 0) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No events found for user: " + user.getName())
                        .data(events)
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // Find the event to update
            Optional<Events> eventToUpdate = Arrays.stream(events)
                    .filter(event -> event.getId().equals(id))
                    .findFirst();

            if (eventToUpdate.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Event " + id
                                + " not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // Now you have the event to update (eventToUpdate.get())
            Events existingEvent = eventToUpdate.get();
            // Perform the update using data from the DTO
            existingEvent.setEventName(dto.getEventName());
            existingEvent.setOrganizationLevel(dto.getOrganizationLevel());
            existingEvent.setCategory(dto.getCategory());
            existingEvent.setLocation(dto.getLocation());
            existingEvent.setStartDateTime(Formatter.getStartLocalDateTime(dto.getStartDateTime()));
            existingEvent.setEndDateTime(Formatter.getEndLocalDateTime(dto.getEndDateTime()));
            existingEvent.setDescriptions(dto.getDescriptions());

            // Save the updated event
            Events updatedEvent = eventRepository.save(existingEvent);

            if (updatedEvent != null) {
                EventsResponse response = new EventsResponse();
                response.setMessage("Announcement updated successfully");
                response.setEvents(updatedEvent);
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            } else {
                throw new ServiceException("Failed to update announcement!");
            }

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // this is to delete my events
    @Override
    public ApiResponse<Object> deleteMyEvent(UUID id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (id == null) {
                throw new BadRequestException("Event Id is required!");
            }

            // Find the event by id
            Optional<Events> eventToDelete = eventRepository.findById(id);

            // Check if the event exists
            if (eventToDelete.isEmpty() || !eventToDelete.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Event " + id
                                + " not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // Check if the logged user is the owner of the event
            Events event = eventToDelete.get();
            if (!event.getOwner().equals(user.getNationalId())) {
                throw new BadRequestException("You do not have permission to delete this event!");
            }

            // Delete the event
            eventRepository.delete(event);

            return ApiResponse.builder()
                    .data("Announcement  deleted successfully")
                    .success(true)
                    .build();
        } catch (NotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> myRecentEvent() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();

            // Find all events by owner
            List<Events> recentEvents = List.of(eventRepository.findAllByOwner(user.getNationalId()));

            // Check if the list is empty
            if (recentEvents.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No events found in your system!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            recentEvents.sort(Comparator.comparing(Events::getStartDateTime).reversed());

            return ApiResponse.builder()
                    .data(recentEvents)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // this is to receive the events from our leaders
    @Override
    public ApiResponse<Object> receivedEvent() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            String userVillage = user.getVillage();
            String userSector = user.getSector();
            String userCell = user.getCell();
            String userDistrict = user.getDistrict();
            String userProvince = user.getProvince();

            List<Events> receivedEvents = eventRepository.findAllByOrganizationLevel(
                    userVillage, userSector, userCell, userDistrict, userProvince);

            // Check if the list is empty
            if (receivedEvents.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No events found for the user!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            receivedEvents.sort(Comparator.comparing(Events::getStartDateTime).reversed());

            return ApiResponse.builder()
                    .data(receivedEvents)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // get numbers of all events by admin
    // @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ApiResponse<Object> getNumberOfAllEvents() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.ADMIN) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            } else {
                Long numberOfAllEvents = eventRepository.count();
                return ApiResponse.builder()
                        .data(numberOfAllEvents)
                        .success(true)
                        .build();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    // get the number of all events I gave
    @Override
    public ApiResponse<Object> getNumberOfAllEventsByMe() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.UMUYOBOZI) {
                System.out.println(" The role is " + user.getRole());
                throw new UnauthorisedException("You are not authorised to perform this action!");

            }

            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                throw new NotFoundException("Leader " + user.getNationalId() + " not found!");
            }

            // Count the events
            long numberOfEvents = eventRepository.countByOwner(
                    leader.get().getNationalId());

            return ApiResponse.builder()
                    .data(numberOfEvents)
                    .success(true)
                    .build();
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> cancelEvent(CancelEventDto dto) throws Exception {
        try {
            UUID eventId = dto.getEventId();
            if (eventId == null || eventId.toString().isBlank()) {
                throw new BadRequestException("please provide the UUID of the event!");
            } else {
                // get the event
                Optional<Events> event = eventRepository.findById(eventId);
                if (!event.isPresent()) {
                    return ApiResponse.builder()
                            .data(event)
                            .success(true)
                            .message("No event " + eventId + " found!")
                            .status(HttpStatus.OK)
                            .build();
                } else {
                    event.get().setStatus(EEvent.CANCELED);
                    eventRepository.save(event.get());
                    return ApiResponse.builder()
                            .message("Event " + eventId + " successfully cancelled!")
                            .status(HttpStatus.OK)
                            .success(true)
                            .build();
                }

            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> postponeEvent(UUID eventId, PostponeEventDto dto) throws Exception {
        try {
            if (dto.getOtherEndDateTime() == null || dto.getOtherEndDateTime().isBlank()
                    || dto.getOtherStartDateTime() == null || dto.getOtherStartDateTime().isBlank()) {
                throw new BadRequestException("Please provide the date the event is postponed to!");
            } else {
                // get the event
                Optional<Events> event = eventRepository.findById(eventId);
                if (!event.isPresent()) {
                    return ApiResponse.builder()
                            .data(event)
                            .message("Event " + eventId + " not found!")
                            .status(HttpStatus.OK)
                            .build();
                } else {
                    // validate the date and time
                    Validator.ValidateEventTime(Formatter.getStartLocalDateTime(dto.getOtherStartDateTime()),
                            Formatter.getEndLocalDateTime(dto.getOtherEndDateTime()));

                    Validator.validateTimeInterval(Formatter.getStartLocalDateTime(dto.getOtherStartDateTime()),
                            Formatter.getEndLocalDateTime(dto.getOtherEndDateTime()));

                    // update the event
                    event.get().setStartDateTime(Formatter.getStartLocalDateTime(dto.getOtherStartDateTime()));
                    event.get().setEndDateTime(Formatter.getEndLocalDateTime(dto.getOtherEndDateTime()));
                    event.get().setStatus(EEvent.POSTPONED);
                    Events e = eventRepository.save(event.get());
                    return ApiResponse.builder()
                            .status(HttpStatus.OK)
                            .message("Event " + eventId + " postponed successfully...")
                            .data(e)
                            .build();
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getMyEvents() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if(user == null || user.getRole() == URole.UMUTURAGE){
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }else {
                List<Events> events = eventRepository.findAllByOwner(user.getId());
                return ApiResponse.builder()
                .data(events)
                .status(HttpStatus.OK)
                .success(true)
                .build();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}