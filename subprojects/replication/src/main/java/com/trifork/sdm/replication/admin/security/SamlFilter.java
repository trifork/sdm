package com.trifork.sdm.replication.admin.security;


import static com.trifork.sdm.replication.admin.models.RequestAttributes.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.*;

import org.slf4j.Logger;

import com.google.inject.Singleton;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.admin.models.IUserRepository;
import com.trifork.sdm.replication.saml.SingleSignonHelper;

import dk.itst.oiosaml.sp.UserAssertion;


/**
 * Inserts the user's CPR into a request attribute called 'CPR'.
 */
@Singleton
public class SamlFilter implements Filter
{
	private static final Logger LOG = getLogger(SamlFilter.class);

	@Inject
	private RID2CPRFacade ridHelper;

	@Inject
	private SingleSignonHelper singleSignonHelper;

	@Inject
	private IUserRepository userRepository;


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		try
		{
			UserAssertion user = singleSignonHelper.getUser();

			// Look up the user's CPR by converting the
			// rID (user id) via a remote webservice.
			// The results are cached.

			String userID = user.getUserId();
			String userCPR = ridHelper.getCPR(userID);
			String userCVR = user.getCPRNumber();

			// Make sure that the user is authorized as admin.

			if (userRepository.isAdmin(userCPR, userCVR))
			{
				LOG.info("User CPR='{}' accessing the admin GUI. Converted from rID={}.", userCPR, userID);

				request.setAttribute(USER_CPR, userCPR);

				chain.doFilter(request, response);
			}
			else
			{
				LOG.warn("Unauthorized access attempt by user CPR={}, CVR={}.", userCPR, userCVR);
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not look up user in the SAML filter.", e);
		}
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}


	@Override
	public void destroy()
	{
	}
}
