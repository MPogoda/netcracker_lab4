/*
 * Copyright (c) 2012. Pogoda M.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.netcracker.lab4;

/**
 * Created by IntelliJ IDEA.
 * User: mpogoda
 * Date: 22/01/12
 * Time: 09:44
 */
public class BigInt {
    private int     length;
    private int[]   chain;
    private boolean negative;

    static String prepareString(String input) {
        input = input.trim();

        if (input.charAt(0) == '+') {
            input = input.substring(1);
        }

        int i = 0;
        boolean negative = false;
        if (input.charAt(0) == '-') {
            ++i;
            negative = true;
        }
        while ((i < input.length()) && (input.charAt(i) == '0')) {
            ++i;
        }

        if (i < input.length()) {
            if (i > ((negative) ? 1 : 0)) {
                input = (negative ? "-" : "") + input.substring(i);
            }
        } else {
            input = "0";
        }

        return input;
    }

    public BigInt(String number) {
        if (number.charAt(0) == '-') {
            negative = true;
            number = number.substring(1);
        }

        length = number.length();

        if (length % 4 != 0) {
            number = ((length % 4 == 1) ? "000" :
                      (length % 4 == 2) ? "00" :
                      "0") + number;
        }

        chain = new int[number.length() / 4];
        for (int i = 0; i < chain.length; ++i) {
            chain[i] = new Integer(number.substring(4 * i, 4 * (i + 1)));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(length + (negative ? 1 : 0));
        if (negative) {
            sb.append('-');
        }
        for (int n : chain) {
            sb.append(String.format("%04d", n));
        }
        return prepareString(sb.toString());
    }
}
