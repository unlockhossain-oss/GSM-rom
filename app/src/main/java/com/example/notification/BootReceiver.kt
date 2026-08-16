package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.GsmApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val app = context.applicationContext as? GsmApplication ?: return

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pendingCustomers = app.repository.getPendingScheduledCustomers()
                    val currentTime = System.currentTimeMillis()

                    for (customer in pendingCustomers) {
                        if (customer.deliveryTimestamp > currentTime) {
                            AlarmScheduler.scheduleDeliveryAlarm(
                                context = context,
                                customerId = customer.id,
                                customerName = customer.customerName,
                                brandModel = "${customer.brand} ${customer.model}",
                                serviceType = customer.serviceType,
                                deliveryTime = "${customer.deliveryDate} ${customer.deliveryTime}",
                                mobileNumber = customer.mobileNumber,
                                triggerTimestampMs = customer.deliveryTimestamp
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
