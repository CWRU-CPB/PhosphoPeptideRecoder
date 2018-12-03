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
package edu.cwru.pp4j.recode.modsites;

/**
 * Encapsulates a protein modification site.
 * 
 * @author Sean Maxwell
 */
public class ModificationSite {
    private int position;
    private double offset;
    private String residue;
    
    /**
     * Constructor.
     */
    public ModificationSite() {
        
    }
    
    /**
     * Set the position of the modification.
     * @param i Modification position.
     * @return This modification.
     */
    public ModificationSite setPosition(int i) {
        position = i;
        return this;
    }
    
    /**
     * Set the mass of the modified amino acid residue.
     * @param d Modification mass offset.
     * @return This modification.
     */
    public ModificationSite setMass(double d) {
        offset = d;
        return this;
    }
    
    /**
     * Set the amino acid residue abbreviation for the modified residue.
     * @param s Modified amino acid abbreviation.
     * @return This modification.
     */
    public ModificationSite setResidue(String s) {
        residue = s;
        return this;
    }
    
    /**
     * Get the position of the modification.
     * @return Position.
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Get the mass of the modified amino acid residue.
     * @return Mass offset.
     */
    public double getMass() {
        return offset;
    }
    
    /**
     * Get the abbreviation for the modified amino acid.
     * @return Position.
     */
    public String getResidue() {
        return residue;
    }
    
    /**
     * Builds a String friendly version of the modification site.
     * 
     * @return Modification site as a String.
     */
    @Override
    public String toString() {
        return String.format("%s%d",residue,position);
    }
}
