/******************************************************************************
 * Copyright (c) 2012. Pogoda M.V.                                            *
 *                                                                            *
 * Permission is hereby granted, free of charge, to any person obtaining a    *
 * copy of this software and associated documentation files (the "Software"), *
 * to deal in the Software without restriction, including without limitation  *
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,   *
 * and/or sell copies of the Software, and to permit persons to whom the      *
 * Software is furnished to do so, subject to the following conditions:       *
 *                                                                            *
 * The above copyright notice and this permission notice shall be included in *
 * all copies or substantial portions of the Software.                        *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,   *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL    *
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING    *
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER        *
 * DEALINGS IN THE SOFTWARE.                                                  *
 ******************************************************************************/

package com.netcracker.lab4;

/**
 * Created by IntelliJ IDEA.
 * User: mpogoda
 * Date: 22/01/12
 * Time: 09:44
 */
public class BigInt implements Comparable<BigInt> {
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

        int length = number.length();

        if (length % 4 != 0) {
            number = ((length % 4 == 1) ? "000" :
                      (length % 4 == 2) ? "00" :
                      "0") + number;
        }

        chain = new int[number.length() / 4];
        for (int i = 0; i < chain.length; ++i) {
            chain[chain.length - i - 1] = new Integer(number.substring(4 * i, 4 * (i + 1)));
        }
    }

    public BigInt(BigInt other) {
        this.negative = other.negative;
        this.chain = new int[other.chain.length];
        System.arraycopy(other.chain, 0, this.chain, 0, other.chain.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(4 * chain.length + (negative ? 1 : 0));
        if (negative) {
            sb.append('-');
        }

        for (int i = chain.length - 1; i >= 0; --i) {
            sb.append(String.format("%04d", chain[i]));
        }
        return prepareString(sb.toString());
    }

    private BigInt internalPlus(BigInt other) {
        int carry = 0;
        for (int i = 0; i < chain.length; ++i) {
            chain[i] += carry;
            if (i < other.chain.length) {
                chain[i] += other.chain[i];
            } else if (carry == 0) {
                break;
            }
            if (chain[i] > 9999) {
                carry = 1;
                chain[i] -= 10000;
            } else {
                carry = 0;
            }
        }

        if (carry != 0) {
            int[] newChain = new int[chain.length + 1];
            System.arraycopy(chain, 0, newChain, 0, chain.length);
            newChain[chain.length] = carry;
            chain = newChain;
        }

        return this;
    }

    private BigInt internalMinus(BigInt other) {
        int carry = 0;
        for (int i = 0; i < chain.length; ++i) {
            chain[i] -= carry;
            if (i < other.chain.length) {
                chain[i] -= other.chain[i];
            } else if (carry == 0) {
                break;
            }

            if (chain[i] < 0) {
                carry = 1;
                chain[i] += 10000;
            } else {
                carry = 0;
            }
        }

        int i = chain.length - 1;
        while (chain[i] == 0) {
            --i;
        }
        if (i < (chain.length - 1)) {
            int[] newChain = new int[i + 1];
            System.arraycopy(chain, 0, newChain, 0, i + 1);
            chain = newChain;
        }

        return this;
    }

    public BigInt plus(BigInt other) {
        BigInt n1 = this;
        BigInt n2 = other;

        if (negative == other.negative) {
            boolean comp = this.compareTo(other) > 0;
            if (comp == negative) {
                n2 = this;
                n1 = other;
            }
            return (new BigInt(n1)).internalPlus(n2);
        }

        negative = !negative;
        boolean comp = (this.compareTo(other) > 0);
        negative = !negative;

        if (comp != negative) {
            n2 = this;
            n1 = other;
        }

        return (new BigInt(n1)).internalMinus(n2);
    }

    public BigInt minus(BigInt other) {
        other.negative = !other.negative;
        BigInt result = this.plus(other);
        other.negative = !other.negative;

        return result;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p/>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p/>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p/>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p/>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(BigInt other) {
        if (negative && !other.negative) {
            return -1;
        }
        if (!negative && other.negative) {
            return 1;
        }

        int neg = negative ? -1 : 1;

        if (other.chain.length > chain.length) {
            return -neg;
        }
        if (other.chain.length < chain.length) {
            return neg;
        }

        for (int i = chain.length - 1; i >= 0; --i) {
            if (chain[i] == other.chain[i]) {
                continue;
            }

            return (chain[i] - other.chain[i]) * neg;
        }

        return 0;
    }

}
