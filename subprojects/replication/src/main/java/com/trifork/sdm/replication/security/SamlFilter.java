package com.trifork.sdm.replication.security;


import static org.slf4j.LoggerFactory.*;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.*;

import org.slf4j.Logger;

import com.google.inject.Singleton;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;


/**
 * Inserts the user's CPR into a request attribute called 'CPR'.
 */
@Singleton
public class SamlFilter implements Filter
{
	private static final Logger LOG = getLogger(SamlFilter.class);

	private CachingRID2CPRFacadeImpl ridHelper;


	@Inject
	public SamlFilter(CachingRID2CPRFacadeImpl ridHelper)
	{
		this.ridHelper = ridHelper;
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}


	@Override
	public void destroy()
	{
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		try
		{
			// Look up the user's CPR by converting the
			// rID (user id) via a remote webservice.
			// The results are cached.

			UserAssertion user = UserAssertionHolder.get();

			String userID = user.getUserId();
			String userCPR = ridHelper.getCPR(userID);

			request.setAttribute("CPR", userCPR);

			LOG.info("User CPR='{}' accessing the admin GUI. Converted from rID={}.", userCPR, userID);

			chain.doFilter(request, response);
		}
		catch (Exception e)
		{
			LOG.error("Could not look up rID for SAML.", e);
		}
	}
}
