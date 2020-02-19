package com.webfleet.oauth.common;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Generate a new random key for each user session, this will be employed to verify
 * callback request.
 */
public class RandomKeyInterceptor extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception
    {
        HttpSession session = request.getSession(true);
        if (session.getAttribute(Constants.RANDOM_KEY_SESSION_ATTRIBUTE) == null)
        {
            session.setAttribute(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, new RandomKey());
        }

        return true;
    }
}
