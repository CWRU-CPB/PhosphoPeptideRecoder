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

import edu.cwru.pp4j.recode.proteins.Peptide;

/**
 * Implements methods to parse potential modification sites from amino acid
 * sequences. E.g., for a peptide HTL at the N-terminus of a protein, the site
 * T2 is a candidate modification site.
 * 
 * @author Sean Maxwell
 */
public class ModificationSiteParser {
    /**
     * Parse all candidate modification sites from a peptide.
     * 
     * @param peptide amino acid sequence to parse
     * @return all potential modification sites (all S/T/Y amino acids)
     * @throws Exception if the request cannot be fulfilled.
     */
    public static ModificationSites parsePhosphorylationSites(Peptide peptide) throws Exception {
        String[] aminoAcids = {"S","T","Y"};
        ModificationSites modifications = new ModificationSites();
        String sequence = peptide.sequence();
        int start = peptide.start();
        int thisAminoAcid = 0;
        
        /* Find all potential phosphorylation sites along peptide */
        for(String aminoAcid : aminoAcids) {
            int lastAminoAcid = -1;
            
            /* While there are still [ in the peptide that we have not parsed yet */
            while((thisAminoAcid = sequence.indexOf(aminoAcid,lastAminoAcid+1)) != -1) {
                /* Add to list of String indices so that sites can easily be
                 * manipulated */
                modifications.addIndex(thisAminoAcid);
                
                /* Modification sites are indexed, starting at 1 in scientific
                 * literature, so we add 1 to the position below to shift string
                 * indices which start at 0 to the corresponding protein index*/
                int position = thisAminoAcid+start+1;

                /* Add modification site to return list */
                ModificationSite modification = new ModificationSite();
                modification.setPosition(position).setResidue(aminoAcid);
                modifications.addSite(modification);

                lastAminoAcid = thisAminoAcid;
            }
        }

        return modifications;
    }
    
    public static int nRecodes(String sequence) {
        int n = 0;
        for(int i=0;i<sequence.length();i++) {
            if(sequence.charAt(i) == 'B' || sequence.charAt(i) == 'U' || sequence.charAt(i) == 'Z') {
                n++;
            }
        }
        return n;
    }
}
