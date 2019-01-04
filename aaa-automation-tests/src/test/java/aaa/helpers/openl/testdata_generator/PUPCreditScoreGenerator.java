package aaa.helpers.openl.testdata_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.modules.policy.PolicyType;
import toolkit.exceptions.IstfException;

public class PUPCreditScoreGenerator {
	private List<CreditScores> cs;

	PUPCreditScoreGenerator() {
		cs = new ArrayList<>();
		cs.add(CreditScores.of(Constants.States.AZ).and("A", 100).and("C", 500).and("D", 600).and("E", 610).and("F", 650).and("G", 700).and("H", 750).and("I", 800).and("J", 900));

		//TODO-dchubkov: add for remaining states
		cs.add(CreditScores.of(Constants.States.CO).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.CT).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.DC).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.DE).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.ID).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.IN).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.KS).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.KY).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.MD).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.MT).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.NJ).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.NV).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));

		cs.add(CreditScores.of(Constants.States.NY).and("A", 100).and("C", 600).and("D", 610).and("E", 650).and("F", 700).and("G", 750).and("H", 800).and("J", 900));

		cs.add(CreditScores.of(Constants.States.OH).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.OK).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.OR).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.PA).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.SD).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.UT).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.VA).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.WV).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
		cs.add(CreditScores.of(Constants.States.WY).and("A", 0).and("C", 0).and("D", 0).and("E", 0).and("F", 0).and("G", 0).and("H", 0).and("I", 0).and("J", 0));
	}

	public int get(PUPOpenLPolicy openLPolicy) {
		PolicyType policyType = PUPTestDataGenerator.getHomePolicyType(openLPolicy);
		return cs.stream().filter(c -> Objects.equals(c.commonState, openLPolicy.getState()) && Objects.equals(c.commonPolicyType, policyType))
				.findFirst().orElseThrow(() -> new IstfException(String.format("Unknown credit score mapping for state=%1$s and policyType=%2$s", openLPolicy.getState(), policyType)))
				.getScore(openLPolicy.getHomeTier());
	}

	private static final class CreditScores {
		String commonState;
		PolicyType commonPolicyType;
		List<Entry> creditScores;

		private CreditScores(String commonState, PolicyType commonPolicyType) {
			this.commonState = commonState;
			this.commonPolicyType = commonPolicyType;
			this.creditScores = new ArrayList<>();
		}

		static CreditScores of(String state) {
			return of(state, PolicyType.HOME_SS_HO3);
		}

		static CreditScores of(String state, PolicyType homePolicyType) {
			return new CreditScores(state, homePolicyType);
		}

		CreditScores and(String homeTier, int creditScoreValue) {
			creditScores.add(new Entry(homeTier, creditScoreValue));
			return this;
		}

		int getScore(String homeTier) {
			Entry entry = creditScores.stream().filter(e -> Objects.equals(e.homeTier, homeTier)).findFirst()
					.orElseThrow(() -> new IstfException(String.format("Unknown credit score mapping for homeTier=%1$s", homeTier)));

			return ThreadLocalRandom.current().nextInt(entry.creditScoreMinValue, entry.creditScoreMaxValue + 1);
		}

		private class Entry {
			String homeTier;
			int creditScoreMinValue;
			int creditScoreMaxValue;

			Entry(String homeTier, int creditScoreValue) {
				this(homeTier, creditScoreValue, creditScoreValue);
			}

			Entry(String homeTier, int creditScoreMinValue, int creditScoreMaxValue) {
				this.homeTier = homeTier;
				this.creditScoreMinValue = creditScoreMinValue;
				this.creditScoreMaxValue = creditScoreMaxValue;
			}
		}
	}
}
