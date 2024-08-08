package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.dto.SeatDto;
import pl.smyk.customerservice.dto.request.ReservationRequest;
import pl.smyk.customerservice.dto.response.ReservationResponse;
import pl.smyk.customerservice.event.ReservationChangeStatusEvent;
import pl.smyk.customerservice.feignClient.AuthServiceClient;
import pl.smyk.customerservice.mapper.MovieMapper;
import pl.smyk.customerservice.mapper.ReservationMapper;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.model.PaymentStatus;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AuthServiceClient authServiceClient;
    private final MovieService movieService;
    private final ApplicationEventPublisher eventPublisher;

    private final Double SEAT_PRICE = 40.0;


    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }

    public Reservation findById(String id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public ReservationDto getReservationById(String id) {
      Reservation reservation = findById(id);
      return ReservationMapper.INSTANCE.reservationToReservationDto(reservation);
    }

    public List<ReservationDto> getReservationsByCustomerEmail(String customerEmail) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getCustomerEmail().equals(customerEmail))
                .map(ReservationMapper.INSTANCE::reservationToReservationDto)
                .collect(Collectors.toList());
    }

    public ReservationDto getReservationByIdAndCustomerEmailIfExists(String reservationId, String customerEmail) {
        Reservation reservation = reservationRepository.findByIdAndCustomerEmail(reservationId, customerEmail);
        if (reservation == null) {
            return null;
        }
        return ReservationMapper.INSTANCE.reservationToReservationDto(reservation);
    }

    public List<ReservationDto> getReservationsAtSelectedPlayTime(Long roomNumber, LocalDateTime selectedPlayTime) {
        List<Reservation> byRoomNumberAndSelectedPlayTime = reservationRepository.findByRoomNumberAndSelectedPlayTime(roomNumber, selectedPlayTime);
        return byRoomNumberAndSelectedPlayTime.stream().map(ReservationMapper.INSTANCE::reservationToReservationDto).collect(Collectors.toList());
    }

    public List<SeatDto> getOccupiedSeatsByRoomNumberAndSelectedPlayTime(Long roomNumber, LocalDateTime selectedPlayTime) {
        List<ReservationDto> reservationsAtSelectedPlayTime = getReservationsAtSelectedPlayTime(roomNumber, selectedPlayTime);
        return reservationsAtSelectedPlayTime.stream()
                .flatMap(reservationDto -> reservationDto.getSeats().stream())
                .collect(Collectors.toList());
    }

    public boolean isSeatOccupied(Long roomNumber, LocalDateTime selectedPlayTime, SeatDto seatDto) {
        List<SeatDto> occupiedSeats = getOccupiedSeatsByRoomNumberAndSelectedPlayTime(roomNumber, selectedPlayTime);
        if (occupiedSeats.stream().anyMatch(occupiedSeat -> occupiedSeat.getRow() == seatDto.getRow() && occupiedSeat.getPlaceNumber() == seatDto.getPlaceNumber())) {
            return true;
        }
        return false;
    }

    public boolean isSeatsOccupied(Long roomNumber, LocalDateTime selectedPlayTime, List<SeatDto> seatDtoList) {
        List<SeatDto> occupiedSeats = getOccupiedSeatsByRoomNumberAndSelectedPlayTime(roomNumber, selectedPlayTime);
        for (SeatDto seatDto : seatDtoList) {
            if (occupiedSeats.stream().anyMatch(occupiedSeat ->
                    occupiedSeat.getRow() == seatDto.getRow() &&
                            occupiedSeat.getPlaceNumber() == seatDto.getPlaceNumber()
            )) {
                return true;
            }
        }
        return false;
    }

    public ReservationResponse createReservation(ReservationRequest reservationRequest) {
        LocalDateTime selectedLocalDatetime = LocalDateTime.of(reservationRequest.getSelectedDate(), reservationRequest.getSelectedTime().getPlayTime());

        Movie movie = movieService.findById(reservationRequest.getMovieId());

        if (!movie.getPlayDates().contains(reservationRequest.getSelectedDate()) || !movie.getPlayTimes().contains(reservationRequest.getSelectedTime())) {
          return ReservationResponse.builder()
            .message("We are not screening this movie at this date time!")
            .errorReason("NOT_SCREENING_AT_DATE")
            .build();
        }

        boolean isOccupied = isSeatsOccupied(movie.getPlayingRoom(), selectedLocalDatetime, reservationRequest.getSeats());
        if (isOccupied) {
            return ReservationResponse.builder()
                    .message("Selected seats are currently occupied!")
                    .errorReason("SEAT_OCCUPIED")
                    .build();
        }

        Reservation newReservation = Reservation.builder()
                .customerEmail(reservationRequest.getCustomerEmail())
                .roomNumber(movie.getPlayingRoom())
                .movie(movie)
                .selectedPlayTime(selectedLocalDatetime)
                .seats(Reservation.Seat.seatDtoToSeat(reservationRequest.getSeats()))
                .totalPrice(reservationRequest.getSeats().size() * SEAT_PRICE)
                .paymentStatus(PaymentStatus.WAITING)
                .build();

        saveReservation(newReservation);

        return ReservationResponse.builder()
                .reservationId(newReservation.getId())
                .customerEmail(reservationRequest.getCustomerEmail())
                .message("Successfully created reservation!")
                .errorReason(null)
                .build();
    }

    @Transactional
    public void changePaymentStatusAsPaid(String reservationId) {
        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("Reservation not found");
        }

        if (!PaymentStatus.PAID.equals(reservation.getPaymentStatus())) {
            reservation.setPaymentStatus(PaymentStatus.PAID);
            Reservation savedReservation = reservationRepository.save(reservation);

            if (PaymentStatus.PAID.equals(savedReservation.getPaymentStatus())) {
                eventPublisher.publishEvent(new ReservationChangeStatusEvent(savedReservation));
            }
        }
    }
}
