package com.bravedroid.presentation;

import com.bravedroid.domain.StandardDeviationCalculator;
import com.bravedroid.util.Logger;
import com.bravedroid.util.Printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.bravedroid.presentation.StandardDeviationPrinter.InputHandler.*;

public class StandardDeviationPrinter {
  private final Logger logger;
  private BufferedReader input;
  private List<Double> numbers = new ArrayList<>();

  public StandardDeviationPrinter(Logger logger) {
    this.logger = logger;
    input = new BufferedReader(new InputStreamReader(System.in));
  }

  public void printStandardDeviation() throws IOException {
    loop:
    while (true) {
      Printer.print("Enter a number :");
      String responseTime = input.readLine();

      InputHandler inputHandler = new InputHandler();
      int result = inputHandler.processInput(responseTime);

      switch (result) {
        case MUST_EXIT:
          throw new MustExitException();
        case IS_BLANK:
          logger.log(numbers);
          Printer.print("Don't enter a blank name ");
          break;
        case IS_NUMERIC:
          logger.log(numbers);
          Printer.print("Input is not numeric ");
          break;
        case DONE:
          double timeInMilliseconds = Double.parseDouble(responseTime.trim());
          numbers.add(timeInMilliseconds);
          logger.log(numbers);
          break;
        case VALID_INPUT:
          printStatistics();
          break loop;
        default:
          throw new IllegalArgumentException("unknown result " + result);
      }
    }
  }

  private void printStatistics() {
    StandardDeviationCalculator standardDeviationCalculator = new StandardDeviationCalculator(numbers);
    final double mean = standardDeviationCalculator.calculateAverage();
    final double minimum = standardDeviationCalculator.findMinimum();
    final double maximum = standardDeviationCalculator.findMaximum();
    final double variance = standardDeviationCalculator.getVariance(mean);
    final double standardDeviation = standardDeviationCalculator.getStandardDeviation(variance);

    Printer.print("The average is " + mean);
    Printer.print("The minimum is " + minimum);
    Printer.print("The maximum is " + maximum);
    Printer.print("The standard deviation is " + standardDeviation);
  }

  private boolean isNumeric(String input) {
    boolean isNumeric = true;
    try {
      Long.parseLong(input);
    } catch (IllegalArgumentException ex) {
      isNumeric = false;
    }
    return isNumeric;
  }

  private boolean isBlank(String value) {
    return value.trim().equals("");
  }

  private boolean mustExitMethod(String input) {
    return input.equals("exit");
  }

  public static class MustExitException extends RuntimeException {
    MustExitException() {
      super("user requested exit exception");
    }
  }

  class InputHandler {
    static final int MUST_EXIT = 0;
    static final int IS_BLANK = 1;
    static final int DONE = 2;
    static final int IS_NUMERIC = 3;
    static final int VALID_INPUT = 4;

    int processInput(String responseTime) {
      if (mustExitMethod(responseTime.trim())) return MUST_EXIT;
      if (isBlank(responseTime)) return IS_BLANK;
      if (!isNumeric(responseTime.trim()) && !responseTime.trim().equals("done")) return DONE;
      if (isNumeric(responseTime.trim())) return IS_NUMERIC;
      return VALID_INPUT;

    }
  }
}
