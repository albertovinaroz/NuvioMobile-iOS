package com.nuvio.app.features.downloads

/**
 * A one-shot, fire-and-forget notification that a download finished — a reliable backup for
 * platforms where the live/ongoing progress indicator (e.g. iOS's Live Activity) isn't
 * guaranteed to reflect the final state while the app is backgrounded. Android already surfaces
 * completion through its own persistent system notification (see [DownloadsLiveStatusPlatform]),
 * so this is currently a no-op there.
 */
internal expect object DownloadsCompletionNotificationPlatform {
    fun notifyDownloadCompleted(title: String)
}
