package com.activedistribution.presenter.signup;

import com.activedistribution.model.SignUpData;

public interface SignUpInteractor {

     interface onSignedUpSuccess{
         void onResponseSuccess(SignUpData response);
         void onUnauthorised(String message);
         void onResponseFailure(String message);
    }

     void onSignUpCall(String email,String password,String confirmpassword,String device_id,String device_type,onSignedUpSuccess signedUpSuccess);
}
