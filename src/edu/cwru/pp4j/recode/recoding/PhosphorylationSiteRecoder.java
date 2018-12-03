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

import edu.cwru.pp4j.recode.proteins.Combinatorics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.cwru.pp4j.recode.proteins.Fasta;
import edu.cwru.pp4j.recode.modsites.ModificationSite;
import edu.cwru.pp4j.recode.modsites.ModificationSites;
import edu.cwru.pp4j.recode.modsites.ModificationSiteAnnotator;
import edu.cwru.pp4j.recode.modsites.ModificationSitesAnnotated;
import edu.cwru.pp4j.recode.modsites.ModificationSiteParser;
import edu.cwru.pp4j.recode.modsites.PhosphoSiteAnnotator;
import edu.cwru.pp4j.recode.modsites.NullSiteAnnotator;
import edu.cwru.pp4j.recode.proteins.PeptideFactory;
import edu.cwru.pp4j.recode.proteins.Peptide;

/**
 * Recodes the known phosphorylation sites located on protein sequences to a 
 * set of custom amino acids, writing the result to a new FASTA file. This 
 * process involves:
 * <ol>
 * <li>Synthetic digestion of each protein sequence to peptides using a specified protease and number of missed cleavages.</li>
 * <li>Parsing of all potential phosphorylation sites of each peptide.</li>
 * <li>Annotating potential sites as known/unknown.</li>
 * <li>Recoding S/T/Y of known sites to B/U/Z using a maximum number of concurrent modifications.</li>
 * <li>Outputting FASTA entries with unique accessions for each peptide that contains at least one recoded phosphorylation site.</li>
 * </ol>
 * 
 * @author Sean Maxwell
 */
public class PhosphorylationSiteRecoder {
    private static final Logger LOGGER = LogManager.getFormatterLogger(PhosphorylationSiteRecoder.class.getName());
        
    /**
     * Creates a unique accession for each recoded peptide that will be output
     * by concatenating the protein accession with the unique combination of 
     * recoded amino acids. This allows easy post-processing and aggregation
     * of peptides from the same parent protein.
     * 
     * @param accession Parent protein accession number
     * @param peptideStart The offset of the first peptide amino acid within the
     *                     protein
     * @param peptideEnd The offset of the last peptide amino acid with the
     *                   protein
     * @param siteNumbers The site numbers to recode (indexes site indices)
     * @param sites Modification sites (e.g. S120, T200 or Y10).
     * 
     * @return Unique accession 
     */
    public String makeAccessionLine(String accession, int peptideStart, int peptideEnd, int[] siteNumbers, ModificationSite[] sites) {
        String outAccession = String.format("%s_%d_%d", accession, peptideStart,peptideEnd);
        for(int i=0;i<siteNumbers.length;i++) {
            outAccession += "_"+sites[siteNumbers[i]];
        }
        return outAccession;
    }
    
    /**
     * Translates amino acids from standard character codes to substitution 
     * codes. 
     * @param c Amino acid to translate
     * @return Substitution code
     * @throws Exception If no substitution code is defined for the argument 
     *         amino acid.
     */
    public char recodeAminoAcid(char c) throws Exception {
        char r;
        if(c == 'S') {
            r='B';
        }
        else if(c == 'T') {
            r='U';
        }
        else if(c == 'Y') {
            r='Z';
        }
        else {
            throw new Exception(String.format("Invalid amino acid %c cannot be recoded",c));
        }
        LOGGER.trace("Recoding amino acid %c->%c",c,r);
        return r;
    }
    
    /**
     * Recodes amino acids in a peptide at the site specified by site indices, 
     * and site numbers. This is part of the combinatorial recoding algorithm
     * where site numbers are a set of binomial coefficients from an n choose k
     * loop that specify which sites to recode, and site indexes specify the 
     * positions of all sites in the full sequence. For example, if site numbers
     * are [0,2], and site indices were [15,22,106] we would recode the amino
     * acids in the peptide sequence at positions 15 and 106.
     * @param sequence Peptide amino acid sequence
     * @param siteNumbers The site numbers to recode (indexes site indices)
     * @param siteIndices Amino acid positions (indexes sequence)
     * @return The recoded sequence as character array
     * @throws Exception If the underlying recode operations fail
     */
    public char[] recodePeptide(String sequence, int[] siteNumbers, Integer[] siteIndices) throws Exception {
        char[] aminoAcids = sequence.toCharArray();
        for(int i=0;i<siteNumbers.length;i++) {
            aminoAcids[siteIndices[siteNumbers[i]]]=recodeAminoAcid(aminoAcids[siteIndices[siteNumbers[i]]]);
        }
        return aminoAcids;
    }
    
    /**
     * Recode the modifications sites on a list of peptides, writing the results
     * to the argument output stream.
     * @param config the parameters specifying how to recode the pepetides
     * @param accession the protein accession
     * @param knownSites set of known modification sites
     * @param peptides list of peptides to recode
     * @param out output stream for writing recoded FASTA entries
     * @throws Exception if the request cannot be fulfilled.
     */
    public void recodePeptides(RecodeConfig config, String accession, ModificationSiteAnnotator knownSites, List<Peptide> peptides, BufferedWriter out)
    throws Exception {
        for(Peptide peptide : peptides) {           
            /* Skip peptides of length < minimum */
            if(peptide.length() < config.getMinPeptideLength() || peptide.length() > config.getMaxPeptideLength()) {
                LOGGER.trace("Skipping peptide %s with length %d because it does not satisfy the configured min/max lengths [%d/%d]",
                        peptide.sequence(),
                        peptide.sequence().length(),
                        config.getMinPeptideLength(),
                        config.getMaxPeptideLength());
                continue;
            }
            
            /* Skip peptides that have non-standard amno acids in their sequence
             * that conflict with our recode namespalce of B/U/Z */
            if(peptide.sequence().contains("B") ||
               peptide.sequence().contains("U") || 
               peptide.sequence().contains("Z")) {
                LOGGER.warn("Skipping peptide %s that contains conflicting non-standard amino acids\n",
                        peptide.sequence());
                continue;
            }
            
            LOGGER.trace("Processing potential modification sites of peptide %s",peptide.sequence());
            
            /* Parse all potential S/T/Y modification sites */
            ModificationSites potentialSites = ModificationSiteParser.parsePhosphorylationSites(peptide);

            /* Filter to known S/T/Y modification sites */
            ModificationSitesAnnotated sites = knownSites.annotate(accession, potentialSites);
            ModificationSites known = sites.getKnown();
            
            /* If no sites on peptide, and the modification site database is not
             * empty, we should skip this peptide because we are only interested
             * in peptides that overlap sites in the reference database. If the
             * reference database is empty, then the user is interested in all
             * peptides, not just the ones that overlap a set of modification
             * sites, so we continue. */
            if(known.size() == 0 && !knownSites.isEmpty()) {
                LOGGER.trace("No annotated sites on peptide %s",peptide.sequence());
                continue;
            }
            
            /* If only digesting the protein, not recoding modification sites,
             * output the peptide here and continue */
            if(config.getDigestOnly()) {
                out.write(">"+accession+"_"+peptide.start()+"_"+peptide.end()+"\n"+peptide.sequence()+"\n");
                continue;
            }
            
            /* Otherwise, combinatorially enumerate known sites up to the 
             * maximum number of concurrent modifications */
            Integer[] knownSiteIndicesAsArray = known.getIndices().toArray(new Integer[known.size()]);
            ModificationSite[] knownSiteAminoAcidsAsArray = known.getSites().toArray(new ModificationSite[known.size()]);
            int N = config.getMaxModifications() > known.size() ? known.size() : config.getMaxModifications();
            for(int k=1;k<=N;k++) {
                LOGGER.trace("Recoding %d sites %d at a time",known.size(),k);
                Combinatorics combinatorics = new Combinatorics(known.size(),k);
                int[] siteNumbers = combinatorics.getCurrent();
                while(siteNumbers.length > 0) {
                    char[] recodedPeptide = recodePeptide(peptide.sequence(),siteNumbers,knownSiteIndicesAsArray);
                    String recodedAccession = makeAccessionLine(accession,peptide.start(),peptide.end(),siteNumbers,knownSiteAminoAcidsAsArray);
                    
                    /* Build a FASTA entry of >{ACCESSION}\n{SEQUENCE}\n */
                    StringBuilder sb = new StringBuilder();
                    sb.append(">");
                    sb.append(recodedAccession);
                    sb.append("\n");
                    sb.append(recodedPeptide);
                    sb.append("\n");
                    out.write(sb.toString());                    
                    LOGGER.trace(sb.toString().replace("\n", "\\n"));
                    
                    /* Get next set of recode sites */
                    siteNumbers = combinatorics.getNext();
                }
            }
            
            /* If peptide starts at protein N-terminus, check for Methionine. */
            if(peptide.start() == 0 && peptide.sequence().charAt(0) == 'M') {
                LOGGER.trace("Cleaving N-term methionine of %s",
                        peptide.sequence());
                peptide = PeptideFactory.cleaveNTerm(peptide);
                List<Peptide> recursiveList = new ArrayList<>();
                recursiveList.add(peptide);
                recodePeptides(config, accession, knownSites, recursiveList, out);
            }
        }

    }
    
    /**
     * Recodes a database of protein sequences using argument configuration.
     * @param config the parameters specifying how to recode the protein 
     * sequences.
     * @return true for success, false for error.
     * @throws Exception if the request cannot be fulfilled.
     */
    public boolean recode(RecodeConfig config) throws Exception {
        /* Load the protein sequence database. If it cannot be loaded,
         * it throws an informative exception which we propagate back
         * up the chain */
        Fasta sequences = new Fasta(config.getDatabase());
        LOGGER.info("Loaded FASTA database %s",config.getDatabase());
        
        /* Connect to the modificationsite database */
        ModificationSiteAnnotator knownSites;
        if(!config.getModSiteDatabase().equals("--Select One--")) {
            knownSites = new PhosphoSiteAnnotator(config.getModSiteDatabase());
            LOGGER.info("Loaded modification site database %s",config.getModSiteDatabase());
        }
        else {
            knownSites = new NullSiteAnnotator();
            LOGGER.info("Will digest protein only (no recode of mondification sites)\n");
        }
        
        /* Open output stream */
        BufferedWriter out = new BufferedWriter(new FileWriter(config.getOutputName()));
        LOGGER.info("Opened output file %s for writing",config.getOutputName());
        
        /* Digest the sequences stored in the database */
        for(String accession : sequences.getAccessions()) {
            String sequence = sequences.getSequence(accession);
            LOGGER.trace("Processing protein sequence with accession %s and length %d",accession,sequence.length());
            
            /* PeptideFactory digests the protein sequence into peptides. The
             * boolean strict digest argument secifies whether to use the Expasy
             * digest rules (which include exceptions and fine-grained cut
             * sites) or to use relaxed rules that ignore exceptions (these are
             * what many wet-bench scientists expect)*/
            PeptideFactory peptideFactory = new PeptideFactory(config.getStrictDigest());
            peptideFactory.setMissedCleavages(config.getMissedCleavages());
            peptideFactory.setProtease(config.getProtease());
            peptideFactory.setSequence(sequence);
            peptideFactory.start();
            
            /* Digest the protein sequence into peptides */
            List<Peptide> peptides = peptideFactory.GetNext(-1);
            
            /* Iterate over the digested peptides search for phosphorylation
             * sites to re-code */
            while(peptides != null) {
                recodePeptides(config, accession, knownSites, peptides, out);
                peptides = peptideFactory.GetNext(-1);
            }
        }
        out.close();
        
        knownSites.finish();
        
        return true;
    }
}
