package io.hhplus.concertreservationservice.application.service.concert

import io.hhplus.concertreservationservice.application.service.concert.request.CreateReserveSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.application.service.concert.response.CreateReservedSeatInfo
import io.hhplus.concertreservationservice.application.service.payment.response.ProcessPaymentInfo
import io.hhplus.concertreservationservice.domain.concert.exception.InvalidReservationStateException
import io.hhplus.concertreservationservice.domain.concert.exception.ReservationNotFoundException
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.hhplus.concertreservationservice.domain.concert.repository.SeatReservationRepository
import io.hhplus.concertreservationservice.domain.payment.exception.PaymentNotCompletedException
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.domain.reservation.exception.AlreadyReservedException
import io.hhplus.concertreservationservice.domain.reservation.extension.toCreateReservedSeatInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: SeatReservationRepository,
    private val seatRepository: SeatRepository,
) {
    fun validateReservation(reservationId: Long): SeatReservation {
        val reservation =
            reservationRepository.findReservationWithLock(reservationId)
                .orElseThrow { ReservationNotFoundException(reservationId) }

        if (!reservation.isReserved() || reservation.isExpired()) {
            throw InvalidReservationStateException(reservation.id)
        }

        return reservation
    }

    fun completeReservation(
        reservation: SeatReservation,
        paymentInfo: ProcessPaymentInfo,
    ) {
        if (!reservation.isCompletePayment(paymentInfo.paymentId)) {
            throw PaymentNotCompletedException(paymentInfo.paymentId)
        }
        reservationRepository.saveReservation(reservation)
    }

    //    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Transactional
    fun createReservationInfo(command: CreateReserveSeatCommand): CreateReservedSeatInfo {
        // 좌석 예약 유무 확인
        val reservedSeat = reservationRepository.findReservedSeatWithLock(command.seatNo, command.scheduleId)

        if (reservedSeat.isPresent) {
            throw AlreadyReservedException(command.seatNo)
        }

        val seat =
            seatRepository.getSeatForReservationWithLock(ReserveSeatCommand(command.scheduleId, command.seatNo))
                .orElseThrow {
                    throw SeatNotFoundException()
                }

        val seatReservation =
            SeatReservation(
                user = command.user,
                seat = seat,
                paymentId = null,
            )
        val savedReservation = reservationRepository.createReservation(seatReservation)

        return savedReservation.toCreateReservedSeatInfo(seat.no)
    }
}
