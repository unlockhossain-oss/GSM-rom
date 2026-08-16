package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.GsmApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class DeliveryReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val customerId = intent.getLongExtra(AlarmScheduler.EXTRA_CUSTOMER_ID, -1L)
        if (customerId == -1L) return

        val customerName = intent.getStringExtra(AlarmScheduler.EXTRA_CUSTOMER_NAME) ?: "Customer"
        val brandModel = intent.getStringExtra(AlarmScheduler.EXTRA_DEVICE_MODEL) ?: "Device"
        val serviceType = intent.getStringExtra(AlarmScheduler.EXTRA_SERVICE_TYPE) ?: "Servicing"
        val deliveryTime = intent.getStringExtra(AlarmScheduler.EXTRA_DELIVERY_TIME) ?: "Now"
        val mobileNumber = intent.getStringExtra(AlarmScheduler.EXTRA_MOBILE_NUMBER) ?: ""

        val app = context.applicationContext as? GsmApplication

        // Trigger Notification
        NotificationHelper.showDeliveryNotification(
            context = context,
            customerId = customerId,
            customerName = customerName,
            brandModel = brandModel,
            serviceType = serviceType,
            deliveryTime = deliveryTime,
            mobileNumber = mobileNumber
        )

        // Optionally play voice reminder if enabled in UserPreferences
        if (app != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = app.userPreferences.settingsFlow.first()
                    if (settings.voiceReminderEnabled) {
                        val customer = app.repository.getCustomerById(customerId)
                        val voicePath = customer?.voiceFilePath
                        if (!voicePath.isNullOrBlank()) {
                            val file = File(voicePath)
                            if (file.exists()) {
                                playReminderVoice(context, file)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun playReminderVoice(context: Context, file: File) {
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
                setDataSource(context, Uri.fromFile(file))
                prepare()
                start()
            }
            player.setOnCompletionListener {
                player.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
