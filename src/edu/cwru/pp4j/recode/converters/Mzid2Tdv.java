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
package edu.cwru.pp4j.recode.converters;

import edu.cwru.pp4j.recode.modsites.ModificationSiteDatabase;
import edu.cwru.pp4j.recode.modsites.ModificationSiteParser;
import edu.cwru.pp4j.recode.mzid.Evidence;
import edu.cwru.pp4j.recode.mzid.Identification;
import edu.cwru.pp4j.recode.mzid.MzIdentMlParser;
import edu.cwru.pp4j.recode.mzid.Peptide;
import java.io.File;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;

import edu.cwru.pp4j.recode.recoding.ReverseRecoder;
import edu.cwru.pp4j.recode.recoding.ReverseRecodeResult;

/**
 * A minimalistic class to convert mzIdentML files to tab-delimited values. The
 * resulting file columns include peptide sequence, modification sites, 
 * peptide start/end position in original protein sequence and scores from
 * MSGF+ identification.
 * 
 * @author Sean Maxwell
 */
public class Mzid2Tdv {
    /**
     * Finds the right most dot in a string
     *
     * @param s the string to search in
     *
     * @return the position in the string of the dot or -1 for no dots.
     * 
     */
    private int getRightmostDot(String s) {
        int offset  = -1;
        int dot     = 0;

        /* Find the rightmost occurance of . in the file name */
        while(dot > -1) {
            dot = s.indexOf('.', offset+1);
            if(dot > -1) {
                offset = dot;
            }
        }

        return offset;

    }
    
    /**
     * Retrieve the file name from the argument path
     *
     * @param path Path to a file
     *
     * @return Name of the file
     */
    private String getName(String path) {
        File f = new File(path);
        String name = f.getName();
        int p = this.getRightmostDot(name);

        /* If the name contains a dot, consider it the extension and remove it*/
        if(p != -1) {
            name = name.substring(0, name.indexOf('.',p));
        }
        
        return name;

    }
    
    /* The start position of the peptide is tunneled in the accession as the
     * second value when splitting accession on "_". This method parses the 
     * accession and returns the value if one has been tunneled and 0 otherwise.
     */
    private Integer getPeptideStart(String accession) {
        String[] parts = accession.split("_");
        try {
            return(Integer.parseInt(parts[1]));
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    /**
     * Convert and mzIdentML file to tab delimited values.
     * @param input Input file path
     * @param outDir Directory where output file should be written
     * @param minPhos Only output peptides with at least this many modifications
     * @param maxPhos Only output peptides with no more than this many modifications
     * @param qThresh Only output peptides with q-values at least this small
     * @param pThresh Only output peptides with scores at least this large
     * @param includeDecoy If true, output decoy database hits. Otherwise, do not
     * @param modDb Path to a modification site database, or null. If set to
     * null, all modifications sites will be output. Otherwise, the value of
     * the known param is used in conjunction with the modDb to determine what
     * sites to output.
     * @param known If set to true, output known modification sites annotated to
     * peptides. If set to false, output unknown modification sites annotated to
     * peptides.
     * 
     * @return Number of peptides output
     * @throws IOException if the underlying IO throws an exception
     */
    public int convert(String input, String outDir, int minPhos, int maxPhos, double qThresh, double pThresh, boolean includeDecoy, String modDb, boolean known) throws Exception {
        MzIdentMlParser mzidParser = new MzIdentMlParser();          
        if(!mzidParser.load(input)) {
            return -1;
        }
        
        /* Setup output file */
        String name = getName(input);
        FileWriter fw = new FileWriter(outDir+name+String.format("-q%.2f",qThresh)+".tdv");
        fw.write("PepSeq\tModPepSeq\tModSites\tNMods\tProtAcc\tPepStart\tPepEnd\tScore\tQVal\tCharge\tm/z\tScan(s)\n");        
        
        /* If modDb is passed, connect for filtering sites */
        ModificationSiteDatabase mdb = new ModificationSiteDatabase();
        if(modDb != null) {
            mdb.connect(modDb);
        }
        
        /* iterate over identifications */
        HashMap<String,String> pepCount = new HashMap();
        HashMap<String,String> resultScans = mzidParser.getIdentificationResultScans();
        for(Identification identification : mzidParser.identifications()) {
            /* Skip identifications that did not pass threshold */
            if(!identification.passThreshold()) {
                continue;
            }
                        
            /* Skip identifications that do not pass the user defined false 
             * discovery rate */
            if(identification.qValue() >= qThresh) {
                continue;
            }
            
            /* Compare a p-value (if it exists) to the user specified threshold */
            if(identification.pValue() >= pThresh) {
                continue;
            }
            
            /* Iterate over the peptide evidence for the identification */
            for(String ref : mzidParser.evidenceRefs(identification.identifier())) {
                Evidence evidence = mzidParser.evidence(ref);
                                
                // Lookup the peptide 
                Peptide peptide = mzidParser.peptide(evidence.peptideRef());
                
                // Extract peptide sequence 
                String peptideSequence = peptide.sequence();
                
                // Extract parent protein accession 
                String proteinAccession = mzidParser.accession(evidence.sequenceRef());
                                
                // If not including decoys 
                if(proteinAccession.startsWith("XXX_") && !includeDecoy) {
                    continue;
                }

                /* Extract peptide start position in the full protein sequence
                 * from the proten accession. It is  tunneled in the accession 
                 * string as {ACCESSION}_{START} during recode */
                Integer peptideOffset = 0;
                if(!proteinAccession.startsWith("XXX_")) {
                    peptideOffset = getPeptideStart(proteinAccession);
                }
           
                /* This counts the number of recoded amino acids in the peptide
                 * sequence (number of occupied phosphosites) */
                int nPhos = ModificationSiteParser.nRecodes(peptideSequence);
                
                /* If it is phosphorylated at least once, but less than or equal
                 * to the max number of allowed site, output the peptide */
                if(nPhos >= minPhos && nPhos <= maxPhos) {
                    /* Reverse-recode each amino acid by converting back to 
                     * original amino acid, assigning absolute position in 
                     * proteion sequence. This returns a ResidueList that 
                     * contains the phosphorylation sites that were reverse 
                     * coded */
                    ReverseRecodeResult rrr = ReverseRecoder.reverseCodePhosphorylationSites(peptideSequence, evidence.start()+peptideOffset);

                    /* Output the phosphopeptide record */
                    fw.write(String.format("%s\t%s\t%s\t%d\t%s\t%d\t%d\t%f\t%f\t%d\t%f\t%s\n",
                            peptideSequence,
                            rrr.getSequence(),
                            rrr.getResidues(),
                            nPhos,
                            proteinAccession,
                            evidence.start()+peptideOffset,
                            evidence.end()+peptideOffset,
                            identification.score(),
                            identification.qValue(),
                            identification.chargeState(),
                            identification.massToCharge(),
                            resultScans.get(identification.identificationResultID())));
                    pepCount.put(evidence.peptideRef(),"1");
                }
            }
        }
        fw.close();
        return pepCount.size();
    }
    
}
