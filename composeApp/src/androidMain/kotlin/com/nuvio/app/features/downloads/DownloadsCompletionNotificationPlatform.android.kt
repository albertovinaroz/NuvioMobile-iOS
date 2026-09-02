package com.nuvio.app.features.downloads

// Android already shows a persistent, reliably-updated system notification for downloads (see
// DownloadsLiveStatusPlatform.android.kt) — there's no separate "live" surface that can silently
// fall behind the way iOS's Live Activity can, so a dedicated completion ping is redundant here.
internal actual object DownloadsCompletionNotificationPlatform {
    actual fun notifyDownloadCompleted(title: String) = Unit
}
