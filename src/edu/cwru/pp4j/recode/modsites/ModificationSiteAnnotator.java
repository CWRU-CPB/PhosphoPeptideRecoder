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
 * Defines the contract that must be implemented by modification site annotating
 * classes.
 * 
 * @author Sean Maxwell
 */
public interface ModificationSiteAnnotator {
    /**
     * Separate a query set of modification sites into "known" and "unknown"
     * based on those that exist in this annotator.
     * @param accession Protein of interest
     * @param querySites Sites to separate
     * @return Sites separated into known and unknown
     * @throws Exception if the request cannot be fulfilled.
     */
    public ModificationSitesAnnotated annotate(String accession, ModificationSites querySites) throws Exception;
        
    /**
     * Test if a modification site annotator is empty (a null annotator).
     * 
     * @return true if the annotator contains no sites, and false otherwise. 
     */
    public boolean isEmpty();
    
    /**
     * Performs any action necessary to close the object and free resources.
     * @throws Exception if the request cannot be fulfilled.
     */
    public void finish() throws Exception;
}