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

import java.util.regex.Pattern;

/**
 * Exposes methods to define a cleavage site and determine where to cleave the
 * protein if a site matches. It uses a regular expression, a window size to
 * search within (specified as amino acids left, and amino acids right) and a
 * cleave offset to specify how far from the cleave site to cut the protein.
 * <br><br>
 * <strong>EXAMPLE:</strong>
 * <br><br>
 * Trypsin cleaves after R or K, not followed by P and at WKP and MRP.
 * <br><br>
 * To define this filter we want to create a filter that looks one amino acid
 * left and one amino acid right of the potential cut site, and rejects K or R
 * followed by P unless the window is WKP or MRP.
 * <br><br>
 * <pre>CleaveageFilter cf = new CleavageSiteMatcher("(WKP)|(MRP)|[KR][^P]",1,1,1);</pre>
 * <br><br>
 This filter would return the following values for matches()
 <br><pre>
 matches("TYRPT",2)

 Return -1, because YR does not cleave on R when followed by P

 matches("TMRPT",2)

 Return 1, because MRP cleaves after R, and we return the offset from the
 cleave site to cut at (offset is 1 so that we cut after R. If we wanted to
 cleave before R, we would have created the filter with cut offset 0)

 </pre><br>
 *
 * @author Sean Maxwell
 */
public class CleavageSiteMatcher {
    private final Pattern filter;
    private final int left;
    private final int right;
    private final int offset;

    /**
     * Constructor. Compiles the argument regular expression String to a Pattern
     * and stores it with the left, right and offset
     *
     * @param pattern Regular Expression String
     * @param c_left How many amino acids to the left to use in the filter window
     * @param c_right How many amino acids to the right to use in the filter window
     * @param c_os The offset at which to cut from the potential cut site
     */
    public CleavageSiteMatcher(String pattern, int c_left, int c_right, int c_os) {
        filter = Pattern.compile(pattern);
        left   = c_left;
        right  = c_right;
        offset = c_os;
    }

    /**
     * Tests if the defined cut-site pattern matches a site in a protein 
     * sequence.
     *
     * @param sequence The full amino acid sequence
     * @param site The position of the potential cut site in the full sequence
     *
     * @return -1 if the site does not match. It returns the offset
     * to cut at if the site does match.
     */
    public int matches(String sequence, int site) {
        int adjusted_left;
        int pad_left;
        int adjusted_right;
        int pad_right;
        String pad_string = "####################";

        /* Safely set the left boundary of the site, so that if a match occurs
         * so close to the begining of the sequence that we cannot extract the
         * required number of characters to the left that we do not have a
         * out of bounds Java exception */
        if(site < this.left) {
            adjusted_left = 0;
            pad_left = this.left - site;
        }
        else {
            adjusted_left = site - this.left;
            pad_left = 0;
        }

        /* Safely set the right boundary of the site. Same explanation as above,
         * except the match occurs close to the end of the sequence */
        if(site+this.right > sequence.length()-1) {
            adjusted_right = sequence.length();
            pad_right = ((site+this.right)-sequence.length())+1;
        }
        else {
            adjusted_right = site + this.right+1;
            pad_right = 0;
        }

        /* Extract the cleaveage site window for confirmation check */
        String s = pad_string.substring(0,pad_left)+
                   sequence.substring(adjusted_left,adjusted_right)+
                   pad_string.substring(0,pad_right);

        if(this.filter.matcher(s).find()) {
            return this.offset;
        }
        else {
            return -1;
        }

    }

    /**
     * Retrieve the internal offset at which this filter cuts from the potential
     * cut site
     *
     * @return offset
     */
    public int offset() {
        return this.offset;
    }

    public String toString(String indent) {
        return String.format("%s{\"CleavageFilter\":{\n%s\t\"pattern\":\"%s\",\n%s\t\"left\":%d,\n%s\t\"right\":%d,\n%s\t\"offset\":%d\n%s\t}\n%s}\n",indent,indent,filter.pattern(),indent,left,indent,right,indent,offset,indent,indent);
    }
}
