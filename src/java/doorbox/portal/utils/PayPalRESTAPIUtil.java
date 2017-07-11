/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.utils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.paypal.api.payments.Address;
import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.AgreementDetails;
import com.paypal.api.payments.ChargeModels;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.FundingInstrument;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.PaymentDefinition;
import com.paypal.api.payments.Plan;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author torinw
 */
public class PayPalRESTAPIUtil {    
    public static final String clientID = "AVMYHX8Mo8XcsElgkbSctj2FOdX4KHM537oLlQio2UsAoyDZuc-ssJ-ZK7mlMeRynLq-Y4k3ZTeFzwxK";
    public static final String clientSecret = "EGQeJi1j4RnYbjAH1Y1pUyGPQiqGGL3m79pvHNN5n1AlWh__7s7VGSLaaRDg2Zk6sZFgHnDFh4mN8-JB";

    protected Plan planInstance = null;
    protected Agreement agreementInstance = null;
    
    /**
     * Main method that calls all methods above in a flow.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
                PayPalRESTAPIUtil apiUtil = new PayPalRESTAPIUtil();

                APIContext context = new APIContext(clientID, clientSecret, "sandbox");

                System.out.println("create plan");
                Plan plan = apiUtil.createPlan(context);
                System.out.println("create plan response:\n" + Plan.getLastResponse());
                
                System.out.println("Activate plan");
                plan = apiUtil.updatePlan(context);
                System.out.println("plan activated");
                plan = apiUtil.retrievePlan(context);
                System.out.println("plan creation response:\n" + Plan.getLastResponse());
                
                
                System.out.println("create agreement using plan");
                Agreement agreement = apiUtil.createAgreement(context);
                System.out.println("create agreement response:\n" + Agreement.getLastResponse());
                
                System.out.println("Activate agreement");
                agreement = apiUtil.retrieveAgreement(context);
                System.out.println("agreement activation response:\n" + Agreement.getLastResponse());
                
        } catch (JsonSyntaxException e) {
                e.printStackTrace();
        } catch (JsonIOException e) {
                e.printStackTrace();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (PayPalRESTException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    /**
     * Create a plan.
     *
     * https://developer.paypal.com/webapps/developer/docs/api/#create-a-plan
     *
     * @return newly created Plan instance
     * @throws PayPalRESTException
     */
    public Plan createPlan(APIContext context) throws PayPalRESTException, IOException {
        // Build Plan object
        Plan plan = new Plan();
        plan.setName("Medium DoorBox SDI Plan 1");
        plan.setDescription("Medium DoorBox Monthly Subscription plus GTA Delivery and Installation Plan version 1");
        plan.setType("INFINITE");

        //currency
        Currency serviceFeeCurrency = new Currency();
        serviceFeeCurrency.setCurrency("CAD");
        serviceFeeCurrency.setValue("8.99");

        Currency serviceFeeTaxCurrency = new Currency();
        serviceFeeTaxCurrency.setCurrency("CAD");
        serviceFeeTaxCurrency.setValue("1.17");

        Currency otpCurrency = new Currency();
        otpCurrency.setCurrency("CAD");
        otpCurrency.setValue("39.99");

        Currency otpTaxCurrency = new Currency();
        otpTaxCurrency.setCurrency("CAD");
        otpTaxCurrency.setValue("5.20");

        //payment_definitions
        PaymentDefinition recurringPaymentDefinition = new PaymentDefinition();
        recurringPaymentDefinition.setName("Monthly Service Fee");
        recurringPaymentDefinition.setType("REGULAR");
        recurringPaymentDefinition.setFrequency("MONTH");
        recurringPaymentDefinition.setFrequencyInterval("1");
        recurringPaymentDefinition.setCycles("0"); // 0 infinite
        recurringPaymentDefinition.setAmount(serviceFeeCurrency);

//        PaymentDefinition otpPaymentDefinition = new PaymentDefinition();
//        otpPaymentDefinition.setName("Delivery and Installation OTP");
//        otpPaymentDefinition.setType("REGULAR");
//        otpPaymentDefinition.setFrequency("MONTH");
//        otpPaymentDefinition.setFrequencyInterval("1");
//        otpPaymentDefinition.setCycles("1"); // one time payment
//        otpPaymentDefinition.setAmount(otpCurrency);

        //MerchantPreferences merchantPreferences = new MerchantPreferences();
        //merchantPreferences.setSetupFee(otpCurrency);

        //charge_models
        ChargeModels serviceFeeChargeModels = new ChargeModels();
        serviceFeeChargeModels.setType("TAX");
        serviceFeeChargeModels.setAmount(serviceFeeTaxCurrency);

        List<ChargeModels> serviceFeeChargeModelsList = new ArrayList<ChargeModels>();
        serviceFeeChargeModelsList.add(serviceFeeChargeModels);
        recurringPaymentDefinition.setChargeModels(serviceFeeChargeModelsList);

        //charge_models
        ChargeModels otpChargeModels = new ChargeModels();
        otpChargeModels.setType("TAX");
        otpChargeModels.setAmount(otpTaxCurrency);
        List<ChargeModels> otpChargeModelsList = new ArrayList<ChargeModels>();
        otpChargeModelsList.add(otpChargeModels);
        recurringPaymentDefinition.setChargeModels(otpChargeModelsList);

        //payment_definition
        List<PaymentDefinition> paymentDefinitionList = new ArrayList<PaymentDefinition>();
        paymentDefinitionList.add(recurringPaymentDefinition);
        //paymentDefinitionList.add(otpPaymentDefinition);
        plan.setPaymentDefinitions(paymentDefinitionList);

        //merchant_preferences
        MerchantPreferences merchantPreferences = new MerchantPreferences();
        merchantPreferences.setSetupFee(otpCurrency);
        merchantPreferences.setReturnUrl("https://portal.thedoorbox.com/subscriptionComplete.xhtml");
        merchantPreferences.setCancelUrl("https://portal.thedoorbox.com/subscriptionCancelled.xhtml");
        merchantPreferences.setMaxFailAttempts("0");
        merchantPreferences.setAutoBillAmount("YES");
        merchantPreferences.setInitialFailAmountAction("CONTINUE");
        plan.setMerchantPreferences(merchantPreferences);

        this.planInstance = plan.create(context);
        return this.planInstance;
    }

    /**
     * Update a plan
     *
     * https://developer.paypal.com/webapps/developer/docs/api/#update-a-plan
     *
     * @return updated Plan instance
     * @throws PayPalRESTException
     */
    public Plan updatePlan(APIContext context) throws PayPalRESTException, IOException {

        List<Patch> patchRequestList = new ArrayList<Patch>();
        Map<String, String> value = new HashMap<String, String>();
        value.put("state", "ACTIVE");

        Patch patch = new Patch();
        patch.setPath("/");
        patch.setValue(value);
        patch.setOp("replace");
        patchRequestList.add(patch);

        this.planInstance.update(context, patchRequestList);
        return this.planInstance;
    }

    /**
     * Retrieve a plan
     *
     * https://developer.paypal.com/webapps/developer/docs/api/#retrieve-a-plan
     *
     * @return the retrieved plan
     * @throws PayPalRESTException
     */
    public Plan retrievePlan(APIContext context) throws PayPalRESTException {
        return Plan.get(context, this.planInstance.getId());
    }

    /**
     * Create an agreement.
     *
     * https://developer.paypal.com/webapps/developer/docs/api/#create-a-plan
     *
     * @return newly created Plan instance
     * @throws PayPalRESTException
     */
    public Agreement createAgreement(APIContext context) throws PayPalRESTException, IOException {
        // Build Plan object
        Address billingAddress = new Address();
        billingAddress.setLine1("123 foo st");
        billingAddress.setLine2("Suite 200");
        billingAddress.setCity("Toronto");
        billingAddress.setCountryCode("CA");
        billingAddress.setPostalCode("A1A 2B2");
        billingAddress.setPhone("+1 416 555 1212"); //E.123 format
        
        Address shippingAddress = new Address();
        shippingAddress.setLine1("123 foo st");
        shippingAddress.setLine2("Suite 200");
        shippingAddress.setCity("Toronto");
        shippingAddress.setCountryCode("CA");
        shippingAddress.setPostalCode("A1A 2B2");
        shippingAddress.setPhone("+1 416 555 1212"); //E.123 format
        
        CreditCard creditCard = new CreditCard();        
        creditCard.setFirstName("Foo");        
        creditCard.setLastName("Bar");
        creditCard.setBillingAddress(billingAddress);
        creditCard.setNumber("4012888888881881");
        creditCard.setType("visa");
        creditCard.setExpireMonth(1);
        creditCard.setExpireYear(2017);
        creditCard.setCvv2("123");
        
        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(creditCard);
        
        Payer payer = new Payer();
        ArrayList<FundingInstrument> fundingInstruments = new ArrayList<FundingInstrument>();
        fundingInstruments.add(fundingInstrument);
        payer.setFundingInstruments(fundingInstruments);
        payer.setPaymentMethod("CREDIT_CARD");
        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setEmail("foo@bar.com");
        //payerInfo.setFirstName("Foo");
        //payerInfo.setLastName("Bar");
        //payerInfo.setPhone("+1 416 555 1212");
        payerInfo.setBillingAddress(billingAddress);
                
        Agreement agreement = new Agreement();
        agreement.setName("Medium DoorBox SDI Agreement 1");
        agreement.setDescription("Medium DoorBox Monthly Subscription plus GTA Delivery and Installation Agreement version 1");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(cal.getTime());
        agreement.setStartDate(startDate);        
        agreement.setPayer(payer);
        agreement.setShippingAddress(shippingAddress);
        
        Plan plan_idOnly = new Plan();
        plan_idOnly.setId(planInstance.getId());
        agreement.setPlan(plan_idOnly);
        
        agreementInstance = agreement.create(context);
        return agreementInstance;
    }
    
     /**
     * Update an agreement
     *
     * https://developer.paypal.com/webapps/developer/docs/api/#update-a-plan
     *
     * @return updated Plan instance
     * @throws PayPalRESTException
     */
    public Agreement updateAgreement(APIContext context) throws PayPalRESTException, IOException {

        List<Patch> patchRequestList = new ArrayList<Patch>();
        Map<String, String> value = new HashMap<String, String>();
        value.put("state", "ACTIVE");

        Patch patch = new Patch();
        patch.setPath("/");
        patch.setValue(value);
        patch.setOp("replace");
        patchRequestList.add(patch);

        this.agreementInstance.update(context, patchRequestList);
        return this.agreementInstance;
    }
    
    /**
     * Retrieve an agreement
     *
     * @return the retrieved plan
     * @throws PayPalRESTException
     */
    public Agreement retrieveAgreement(APIContext context) throws PayPalRESTException {
        return Agreement.get(context, this.agreementInstance.getId());
    }
}
