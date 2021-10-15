package com.mironov.psychologicaltest.security

import android.util.Log
import java.security.NoSuchAlgorithmException
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class LoginProvider {

     val key= byteArrayOf(91, -6, 29, -96, 43, -94, -10, 106, 122, 63, -35, 36, 24, -120, 34, 69, -48, 53, -69, 30, 84, -51, -47, -94, 105, -32, 106, 21, 38, 105, 107, -116)
     val salt = "намылить".encodeToByteArray()

     fun checkPassword( password:String):Boolean {
          val iterations = 1000;

          // Generate a 256-bit key
          val outputKeyLength = 256;
          var keyFactory: SecretKeyFactory?
          keyFactory=null

          try {
               keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
          } catch (e: NoSuchAlgorithmException) {

          }
          val passphraseOrPin = password.toCharArray()
          val keySpec: KeySpec = PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength)

          val p=keyFactory?.generateSecret(keySpec)?.encoded

          return key.contentEquals(p)
     }
}