package ru.hse.kuzyaka.stackcalculator;

import javax.naming.OperationNotSupportedException;
import java.util.Scanner;
import java.util.Stack;

public class StackCalculator {
    private Stack<Integer> calculator;
    private Scanner scanner;
    public StackCalculator(Stack<Integer> stack) {
        calculator = stack;
    }


    public int evaluate(String expression) throws OperationNotSupportedException {
        if(expression.trim().isEmpty()) {
            if(calculator.size() > 1) {
                throw new IllegalArgumentException();
            } else {
                return calculator.pop();
            }
        }
        int toSkip;
        String trimExpression = expression.trim();
        try {
            int index = trimExpression.indexOf(' ');
            if (index == -1) {
                index = trimExpression.length();
            }
            int num = Integer.parseInt(trimExpression.substring(0, index));
            calculator.push(num);
            toSkip = String.valueOf(num).length();
        } catch (NumberFormatException e) {
            char op = trimExpression.charAt(0);
            toSkip = 1;
            int second = calculator.pop();
            int first = calculator.pop();
            switch (op) {
                case '+':
                    calculator.push(second + first);
                    break;
                case '*':
                    calculator.push(second * first);
                    break;
                case '/':
                    calculator.push(first / second);
                    break;
                case '-':
                    calculator.push(first - second);
                    break;
                default:
                    throw (new OperationNotSupportedException());
            }
        }
        return evaluate(trimExpression.substring(toSkip));
    }
}
