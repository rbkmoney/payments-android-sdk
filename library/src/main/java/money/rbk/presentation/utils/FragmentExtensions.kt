package money.rbk.presentation.utils

import androidx.fragment.app.Fragment
import money.rbk.presentation.exception.FragmentArgumentNonnullException

internal fun Fragment.getArgStringOrError(key: String): String =
    arguments?.getString(key) ?: throw FragmentArgumentNonnullException(this.javaClass, key)

internal fun Fragment.getArgIntOrError(key: String): Int =
    arguments?.getInt(key) ?: throw FragmentArgumentNonnullException(this.javaClass, key)
