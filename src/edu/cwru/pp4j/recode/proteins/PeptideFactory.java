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
package edu.cwru.pp4j.recode.proteins;

import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Encapsulates the necessary information and algorithms to iteratively create
 * a set of peptides from a protein sequence using a defined protease. The
 * peptides can be "streamed" in that once the object is created and initialized
 * they are returned by calling getNext() on the object until it returns an
 * empty array. This class was developed for use in sequential processing algorithms
 * that need to generate numbers of peptides too large to store in memory
 * all at once.<br><br>
 * 
 * The rules by which Proteases cleave protein sequences are complex, but to
 * understand this algorithm you only need to know:<br>
 * <ol>
 * <li> A single cleavage event may cut the protein sequence more than once. </li>
 * <li> Cleavages are likely to happen but not guaranteed (termed as a "missed cleavage").</li>
 * </ol>
 * 
 * For the first item, we search along a protein sequence until we find the next
 * cut site and then return an array containing the peptides that were created 
 * by cleaving at that site. Subsequent searched start from the last site 
 * and move toward the end of the protein.<br><br>
 * 
 * For the second item, the number of missed cleavages sets a sort of 
 * sliding window on what peptides can be generated. If the user specifies 
 * N missed cleavages, the implementation allocates N+1 queues to hold peptide
 * sequences. The order in which peptides are added and removed from the queues
 * simulates a sliding window that encapsulates the generation of peptides with
 * missed cleavages.<br><br>
 * 
 * Pseudocode:
 * <pre>
 * queues[] &lt;- queues up to number of missed cleavages + 1
 * 
 * <strong>while</strong> there are cleave sites remaining
 *      peptides[] &lt;- peptides from next cleave event 
 *      <strong>for</strong> i=0 to number of peptides - 1
 *          <strong>for</strong> j=0 to number of missed cleavages
 *              queues[j].add(peptides[i]);
 *          <strong>endfor</strong>
 *          <strong>for</strong> j=0 to the number of full queues -1
 *              output queues[j] as string
 *              queues[j].remove()
 *          <strong>endfor</strong>
 *      <strong>endfor</strong>
 * <strong>endwhile</strong>
 * </pre>
 * 
 * Example:
 * <pre>
 * Protease: Trypsin (cleaves after R and K)
 * Peptide: AKTRL
 * Missed Cleavages: 2
 * 
 * queues = [[],[],[]]
 * First Call to GetNext()
 *      peptides = ["AK"]
 *      queues = [["AK"],["AK"]]
 *      output : "AK"
 * 
 * Second Call to GetNext()
 *      peptides = ["TR"]
 *      queues = [["TR"],["AK","TR"]]
 *      output : "TR"
 *      output : "AKTR"
 * 
 * Third Call to GetNext()
 *      peptides = ["L"]
 *      queues = [["L"],["TR","L"],["AK","TR","L"]]
 *      output : "L"
 *      output : "TRL"
 *      output : "AKTRL"
 * </pre>
 * 
 * @author Sean Maxwell
 */
public class PeptideFactory {
    private final HashMap<String,Protease> proteases;
    private Matcher m;
    private String  seq = "";
    private String  pro = "";
    private int     start = 0;
    private String  message = "OK";
    private LinkedBlockingQueue[] peptides;
    private long    found    = 0;
    private int     count    = 0;
    private int     nmiss    = 0;
    private int     search   = 0;
    private Protease prot;

    /**
     * Constructor creates a new peptide factory with a database of available
     * proteases configured.
     *
     * @param strict Indicates if the proteases should use Expasy rules verbatim
     * or use a more relaxed approach of cleaving predictably on single amino
     * acid codes.
     *
     */
    public PeptideFactory(boolean strict) {
        /* Create the lookup table of Protease deffinitions */
        proteases = new HashMap<>();
        proteases.put("AspN",           new Protease("D",0));
        proteases.put("AspN/N->D",      new Protease("[DE]",0));
        proteases.put("Chymotrypsin",   new Protease("[FYW]",1));
        proteases.put("GluC",           new Protease("[E]",1));
        proteases.put("LysC",           new Protease("K",1));
        proteases.put("Pepsin, pH=1.3", new Protease("[FL]",0));
        proteases.put("Pepsin, pH=2.0", new Protease("[FLWY]",0));
        proteases.put("Trypsin",        new Protease("[RK]",1));
        proteases.put("Non-specific",   new Protease(".",1));

        /* If we are being strict, create site matchers and exclusions for 
         * relevant proteases.
         */
        if(strict) {
            /* Add site matchers */
            ((Protease)proteases.get("Chymotrypsin"  )).addMatcher("([FY][^P])|(W[^MP])"   , 0, 1, 1);
            
            /* It is suggested in the Expasy documentation that the Glutamyl 
             * endopeptidase is inhibited form cleaving when Proline(P) appears
             * 2 postions before E, after E or 2 amino acids after E, and that
             * Asp (D) immediately after E has a similar affect. However, in
             * in testing this does not seem to be enforced on the PeptideCutter
             * interface and cleavage happens predictably on all E
            ((Protease)proteases.get("GluC"          )).addMatcher("[^P]..[E][^DP][^P]"    , 0, 1, 1);
            */
            
            ((Protease)proteases.get("Pepsin, pH=1.3")).addMatcher("[^HKR][^P][^R][FL][^P]", 3, 1, 0);
            ((Protease)proteases.get("Pepsin, pH=1.3")).addMatcher("[^HKR][^P][FL].[^P]"   , 2, 2, 1);
            ((Protease)proteases.get("Pepsin, pH=2.0")).addMatcher("[^HKR][^P][^R][FLWY][^P]"  , 3, 1, 0);
            ((Protease)proteases.get("Pepsin, pH=2.0")).addMatcher("[^HKR][^P][FLWY].[^P]"     , 2, 2, 1);
            ((Protease)proteases.get("Trypsin"       )).addMatcher("(WKP)|(MRP)|[KR][^P]"    , 1, 1, 1);

            /* Add site exclusions */
            ((Protease)proteases.get("Trypsin"       )).addExclusion("([CD]KD)|(CK[HY])|(CRK)|(RR[HR])", 1, 1);
        }
        else {
            /* Pepsin cuts both before and after the cut site, so add the secondary
             * cut sites now */
            ((Protease)proteases.get("Pepsin, pH=1.3")).addMatcher("[FL]" , 0, 0, 0);
            ((Protease)proteases.get("Pepsin, pH=2.0")).addMatcher("[FLWY]"   , 0, 0, 0);
            ((Protease)proteases.get("Pepsin, pH=1.3")).addMatcher("[FL]" , 0, 0, 1);
            ((Protease)proteases.get("Pepsin, pH=2.0")).addMatcher("[FLWY]"   , 0, 0, 1);
        }
        
    }

    /**
     * Stores the argument amino acid sequence in the factory for
     * use when generating peptides.
     *
     * @param s The sequence string to associate with this object.
     *
     */
    public void setSequence(String s) {
        this.seq = s.toUpperCase();
    }

    /**
     * Returns the sequence String stored in the factory.
     *
     * @return The internal sequence String.
     *
     */
    public String getSequence() {
        return this.seq;
    }

    /**
     * Configures the protease the factory will use to generate peptides.
     *
     * @param protease String name of the desired protease.
     *
     * @return true for success and false if the requested protease is invalid.
     *
     */
    public boolean setProtease(String protease) {
        if(this.proteases.get(protease) != null) {
            this.pro = protease;
            return true;
        }
        else {
            this.message = "The requested protease is not contained in this object";
            return false;
        }
    }

    /**
     * Adds a custom protease to the factory that can be used for
     * generating peptides.
     *
     * @param name String name of the protease
     * @param reg String regular expression that defines the cleavage rule(s)
     * @param co Cut offset from match position. 0 is left of, 1 is right of.
     *
     * @return true for success and false for failure.
     * 
     */
    public boolean addProtease(String name, String reg, int co) {
        try {
            this.proteases.put(name, new Protease(reg, co));
            return true;
        }
        catch(Exception e) {
            this.message = e.toString();
            return false;
        }
    }

    public boolean addCleavageSiteMatcher(String name, String reg, int left, int right, int co) {
        Protease protease = (Protease)proteases.get(name);
        if(protease == null) {
            this.message="Protease does not exist";
            return false;
        }

        if(((Protease)proteases.get(name)).addMatcher(reg , left, right, co)) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean addCleavageSiteExclusion(String name, String reg, int left, int right) {
        Protease protease = (Protease)proteases.get(name);
        if(protease == null) {
            this.message="Protease does not exist";
            return false;
        }

        if(protease.addExclusion(reg , left, right)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the String name of the configured protease.
     *
     * @return Protease name
     *
     */
    public String getProteaseName() {
        return this.pro;
    }

    /**
     * Returns the configured Protease object for the factory.
     * @return The protease configured for the factory
     */
    public Protease getProtease() {
        return this.proteases.get(this.pro);
    }

    /**
     * Set the number of missed cleavages to allow when generating peptides.
     *
     * @param missed The number of missed cleavages
     */
    public void setMissedCleavages(int missed) {
        this.nmiss = missed;
    }

    /**
     * Retrieve the current setting for missed cleavages.
     *
     * @return The number of missed cleavages the object is using
     */
    public int getMissedCleavages() {
        return this.nmiss;
    }

    /**
     * Retrieve how many peptides have been found up to now. The value is
     * updated every time GetNext() is called.
     *
     * @return The number of peptides generated thus far
     */
    public long peptideCount() {
        return this.found;
    }
    
    /**
     * Retrieves the internal message String for the last operation.
     * 
     * @return String message.
     * 
     */
    public String getMessage() {
            return this.message;
    }

    /**
     * Initializes the factory to sequentially generate peptides.
     *
     * @return true for success and false when an error occurs.
     *
     * @see #GetNext
     * @see #getMessage
     *
     */
    public boolean start() {
        int i;

        try {
            this.peptides = new LinkedBlockingQueue[this.nmiss+1];

            /* Initialize the peptide stacks for missed cleavages */
            for(i=0;i<this.nmiss+1;i++) {
                peptides[i] = new LinkedBlockingQueue();
            }

            /* initialize the internal reference to the requested protease */
            this.prot = (Protease)this.proteases.get(this.pro);

            /* Set the matcher to the configured protease pattern matcher */
            this.m  = this.prot.getMatcher(this.seq); //cutters.matcher(this.seq);

            /* Set start position (the position to cut from) to 0 */
            this.start  = 0;
            this.found  = 0;
            this.count  = 0;
            this.search = 0;
            return true;
        }
        catch(Exception e) {
            this.message = e.toString();
            return false;
        }
    }

    /**
     * Trim a Peptide array to the argument length.
     *
     * @param array Array to trim
     * @param length New array length
     *
     * @return Trimmed array
     */
    private PeptideSimple[] TrimPeptideArray(PeptideSimple[] array, int length) {
        PeptideSimple[] pa = new PeptideSimple[length];
        int i;

        for(i=0;i<length;i++) {
            pa[i] = array[i];
        }

        return pa;
    }

    /**
     * Generates an array of peptides from an amino acid sequence, a cleavage
     * site position and an array of offsets from the site position at which to
     * cleave the sequence.
     *
     * @param seq The full protein sequence
     * @param offsets The offsets from the cut site to cleave at
     * @param site The cut site
     * 
     * @return The array of peptides
     */
    private PeptideSimple[] CutPeptide(String seq, int[] offsets, int site) {
        int j;
        int k;
        int n_offsets = 0;
        PeptideSimple r[];

        /* Get the number of offsets */
        for(j=0;j<offsets.length;j++) {
            if(offsets[j] != -1) {
                n_offsets++;
            }
        }

        /* Allocate return array */
        r = new PeptideSimple[n_offsets];

        /* Extract the peptides */
        k = 0;
        for(j=0;j<offsets.length;j++) {
            if(offsets[j] != -1) {
                /* If the pepetide would be empty because multiple cuts happened
                 * in the same place, skip it because we don't want empty
                 * strings to be returned in the array.
                 */
                if((site+offsets[j])-this.start > 0) {
                    r[k] = new PeptideSimple(seq.substring(this.start,site+offsets[j]),
                                       this.start);
                    k++;
                }

                /* Move the start pointer to the next cut-from position */
                this.start = site+offsets[j];
            }
        }

        /* Trim any null elements off the end of the array. They appear there
         * when empty strings were skipped in the loop.
         */
        return this.TrimPeptideArray(r,k);
        
    }

    /**
     * Returns the next set if peptides from the factory.
     *
     * @param onlyPeptidesWithThisManyMissedCleavages Dictates that the factory
     * only return peptides with exactly this many missed cleavages.
     * 
     * @return Peptides while the end of the sequence has not been reached, and
     * null otherwise.
     *
     * @see #start()
     *
     */
    public List<Peptide> GetNext(int onlyPeptidesWithThisManyMissedCleavages) {
        PeptideSimple[] r;
        String   pep;
        Object[] peps;
        List<Peptide> rpeps = new ArrayList<>();
        int i;
        int j;
        int k;
        int t;
        int nres = 0;
        int cut_sites;
        int[] cut_pos = new int[5];
        int res_count = 0;
        int r_start;
        
        /* If we reached the end of the sequence, return null */
        if(this.start == -1) {
            return null;
        }

        /* If our search parameter is beyond the end of the string */
        if(this.search > this.seq.length()) {
            return null;
        }

        /* Extract the next peptide */
        if(this.m.find(this.search)) {
            cut_sites = 0;

            /* The maximum window for cut sites is 5 base pairs, so the cut
             * position array is a static 5 elements and we reset it on every
             * iteration of the loop. If we ever have to increase the window for
             * a new protease we will want to increase this length.
             */
            cut_pos[0] = -1;
            cut_pos[1] = -1;
            cut_pos[2] = -1;
            cut_pos[3] = -1;
            cut_pos[4] = -1;

            /* Check for exceptions to cut site */
            for(i=0;i<this.prot.excluderCount();i++) {
                /* If the candidate cut position is in a region that matches one
                 * of the configured exceptions to the cut rules for the
                 * protease, move the search pointer one position forward, and
                 * continue searching using recursion.
                 */
                if(this.prot.isExclusion(i, seq, this.m.start())) {
                    this.search = this.m.start()+1;
                    return this.GetNext(onlyPeptidesWithThisManyMissedCleavages);
                }
            }

            /* If the candidate cut site was not rejected by an exception, we
             * need to test each cut rule for the protease to determine how many
             * positions will cleave in the region */
            for(i=0;i<this.prot.matcherCount();i++) {
                cut_pos[i] = this.prot.isCleaveSite(i, seq, this.m.start());
                if(cut_pos[i] != -1) {
                    cut_sites++;
                }
            }

            /* If all the cut rules failed to find a cleave site, move the
             * search position one character forward and continue using
             * recursion */
            if(this.prot.matcherCount() > 0 && cut_sites == 0) {
                this.search = this.m.start()+1;
                return this.GetNext(onlyPeptidesWithThisManyMissedCleavages);
            }

            /* If there were no cut rules to use, because the protease cleaves
             * predictably on a single amino acid, use the offset from the
             * protease object for the cut position */
            else if(this.prot.matcherCount() == 0) {
                cut_pos[0] = this.prot.offset();
                cut_sites = 1;
            }

            /* Cut the peptide on all cut sites. This uses, and updates the
             * private member "start" so that at completion, it is positioned
             * at the last cut site. */
            r = this.CutPeptide(this.seq, cut_pos, this.m.start());

            /* If a situation occured where no cuts were made */
            if(r.length == 0) {
                System.err.printf("Strange no-cleave with sites for %s (%d:%d)\nThis is not necessarily an error, but you may want to manually verify that the last few peptides of the protein are being cleaved as expected\n",this.seq,cut_sites,this.m.start());
                this.search = this.m.start()+1;
                return this.GetNext(onlyPeptidesWithThisManyMissedCleavages);
            }

            /* Update the search parameter so that searching continues just past
             * the last candidate cleave site */
            this.search = this.m.start()+1;

            /* Update the found counter to include all the cleavages found for
             * this candidate site */
            this.found += r.length;
        }

        /* Otherwise, we reached the end of the sequence. Extract the final
         * peptide if there is anything left */
        else {
            if(this.start < this.seq.length()) {
                r = new PeptideSimple[1];
                r[0] = new PeptideSimple(this.seq.substring(this.start, this.seq.length()),
                             this.start);
                this.found++;
            }
            else {
                r = new PeptideSimple[0];
            }
            this.start = -1;
            this.search = this.seq.length()-1;
        }

        /* Loop over the returned peptides */
        for(j=0;j<r.length;j++) {
            
            /* nres controls how many stacks we pop when generating peptides to
             * return. nres is equal to the number of fully populated stacks,
             * where a stack is fully populated when its size is equal to its
             * index+1 */
            nres=0;
            for(i=0;i<this.nmiss+1;i++) {
                this.peptides[i].add(r[j]);
                if(this.peptides[i].size() == i+1) {
                    nres++;
                    //System.out.printf("%d is full: %s\n",nres,this.peptides[i].toString());
                }
            }
            
            /* Loop over the number of stacks that have been fully populated. A
             * stack is fully populated when there are the same number of
             * elements in the stack as the value of it's index in the array.
             * This is equivalent to the number of missed cleavages represented
             * by the stack */
            for(i=0;i<nres;i++) {
                pep = "";

                /* Add each peptides on the stack to the temporary string */
                peps = this.peptides[i].toArray();
                r_start = ((PeptideSimple)peps[0]).start();
                for(k=0;k<peps.length;k++) {
                    pep += ((PeptideSimple)peps[k]).sequence();
                }

                /* Add the result to the return array */
                if(onlyPeptidesWithThisManyMissedCleavages == -1 ||
                   onlyPeptidesWithThisManyMissedCleavages == i) {
                    //System.out.printf("%d Adding %s\n",nres,pep);
                    rpeps.add(new PeptideSimple(pep,r_start));
                    res_count++;
                    
                    /* Update the count of how many peptides were generated */
                    this.count++;
                }
                
                /* Remove the oldest peptide from this queue */
                this.peptides[i].remove();
            }
        }

        return rpeps;

    }

    /**
     * Removes the N-terminus amino acid residue from a peptide. This is 
     * intended to be used for cleaving N-terminus methionine, but no explicit 
     * check is made to restrict removal of other amino acids.
     * 
     * @param p Peptide to remove N-Terminus residue from
     * @return A peptide derived by removing the N-terminus amino acid from the
     * argument peptide, and incrementing the start position by 1.
     */
    public static Peptide cleaveNTerm(Peptide p) {
        return new PeptideSimple(p.sequence().substring(1),p.start()+1);
    }
    
    /**
     * Calculates how many peptides will be generated by this factory during
     * a full run.
     *
     * @param onlyPeptidesWithThisManyMissedCleavages Dictates that the factory
     * only count peptides with exactly this many missed cleavages.
     * 
     * @return How many peptides the object will create
     */
    public int howMany(int onlyPeptidesWithThisManyMissedCleavages) {
        boolean status;
        List<Peptide> peptide;
        int n = 0;

        status = this.start();
        if(status == false) {
            return 0;
        }

        peptide = this.GetNext(onlyPeptidesWithThisManyMissedCleavages);
        while(peptide != null) {
            n += peptide.size();
            peptide = this.GetNext(onlyPeptidesWithThisManyMissedCleavages);
        }

        return n;
    }

}
