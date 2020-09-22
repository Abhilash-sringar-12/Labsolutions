package com.example.labsolutions.services;

import com.example.labsolutions.pojos.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class UpdateToken {

    public UpdateToken() {

    }

    public static void updateAccessToken(String currentToken, String userType) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentToken.isEmpty())
            currentToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(currentToken);
        if (userType == "admin") {

            FirebaseDatabase.getInstance().getReference("admin").child(firebaseUser.getUid()).child("token").setValue(token);
        } else if (userType == "workAdmin") {

            FirebaseDatabase.getInstance().getReference("workadmin").child(firebaseUser.getUid()).child("token").setValue(token);
        } else {
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("token").setValue(token);

        }
    }
}
