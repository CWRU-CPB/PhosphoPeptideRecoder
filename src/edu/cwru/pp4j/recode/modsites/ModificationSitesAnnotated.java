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
 * Encapsulates a set of modification sites that are divided into "known" and
 * "unknown". For example, if a peptide was identified by tandem MS and
 * only one of them has been previously detected experimentally, then one site 
 * would be "known" and one would be "unknown".
 * 
 * @author Sean Maxwell
 */
public class ModificationSitesAnnotated {
    ModificationSites known;
    ModificationSites unknown;
    
    /**
     * Constructor initializes new empty object.
     */
    public ModificationSitesAnnotated() {
        known = new ModificationSites();
        unknown = new ModificationSites();
    }
    
    /**
     * Add a new known site.
     * @param s Modification suite (amino acid and position, e.g. S12)
     * @param i Site index (position within parent sequence)
     */
    public void addKnown(ModificationSite s, int i) {
        known.addSite(s);
        known.addIndex(i);
    }
    
    /**
     * Add a new unknown site.
     * @param s Modification suite (amino acid and position, e.g. S12)
     * @param i Site index (position within parent sequence)
     */
    public void addUnknown(ModificationSite s, int i) {
        unknown.addSite(s);
        unknown.addIndex(i);
    }
    
    /**
     * Fetch all known sites stored in object.
     * @return modification sites.
     */
    public ModificationSites getKnown() {
        return known;
    }
    
    /**
     * Fetch all unknown sites stored in object.
     * @return modification sites.
     */
    public ModificationSites getUnknown() {
        return unknown;
    }
}
