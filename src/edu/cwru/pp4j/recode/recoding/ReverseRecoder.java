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
 * Translates a protein sequence containing non-standard amino acids to one 
 * containing all standard amino acids. 
 * 
 * @author Sean Maxwell
 */
public class ReverseRecoder {
    /**
     * Translate a protein sequence containing phosphorylation sites coded as
     * custom amino acids back to a standard amino acid sequence and a list
     * of modification sites.
     * 
     * @param sequence protein sequence to process
     * @param sequenceOffset protein sequence offset from beginning of full protein 
     * @return the result reverse recoding
     */
    public static ReverseRecodeResult reverseCodePhosphorylationSites(String sequence, Integer sequenceOffset) {
        ReverseRecodeResult rrr = new ReverseRecodeResult();

        for(int i=0;i<sequence.length();i++) {
            String aa = sequence.substring(i, i+1);
            switch (aa) {
                case "B":
                    rrr.addResidue(new Residue(i+sequenceOffset,"S"));
                    rrr.appendSequence("s");
                    break;
                case "U":
                    rrr.addResidue(new Residue(i+sequenceOffset,"T"));
                    rrr.appendSequence("t");
                    break;
                case "Z":
                    rrr.addResidue(new Residue(i+sequenceOffset,"Y"));
                    rrr.appendSequence("y");
                    break;
                default:
                    rrr.appendSequence(aa);
                    break;
            }
        }
        
        return rrr;
    }
}
