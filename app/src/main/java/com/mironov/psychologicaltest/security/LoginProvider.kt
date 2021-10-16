package com.mironov.psychologicaltest.security

import java.security.NoSuchAlgorithmException
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class LoginProvider {

     val key= byteArrayOf(-95, 117, -112, -11, 17, 78, -124, -55, -9, -37, -31, 49, -13, 109, -6, -50, 93, 7, 38, -75, 67, -55, -92, 54, -111, 3, 31, 10, -28, -22, 92, 87)
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