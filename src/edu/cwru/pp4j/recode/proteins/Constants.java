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

/**
 * High accuracy masses of fundamental particles, elements and molecules.
 * 
 * @author Sean Maxwell
 */
public class Constants {
    /**
     * Mass of Carbon 12.
     */
    public static final double MASS_CARBON     = 12.0;
    
    /**
     * Mass of Oxygen.
     */
    public static final double MASS_OXYGEN     = 15.994915;
    
    /**
     * Mass of Nitrogen.
     */
    public static final double MASS_NITROGEN   = 14.003074;
   
    /**
     * Mass of Phosphorous.
     */
    public static final double MASS_PHOSPHORUS = 30.973761998;
    
    /**
     * Mass of Hydrogen.
     */
    public static final double MASS_HYDROGEN   = 1.007825;
    
    /**
     * Mass of a proton.
     */
    public static final double MASS_PROTON     = 1.007276466812;
    
    /**
     * Mass of an electron.
     */
    public static final double MASS_ELECTRON   = 0.000548579909;
    
    /**
     * Mass of water (H2O).
     */
    public static final double MASS_WATER      = (2*MASS_HYDROGEN)+MASS_OXYGEN;
    
    /**
     * Mass of Ammonia (NH3).
     */
    public static final double MASS_AMMONIA    = (3*MASS_HYDROGEN)+MASS_NITROGEN;
    
    /**
     * Mass of Phosphate (PO4).
     */
    public static final double MASS_PHOSPHATE  = (4*MASS_OXYGEN)+MASS_PHOSPHORUS;
    
    public static final double MASS_PHOSPHORYL  = (3*MASS_OXYGEN)+MASS_PHOSPHORUS;
    
    /**
     * Mass of Carbon Monoxide (CO).
     */
    public static final double MASS_CO         = MASS_CARBON+MASS_OXYGEN;
}
