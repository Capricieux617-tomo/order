package com.example.nagoyameshi.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {

  @NotNull(message = "定休日を確認の上、予約日を選択してください。")
  // コメント: @FutureOrPresent を追加し、過去の日付を指定できないようにすべきです
  private LocalDate orderDate; 

  @NotNull(message = "予約時間を選択してください。")
  // コメント: 時間範囲のカスタムバリデーション（営業時間内かどうかなど）を追加すべきです
  private LocalTime orderTime; 
  
  @NotNull(message = "人数を入力してください。")
  @Min(value = 1, message = "人数は1人以上に設定してください。")
  // コメント: @Max アノテーションで人数の上限（例：20人など）を設定すべきです
  private Integer numberOfPeople;
  
  // コメント: 全体に対するクロスフィールドバリデーション（予約日と時間の組み合わせが有効か）を追加すべきです
}