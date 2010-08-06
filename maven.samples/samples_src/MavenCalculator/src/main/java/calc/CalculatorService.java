/*
 * Copyright (c) 2010, Oracle. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Oracle nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package calc;

import javax.annotation.Resource;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/** Calculator Web Service
 *
 * @author mkuchtiak
 */

@WebService(serviceName="CalculatorService")
public class CalculatorService {

    // use @Resource injection to create a WebServiceContext for server logging
    private @Resource
    WebServiceContext webServiceContext;

    /** Calculates the sum of two numbers
     *
     * @param x integer number
     * @param y integer number
     * @return sum of two numbers
     * @throws calc.NegativeNumberException if one of two numbers is negative
     */
    @WebMethod(operationName = "add")
    public int add(@WebParam(name = "x")int x, @WebParam(name = "y")int y)
        throws NegativeNumberException {

        getServletContext().log("Parameters: x="+x+", y="+y);
        if (x < 0) {
            throw new NegativeNumberException("x is less then zero");
        } else if (y < 0) {
            throw new NegativeNumberException("y is less then zero");
        } else {
            return x+y;
        }
    }

    /** Used for server logging.
     *  The operation is oneway: provides no response
     *
     * @param text
     */
    @WebMethod(operationName = "log")
    @Oneway
    public void logServer(@WebParam(name = "message") String text) {
        // log message onto server
        getServletContext().log(text);
    }

    /** Get ServletContext.
     *
     * @return ServletContext object
     */
    private ServletContext getServletContext() {
        return (ServletContext) webServiceContext.getMessageContext().get(
                MessageContext.SERVLET_CONTEXT);
    }

}
