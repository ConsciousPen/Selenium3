package aaa.helpers;

import com.exigen.istf.timesetter.client.TimeSetterClient;
import toolkit.config.PropertyProvider;
import toolkit.utils.datetime.DateTime;

@SuppressWarnings("deprecation")
public class TimeSetterBctClient extends TimeSetterClient{

	public TimeSetterBctClient() {
		super();
	}

	public TimeSetterBctClient(String host) {
		super(host);
	}

	@Override
	public DateTime getDateTime() {
		String date = PropertyProvider.getProperty("time.usedate");
		if (PropertyProvider.getProperty("time.uselocal", "true").equals("true") && !date.isEmpty()) {
			return new DateTime(date, DateTime.MM_DD_YYYY);
		}
		return super.getDateTime();
	}
}
