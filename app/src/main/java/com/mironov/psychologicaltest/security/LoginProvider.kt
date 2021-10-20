package com.mironov.psychologicaltest.security

import java.security.NoSuchAlgorithmException
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class LoginProvider {

     val key= byteArrayOf(-122, -82, -121, 119, 2, 74, 78, 126, 116, 98, 109, 28, 58, 105, 0, 7, -85, -102, 121, 79, 72, 40, 114, 115, -28, 107, 103, -114, -22, 3, 78, 49)
     val salt = "намылить".encodeToByteArray()

     fun checkPasswordWithKeyFactory( password:String):Boolean {
          val iterations = 1000;

          // Generate a 256-bit key
          val outputKeyLength = 256;
          var keyFactory: SecretKeyFactory?
          keyFactory=null

          try {
               keyFactory = SecretKeyFactory.getInstance("PBEwithHmacSHA1");
          } catch (e: NoSuchAlgorithmException) {

          }
          val passphraseOrPin = password.toCharArray()
          val keySpec: KeySpec = PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength)

          val p=keyFactory?.generateSecret(keySpec)?.encoded

          return key.contentEquals(p)
     }

     fun checkPassword( password:String):Boolean {
          val key="суперпсихолог"
          return key.contentEquals(password)
     }
}