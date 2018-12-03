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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class ProteaseTest {
    Protease t;
    
    public ProteaseTest() {
        
    }
    
    @Before
    public void setUp() {
        t = new Protease("[RK]",1);
        t.addMatcher("DRI",1,1,0);
        t.addExclusion("YYRYTI", 2, 3);
    }

    @Test
    public void testOffset_0args() {
        System.out.println("offset()");
        int expResult = 1;
        int result = t.offset();
        assertEquals(expResult, result);
    }

    @Test
    public void testOffset_int() {
        System.out.println("offset(int)");
        int i = 0;
        int expResult = 0;
        int result = t.offset(i);
        assertEquals(expResult, result);
    }

    @Test
    public void testMatcherCount() {
        System.out.println("matcherCount");
        int expResult = 1;
        int result = t.matcherCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testExcluderCount() {
        System.out.println("excluderCount");
        int expResult = 1;
        int result = t.excluderCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsExclusion_true() {
        System.out.println("isExclusion expecting true");
        int i = 0;
        String seq = "TTTYYRYTITTT";
        int site = 5;
        boolean expResult = true;
        boolean result = t.isExclusion(i, seq, site);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsExclusion_false() {
        System.out.println("isExclusion expecting false");
        int i = 0;
        String seq = "TTTZYRYTITTT";
        int site = 5;
        boolean expResult = false;
        boolean result = t.isExclusion(i, seq, site);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsCleaveSite_true() {
        System.out.println("isCleaveSite expecting true");
        int i = 0;
        String seq = "TTTDRIYYY";
        int site = 4;
        int expResult = 0;
        int result = t.isCleaveSite(i, seq, site);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsCleaveSite_false() {
        System.out.println("isCleaveSite expecting false");
        int i = 0;
        String seq = "TTTGRIYYY";
        int site = 4;
        int expResult = -1;
        int result = t.isCleaveSite(i, seq, site);
        assertEquals(expResult, result);
    }
    
}
