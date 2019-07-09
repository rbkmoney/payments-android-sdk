package money.rbk.data.exception

import money.rbk.domain.entity.ApiError

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 09.07.19
 */
sealed class ApiException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)

internal class InternalServerError(code: Int) : ApiException(
    "Internal server error, code: $code"
)

internal class ClientError(code: Int, val error: ApiError) : ApiException(
    "Client error, code: $code"
)

internal class ResponseParsingException(stringBody: String, e: Exception) :
    ApiException(stringBody, e)
