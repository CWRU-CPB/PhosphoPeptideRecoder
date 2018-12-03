/* Copyright 2018 Case Western Reserve University
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.cwru.pp4j.recode.proteins;

/**
 * Provides methods to generate all choose(n,k) sets of n objects taken k at a
 * time.
 *
 * For <strong>n</strong> symbols taken <strong>k</strong> at a time, the
 * algorithm conceptually creates an array of length
 * <strong>n</strong>, and assigns <strong>k</strong> pointers to it at the
 * left-most possible non-overlapping positions. The pointers are slid along
 * the array until they have all reached the rightmost positions. The
 * combination indices correspond to the positions of the pointers on the
 * array.<br><br>
 *
 * Example: <strong>n</strong>=5,<strong>k</strong>=3
 *
 * <pre>
 * SYMBOLS : 1 2 3 4 5
 * POINTERS: | | |
 *
 * 1 2 3 4 5    COMBINATION
 * | | |        1 = (1,2,3)
 * | |   |      2 = (1,2,4)
 * | |     |    3 = (1,2,5)
 * |   | |      4 = (1,3,4)
 * |   |   |    5 = (1,3,5)
 * |     | |    6 = (1,4,5)
 *   | | |      7 = (2,3,4)
 *   | |   |    8 = (2,3,5)
 *   |   | |    9 = (2,4,5)
 *     | | |   10 = (3,4,5)
 * </pre>
 *
 * In practice, there is not an actual array, and the pointer positions and 
 * stop positions are stored as integers in private member arrays of length
 * <strong>k</strong>. However, the explanation and example are more clear when
 * using an array to visualize what is meant by sliding the pointers.
 *
 * @author Sean Maxwell
 *
 */
public class Combinatorics {

    /*************************************************************************/
    /*                          PRIVATE MEMBERS                              */
    /*************************************************************************/

    /**
     * Private member to store the pointer positions for the symbol position
     * array.
     */
    private final int[] pointers;

    /**
     * Private member to hold the position at which a pointer cannot be moved
     * farther right
     */
    private final int[] stops;

    /**
     * The number of symbols to choose at a time, specified when calling the
     * Constructor.
     */
    private final int pk;

    /**
     * Internal error message
     */
     private String error = "OK";

     /*************************************************************************/
     /*                          PRIVATE METHODS                              */
     /*************************************************************************/

     /**
      * Recursively tries to move the least significant pointer to the next 
      * position. Recursion happens when a pointer has reached it's stop 
      * position and the next most significant pointer must be moved instead.
      *
      * @param p pointer position in pointers[] to attempt to move.
      *
      * @return boolean status of the call. A return value of true indicates 
      * a least one pointer was moved (bubbled) and a return value of false 
      * means that all pointers have reached their stop positions.
      */
     private boolean bubblePointers(int p) {
         /* If we are calling with an invalid pointer */
         if(p < 0) {
             return false;
         }

         /* IF this pointer is at it's stop position */
         if(this.pointers[p] == this.stops[p]) {
             /* IF this is the most significant pointer, and it is at it's stop
              * position, return false because there is nothing left to do.
              */
             if(p == 0) {
                 return false;
             }

             /* ELSE there are still more significant pointers to move */
             else {
                 /* Try to recurse to the next most significant pointer.
                  *
                  * IF the recursion returns true, home this pointer to the next
                  * most significant pointers new position. */
                 if(this.bubblePointers(p-1)) {
                     this.pointers[p] = this.pointers[p-1]+1;
                     return true;
                 }

                 /* ELSE the recursion failed. Everything is at it's stop point,
                  * Return false up the chain. */
                 else {
                     return false;
                 }
             }
        }

        /* ELSE this pointer still has room to move right, so shift it right
         * one place. */
        else {
            this.pointers[p]++;
        }

         return true;
     }

     /*************************************************************************/
     /*                          PUBLIC  METHODS                              */
     /*************************************************************************/

    /**
     * Creates a new Combinatorics object initialized to process a set of 
     * <strong>n</strong> symbols <strong>k</strong> at a time.
     *
     * @param n The number of symbols available.
     * @param k The number of symbols to choose at a time.
     *
     */
    public Combinatorics(int n, int k) {
        /* If k is greater than n, 0 subsets can be drawn  */
        if(k > n) {
            k = 0;
            n = 0;
        }
        pk       = k;
        pointers = new int[k];
        stops    = new int[k];
        int i;

        /* Initialize both the pointer array and the stop position array */
        for(i=0;i<k;i++) {
            pointers[i] = i;
            stops[i]    = n-(k-i);
        }

    }

    /**
     * Returns the private member String containing error messages.
     * 
     * @return last error message assigned to the private member "error"
     */
    public String getLastError() {
        return this.error;
    }

    /**
     * Calculates the number of possible combinations of <strong>n</strong>
     * symbols taken <strong>k</strong> at a time using the multiplicative
     * formula.
     *
     * @param n Number of symbols available
     * @param k Number of symbols to choose at a time from set of size
     *          <strong>n</strong> symbols.
     *
     * @return Number of possible combinations in long format
     */
    public final long nChooseK(int n, int k) {
        double t = 1;
        long   i = 0;

        for(i=0;i<k;i++) {
            t = t*(n-i);

            /* Check for overflow before division */
            if(((long)(t+0.5) == java.lang.Long.MAX_VALUE)) {
                this.error="Overflow in NChooseK. Too many possibilities.";
                return -1L;
            }
            
            t = t/(k-i);

        }
        return (long)(t+0.5);
    }

    /**
     * Returns the current combination from the object, without updating the
     * internal state.
     *
     * @return combination as int array
     *
     * @see #getNext()
     */
    public int[] getCurrent() {
        return this.pointers;
    }

    /**
     * Returns the next combination from the object, updating the objects
     * internal state. To go to a specific combination use GoTo(long).
     *
     * @return combination as int array
     *
     */
    public int[] getNext() {
        boolean status;

        status = this.bubblePointers(pk-1);
        if(status == false) {
            return new int[0];
        }
        else {
           return this.pointers;
        }
    }
    
}