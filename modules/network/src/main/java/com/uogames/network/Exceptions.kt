package com.uogames.network

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
class RequestHeaderFieldsTooLargeException(message: String) : Exception("431 Request Header Fields Too Large \n$message")

suspend inline fun <reified T> HttpResponse.ifSuccess(): T {
	if (status.isSuccess()) {
		return body() as T
	} else {
		when (status) {
			HttpStatusCode.Unauthorized -> throw UnauthorizedException(body())
			HttpStatusCode.BadRequest -> throw BadRequestException(body())
			HttpStatusCode.PaymentRequired -> throw PaymentRequiredException(body())
			HttpStatusCode.Forbidden -> throw ForbiddenException(body())
			HttpStatusCode.NotFound -> throw NotFoundException(body())
			HttpStatusCode.MethodNotAllowed -> throw MethodNotAllowedException(body())
			HttpStatusCode.NotAcceptable -> throw NotAcceptableException(body())
			HttpStatusCode.ProxyAuthenticationRequired -> throw ProxyAuthenticationRequiredException(body())
			HttpStatusCode.RequestTimeout -> throw RequestTimeoutException(body())
			HttpStatusCode.Conflict -> throw ConflictException(body())
			HttpStatusCode.Gone -> throw GoneException(body())
			HttpStatusCode.LengthRequired -> throw LengthRequiredException(body())
			HttpStatusCode.PreconditionFailed -> throw PreconditionFailedException(body())
			HttpStatusCode.PayloadTooLarge -> throw PayloadTooLargeException(body())
			HttpStatusCode.RequestURITooLong -> throw RequestUriTooLongException(body())
			HttpStatusCode.UnsupportedMediaType -> throw UnsupportedMediaTypeException(body())
			HttpStatusCode.RequestedRangeNotSatisfiable -> throw RequestedRangeNotSatisfiableException(body())
			HttpStatusCode.ExpectationFailed -> throw ExpectationFailedException(body())
			HttpStatusCode.UnprocessableEntity -> throw UnprocessableEntityException(body())
			HttpStatusCode.Locked -> throw LockedException(body())
			HttpStatusCode.FailedDependency -> throw FailedDependencyException(body())
			HttpStatusCode.UpgradeRequired -> throw UpgradeRequiredException(body())
			HttpStatusCode.TooManyRequests -> throw TooManyRequestsException(body())
			HttpStatusCode.RequestHeaderFieldTooLarge -> throw  RequestHeaderFieldsTooLargeException(body())
			else -> throw Exception("We wanted to get status code 2XX but got ${status.value}")
		}
	}
}
