package com.nuvio.app.features.downloads

import kotlinx.coroutines.runBlocking
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.downloads_completed_notification_body
import nuvio.composeapp.generated.resources.downloads_live_completed
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSUUID
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

// A completion event reliably reaches didFinishDownloadingTo even when the app only gets a brief
// background wake to service it — unlike the Live Activity update in DownloadsLiveActivityManager,
// scheduling a local notification here is a single synchronous call with no async work riding on
// that wake window, so it isn't exposed to the same "ran out of time" failure mode. If the user
// doesn't have notification permission granted, this silently does nothing — it's a bonus signal,
// not the download's only outcome (the Downloads screen itself is always accurate).
internal actual object DownloadsCompletionNotificationPlatform {
    actual fun notifyDownloadCompleted(title: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(runBlocking { getString(Res.string.downloads_live_completed) })
            setBody(runBlocking { getString(Res.string.downloads_completed_notification_body, title) })
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 0.1,
            repeats = false,
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "downloads.completed.${NSUUID().UUIDString}",
            content = content,
            trigger = trigger,
        )
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { _ -> }
    }
}
