package aaa.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;

public class TestQuoteCreateAndCopy extends HomeSSBaseTest {
	
	@Test
    @TestInfo(component = "Quote.PersonalLines")
    public void testPolicyCreation() {
        mainApp().open();

        createQuote();
        
	}

}
