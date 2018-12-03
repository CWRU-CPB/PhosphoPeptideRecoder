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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * An object to compile an MSGF+ configuration to a command line. It maps 
 * enum parameters to corresponding MSGF+ command line switches.
 * 
 * @author Sean Maxwell
 */
public class PhosMsgfCommand {
    /**
     * A map of enums to their corresponding MSGF+ command line switches.
     */
    private static final Map<Enum,String> switches;
    static {
        Map<Enum,String> t = new HashMap<>();
        t.put(PhosMsgfParamEnum.SEQUENCEDATABASE,"d");
        t.put(PhosMsgfParamEnum.OUTPUT,"o");
        t.put(PhosMsgfParamEnum.PRECURSORMASSTOLERANCE,"t");
        t.put(PhosMsgfParamEnum.ISOTOPERRORRANGE,"ti");
        t.put(PhosMsgfParamEnum.THREADS,"thread");
        t.put(PhosMsgfParamEnum.SEARCHDECOY,"tda");
        t.put(PhosMsgfParamEnum.FRAGMENTATIONMETHOD,"m");
        t.put(PhosMsgfParamEnum.MS2DETECTORTYPE,"inst");
        t.put(PhosMsgfParamEnum.ENZYME,"e");
        t.put(PhosMsgfParamEnum.PREDIGESTENZYME,"pde");
        t.put(PhosMsgfParamEnum.PROTOCOL,"protocol");
        t.put(PhosMsgfParamEnum.NUMBEROFTOLERABLETERMINI,"ntt");
        t.put(PhosMsgfParamEnum.MODIFICATIONS,"mod");
        t.put(PhosMsgfParamEnum.MAXPEPTIDELENGTH,"maxLength");
        t.put(PhosMsgfParamEnum.MINPEPTIDELENGTH,"minLength");
        t.put(PhosMsgfParamEnum.PRECURSORMINCHARGE,"minCharge");
        t.put(PhosMsgfParamEnum.PRECURSORMAXCHARGE,"maxCharge");
        t.put(PhosMsgfParamEnum.NUMMATCHESPERSPECTRUM,"n");
        t.put(PhosMsgfParamEnum.EXTRAFEATURES,"addFeatures");
        //t.put(PhosMsgfParamEnum.MASSOFCHARGECARRIER,"ccm");
        t.put(PhosMsgfParamEnum.ALLOWPREFIXMATCHES, "prefixMatchesAllowed");
        t.put(PhosMsgfParamEnum.IGNOREMETCLEAVAGE, "ignoreMetCleavage");
        t.put(PhosMsgfParamEnum.SPECTRUMFILE,"s");
        t.put(PhosMsgfParamEnum.ENGINE,"engine");
        t.put(PhosMsgfParamEnum.MAXMISSEDCLEAVAGES,"maxMissedCleavages");
        switches = t;
    }
    
    /**
     * Compile an (Phospho)MSGF+ configuration to a command line, including the
     * path to the java JRE bin. Uses the java.home property of the currently
     * executing JVM to find path to JRE bin.
     * 
     * @param config Definition of an MSGF+ analysis
     * @return A command that can be executed in a terminal or process that runs
     * MSGF+ with the parameters defined in the configuration.
     */
    public static String compile(PhosMsgfConfig config) {
        StringBuilder sb = new StringBuilder();
        String java = System.getProperty("java.home");
        sb.append(java);
        sb.append("/bin/java -Xmx2048m -jar ./lib/");
        sb.append(config.getEngine());
        sb.append(" ");
        
        for(Enum e : switches.keySet()) {
            /* Differences between command line options for MSGFPlus and
             * PhosphoMSGFPlus mean that not all options will be defined in
             * some cases (valid options for MSGFPlus are a subsets of valid 
             * options for PhosphoMSGFPlus, so just skip undefined options */ 
            if(config.getConfigValue(e) == null) continue;
            
            /* The engine is not a command line switch */
            if(switches.get(e).equals("engine")) continue;
            
            /* Add option to command line */
            sb.append("-");
            sb.append(switches.get(e));
            sb.append(" ");
            sb.append(config.getConfigValue(e));
            sb.append(" ");
        }
        sb.delete(sb.length()-1, sb.length());
        return sb.toString();
    }
    
    /**
     * Compiles an MSGF+ configuration to an array of command line switches and
     * values. This is currently only for debugging and verbose output.
     * 
     * @param config Definition of an MSGF+ analysis
     * @return Command switches and values.
     */
    public static String[] toArgs(PhosMsgfConfig config) {
        List<String> argList = new ArrayList<>();
        for(Enum e : switches.keySet()) {
            /* Differences between command line options for MSGFPlus and
             * PhosphoMSGFPlus mean that not all options will be defined in
             * some cases (valid options for MSGFPlus are a subsets of valid 
             * options for PhosphoMSGFPlus, so just skip undefined options */ 
            if(config.getConfigValue(e) == null) continue;
            
            /* The engine is not a command line switch */
            if(switches.get(e).equals("engine")) continue;
            
            argList.add("-"+switches.get(e));
            argList.add(config.getConfigValue(e));
        }
        return argList.toArray(new String[argList.size()]);
    }
}
