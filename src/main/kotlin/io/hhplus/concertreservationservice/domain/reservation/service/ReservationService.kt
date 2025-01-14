package io.hhplus.concertreservationservice.domain.reservation.service

import io.hhplus.concertreservationservice.domain.concert.exception.InvalidReservationStateException
import io.hhplus.concertreservationservice.domain.concert.exception.InvalidReservationUserException
import io.hhplus.concertreservationservice.domain.concert.exception.ReservationNotFoundException
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.hhplus.concertreservationservice.domain.concert.service.request.CreateReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.ValidReservationCommand
import io.hhplus.concertreservationservice.domain.concert.service.response.CreateReservedSeatInfo
import io.hhplus.concertreservationservice.domain.payment.exception.PaymentNotCompletedException
import io.hhplus.concertreservationservice.domain.payment.service.response.ProcessPaymentInfo
import io.hhplus.concertreservationservice.domain.reservation.SeatReservation
import io.hhplus.concertreservationservice.domain.reservation.exception.AlreadyReservedException
import io.hhplus.concertreservationservice.domain.reservation.extension.toCreateReservedSeatInfo
import io.hhplus.concertreservationservice.domain.reservation.repository.SeatReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: SeatReservationRepository,
    private val seatRepository: SeatRepository,
) {
    fun validateReservation(command: ValidReservationCommand): SeatReservation {
        val reservation =
            reservationRepository.findReservationWithLock(command.reservationId) ?: throw ReservationNotFoundException(
                command.reservationId,
            )

        with(reservation) {
            require(user.id == command.userId) {
                throw InvalidReservationUserException(id, command.userId)
            }

            require(isReserved() && !isExpired()) {
                throw InvalidReservationStateException(id)
            }
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

    @Transactional
    fun createReservationInfo(command: CreateReserveSeatCommand): CreateReservedSeatInfo {
        // 좌석 예약 유무 확인
        val reservedSeat =
            reservationRepository.findReservedSeatWithLock(command.seatNo, command.scheduleId)
                ?.let { throw AlreadyReservedException(command.seatNo) }

        val seat =
            seatRepository.getSeatForReservationWithLock(ReserveSeatCommand(command.scheduleId, command.seatNo))
                ?: throw SeatNotFoundException()

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
