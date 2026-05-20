package school.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InputHelper {

    public static int intInput(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    public static double doubleInput(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    public static LocalDate dateInput(Scanner sc) {
        while (true) {
            try {
                return LocalDate.parse(sc.nextLine().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                System.out.print("Invalid date. Use YYYY-MM-DD: ");
            }
        }
    }
}
