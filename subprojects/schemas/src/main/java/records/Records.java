package records;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.Haendelse;
import com.trifork.stamdata.views.cpr.KommunaleForhold;
import com.trifork.stamdata.views.cpr.MorOgFaroplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.views.cpr.Valgoplysninger;


@XmlRootElement(name="records")
@XmlAccessorType(XmlAccessType.FIELD)
public class Records {
	@XmlElementRefs({
		@XmlElementRef(type=Person.class),
		@XmlElementRef(type=BarnRelation.class),
		@XmlElementRef(type=Civilstand.class),
		@XmlElementRef(type=Foedselsregistreringsoplysninger.class),
		@XmlElementRef(type=Folkekirkeoplysninger.class),
		@XmlElementRef(type=ForaeldremyndighedsRelation.class),
		@XmlElementRef(type=Haendelse.class),
		@XmlElementRef(type=KommunaleForhold.class),
		@XmlElementRef(type=MorOgFaroplysninger.class),
		@XmlElementRef(type=Statsborgerskab.class),
		@XmlElementRef(type=Udrejseoplysninger.class),
		@XmlElementRef(type=UmyndiggoerelseVaergeRelation.class),
		@XmlElementRef(type=Valgoplysninger.class),
	})
	public List<View> records;
}
