package stephen.papadatos.wellthon

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import stephen.papadatos.wellthon.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth0: Auth0
    private lateinit var credentialsManager: SecureCredentialsManager

    companion object {
        const val KEY_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
        private const val EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN"
        private const val EXTRA_ID_TOKEN = "com.auth0.ID_TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth0 = Auth0(this)
        auth0.isOIDCConformant = true
        credentialsManager = SecureCredentialsManager(
            this,
            AuthenticationAPIClient(auth0),
            SharedPreferencesStorage(this)
        )

        if (intent.getBooleanExtra(KEY_CLEAR_CREDENTIALS, false)) {
            credentialsManager.clearCredentials()
        }

        if (credentialsManager.hasValidCredentials()) {
            credentialsManager.getCredentials(object : BaseCallback<Credentials, CredentialsManagerException> {
                override fun onSuccess(credentials: Credentials?) {
                    credentials!!.also {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.accessToken)
                        intent.putExtra(EXTRA_ID_TOKEN, credentials.idToken)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(error: CredentialsManagerException?) {
                    finish()
                }
            })
        }

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginActivity = this
    }

    fun login() {
        WebAuthProvider.init(this)
            .withScheme("demo")
            .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
            .start(this, webCallBack)
    }

    private val webCallBack: AuthCallback = object : AuthCallback {
        override fun onSuccess(credentials: Credentials) {
            credentialsManager.saveCredentials(credentials)
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        override fun onFailure(dialog: Dialog) {
            dialog.show()
        }

        override fun onFailure(exception: AuthenticationException?) {
            Toast.makeText(applicationContext, "Error: " + exception!!.message, Toast.LENGTH_SHORT).show()
        }
    }
}
