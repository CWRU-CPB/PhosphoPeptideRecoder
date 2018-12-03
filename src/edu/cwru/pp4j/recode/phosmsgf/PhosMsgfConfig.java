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
package edu.cwru.pp4j.recode.phosmsgf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Encapsulates the parameters that define an MSGF+ analysis. It includes 
 * setters for all parameters and uses a functional programming approach where
 * setters return the object. E.g.,
 * <pre>
 * PhosMsgfConfig cfg = new PhosMsgfConfig("./");
 * cfg.setThreadCount(2).setPrecursorMinCharge(2);
 * </pre>
 * It includes an abbreviated set of getters for individual parameters, and a
 * generic getter that fetches parameters by an enum argument.
 * 
 * @author Sean Maxwell
 */
public class PhosMsgfConfig {
    private final HashMap<Enum,String> strings;
    private final List<String> spectra;
    private final String outputDir;
    
    private String removeExtension(String s) {
        return s.lastIndexOf('.') > s.lastIndexOf(File.separatorChar) ? s.substring(0,s.lastIndexOf('.')) : s;
    }
    
    /**
     * Constructor initializes a new empty configuration that points to an
     * output directory.
     * 
     * @param s Output directory path 
     */
    public PhosMsgfConfig(String s) {
        strings = new HashMap<>();
        spectra = new ArrayList<>();
        outputDir = s;
    }
    
    /**
     * Sets the number of threads that MSGF+ will launch to perform the search.
     * 
     * @param i Number of threads
     * 
     * @return this object.
     */
    public PhosMsgfConfig setThreadCount(Integer i) {
        strings.put(PhosMsgfParamEnum.THREADS, String.format("%d", i));
        return this;
    }
    
    /**
     * Sets the path to the sequence database that will be used for the search.
     * 
     * @param s Path to sequence database
     * 
     * @return this object.
     */
    public PhosMsgfConfig setSequenceDatabase(String s) {
        strings.put(PhosMsgfParamEnum.SEQUENCEDATABASE, s);
        return this;
    }
    
    /**
     * Set the minimum charge to use when searching a range of precursor ion
     * m/z value.
     * 
     * @param i Minimum precursor charge
     * 
     * @return this object
     */
    public PhosMsgfConfig setPrecursorMinCharge(Integer i) {
        strings.put(PhosMsgfParamEnum.PRECURSORMINCHARGE, String.format("%d",i));
        return this;
    }
    
    /**
     * Set the maximum charge to use when searching a range of precursor ion
     * m/z value.
     * 
     * @param i Maximum precursor charge
     * 
     * @return this object
     */
    public PhosMsgfConfig setPrecursorMaxCharge(Integer i) {
        strings.put(PhosMsgfParamEnum.PRECURSORMAXCHARGE, String.format("%d",i));
        return this;
    }
    
    /**
     * Set the precursor mass tolerance, which is a range around the expected
     * mass within which we accept values. MSGF+ accept a format that allows
     * asymmetrical ranges to be specified, but for simplicity we only accept
     * ranges that extend the same distance above and below the expected value.
     * 
     * @param d 1/2 the range width
     * @param units The units of the range (ppm|Da)
     * 
     * @return this object
     */
    public PhosMsgfConfig setPrecursorMassTolerance(Double d, String units) {
        if(units.equals("ppm")) {
            strings.put(PhosMsgfParamEnum.PRECURSORMASSTOLERANCE, String.format("%d%s", d.intValue(), units));
        }
        else {
            strings.put(PhosMsgfParamEnum.PRECURSORMASSTOLERANCE, String.format("%f%s", d, units));
        }
        return this;
    }
    
    /**
     * Sets the isotope error range, which is the range of isotopes heavier and 
     * lighter than the primary isotope. 
     * @param l The low end of the isotope range
     * @param h The high end of the isotope range
     * @return this object.
     */
    public PhosMsgfConfig setIsotopErrorRange(Integer l, Integer h) {
        strings.put(PhosMsgfParamEnum.ISOTOPERRORRANGE, String.format("%d,%d",l,h));
        return this;
    }
    
    /**
     * Sets the fragmentation method (the method that breaks peptides into ions)
     * @param s Fragmentation method name
     * @return this object.
     */
    public PhosMsgfConfig setFragmentationMethod(String s) {
        String intCode;
        switch(s) {
            case "Auto" : intCode="0";
                          break;
            case "CID"  : intCode="1";
                          break;
            case "ETD"  : intCode="2";
                          break;
            case "HCD"  : intCode="3";
                          break;
            default     : intCode = "-1";
        }      
        strings.put(PhosMsgfParamEnum.FRAGMENTATIONMETHOD, intCode);
        return this;
    }
    
    /**
     * Set the MS2 detector type (the instrument that measures the m/z of the
     * fragmented peptide ions).
     * @param s MS2 detector type
     * @return this object.
     */
    public PhosMsgfConfig setMs2DetectorType(String s) {
        String intCode;
        switch(s) {
            case "Low-res LCQ/LTQ" : intCode="0";
                                     break;
            case "Orbitrap/FTICR"  : intCode="1";
                                     break;
            case "TOF"             : intCode="2";
                                     break;
            case "Q-Exactive"      : intCode="3";
                                     break;
            default                : intCode = "-1";
        }
        strings.put(PhosMsgfParamEnum.MS2DETECTORTYPE, intCode);
        return this;
    }
    
    /**
     * Sets the protocol used by MSGF+. These are models trained by the 
     * developers and included with the software. 
     * @param s Protocol name
     * @return this object.
     */
    public PhosMsgfConfig setProtocol(String s) {
        String intCode;
        switch(s) {
            case "Auto"              : intCode="0";
                                     break;
            case "Phosphorylation"   : intCode="1";
                                     break;
            case "iTRAQ"             : intCode="2";
                                     break;
            case "iTRAQPhospho"      : intCode="3";
                                     break;
            case "TMT"               : intCode="4";
                                     break;
            case "Standard"          : intCode="5";
                                     break;
            default                  : intCode = "-1";
        }
        strings.put(PhosMsgfParamEnum.PROTOCOL, intCode);
        return this;
    }
    
    /**
     * Sets the protease to use for digesting protein sequences.
     * @param s Protease name
     * @return this object.
     */
    public PhosMsgfConfig setEnzyme(String s) {
        String intCode;
        switch(s) {
            case "Non-specific"      : intCode="0";
                                     break;
            case "Trypsin"           : intCode="1";
                                     break;
            case "Chymotrypsin"      : intCode="2";
                                     break;
            case "Lys-C"             : intCode="3";
                                     break;
            case "Lys-N"             : intCode="4";
                                     break;
            case "glutamyl endopeptidase": intCode="5";
                                     break;
            case "Arg-C"             : intCode="6";
                                     break;
            case "Asp-N"             : intCode="7";
                                     break;
            case "alphaLP"           : intCode="8";
                                     break;
            case "no cleavage"       : intCode="9";
                                     break;
            default                  : intCode = "-1";
        }
        strings.put(PhosMsgfParamEnum.ENZYME, intCode);
        return this;
    }
    
    /**
     * Sets the pre-digest enzyme used to digest proteins to the peptides in the
     * pre-digest database.
     * @param s Protease name
     * @return this object.
     */
    public PhosMsgfConfig setPreDigestEnzyme(String s) {
        String intCode;
        switch(s) {
            case "Non-specific"      : intCode="0";
                                     break;
            case "Trypsin"           : intCode="1";
                                     break;
            case "Chymotrypsin"      : intCode="2";
                                     break;
            case "Lys-C"             : intCode="3";
                                     break;
            case "Lys-N"             : intCode="4";
                                     break;
            case "glutamyl endopeptidase": intCode="5";
                                     break;
            case "Arg-C"             : intCode="6";
                                     break;
            case "Asp-N"             : intCode="7";
                                     break;
            case "alphaLP"           : intCode="8";
                                     break;
            case "no cleavage"       : intCode="9";
                                     break;
            default                  : intCode = "-1";
        }
        strings.put(PhosMsgfParamEnum.PREDIGESTENZYME, intCode);
        return this;
    }
    
    /**
     * Sets the text content of the modifications file.
     * @param s Modification text
     * @return this object.
     */
    public PhosMsgfConfig setModifications(String s) {
        strings.put(PhosMsgfParamEnum.MODIFICATIONS, s);
        return this;
    }
    
    /**
     * Set the minimum peptide length to consider.
     * @param i Peptide length in amino acids
     * @return this object.
     */
    public PhosMsgfConfig setMinPeptideLength(Integer i) {
        strings.put(PhosMsgfParamEnum.MINPEPTIDELENGTH, String.format("%d",i));
        return this;
    }
    
    /**
     * Set the maximum peptide length to consider.
     * @param i Maximum peptide length
     * @return this object.
     */
    public PhosMsgfConfig setMaxPeptideLength(Integer i) {
        strings.put(PhosMsgfParamEnum.MAXPEPTIDELENGTH, String.format("%d",i));
        return this;
    }
    
    /**
     * Specify if the decoy database should be searched.
     * @param i 0 for don't search, 1 for search
     * @return this object.
     */
    public PhosMsgfConfig setSearchDecoyDatabase(Integer i) {
        strings.put(PhosMsgfParamEnum.SEARCHDECOY, String.format("%d",i));
        return this;
    }
    
    /**
     * Sets the number of termini that can be skipped during digest. For example
     * when using Trypsin that cleaves on R or K, peptide should all end with
     * R or K, but setting this to 1 or 0 means a peptide could contain and R
     * or K internally (a missed cleavage). The way MSGF+ handles this 
     * internally is slightly confusing and I recommend users evaluate the
     * output when not using the value 2.
     * @param i Number of tolerable termini
     * @return this object.
     */
    public PhosMsgfConfig setNumberOfTolerableTermini(Integer i) {
        strings.put(PhosMsgfParamEnum.NUMBEROFTOLERABLETERMINI, String.format("%d",i));
        return this;
    }
    
    /**
     * Set number of matches per spectrum. 
     * @param i Number of matches
     * @return this object.
     */
    public PhosMsgfConfig setNumberOfMatchesPerSpectrum(Integer i) {
        strings.put(PhosMsgfParamEnum.NUMMATCHESPERSPECTRUM, String.format("%d",i));
        return this;
    }
    
    /**
     * Specifies if extra attributes should be included in output.
     * @param i 0 for no, 1 for yes.
     * @return this object.
     */
    public PhosMsgfConfig setOutputExtraFeatures(Integer i) {
        strings.put(PhosMsgfParamEnum.EXTRAFEATURES, String.format("%d",i));
        return this;
    }
    
    /**
     * Sets a different mass for the charge carrier particla (rather than the
     * default mass of a proton).
     * @param d Charge carrier mass
     * @return  this object.
     */
    public PhosMsgfConfig setMassOfChargeCarrier(Double d) {
        strings.put(PhosMsgfParamEnum.MASSOFCHARGECARRIER, String.format("%f",d));
        return this;
    }
    
    /**
     * Sets an integer flag specifying whether prefixes of a peptide that are
     * identical to a previously matched peptide should be added as matches in
     * the result, or if only the full peptide sequence should be considered
     * for a match.
     * 
     * @param i 1 for allow prefix matches, 0 for disallow.
     * @return this object.
     */
    public PhosMsgfConfig setPrefixMatchesAllowed(Integer i) {
        strings.put(PhosMsgfParamEnum.ALLOWPREFIXMATCHES, String.format("%d",i));
        return this;
    }
    
    /**
     * Sets an integer flag specifying whether protein N-terminal methionine 
     * cleavages should be ignored (not performed).
     * 
     * @param i 0 for cleave protein N-term methionine, 1 do not.
     * @return this object.
     */
    public PhosMsgfConfig setIgnoreProteinNTermMethionineCleavage(Integer i) {
        strings.put(PhosMsgfParamEnum.IGNOREMETCLEAVAGE, String.format("%d",i));
        return this;
    }
    
    /**
     * Sets the maximum number of missed cleavages to allow while enumerating
     * candidate peptides.
     * 
     * @param i 0 for cleave protein N-term methionine, 1 do not.
     * @return this object.
     */
    public PhosMsgfConfig setMaxMissedCleavages(Integer i) {
        strings.put(PhosMsgfParamEnum.MAXMISSEDCLEAVAGES, String.format("%d",i));
        return this;
    }
    
    /**
     * Specify the path to the spectrum spectrum that will be processed. This
     * operation sets the output file path as the spectrum file name with an
     * mzid extension.
     * @param s File path
     * @return this object.
     */
    public PhosMsgfConfig setSpectrum(String s) {
        File f = new File(s);
        strings.put(PhosMsgfParamEnum.SPECTRUMFILE, s);
        strings.put(PhosMsgfParamEnum.OUTPUT,outputDir+"/"+removeExtension(f.getName())+".mzid");
        return this;
    }
    
    /**
     * Attach a spectrum file to the configuration. It is helpful to attach all
     * spectrum files that will be process to the config so that subsequent 
     * operations can access them.
     * @param s Spectrum file path
     * @return this object.
     */
    public PhosMsgfConfig attachSpectrum(String s) {
        spectra.add(s);
        return this;
    }
    
    /**
     * Generic getter to retrieve parameter values.
     * @param e Parameter enum value
     * @return Parameter value
     * @see edu.cwru.pp4j.recode.phosmsgf.PhosMsgfParamEnum
     */
    public String getConfigValue(Enum e) {
        return strings.get(e);
    }
    
    /**
     * Get the list of spectrum file paths attached to the config.
     * @return Spectrum file paths
     */
    public List<String> getSpectra() {
        return spectra;
    }
    
    /**
     * Get the output file path. 
     * @return The path to the output mzid file
     * @see #attachSpectrum(java.lang.String) 
     */
    public String getOutputFile() {
        return strings.get(PhosMsgfParamEnum.OUTPUT);
    }
    
    /**
     * Get the output directory.
     * @return Output directory path
     */
    public String getOutputDir() {
        return outputDir;
    }
    
    /**
     * The search is run in an external JVM process that runs a JAR file with 
     * the parameters specified by this config object. This option specifies
     * which JAR file will be executed.
     * 
     * @param s Name of JAR file that is in the lib directory
     * @return this object.
     */
    public PhosMsgfConfig setEngine(String s) {
        this.strings.put(PhosMsgfParamEnum.ENGINE, s);
        return this;
    }
    
    /**
     * Retrieve the name of that JAR file that contains the search engine to
     * use for the analysis.
     * 
     * @return JAR file name (must be a JAR file in the ./lib directory).
     */
    public String getEngine() {
        return this.strings.get(PhosMsgfParamEnum.ENGINE);
    }
}
