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
 * A minimal base class that specialized peptide classes can extend. It 
 * implements essential methods and a few utility methods for common tasks,
 * such as computing the peptide mass with water loss, or computing an m/z 
 * value for a peptide.
 * 
 * @author Sean Maxwell
 */
public class PeptideSimple implements Peptide {
    /**
     * Internal member to store amino acid sequence of peptide.
     */
    protected String   sequence;
    
    /**
     * Internal member to store start position of peptide in full protein.
     */
    protected int      start;
    
    /**
     * Internal member to store end position of peptide in full protein.
     */
    protected int      end;
    
    /**
     * Internal member to store length in amino acids of peptide.
     */
    protected int      length;
    
    /**
     * Internal member to store mass of peptide.
     */
    protected double   totalMass;
    
    private void init(String s, int position) {
        sequence = s;
        start    = position;
        end      = position+s.length()-1;
        length   = s.length();
        totalMass = 0.0;
    }
    
    /**
     * Constructor initializes internal data using argument sequence and 
     * position.
     * @param s Amino acid sequence of peptide
     * @param position Position of peptide in full protein sequence
     */
    public PeptideSimple(String s, int position) {
        init(s,position);
    }
    
    /**
     * Evaluate the amino acid sequence to determine if the sequence is likely 
     * to exhibit water loss.
     * 
     * @return True for likely, false of not likely
     */
    public boolean probableWaterLoss() {
        int i;
        char r;

        /* Check for E at N-terminus */
        if(sequence.charAt(0) == 'E') {
            return true;
        }
        
        for(i=0;i<sequence.length();i++) {
            r = sequence.charAt(i);
            if(r == 'S' || r == 'T') {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Evaluate the amino acid sequence to determine if the sequence is likely 
     * to exhibit ammonia loss.
     * 
     * @return True for likely, false for not likely
     */
    public boolean probableAmmoniaLoss() {
        int i;
        char r;
        
        for(i=0;i<sequence.length();i++) {
            r = sequence.charAt(i);
            if(r == 'R' || r == 'K' || r == 'Q' || r == 'N') {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Compute the mass of the peptide with water loss.
     * 
     * @return Peptide mass
     */
    public double removeWater() {
        return totalMass-Constants.MASS_WATER;
    }
    
    /**
     * Computes the mass of the peptide with water added.
     * 
     * @return Peptide mass 
     */
    public double addWater() {
        return totalMass + Constants.MASS_WATER;
    }
    
    /**
     * Computes the mass of the peptide with ammonia removed.
     * 
     * @return Peptide mass. 
     */
    public double removeAmmonia() {
        return totalMass - Constants.MASS_AMMONIA;
    }
    
    /**
     * Computes the mass of the peptide with carbon monoxide removed.
     * 
     * @return Peptide mass. 
     */
    public double removeCarbonMonoxide() {
        return totalMass - (Constants.MASS_CARBON+Constants.MASS_OXYGEN);
    }
    
    /**
     * Computes an m/z value for the peptide.
     * 
     * @param z That charge state to use
     * 
     * @return Peptide m/z
     */
    public double applyCharge(int z) {
        return (totalMass+(z*Constants.MASS_PROTON))/z;
    }
    
    /**
     * @return The start position of the peptide in the full protein sequence. 
     */
    @Override
    public int start() {
        return start;
    }

    /**
     * @return The end position of the peptide in the full protein sequence.
     */
    @Override
    public int end() {
        return end;
    }
    
    /**
     * @return The length of the peptide in amino acids.
     */
    @Override
    public int length() {
        return length;
    }

    /**
     * @return The amino acid sequence of the peptide.
     */
    @Override
    public String sequence() {
        return sequence;
    }

    /**
     * @return The mass of the peptide.
     */
    @Override
    public double mass() {
        /**
         * @TODO implement the correct MI mass calculation in #init and return
         * here.
         * 
         * return totalMass;
         */
        
        throw new UnsupportedOperationException("Not supported.");
    }
}
