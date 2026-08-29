package com.yugentech.sessions.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val firebaseModule = module {

    single { FirebaseAuth.getInstance() }

    single { FirebaseFirestore.getInstance() }
}
