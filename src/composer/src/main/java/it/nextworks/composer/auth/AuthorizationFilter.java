/*
 * Copyright 2020 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.nextworks.composer.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void init(FilterConfig filterConfig){
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        final String val = req.getHeader("Authorization");
        //log.debug("Authorization header : " + val);
        if (val == null || val.length() == 0) {
            //((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The 'Authorization' header is missing. Please provide a valid token.");
            log.error("The 'Authorization' header is missing. Please provide a valid bearer token.\n");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().print("The 'Authorization' header is missing. Please provide a valid bearer token.\n");
        } else if(!val.matches("Bearer (.+)")){
            log.error("Token format is not valid. Please provide a valid bearer token.\n");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().print("Token format is not valid. Please provide a valid bearer token.\n");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy(){

    }
}
