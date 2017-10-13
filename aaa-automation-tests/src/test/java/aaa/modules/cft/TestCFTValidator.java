package aaa.modules.cft;

import aaa.helpers.constants.Groups;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;

import java.io.File;

public class TestCFTValidator extends ControlledFinancialBaseTest {

	private static final String DOWNLOAD_DIR = "src/test/resources/cft";
	private static final String SOURCE_DIR = "/home/mp2/pas/sit/FIN_E_EXGPAS_PSFTGL_7000_D/outbound";

	private SSHController sshController = new SSHController(
			PropertyProvider.getProperty(TestProperties.APP_HOST),
			PropertyProvider.getProperty(TestProperties.SSH_USER),
			PropertyProvider.getProperty(TestProperties.SSH_PASSWORD));

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void validate(@Optional(StringUtils.EMPTY) String state) throws SftpException, JSchException {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusYears(1).plusDays(25).plusMonths(13));
		runCFTJobs();
		//Remote path from server -
		sshController.downloadFolder(new File(SOURCE_DIR), new File(DOWNLOAD_DIR));

	}

}
