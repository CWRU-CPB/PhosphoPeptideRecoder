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
package edu.cwru.pp4j.recode.recoding;

/**
 * Encapsulates the result of recoding a protein sequence (an amino acid 
 * sequence containing custom amino acids and translating them back to standard
 * amino acids). This includes a sequence containing all standard amino acids
 * and a list of residues that were translated back to standard amino acids 
 * (which are modification sites).
 * 
 * @author Sean Maxwell
 */
public class ReverseRecodeResult {
    public ResidueList residues;
    private final StringBuilder sequence;
    
    public ReverseRecodeResult() {
        residues = new ResidueList();
        sequence = new StringBuilder();
    }
    
    public void addResidue(Residue r) {
        residues.add(r);
    }
    
    public ResidueList getResidues() {
        return residues;
    }
    
    public void appendSequence(String aa) {
        sequence.append(aa);
    }
    
    public String getSequence() {
        return sequence.toString();
    }
}
