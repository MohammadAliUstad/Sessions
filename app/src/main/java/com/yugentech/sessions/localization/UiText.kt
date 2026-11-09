package com.yugentech.sessions.localization

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Text worked out away from the UI (in a calculator, view model or service) without fixing its
 * language there: it stays a resource reference until it's shown, so it always reads in the
 * app's current language, even after the language changes while the state is alive.
 */
sealed interface UiText {

    /** Text that isn't translated (a number, a user's own words). */
    data class Raw(val value: String) : UiText

    /** A string resource, with its format arguments (which may be [UiText] themselves). */
    class Res(@param:StringRes val id: Int, vararg val args: Any) : UiText {
        override fun equals(other: Any?) = other is Res && id == other.id && args.contentEquals(other.args)
        override fun hashCode() = 31 * id + args.contentHashCode()
    }

    /** A plurals resource for [count], with its format arguments. */
    class Plural(@param:PluralsRes val id: Int, val count: Int, vararg val args: Any) : UiText {
        override fun equals(other: Any?) =
            other is Plural && id == other.id && count == other.count && args.contentEquals(other.args)
        override fun hashCode() = 31 * (31 * id + count) + args.contentHashCode()
    }

    fun asString(context: Context): String = when (this) {
        is Raw -> value
        is Res -> context.getString(id, *resolve(args, context))
        is Plural -> context.resources.getQuantityString(id, count, *resolve(args, context))
    }

    companion object {
        val Empty: UiText = Raw("")

        private fun resolve(args: Array<out Any>, context: Context): Array<Any> =
            Array(args.size) { i -> (args[i] as? UiText)?.asString(context) ?: args[i] }
    }
}

/** [UiText] in the app's current language, for showing in Compose. */
@Composable
fun UiText.asString(): String = asString(LocalContext.current)
