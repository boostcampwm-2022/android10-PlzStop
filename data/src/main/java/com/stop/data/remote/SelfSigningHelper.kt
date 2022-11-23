package com.stop.data.remote

import android.content.Context
import com.stop.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

@Singleton
class SelfSigningHelper @Inject constructor(
    @ApplicationContext context: Context
) {

    lateinit var trustManagerFactory: TrustManagerFactory
    lateinit var sslContext: SSLContext

    init {
        val certificateFactory: CertificateFactory
        val apisCertificate: Certificate
        val apisCaInput: InputStream

        val tMapCertificate: Certificate
        val tMapCalInput: InputStream

        try {
            certificateFactory = CertificateFactory.getInstance("X.509")

            apisCaInput = context.resources.openRawResource(R.raw.gsrsaovsslca2018)
            apisCertificate = certificateFactory.generateCertificate(apisCaInput)
//            println("ca = ${(certificate as X509Certificate).subjectDN}")

            tMapCalInput = context.resources.openRawResource(R.raw.sectigo_rsa_organization_validation_secure_server_ca)
            tMapCertificate = certificateFactory.generateCertificate(tMapCalInput)

            // Create a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            with(keyStore) {
                load(null, null)
                keyStore.setCertificateEntry("ca", apisCertificate)
                keyStore.setCertificateEntry("ca", tMapCertificate)
            }

            // Create a TrustManager that trusts the CAs in our KeyStore
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
            trustManagerFactory.init(keyStore)

            // Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            apisCaInput.close()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}