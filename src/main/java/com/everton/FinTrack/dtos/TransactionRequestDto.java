package com.everton.FinTrack.dtos;

import org.springframework.web.multipart.MultipartFile;
import com.everton.FinTrack.enums.Category;
import com.everton.FinTrack.enums.MethodPayment;
import com.everton.FinTrack.enums.Type;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TransactionRequestDto {

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor deve ser maior que zero")
    @Digits(integer = 15, fraction = 2, message = "Valor inválido")
    @Column(precision = 15, scale = 2)
    private BigDecimal value;

    @PastOrPresent(message = "A data não pode ser futura")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Type type; //Entry or Expense

    @Enumerated(EnumType.STRING)
    private MethodPayment payment; // pix,debit,credit,money

    @Enumerated(EnumType.STRING)
    private Category category; // Food,Fuel,maintenance,leisure, expense_bill

    private String observation;

    private String receiptFileId;

    private String receiptFileUrl;

      private MultipartFile file;

}
