package com.uogames.clientApi.version3.network

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

class BadRequestException(message: String) : Exception("400 Bad Request \n$message")
class UnauthorizedException(message: String) : Exception("401 Unauthorized \n$message")
class PaymentRequiredException(message: String) : Exception("402 Payment Required \n$message")
class ForbiddenException(message: String) : Exception("403 Forbidden \n$message")
class NotFoundException(message: String) : Exception("404 Not Found \n$message")
class MethodNotAllowedException(message: String) : Exception("405 Method Not Allowed \n$message")
class NotAcceptableException(message: String) : Exception("406 Not Acceptable \n$message")
class ProxyAuthenticationRequiredException(message: String) : Exception("407 Proxy Authentication Required \n$message")
class RequestTimeoutException(message: String) : Exception("408 Request Timeout \n$message")
class ConflictException(message: String) : Exception("409 Conflict \n$message")
class GoneException(message: String) : Exception("410 Gone \n$message")
class LengthRequiredException(message: String) : Exception("411 Length Required \n$message")
class PreconditionFailedException(message: String) : Exception("412 Precondition Failed \n$message")
class PayloadTooLargeException(message: String) : Exception("413 Payload Too Large \n$message")
class RequestUriTooLongException(message: String) : Exception("414 URI Too Long \n$message")
class UnsupportedMediaTypeException(message: String) : Exception("415 Unsupported Media Type \n$message")
class RequestedRangeNotSatisfiableException(message: String) : Exception("416 Range Not Satisfiable \n$message")
class ExpectationFailedException(message: String) : Exception("417 Expectation Failed \n$message")
class UnprocessableEntityException(message: String) : Exception("422 Unprocessable Entity \n$message")
class LockedException(message: String) : Exception("423 Locked \n$message")
class FailedDependencyException(message: String) : Exception("424 Failed Dependency \n$message")
class UpgradeRequiredException(message: String) : Exception("426 Upgrade Required \n$message")
class TooManyRequestsException(message: String) : Exception("429 Too Many Requests \n$message")
class RequestHeaderFieldsTooLargeException(message: String) :
    Exception("431 Request Header Fields Too Large \n$message")

suspend inline fun <reified T> HttpResponse.ifSuccess(): T {
    if (status.isSuccess()) {
        return body() as T
    } else {
        throw when (status) {
            HttpStatusCode.Unauthorized -> UnauthorizedException(body())
            HttpStatusCode.BadRequest -> BadRequestException(body())
            HttpStatusCode.PaymentRequired -> PaymentRequiredException(body())
            HttpStatusCode.Forbidden -> ForbiddenException(body())
            HttpStatusCode.NotFound -> NotFoundException(body())
            HttpStatusCode.MethodNotAllowed -> MethodNotAllowedException(body())
            HttpStatusCode.NotAcceptable -> NotAcceptableException(body())
            HttpStatusCode.ProxyAuthenticationRequired -> ProxyAuthenticationRequiredException(body())
            HttpStatusCode.RequestTimeout -> RequestTimeoutException(body())
            HttpStatusCode.Conflict -> ConflictException(body())
            HttpStatusCode.Gone -> GoneException(body())
            HttpStatusCode.LengthRequired -> LengthRequiredException(body())
            HttpStatusCode.PreconditionFailed -> PreconditionFailedException(body())
            HttpStatusCode.PayloadTooLarge -> PayloadTooLargeException(body())
            HttpStatusCode.RequestURITooLong -> RequestUriTooLongException(body())
            HttpStatusCode.UnsupportedMediaType -> UnsupportedMediaTypeException(body())
            HttpStatusCode.RequestedRangeNotSatisfiable -> RequestedRangeNotSatisfiableException(body())
            HttpStatusCode.ExpectationFailed -> ExpectationFailedException(body())
            HttpStatusCode.UnprocessableEntity -> UnprocessableEntityException(body())
            HttpStatusCode.Locked -> LockedException(body())
            HttpStatusCode.FailedDependency -> FailedDependencyException(body())
            HttpStatusCode.UpgradeRequired -> UpgradeRequiredException(body())
            HttpStatusCode.TooManyRequests -> TooManyRequestsException(body())
            HttpStatusCode.RequestHeaderFieldTooLarge -> RequestHeaderFieldsTooLargeException(body())
            else -> Exception("We wanted to get a status code 2XX but got the ${status.value} \n${body<String>()}")
        }
    }
}
