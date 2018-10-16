package aaa.helpers.rest.dtoAdmin.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ClaimsMatchingMicroservice {
//	claimsmicro
	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public ClaimsMatchingMicroservice(List<String> strings) {
		setMatches(strings);
	}

	private List<String> matches;

	public List<String> getListMake() {
		return matches;
	}

	public void setMatches(List<String> makes) {
		this.matches = matches;
	}

}
