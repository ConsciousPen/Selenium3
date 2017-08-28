package aaa.main.modules.policy.pup.defaulttabs;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.TextBox;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;

public class PurchaseTab extends Purchase {
	
	public TextBox cash = new  TextBox(By.xpath(".//input[@id='purchaseForm:amount_0']"));
	public TextBox check = new  TextBox(By.xpath(".//input[@id='purchaseForm:amount_1']"));
	public TextBox checkReference = new  TextBox(By.xpath(".//input[@id='purchaseForm:reference_1']"));
	public TextBox visa = new  TextBox(By.xpath(".//input[@id='purchaseForm:amount_2']"));
	public TextBox ach = new  TextBox(By.xpath(".//input[@id='purchaseForm:amount_3']"));
	
	public Dialog acceptcontinueDialog = new Dialog("//div[@id='purchaseForm:VoiceSignatureDialog_container' and not(contains(@style, 'hidden'))]");
	public Button acceptcontinueButton = new Button(By.id("purchaseForm:acceptBtn"));
	public TextBox totalBalance = new TextBox(By.id("purchaseForm:downpaymentComponent_minimumRequiredAmount_disabled"));
	
	public PurchaseTab() {
		super(PurchaseMetaData.PurchaseTab.class);
	}
	
    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);
        return this;
    }

    public Tab superfillTab(TestData td) {
        String remainingValue = remainingBalanceDueToday.getValue();
        String total = totalBalance.getValue();
        if(!StringUtils.isEmpty(remainingValue) && !StringUtils.isEmpty(total)){
            if((visa.isPresent() && !ach.isPresent()) || (!visa.isPresent() && ach.isPresent())){
                visa.setValue(total);
            }
            if(visa.isPresent() && ach.isPresent()){
                BigDecimal totalValue = new BigDecimal(total.substring(1));
                visa.setValue(totalValue.subtract(BigDecimal.valueOf(50)).toString());
                ach.setValue("50");
            }
        }
        assetList.fill(td);
        return this;
    }

    @Override
    public Tab submitTab() {
        btnApplyPayment.click();
        confirmPurchase.confirm();
        if (acceptcontinueDialog.isPresent()) {
            acceptcontinueButton.click();
        }
        return this;
    }
	
}
