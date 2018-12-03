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
package edu.cwru.pp4j.recode.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Runs an external process and waits until it completes. Reads IO into internal
 * members that can be retrieved after process completes.
 * 
 * @author Sean Maxwell
 */
public class ExternalProcess {
    private static final Logger logger = LogManager.getFormatterLogger(ExternalProcess.class.getName());
    private StringBuilder stderrb;
    private StringBuilder stdoutb;
    
    /**
     * The class extends Thread so that it can run in parallel with an external
     * process and empty the OS buffers for STDOUT and STDERR that the process
     * writes to. Based on the tutorial by Michael C. Daconta at:
     *
     * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
     */
    public class AsyncOutputReader extends Thread {

        public InputStream stream;
        public String name;
        public PipedOutputStream pout;

        /**
         * Constructor
         *
         * @param is The stream to read from
         * @param sname The name to identify the stream by
         * @param p If a process is displaying the output in real time, pipe the
         * output over to it.
         */
        public AsyncOutputReader(InputStream is, String sname, PipedOutputStream p)
        {
            stream = is;
            pout = p;
            name = sname;
        }

        /**
         * Override the Thread run method which will do the actual work of
         * connecting to the stream and reading from it until the exe exits.
         */
        @Override
        public void run()
        {
            stdoutb = new StringBuilder();
            stderrb = new StringBuilder();
            
            /* Try to connect the output of the argument stream, to a buffered
             * reader, and read from it until EOF */
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                String line;
                while ( (line = br.readLine()) != null) {
                    if(name.equals("STDERR")) {
                        stderrb.append(line);
                        stderrb.append("\n");
                    }
                    else if(name.equals("STDOUT")) {
                        stdoutb.append(line);
                        stdoutb.append("\n");
                    }
                    
                    if(pout != null) {
                        pout.write(line.getBytes());
                        pout.write('\n');
                    }
                }
            }
            catch (java.io.IOException e)
            {
                logger.error("Threw exception -> %s",e.getMessage());
            }
        }
    }

    public int runCmd(String cmd, PipedOutputStream progress) {
        stdoutb = null;
        stderrb = null;
        
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            AsyncOutputReader stdout = new AsyncOutputReader(p.getInputStream(),"STDOUT",progress);
            AsyncOutputReader stderr = new AsyncOutputReader(p.getErrorStream(),"STDERR",progress);

            /* Start the IO readers to empty the OS buffers for the exe */
            stdout.start();
            stderr.start();

            /* Wait for the exe to finish */
            p.waitFor();
            progress.write("External process exiting\n".getBytes());
            return p.exitValue();
        }
        catch(IOException | InterruptedException e) {
            logger.error("Threw exception -> %s",e.getMessage());
            return -1;
        }
    }
    
    public String getStdout() {
        return stdoutb != null ? stdoutb.toString() : "";
    }
    
    public String getStderr() {
        return stderrb != null ? stderrb.toString() : "";
    }
    
}
