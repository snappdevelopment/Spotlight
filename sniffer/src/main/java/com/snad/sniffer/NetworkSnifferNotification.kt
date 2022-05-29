package com.snad.sniffer

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat.getSystemService
import com.snad.sniffer.ui.NetworkSnifferActivity

internal object NetworkSnifferNotification {

    private const val CHANNEL_ID = "network_sniffer_notification"

    fun send(context: Context) {

        val activityIntent = Intent(context, NetworkSnifferActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, activityIntent, FLAG_IMMUTABLE)

        val bubbleData = Notification.BubbleMetadata.Builder(
            bubbleIntent,
            Icon.createWithResource(context, R.drawable.ic_youtube)
        )
//            .setAutoExpandBubble(true)
//            .setSuppressNotification(true)
            .setDesiredHeight(600)
            .build()

        val person = Person.Builder()
            .setName("Network Sniffer")
            .setImportant(true)
            .build()

        createShortcut(context, person)

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentIntent(bubbleIntent)
            .setContentTitle("Network Sniffer")
            .setContentText("Monitor api calls and their responses")
            .setSmallIcon(R.drawable.ic_youtube)
            .setBubbleMetadata(bubbleData)
            .addPerson(person)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setStyle(Notification.MessagingStyle(person))
            .setShortcutId(person.name.toString())
            .setLocusId(LocusId(person.name.toString()))
            .build()

        val channel = NotificationChannel(CHANNEL_ID, "NetworkSniffer", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Notification for the NetworkSniffer"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(R.id.network_sniffer_notification_id, notification)
    }

    private fun createShortcut(context: Context, person: Person) {
        val shortcutId = person.name.toString()

        val shortcut = ShortcutInfo.Builder(context, shortcutId)
            .setLocusId(LocusId(shortcutId))
            .setShortLabel(shortcutId)
            .setIcon(Icon.createWithResource(context, R.drawable.ic_youtube))
            .setLongLived(true)
            .setPerson(person)
            .setCategories(setOf(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION))
            .setIntent(Intent(context, NetworkSnifferActivity::class.java).setAction(Intent.ACTION_VIEW))
            .build()

        val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        shortcutManager.addDynamicShortcuts(listOf(shortcut))
    }
}
