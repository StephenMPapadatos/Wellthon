package stephen.papadatos.wellthon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.databinding.DataBindingUtil
import stephen.papadatos.wellthon.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainActivity = this
    }

    fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(LoginActivity.KEY_CLEAR_CREDENTIALS, true)
        startActivity(intent)
        finish()
    }
}
