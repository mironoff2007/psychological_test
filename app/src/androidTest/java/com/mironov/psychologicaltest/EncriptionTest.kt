package com.mironov.psychologicaltest;

import android.util.Base64
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@RunWith(AndroidJUnit4::class)
public class EncriptionTest {

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun AsyncEncTest() {

        val targetString = "Hello";

        // Generate key pair for 2048-bit RSA encryption and decryption
        var publicKey: Key? = null
        var privateKey: Key? = null
        try {
            var kpg: KeyPairGenerator? = KeyPairGenerator.getInstance("RSA")
            kpg!!.initialize(2048)
            var kp: KeyPair? = kpg.genKeyPair()
            publicKey = kp!!.public
            privateKey = kp!!.private
        } catch (e: Exception) {
            Log.e("My_tag", "RSA key pair error")
        }

        Log.e("My_tag", "publicKey=$publicKey")
        Log.e("My_tag", "privateKey=$privateKey")

        // Encode the original data with the RSA private key
        var encodedBytes: ByteArray? = null
        try {
            var c: Cipher? = Cipher.getInstance("RSA")
            c!!.init(Cipher.ENCRYPT_MODE, privateKey)
            encodedBytes = c.doFinal(targetString.toByteArray())
        } catch (e: Exception) {
            Log.e("My_tag", "RSA encryption error")
        }

        Log.d("My_tag", "Encoded string: " + (Base64.encodeToString(encodedBytes, Base64.DEFAULT)))

        // Decode the encoded data with the RSA public key

        // Decode the encoded data with the RSA public key
        var decodedBytes: ByteArray? = null
        try {
            var c: Cipher? = Cipher.getInstance("RSA")
            c!!.init(Cipher.DECRYPT_MODE, publicKey)
            decodedBytes = c!!.doFinal(encodedBytes)
        } catch (e: Exception) {
            Log.e("My_tag", "RSA decryption error")
        }

        Log.d("My_tag", "Decoded string: " + String((decodedBytes)!!))

        assertEquals(targetString, String((decodedBytes)!!))
    }

    @Test
    fun encriptPBKDF2Test() {
        val iterations = 1000;

        // Generate a 256-bit key
        val outputKeyLength = 256;
        var keyFactory: SecretKeyFactory?
        keyFactory=null

        try {
             keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (e: NoSuchAlgorithmException) {
            Log.d("My_tag", e.toString())
        }
        val passphraseOrPin = "ширякула".toCharArray()
        val salt = "намылить".encodeToByteArray()
        val keySpec: KeySpec = PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength)

        Log.d("My_tag", "key="+keyFactory?.generateSecret(keySpec)?.encoded)

        var keyFactory2: SecretKeyFactory?
        keyFactory2=null

        try {
            keyFactory2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (e: NoSuchAlgorithmException) {
            Log.d("My_tag", e.toString())
        }
        val passphraseOrPin2 = "ширякула".toCharArray()
        val salt2 = "намылить".encodeToByteArray()
        val keySpec2: KeySpec = PBEKeySpec(passphraseOrPin2, salt2, iterations, outputKeyLength)

        val h1=keyFactory?.generateSecret(keySpec)
        val h2=keyFactory2?.generateSecret(keySpec2)

        val enc1=h1?.encoded
        val enc2=byteArrayOf(91, -6, 29, -96, 43, -94, -10, 106, 122, 63, -35, 36, 24, -120, 34, 69, -48, 53, -69, 30, 84, -51, -47, -94, 105, -32, 106, 21, 38, 105, 107, -116)


        val enc1Hash=h1?.encoded.hashCode()
        val enc2Hash=h2?.encoded.hashCode()

        assertEquals(enc1?.contentEquals(enc2),true )
    }
}
