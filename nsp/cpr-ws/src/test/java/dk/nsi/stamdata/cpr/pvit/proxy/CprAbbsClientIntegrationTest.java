package dk.nsi.stamdata.cpr.pvit.proxy;

import dk.nsi.stamdata.cpr.integrationtest.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CprAbbsClientIntegrationTest {
	private CprAbbsStubJettyServer server;
	private SecurityWrapper securityHeaders;

	@Before
	public void setupSecurityHeaders() throws Exception {
		securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper("22345678", "foo", "bar");
	}

	@Before
	public void startServer() throws Exception {
		server = new CprAbbsStubJettyServer();
		server.startServer(8099);
	}

	@After
	public void stopServer() throws Exception {
		server.stopServer();
	}

	@Test
	public void canCallService() throws MalformedURLException, CprAbbsException {
		CprAbbsClient client = new CprAbbsClient("http://localhost:8099/cprabbs/service/cprabbs");
		List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), null);

		assertEquals(1, changedCprs.size());
		assertEquals("0000000000", changedCprs.get(0));
	}

	@Test
	public void callsWithSinceWhichDoesntTriggerSpecialBehaviourInStub() throws MalformedURLException, CprAbbsException {
		CprAbbsClient client = new CprAbbsClient("http://localhost:8099/cprabbs/service/cprabbs");
		List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), CprAbbsFacadeStubImplementation.SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES.plusDays(1));

		assertEquals(1, changedCprs.size());
		assertEquals("2222222222", changedCprs.get(0));
	}

	@Test
	public void callsWithSinceWhichTriggersSpecialBehaviourInStub() throws MalformedURLException, CprAbbsException {
		CprAbbsClient client = new CprAbbsClient("http://localhost:8099/cprabbs/service/cprabbs");
		List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), CprAbbsFacadeStubImplementation.SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES);

		assertEquals(1, changedCprs.size());
		assertEquals("1111111111", changedCprs.get(0));
	}

	@Test
	public void forwardsIdcardCvrInSecurityHeaders() throws Exception {
		securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper("12345678", "foo", "bar"); // cvr starting with 1 triggers special behaviour in stub service
		CprAbbsClient client = new CprAbbsClient("http://localhost:8099/cprabbs/service/cprabbs");
		List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), null);

		assertEquals(1, changedCprs.size());
		assertEquals("1234567800", changedCprs.get(0));

	}
}
