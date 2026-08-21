package dev.foss.obdforge.about

import android.content.Context
import org.json.JSONObject

data class DonationLink(val label: String, val url: String)

data class DonationsConfig(
    val enabled: Boolean,
    val message: String,
    val links: List<DonationLink>,
)

object DonationsLoader {
    fun load(context: Context): DonationsConfig {
        val loaded = try {
            val json = context.assets.open("donations.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val message = root.optString("message", "")
            val links = mutableListOf<DonationLink>()
            val arr = root.optJSONArray("links")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val item = arr.getJSONObject(i)
                    links.add(DonationLink(item.optString("label"), item.optString("url")))
                }
            }
            DonationsConfig(enabled = true, message = message, links = links)
        } catch (_: Exception) {
            DonationsConfig(enabled = true, message = "", links = emptyList())
        }
        return ensureVenmo(loaded)
    }

    fun ensureVenmo(config: DonationsConfig): DonationsConfig {
        val links = config.links.toMutableList()
        val idx = links.indexOfFirst { it.url == ProductUpdate.DONATION_URL }
        if (idx >= 0) {
            links[idx] = DonationLink("Donate via Venmo", ProductUpdate.DONATION_URL)
        } else {
            links.add(0, DonationLink("Donate via Venmo", ProductUpdate.DONATION_URL))
        }
        return config.copy(enabled = true, links = links)
    }
}
