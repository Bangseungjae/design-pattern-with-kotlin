package _3_behavioral_patterns._5_chain_of_responsibility

import java.lang.IllegalArgumentException

fun main() {
    val req = Request("developer@company.com", "왜 갑자기 빌드가 안 돼죠?")
    val chain = basicValidation(authentication(finalResponse()))
    val res = chain(req)
    println(res)
}


data class Request(val email: String, val question: String) {
    fun isKnownEmail(): Boolean {
        return true
    }

    fun isFromJuniorDeveloper(): Boolean {
        return false
    }
}

typealias Handler = (request: Request) -> Response

val authentication = fun(next: Handler) =
    fun(request: Request): Response {
        if (!request.isKnownEmail()) {
            throw IllegalArgumentException()
        }
        return next(request)
    }

val basicValidation = fun(next: Handler) =
    fun(request: Request): Response {
        if (request.email.isEmpty() || request.question.isEmpty()) {
            throw IllegalArgumentException()
        }
        return next(request)
    }

val finalResponse = fun() =
    fun(request: Request): Response {
        println("question: ${request.question}")
        return Response("I don't know")
    }

fun handleRequest(r: Request) {
    // 유효성 검사
    if (r.email.isEmpty() || r.question.isEmpty()) {
        return
    }

    // 인증
    // 이 사용자가 누구인지 알아낸다.
//    if (r.isKnownEmail()) {
//        return
//    }

    // 인가
    // 주니어 개발자의 요청은 자동으로 무시된다.
//    if (r.isFromJuniorDeveloper()) {
//        return
//    }

    println("모르겠네요. StackOverflow는 검색해 보셨나요?")
}

//interface Handler {
//    fun handle(request: Request): Response
//}

data class Response(val answer: String)
