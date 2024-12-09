/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modal;

import java.util.Arrays;

/**
 * Enum to represent the types of meals.
 * 

 */



public enum MealType {
    BREAKFAST(0),
    LUNCH(1),
    DINNER(2),
    SNACK(3);

    private final int value;

    MealType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MealType fromString(String type) {
        for (MealType mealType : MealType.values()) {
            if (mealType.name().equalsIgnoreCase(type)) {
                return mealType;
            }
        }
        return null;
    }
}


