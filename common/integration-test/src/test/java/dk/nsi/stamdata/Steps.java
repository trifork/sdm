package dk.nsi.stamdata;

import junit.framework.Assert;
import cucumber.annotation.en.Given;

public class Steps
{
    @Given("Udsagn on port (.*) and security level ([a-z]*)")
    public void givenUdsagn(int port, String level)
    {
        Assert.assertEquals(8080, port);
    }
}
