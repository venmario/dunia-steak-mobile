package com.example.restoapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.restoapp.R
import com.example.restoapp.databinding.ActivityMainBinding
import com.example.restoapp.util.setFcmTokens
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    companion object{
        val OLD_FCM_TOKEN = "OLD_FCM_TOKEN"
        val CURRENT_FCM_TOKEN = "CURRENT_FCM_TOKEN"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            Log.d("TAG", token)
            setFcmTokens(this,token)
        })
        askNotificationPermission()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = (supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment).navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.historyDetailFragment
                || destination.id == R.id.historyListFragment
                || destination.id == R.id.productDetailFragment
                || destination.id == R.id.listNotificationFragment
                || destination.id == R.id.poinOrderFragment
                || destination.id == R.id.detailPoinOrderFragment
                || destination.id == R.id.editProfileFragment
                || destination.id == R.id.helpCenterFragment
                || destination.id == R.id.termsServiceFragment
                || destination.id == R.id.settingFragment
                || destination.id == R.id.changePasswordFragment
                || destination.id == R.id.confirmOrderPointFragment) {
                binding.bottomNav.visibility = View.GONE
            }else{
                binding.bottomNav.visibility = View.VISIBLE
            }
        }

        val extras = intent.extras
        if (extras != null) {
            extras.getString("transaction_id")?.let { transactionId ->
                Log.d("extra", "transaction id : $transactionId")
                // Navigate to history detail fragment with transactionId as argument
//                val action = navController.findDestination(R.id.actionToHistoryDetail)
                val action = MainFragmentDirections.actionNotifToDetailHistory(transactionId)
                navController.navigate(action)
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}