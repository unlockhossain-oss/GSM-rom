package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object AlarmScheduler {

    const val EXTRA_CUSTOMER_ID = "EXTRA_CUSTOMER_ID"
    const val EXTRA_CUSTOMER_NAME = "EXTRA_CUSTOMER_NAME"
    const val EXTRA_DEVICE_MODEL = "EXTRA_DEVICE_MODEL"
    const val EXTRA_SERVICE_TYPE = "EXTRA_SERVICE_TYPE"
    const val EXTRA_DELIVERY_TIME = "EXTRA_DELIVERY_TIME"
    const val EXTRA_MOBILE_NUMBER = "EXTRA_MOBILE_NUMBER"

    fun scheduleDeliveryAlarm(
        context: Context,
        customerId: Long,
        customerName: String,
        brandModel: String,
        serviceType: String,
        deliveryTime: String,
        mobileNumber: String,
        triggerTimestampMs: Long
    ) {
        if (triggerTimestampMs <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, DeliveryReminderReceiver::class.java).apply {
            putExtra(EXTRA_CUSTOMER_ID, customerId)
            putExtra(EXTRA_CUSTOMER_NAME, customerName)
            putExtra(EXTRA_DEVICE_MODEL, brandModel)
            putExtra(EXTRA_SERVICE_TYPE, serviceType)
            putExtra(EXTRA_DELIVERY_TIME, deliveryTime)
            putExtra(EXTRA_MOBILE_NUMBER, mobileNumber)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            customerId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimestampMs,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimestampMs,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimestampMs,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimestampMs,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimestampMs,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelDeliveryAlarm(context: Context, customerId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DeliveryReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            customerId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
