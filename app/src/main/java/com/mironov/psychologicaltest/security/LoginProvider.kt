package com.mironov.psychologicaltest.security

import java.security.NoSuchAlgorithmException
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class LoginProvider {

     val iterations = 1000
     val outputKeyLength = 256
     val algorithm="PBEwithHmacSHA1"

     val key= byteArrayOf()
     val salt = "намылить".encodeToByteArray()

     fun getEncodedKey(password:String):ByteArray?{
          var keyFactory: SecretKeyFactory?
          keyFactory=null

          try {
               keyFactory = SecretKeyFactory.getInstance(algorithm);
          } catch (e: NoSuchAlgorithmException) {

          }
          val passphraseOrPin = password.toCharArray()
          val keySpec: KeySpec = PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength)

          return keyFactory?.generateSecret(keySpec)?.encoded
     }

     fun checkPasswordWithKeyFactory(password:String):Boolean {
          return key.contentEquals(getEncodedKey(password))
     }

     fun checkPassword(password:String):Boolean {
          val key="суперпсихолог"
          return key.contentEquals(password)
     }
}