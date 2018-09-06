package aaa.helpers;

import java.time.LocalDateTime;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import com.exigen.istf.timesetter.client.config.ClientConfig;
import toolkit.config.PropertyProvider;
import toolkit.utils.datetime.DateTimeUtils;

@SuppressWarnings("deprecation")
public class TimeSetterBctClient extends TimeSetterClient {

	public TimeSetterBctClient() {
	}

	public TimeSetterBctClient(ClientConfig config) {
		super(config);
	}

	@Override
	public LocalDateTime getDateTime() {
		String date = PropertyProvider.getProperty("time.usedate");
		if (PropertyProvider.getProperty("time.uselocal", "true").equals("true") && !date.isEmpty()) {
			return LocalDateTime.parse(date, DateTimeUtils.MM_DD_YYYY);
		}
		return super.getDateTime();
	}
}
