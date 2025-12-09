package com.example.dweb_App.data.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public enum PointsCategories {

    CATEGORY_0(0,new BigDecimal("0.00"),"Catégorie 0", LocalTime.of(19,00)),
    CATEGORY_A( 100,new BigDecimal("200.00"), "Catégorie A", LocalTime.of(19,00)),
    CATEGORY_B( 50,new BigDecimal("100.00"), "Catégorie B",LocalTime.of(22,00)),
    CATEGORY_C(25,new BigDecimal("50.00"), "Catégorie C", LocalTime.of(00,00));


    public final int points;
    public final BigDecimal bonusAmount;
    public final String frenchName;
    public final LocalTime timeInterval;

    PointsCategories(int points, BigDecimal bonusAmount, String frenchName, LocalTime timeInterval) {
        this.points = points;
        this.bonusAmount = bonusAmount;
        this.frenchName = frenchName;
        this.timeInterval = timeInterval;
    }

    public static PointsCategories  formValue(String stringDate){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);

        LocalDateTime dateTime = LocalDateTime.parse(stringDate, formatter);
        LocalTime timeOnly = dateTime.toLocalTime();

        LocalTime rightDate = LocalTime.of(timeOnly.getHour(), timeOnly.getMinute());

        if(rightDate.isBefore(CATEGORY_0.timeInterval)) return CATEGORY_0;
        else if(rightDate.isAfter(CATEGORY_A.timeInterval) && rightDate.isBefore(CATEGORY_B.timeInterval)) return CATEGORY_A;
        else if(rightDate.isAfter(CATEGORY_B.timeInterval) && rightDate.isBefore(CATEGORY_C.timeInterval)) return CATEGORY_B;
        else return CATEGORY_C;
    }

    public static PointsCategories getCategoryByPoints(int points){

        for(PointsCategories category:values()){
            if(points==category.points){
                return category;
            }

        }
        return null;
    }
}
