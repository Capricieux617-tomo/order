package com.example.nagoyameshi.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	public boolean existsByRestaurantAndOrderDateAndOrderTimeAndNumberOfPeople(Restaurant restaurant,
			LocalDate orderDate, LocalTime orderTime, int numberOfPeople);
	
	public boolean existsByRestaurantAndOrderDate(Restaurant restaurant, LocalDate orderDate);


	public boolean existsByUserAndOrderDateAndOrderTime(User user, LocalDate orderDate, LocalTime orderTime);

	public boolean existsByRestaurantAndOrderDateAndOrderTimeAndUser(Restaurant restaurant, LocalDate orderDate,
			LocalTime orderTime, User user);

	public boolean existsByRestaurantAndOrderDateAndUser(Restaurant restaurant, LocalDate orderDate, User user);

	

}
