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

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a set of modification sites.
 * 
 * @author Sean Maxwell
 */
public class ModificationSites {
    List<ModificationSite> sites;
    List<Integer> indices;
   
    /**
     * Constructor initializes a new empty set.
     */
    public ModificationSites() {
        sites = new ArrayList<>();
        indices = new ArrayList<>();
    }
    
    /**
     * Add a modification site to the set.
     * @param s the modification site to be added
     */
    public void addSite(ModificationSite s) {
        sites.add(s);
    }
    
    /**
     * The position of a modification site. This is used to reference the site location 
     * relative to the parent element. I.e., if the parent is a peptide, then
     * these indices are the position of the site relative to the peptide start,
     * and if the parent is a full protein these indices are relative to the
     * start of the protein. These are expected to be in one-to-one 
     * correspondence with sites so that modification site i and index
     * i refer to the same entity.
     * @param i Site index.
     */
    public void addIndex(Integer i) {
        indices.add(i);
    }
    
    /**
     * Get all modification sites.
     * @return THe full set of modification sites.
     */
    public List<ModificationSite> getSites() {
        return sites;
    }
    
    /**
     * Get all modification site indices.
     * @return All modification site positions within the parent amino acid 
     * sequence.
     */
    public List<Integer> getIndices() {
        return indices;
    }
    
    /**
     * Report how many modifications are stored in the set.
     * @return Number of modifications stored in this object.
     */
    public int size() {
        return indices.size();
    }
}
