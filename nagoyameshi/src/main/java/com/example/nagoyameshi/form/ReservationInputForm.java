package com.example.nagoyameshi.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {

  @NotNull(message = "定休日を確認の上、予約日を選択してください。")
  private LocalDate orderDate; 

  @NotNull(message = "予約時間を選択してください。")
  private LocalTime orderTime; 
  
  @NotNull(message = "人数を入力してください。")
  @Min(value = 1, message = "人数は1人以上に設定してください。")
  private Integer numberOfPeople;
  
 
}