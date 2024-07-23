package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.dto.response.ReservationResponse;
import pl.smyk.customerservice.mapper.ReservationMapper;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.ReservationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;


    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }

    public Reservation findById(String id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    public List<ReservationDto> findReservationsByCustomerEmail(String customerEmail) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getCustomerEmail().equals(customerEmail))
                .map(ReservationMapper.INSTANCE::reservationToReservationDto)
                .collect(Collectors.toList());
    }

//    public ReservationResponse createReservation() {
//        //handle reservation bussines logic
//        return new ReservationResponse();
//    }


}
