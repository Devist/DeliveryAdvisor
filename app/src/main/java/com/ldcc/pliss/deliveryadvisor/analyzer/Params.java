package com.ldcc.pliss.deliveryadvisor.analyzer;

import android.util.Log;

import java.util.List;


public class Params {

    List<String> tokens;

    boolean isCurrentCustomer;
    boolean isNextCustomer;
    boolean isCall;
    String  isCompleteDelivery;
    boolean isDirectReceipt;
    String  isOtherReceipt;
    String  isPlaceReceipt;
    boolean isNavigate;

    Params(List<String> tokens){
        this.tokens = tokens;
        isCurrentCustomer   = isCurrentCustomer();
        isNextCustomer      = isNextCustomer();
        isCall              = isCall();
        isCompleteDelivery  = isCompleteDelivery();
        isDirectReceipt     = isDirectReceipt();
        isOtherReceipt      = isOtherReceipt();
        isPlaceReceipt      = isPlaceReceipt();
        isNavigate          = isNavigate();
    }

    private boolean isCurrentCustomer(){
        for(String keyword : ParamSet.currentCustomer) {
            if(tokens.contains(keyword))
                return true;
        }
        return false;
    }

    private boolean isNextCustomer(){
        for(String keyword : ParamSet.nextCustomer) {
            if(tokens.contains(keyword))
                return true;
        }
        return false;
    }

    private boolean isCall(){
        for(String keyword : ParamSet.callKeywords) {
            if(tokens.contains(keyword))
                return true;
        }
        return false;
    }

    private String isCompleteDelivery(){
        for(String keyword : ParamSet.deliveryKeywords) {
            if(tokens.contains(keyword)) {
                for(String doneKeyword : ParamSet.deliveryDoneKeywords){
                    if(tokens.contains(doneKeyword))
                        return "배송완료";
                }
                for(String cancleKeyword : ParamSet.deliveryCancleKeywords){
                    if(tokens.contains(cancleKeyword))
                        return "배송미완료";
                }
            }
        }

        if(tokens.contains(ParamSet.done))
            return "배송완료";

        for(String cancleKeyword : ParamSet.cancle){
            if(tokens.contains(cancleKeyword))
                return "배송미완료";
        }



        return null;
    }

    private boolean isDirectReceipt(){
        for(String keyword : ParamSet.directKeywords) {
            if(tokens.contains(keyword)) {
                for(String combKeyword : ParamSet.directCombination) {
                    if (tokens.contains(combKeyword))
                        return true;
                }
            }
        }

        for(String keyword : ParamSet.directKeywords2){
            if(tokens.contains(keyword))
                return true;
        }

        return false;
    }

    private String isOtherReceipt(){
        for(String keyword : ParamSet.otherCombination){
            if(tokens.contains(keyword)){
                for(String familyKeyword : ParamSet.otherFamilyKeywords){
                    if(tokens.contains(familyKeyword))
                        return "대리수령:가족";
                }

                for(String companyKeyword : ParamSet.otherCompanyKeywords){
                    if(tokens.contains(companyKeyword))
                        return "대리수령:동료";
                }

                for(String friendKeyword : ParamSet.otherFriendKeywords){
                    if(tokens.contains(friendKeyword))
                        return "대리수령:친구/동거인";
                }

                for(String otherKeyword : ParamSet.otherKeywords){
                    if(tokens.contains(otherKeyword)){
                        String result = "대리수령자없음";
                        for(String familyKeyword : ParamSet.otherFamilyKeywords){
                            if(tokens.contains(familyKeyword))
                                result = "대리수령:가족";
                        }

                        for(String companyKeyword : ParamSet.otherCompanyKeywords){
                            if(tokens.contains(companyKeyword))
                                result = "대리수령:동료";
                        }

                        for(String friendKeyword : ParamSet.otherFriendKeywords){
                            if(tokens.contains(friendKeyword))
                                result = "대리수령:친구/동거인";
                        }
                        return result;
                    }
                }
            }
        }

        return null;
    }

    private String isPlaceReceipt(){
        for(String keyword : ParamSet.doors){
            if(tokens.contains(keyword))
                return "택배보관:문";
        }

        for(String keyword : ParamSet.door){
            if(tokens.contains(keyword)){
                for(String combKeyword : ParamSet.doorCombination){
                    if(tokens.contains(combKeyword)){
                        return  "택배보관:문";
                    }
                }
            }
        }

        for(String keyword : ParamSet.security){
            if(tokens.contains(keyword))
                return "택배보관:경비실";
        }

        for(String keyword : ParamSet.storages){
            if(tokens.contains(keyword)){
                for(String combKeyword : ParamSet.storagesCombination){
                    if(tokens.contains(combKeyword))
                        return "택배보관:보관실";
                }
            }
        }

        if(tokens.contains(ParamSet.unmanned))
            return "택배보관:보관실";

        return null;
    }

    private boolean isNavigate() {
        for (String keyword : ParamSet.naviLocation) {
            if (tokens.contains(keyword)) {
                for (String combKeyword : ParamSet.naviCombination) {
                    if (tokens.contains(combKeyword))
                        return true;
                }
            }
        }

        for (String keyword : ParamSet.nextLocation) {
            if (tokens.contains(keyword)) {
                for (String combKeyword : ParamSet.nextLocationCombination) {
                    if (tokens.contains(combKeyword))
                        return true;
                }
            }
        }

        for (String keyword : ParamSet.howLocation) {
            if (tokens.contains(keyword)) {
                for (String combKeyword : ParamSet.howLocationCombination) {
                    if (tokens.contains(combKeyword))
                        return true;
                }
            }
        }

        return false;
    }

    public void setCurrentCustomer(boolean currentCustomer) {
        isCurrentCustomer = currentCustomer;
    }

    public void setNextCustomer(boolean nextCustomer) {
        isNextCustomer = nextCustomer;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public String getIsCompleteDelivery() {
        return isCompleteDelivery;
    }

    public void setIsCompleteDelivery(String isCompleteDelivery) {
        this.isCompleteDelivery = isCompleteDelivery;
    }

    public void setDirectReceipt(boolean directReceipt) {
        isDirectReceipt = directReceipt;
    }

    public String getIsOtherReceipt() {
        return isOtherReceipt;
    }

    public void setIsOtherReceipt(String isOtherReceipt) {
        this.isOtherReceipt = isOtherReceipt;
    }

    public String getIsPlaceReceipt() {
        return isPlaceReceipt;
    }

    public void setIsPlaceReceipt(String isPlaceReceipt) {
        this.isPlaceReceipt = isPlaceReceipt;
    }

    public void setNavigate(boolean navigate) {
        isNavigate = navigate;
    }
}
