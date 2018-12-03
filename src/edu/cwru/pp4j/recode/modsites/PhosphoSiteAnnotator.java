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
 * A class to annotate protein modification sites. Requires a database of
 * phosphorylation sites to have been imported by a 
 * ModificationSiteDatabaseImporter which is the source of modification 
 * sites for this object.
 * 
 * @author Sean Maxwell
 */
public class PhosphoSiteAnnotator implements ModificationSiteAnnotator {
    private final ModificationSiteDatabase modificationSites;
    
    /**
     * Constructor initializes a connection to the modification database
     * at the specified path.
     * @param path Path to modification database
     * @throws Exception if the request cannot be fulfilled.
     */
    public PhosphoSiteAnnotator(String path) throws Exception {
        modificationSites = new ModificationSiteDatabase();
        modificationSites.connect(path);
    }
    
    /**
     * Separates a set of query sites into know and unknown sites.
     * @param accession Uniprot protein access
     * @param querySites Modification sites to separate
     * @return Sites separated into known (exist in database) and unknown (do
     * not exist in database).
     * @throws Exception if the request cannot be fulfilled.
     */
    @Override
    public ModificationSitesAnnotated annotate(String accession, ModificationSites querySites) throws Exception {
        ModificationSitesAnnotated annotated = new ModificationSitesAnnotated();
        for(int i=0;i<querySites.size();i++) {
            ModificationSite site = querySites.getSites().get(i);
            Integer index = querySites.getIndices().get(i);
            if(modificationSites.exists(accession, site.toString())) {
                annotated.addKnown(site,index);
            }
            else {
                annotated.addUnknown(site,index);
            }
        }
        return annotated;
    }
        
    /**
     * Returns false, because even if no records exist, this database is 
     * expected to contain data.
     * 
     * @return false always.
     */
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * Closes an active connection.
     * 
     * @throws Exception if the underlying request throws an exception.
     */
    @Override
    public void finish() throws Exception {
        modificationSites.disconnect();
    }
}
