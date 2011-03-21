package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output(name="Person")
public class NavneBeskyttelse extends CPREntity {
	
	Date navneBeskyttelseStartDato;
	Date navneBeskyttelseSletteDato;
	
	
	@Id
	@Output
	@Override
	public String getCpr() {
		return cpr;
	}

	@Output
	public Date getNavneBeskyttelseStartDato() {
		return navneBeskyttelseStartDato;
	}



	public void setNavneBeskyttelseStartDato(Date startDato) {
		this.navneBeskyttelseStartDato = startDato;
	}



	@Output
	public Date getNavneBeskyttelseSletteDato() {
		return navneBeskyttelseSletteDato;
	}



	public void setNavneBeskyttelseSletteDato(Date sletteDato) {
		this.navneBeskyttelseSletteDato = sletteDato;
	}
}
