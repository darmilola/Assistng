package ng.assist.UIs.model;

import java.util.ArrayList;


public class RemitaBank {

    private String bankName;
    private String bankCode;
    private ArrayList<RemitaBank> remitaBanks = new ArrayList<>();

   public RemitaBank(String mBankName,String mBankCode){
             this.bankCode = mBankCode;
             this.bankName = mBankName;
   }

   public RemitaBank(){

   }
    public String getBankCode() {
        return bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void populateBanks(){
//heritage, suntrust keystone
       RemitaBank fidelity = new RemitaBank("Fidelity","070");
       RemitaBank fcmb  = new RemitaBank("Fcmb","214");
       RemitaBank heritage = new RemitaBank("Unity","215");
       RemitaBank zenith = new RemitaBank("Zenith","057");
       RemitaBank jaiz = new RemitaBank("Jaiz","301");
       RemitaBank keystone = new RemitaBank("Providus","101");
       RemitaBank coronation = new RemitaBank("Coronation","459");
       RemitaBank sterling = new RemitaBank("Sterling","232");

       remitaBanks.add(fidelity);
       remitaBanks.add(fcmb);
       remitaBanks.add(heritage);
       remitaBanks.add(zenith);
       remitaBanks.add(jaiz);
       remitaBanks.add(keystone);
       remitaBanks.add(coronation);
       remitaBanks.add(sterling);


       //  String[] bankNames = {"Fidelity","Fcmb","Heritage","Sterling","SunTrust","Unity","Zenith","Jaiz","Keystone","Providus","Coronation"};
    }

    public ArrayList<RemitaBank> getRemitaBanks() {
        return remitaBanks;
    }
}
