package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID_DELIVERY = "gsm_delivery_channel"
    private const val CHANNEL_NAME_DELIVERY = "Customer Delivery Reminders"
    private const val CHANNEL_DESC_DELIVERY = "Notifications for customer phone delivery deadlines and status reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_DELIVERY,
                CHANNEL_NAME_DELIVERY,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_DELIVERY
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDeliveryNotification(
        context: Context,
        customerId: Long,
        customerName: String,
        brandModel: String,
        serviceType: String,
        deliveryTime: String,
        mobileNumber: String
    ) {
        createNotificationChannels(context)

        // Intent to open app directly to customer
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_CUSTOMER_ID", customerId)
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            customerId.toInt(),
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_DELIVERY)
            .setSmallIcon(R.drawable.gsm_service_icon_1786719198213)
            .setContentTitle("⏰ Customer Delivery Reminder")
            .setContentText("Customer: $customerName | $brandModel ($serviceType)")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Customer: $customerName\n" +
                    "Device: $brandModel\n" +
                    "Service: $serviceType\n" +
                    "Scheduled Delivery Time: $deliveryTime\n" +
                    "Contact: $mobileNumber"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(appPendingIntent)

        // Direct Call Action
        if (mobileNumber.isNotBlank()) {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$mobileNumber")
            }
            val callPendingIntent = PendingIntent.getActivity(
                context,
                (customerId + 10000).toInt(),
                callIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                android.R.drawable.ic_menu_call,
                "Call Customer",
                callPendingIntent
            )
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(customerId.toInt(), builder.build())
    }
}
