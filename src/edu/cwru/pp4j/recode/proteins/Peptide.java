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
 * Specifies the minimum contract for classes that represent peptides.
 * 
 * @author Sean Maxwell
 */
public interface Peptide {    
    /**
     * The start position of the peptide in the full protein.
     * 
     * @return Start position
     */
    public int start();
    
    /**
     * The end position of the peptide in the full protein.
     * 
     * @return End position 
     */
    public int end();
    
    /**
     * The length of the peptide in amino acids.
     * 
     * @return Number of amino acids in peptide 
     */
    public int length();
    
    /**
     * The amino acid sequence of the peptide.
     * 
     * @return Amino acid sequence of the peptide. 
     */
    public String sequence();
    
    /**
     * The monoisotopic mass of the peptide.
     * 
     * @return The monoisotopic mass of the peptide. 
     */
    public double mass();
 
}
