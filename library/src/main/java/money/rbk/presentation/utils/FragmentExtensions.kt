package money.rbk.presentation.utils

import androidx.fragment.app.Fragment
import money.rbk.presentation.exception.FragmentArgumentNonnullException

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 08.07.19
 */

fun Fragment.getArgStringOrError(key: String): String =
    arguments?.getString(key) ?: throw FragmentArgumentNonnullException(this.javaClass, key)

fun Fragment.getArgIntOrError(key: String): Int =
    arguments?.getInt(key) ?: throw FragmentArgumentNonnullException(this.javaClass, key)
