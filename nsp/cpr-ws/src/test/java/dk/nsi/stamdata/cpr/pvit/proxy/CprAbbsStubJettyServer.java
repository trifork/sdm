package dk.nsi.stamdata.cpr.pvit.proxy;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class CprAbbsStubJettyServer {

	private Server server;

	public void startServer(int port) throws Exception {
		server = new Server(port);

		WebAppContext cprabbsContext = new WebAppContext();
		cprabbsContext.setWar("src/test/resources/cprabbs-stub-webapp");
		cprabbsContext.setContextPath("/cprabbs");
		server.addHandler(cprabbsContext);

		server.start();
	}

	public void stopServer() throws Exception {
		if (server != null && server.isRunning()) {
			server.stop();
		}
		server = null;
	}
}
