package com.example.nagoyameshi.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
    @Autowired
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) {
        Reservation reservation = new Reservation();
        Restaurant restaurant = restaurantRepository.getReferenceById(reservationRegisterForm.getRestaurantId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        LocalDate orderDate = LocalDate.parse(reservationRegisterForm.getOrderDate());
        LocalTime orderTime = LocalTime.parse(reservationRegisterForm.getOrderTime());

        reservation.setRestaurant(restaurant);
        reservation.setUser(user);
        reservation.setOrderDate(orderDate);
        reservation.setOrderTime(orderTime);
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());

        // ①営業時間外の予約チェック
        if (!isReservableTime(restaurant, orderTime)) {
            throw new IllegalArgumentException("予約可能な時間ではありません。営業時間をご確認ください。");
        }

        // ②同じユーザーによる同日同時間の予約チェック
        if (reservationRepository.existsByUserAndOrderDateAndOrderTime(user, orderDate, orderTime)) {
            throw new IllegalArgumentException("既に同じ日時・時刻に予約があります。");
        }

        // ③同じユーザーによる同日の同店舗への予約チェック
        if (reservationRepository.existsByRestaurantAndOrderDateAndUser(restaurant, orderDate, user)) {
            throw new IllegalArgumentException("既に同日に同じ店舗への予約があります。");
        }

        // ④定休日チェック
        if (isHoliday(restaurant, orderDate)) {
            throw new IllegalArgumentException("指定した日付は定休日です。");
        }

        reservationRepository.save(reservation);
    }

    private boolean isReservableTime(Restaurant restaurant, LocalTime orderTime) {
        return orderTime.isAfter(restaurant.getOpenTime()) && orderTime.isBefore(restaurant.getCloseTime());
    }

    private boolean isHoliday(Restaurant restaurant, LocalDate orderDate) {
        String holidays = restaurant.getHolidays();  // 例: "月曜日,水曜日,金曜日"
        
        if (holidays != null && !holidays.isEmpty()) {
            // 定休日を/または,または空白で分割し、曜日ごとに判定
            String[] holidayArray = holidays.split("[,/]+");
            DayOfWeek orderDayOfWeek = orderDate.getDayOfWeek();  // 予約日の日曜日（例: 月曜日）

            // 定休日が予約日と一致するかチェック
            for (String holiday : holidayArray) {
                switch (holiday.trim()) {
                    case "月曜日":
                        if (orderDayOfWeek == DayOfWeek.MONDAY) {
                            return true;
                        }
                        break;
                    case "火曜日":
                        if (orderDayOfWeek == DayOfWeek.TUESDAY) {
                            return true;
                        }
                        break;
                    case "水曜日":
                        if (orderDayOfWeek == DayOfWeek.WEDNESDAY) {
                            return true;
                        }
                        break;
                    case "木曜日":
                        if (orderDayOfWeek == DayOfWeek.THURSDAY) {
                            return true;
                        }
                        break;
                    case "金曜日":
                        if (orderDayOfWeek == DayOfWeek.FRIDAY) {
                            return true;
                        }
                        break;
                    case "土曜日":
                        if (orderDayOfWeek == DayOfWeek.SATURDAY) {
                            return true;
                        }
                        break;
                    case "日曜日":
                        if (orderDayOfWeek == DayOfWeek.SUNDAY) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;  // 定休日に該当しない場合
    }
}
